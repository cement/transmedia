package cn.hnen.transmedia.service;

import cn.hnen.transmedia.config.MediaDistributeConfig;
import cn.hnen.transmedia.entry.ResponseModel;
import cn.hnen.transmedia.jpaentry.MediaTransInfoEntry;
import cn.hnen.transmedia.repository.MediaTransRepository;
import cn.hnen.transmedia.util.MediaReplaceHandler;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static cn.hnen.transmedia.config.MediaDistributeConfig.downloadMediaDir;
import static cn.hnen.transmedia.config.MediaDistributeConfig.replaceDownloadApi;
import static cn.hnen.transmedia.config.MediaDistributeConfig.replaceDownloadReportApi;
import static cn.hnen.transmedia.entry.BusinessEnum.*;
import static cn.hnen.transmedia.jpaentry.MediaTransInfoEntry.*;

/**
 * @author  YSH
 * @create 20181203
 */
@Slf4j
@Service
public class MediaReplaceService {

    @Autowired
    public MediaReplaceHandler uploadHandler;

    @Autowired
    private RestTemplate restTemplate;


    @Autowired
    private MediaTransRepository mediaDownRepository;

    public  ResponseModel uploadMedia(MultipartFile file){

        long start = System.currentTimeMillis();
        log.info("替换上传 开始: {}", file.getOriginalFilename());

        ResponseModel responseModel = new ResponseModel();
        String fileName = file.getOriginalFilename();
        Path path = Paths.get( downloadMediaDir,fileName);
        log.info("文件位置:{}",path);
        if (Files.exists(path)){
            long stop = System.currentTimeMillis();
           /*记录日志*/
            log.info("替换上传 文件已存在,文件名：{}",fileName);

            /*记录到数据库*/
            MediaTransInfoEntry transInfo = new MediaTransInfoEntry();
                transInfo.setDownloadMediaDir(downloadMediaDir);
                transInfo.setDownLoadResult(DOWN_RESULT_EXIST);
                transInfo.setFileName(fileName);
                transInfo.setDownloadType(DOWN_TYPE_REPLACE);
                transInfo.setDownLoadDuration(stop - start);
                transInfo.setDescribe("替换上传 文件已存在,文件名:"+fileName);
            mediaDownRepository.save(transInfo);
            /*返回值*/
             responseModel = ResponseModel.warp(EXISTED).setData(fileName);
        }else{
            try {

                 file.transferTo(path);

                 long stop = System.currentTimeMillis();
                /*记录日志*/
                log.info("替换上传 完成  文件名称: {},文件大小:{}K,耗时 {}毫秒", fileName, file.getSize() / 1024, (stop - start));
                /*记录到数据库*/
                MediaTransInfoEntry transInfo = new MediaTransInfoEntry();
                    transInfo.setDownloadMediaDir(downloadMediaDir);
                    transInfo.setDownLoadResult(DOWN_RESULT_SUCCESS);
                    transInfo.setFileName(fileName);
                    transInfo.setDownloadType(DOWN_TYPE_REPLACE);
                    transInfo.setDownLoadDuration(stop - start);
                    transInfo.setDescribe("替换上传 完成  文件名称: "+fileName);
                mediaDownRepository.save(transInfo);

                /*返回值*/
                responseModel = ResponseModel.warp(SUCCESS).setData(fileName);
            } catch (IOException e) {
                long stop = System.currentTimeMillis();

                /*记录日志*/
                log.error("替换上传 失败:  文件名称: {}, 失败原因: {},耗时:{}", fileName, e.getMessage(), stop - start);

                /*记录到数据库*/
                MediaTransInfoEntry transInfo = new MediaTransInfoEntry();
                    transInfo.setDownloadMediaDir(downloadMediaDir);
                    transInfo.setDownLoadResult(DOWN_RESULT_FAILED);
                    transInfo.setFileName(fileName);
                    transInfo.setDownloadType(DOWN_TYPE_REPLACE);
                    transInfo.setDownLoadDuration(stop - start);
                    transInfo.setDescribe(e.getMessage() != null ? e.getMessage() : e.toString());
                mediaDownRepository.save(transInfo);
                /*返回值*/
                 responseModel = ResponseModel.warp(FAILED).setData(fileName).setDetail(e.getMessage());
                e.printStackTrace();
            }
        }
        return responseModel;
    }

    /*此处不能用同步，否则立即返回流断了无法上传*/
//    @Async
    public  ResponseModel uploadMediaReport(MultipartFile file){
        ResponseModel responseModel = uploadMedia(file);
        replaceMediaReport(responseModel);
        log.info(">>>>>>>>> uploadMediaReport >>>>>>>>>>{}",responseModel);
        return responseModel;
    }





    public void replaceMediaReport(ResponseModel model){
        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
        param.add("report",  JSON.toJSONString(model));
        ResponseModel responseModel = restTemplate.postForObject(replaceDownloadReportApi, param, ResponseModel.class);
        log.info("替换分发  结果汇报返回：>>>>{}",responseModel);
    }



    public ResponseModel downMedia(String fileName) {

        long start = System.currentTimeMillis();
        log.info("替换下载 开始， 文件名 {}", fileName);

        ResponseModel responseModel = new ResponseModel();
        Path targetPath = Paths.get(downloadMediaDir, fileName);
        log.info("文件位置:{}",targetPath);
        if (Files.exists(targetPath)) {

            long stop = System.currentTimeMillis();
            /*记录日志*/
            log.info("替换下载 文件已存在,文件名：{}",fileName);
            /*记录到数据库*/
            MediaTransInfoEntry transInfo = new MediaTransInfoEntry();
                transInfo.setDownloadMediaDir(downloadMediaDir);
                transInfo.setDownLoadResult(DOWN_RESULT_EXIST);
                transInfo.setFileName(fileName);
                transInfo.setDownloadType(DOWN_TYPE_REPLACE);
                transInfo.setDownLoadDuration(stop - start);
                transInfo.setDescribe("替换下载 文件已存在,文件名:"+fileName);
            mediaDownRepository.save(transInfo);
            /*返回值*/
            responseModel = ResponseModel.warp(EXISTED).setData(fileName);
        }else{
            try {
                MultiValueMap paramsMap = new LinkedMultiValueMap();
                paramsMap.add(MediaDistributeConfig.downloadFilekey,fileName);
                ResponseEntity<Resource>  respEntity = restTemplate.postForEntity(replaceDownloadApi, paramsMap,Resource.class);
                InputStream inputStream = respEntity.getBody().getInputStream();
                long size = Files.copy(inputStream, targetPath);

                long stop = System.currentTimeMillis();
                /*记录日志*/
                log.info("替换下载 完成  文件名称: {},文件大小:{}K,耗时 {}毫秒", fileName, size/ 1024, (stop - start));
                /*记录到数据库*/
                MediaTransInfoEntry transInfo = new MediaTransInfoEntry();
                    transInfo.setDownloadMediaDir(downloadMediaDir);
                    transInfo.setDownLoadResult(DOWN_RESULT_SUCCESS);
                    transInfo.setFileName(fileName);
                    transInfo.setDownloadType(DOWN_TYPE_REPLACE);
                    transInfo.setDownLoadDuration(stop - start);
                   transInfo.setDescribe("替换下载 完成,文件名:"+fileName);
                mediaDownRepository.save(transInfo);
                /*返回值*/
                responseModel= ResponseModel.warp(SUCCESS).setData(fileName);
            } catch (Exception e) {

                long stop = System.currentTimeMillis();
                /*记录日志*/
                log.error("替换下载 失败:  文件名称: {}, 失败原因: {},耗时:{}", fileName, e.getMessage(), stop - start);
                /*记录到数据库*/
                MediaTransInfoEntry transInfo = new MediaTransInfoEntry();
                transInfo.setDownloadMediaDir(downloadMediaDir);
                transInfo.setDownLoadResult(DOWN_RESULT_FAILED);
                transInfo.setFileName(fileName);
                transInfo.setDownloadType(DOWN_TYPE_REPLACE);
                transInfo.setDownLoadDuration(stop - start);
                mediaDownRepository.save(transInfo);
                /*返回值*/
                responseModel = ResponseModel.warp(FAILED).setData(fileName).setMessage(e.getMessage());
                /*打印错误信息*/
                e.printStackTrace();
            }
        }
        return responseModel;
    }

    /*此处不需要异步步，这样调用端可以选择同步或异步。*/
//   @Async
    public ResponseModel downMediaReport(String fileName) {
        ResponseModel responseModel = downMedia(fileName);
        replaceMediaReport(responseModel);
        log.info(">>>>>>>>>> downMediaReport >>>>>>>>>{}",responseModel);
        return responseModel;
    }



}
