package cn.hnen.transmedia.handler;

import cn.hnen.transmedia.config.MediaDistributeConfig;
import cn.hnen.transmedia.entry.ResponseModel;
import cn.hnen.transmedia.exception.MediaDownloadException;
import cn.hnen.transmedia.exception.MediaUploadException;
import cn.hnen.transmedia.jpaentry.MediaTransInfoEntry;
import cn.hnen.transmedia.repository.MediaTransRepository;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static cn.hnen.transmedia.config.MediaDistributeConfig.*;
import static cn.hnen.transmedia.entry.BusinessEnum.*;
import static cn.hnen.transmedia.jpaentry.MediaTransInfoEntry.*;
import static cn.hnen.transmedia.jpaentry.MediaTransInfoEntry.TYPE_DOWN_REPLACE;


/**
 * @author  YSH
 * @create 20181203
 */
@Slf4j
@Component
public class MediaReplaceHandler {

    @Autowired
    public RestTemplate restTemplate;
    @Autowired
    private MediaTransRepository mediaDownRepository;
    private int testCount;

    public ResponseModel uploadMedia(MultipartFile file){


//        log.info("handler1  开始上传 ...... {}",file.getOriginalFilename());
//        //TODO
//        if (1==1)throw new MediaUploadException("handler1  测试 上传异常！");

        long start = System.currentTimeMillis();
        log.info("替换上传 开始: {}", file.getOriginalFilename());

        ResponseModel responseModel = null;
        String fileName = file.getOriginalFilename();
        Path path = Paths.get( mediaRootDir,fileName);
        log.info("文件位置:{}",path);
        if (Files.exists(path)){


            long stop = System.currentTimeMillis();
            /*记录日志*/
            log.info("替换上传 文件已存在,文件名：{}",fileName);

            /*记录到数据库*/
            MediaTransInfoEntry transInfo = new MediaTransInfoEntry();
            transInfo.setDownloadMediaDir(mediaRootDir);
            transInfo.setDownLoadResult(RESULT_DOWN_EXIST);
            transInfo.setFileName(fileName);
            transInfo.setDownloadType(TYPE_DOWN_REPLACE);
            transInfo.setDownLoadDuration(stop - start);
            transInfo.setDescribe("替换上传 文件已存在,文件名:"+fileName);
            mediaDownRepository.save(transInfo);
            /*返回值*/
            responseModel = ResponseModel.warp(EXISTED).setResult(fileName);
        }else{
            try {

                file.transferTo(path);

                long stop = System.currentTimeMillis();
                /*记录日志*/
                log.info("替换上传 完成  文件名称: {},文件大小:{}K,耗时 {}毫秒", fileName, file.getSize() / 1024, (stop - start));
                /*记录到数据库*/
                MediaTransInfoEntry transInfo = new MediaTransInfoEntry();
                transInfo.setDownloadMediaDir(mediaRootDir);
                transInfo.setDownLoadResult(RESULT_DOWN_SUCCESS);
                transInfo.setFileName(fileName);
                transInfo.setDownloadType(TYPE_DOWN_REPLACE);
                transInfo.setDownLoadDuration(stop - start);
                transInfo.setDescribe("替换上传 完成  文件名称: "+fileName);
                mediaDownRepository.save(transInfo);

                /*返回值*/
                responseModel = ResponseModel.warp(SUCCESS).setResult(fileName);
            } catch (IOException e) {
                long stop = System.currentTimeMillis();

                /*记录日志*/
                log.error("替换上传 失败:  文件名称: {}, 失败原因: {},耗时:{}", fileName, e.getMessage(), stop - start);

                /*记录到数据库*/
                MediaTransInfoEntry transInfo = new MediaTransInfoEntry();
                transInfo.setDownloadMediaDir(mediaRootDir);
                transInfo.setDownLoadResult(RESULT_DOWN_FAILED);
                transInfo.setFileName(fileName);
                transInfo.setDownloadType(TYPE_DOWN_REPLACE);
                transInfo.setDownLoadDuration(stop - start);
                transInfo.setDescribe(e.getMessage() != null ? e.getMessage() : e.toString());
                mediaDownRepository.save(transInfo);
                /*返回值*/
                responseModel = ResponseModel.warp(FAILED).setResult(fileName).setDetail(e.getMessage());
                e.printStackTrace();

                /*抛出异常，为了@Retryable重试*/
                MediaUploadException exception = new MediaUploadException(e);
                exception.setResult(responseModel);
                throw exception;

            }
        }
        return responseModel;
    }




    public ResponseModel downMedia(String fileName){

//        log.info("handler1  开始下载 ...... {}",fileName);
//        //TODO
//        if (1==1)throw new MediaDownloadException("handler1  测试 下载 异常！");

        long start = System.currentTimeMillis();
        log.info("替换下载 开始， 文件名 {}", fileName);

        ResponseModel responseModel = null;
        Path targetPath = Paths.get(mediaRootDir, fileName);
        log.info("文件位置:{}",targetPath);
        if (Files.exists(targetPath)) {
            long stop = System.currentTimeMillis();
            /*记录日志*/
            log.info("替换下载 文件已存在,文件名：{}",fileName);
            /*记录到数据库*/
            MediaTransInfoEntry transInfo = new MediaTransInfoEntry();
            transInfo.setDownloadMediaDir(mediaRootDir);
            transInfo.setDownLoadResult(RESULT_DOWN_EXIST);
            transInfo.setFileName(fileName);
            transInfo.setDownloadType(TYPE_DOWN_REPLACE);
            transInfo.setDownLoadDuration(stop - start);
            transInfo.setDescribe("替换下载 文件已存在,文件名:"+fileName);
            mediaDownRepository.save(transInfo);
            /*返回值*/
            responseModel = ResponseModel.warp(EXISTED).setResult(fileName);
        }else{
            try {
                MultiValueMap paramsMap = new LinkedMultiValueMap();
                paramsMap.add(downloadFilekey,fileName);
                Resource resource = restTemplate.getForObject(replaceDownloadApi + "/" + fileName, Resource.class);
                InputStream inputStream = resource.getInputStream();
                long size = Files.copy(inputStream, targetPath);

                long stop = System.currentTimeMillis();
                /*记录日志*/
                log.info("替换下载 完成  文件名称: {},文件大小:{}K,耗时 {}毫秒", fileName, size/ 1024, (stop - start));
                /*记录到数据库*/
                MediaTransInfoEntry transInfo = new MediaTransInfoEntry();
                transInfo.setDownloadMediaDir(mediaRootDir);
                transInfo.setDownLoadResult(RESULT_DOWN_SUCCESS);
                transInfo.setFileName(fileName);
                transInfo.setDownloadType(TYPE_DOWN_REPLACE);
                transInfo.setDownLoadDuration(stop - start);
                transInfo.setDescribe("替换下载 完成,文件名:"+fileName);
                mediaDownRepository.save(transInfo);
                /*返回值*/
                responseModel= ResponseModel.warp(SUCCESS).setResult(fileName);
            } catch (Exception e) {

                long stop = System.currentTimeMillis();
                /*记录日志*/
                log.error("替换下载 失败:  文件名称: {}, 失败原因: {},耗时:{}", fileName, e.getMessage(), stop - start);
                /*记录到数据库*/
                MediaTransInfoEntry transInfo = new MediaTransInfoEntry();
                transInfo.setDownloadMediaDir(mediaRootDir);
                transInfo.setDownLoadResult(RESULT_DOWN_FAILED);
                transInfo.setFileName(fileName);
                transInfo.setDownloadType(TYPE_DOWN_REPLACE);
                transInfo.setDownLoadDuration(stop - start);
                mediaDownRepository.save(transInfo);
                /*返回值*/
                responseModel = ResponseModel.warp(FAILED).setResult(fileName).setMessage(e.getMessage());

                /*打印错误信息*/
                e.printStackTrace();

                /*接着在抛出异常，为了@Retryable重试*/
                MediaDownloadException ex = new MediaDownloadException(e);
                ex.setResult(responseModel);
                throw ex;
            }
           
        }

        return responseModel;
    }



    public void replaceMediaReport(ResponseModel model){
        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
        param.add("report",  JSON.toJSONString(model));
        String result = restTemplate.postForObject(replaceDownloadReportApi, param, String.class);
        log.info("替换分发  结果汇报返回：>>>>{}",result);
    }

    @RecordLog
    public Boolean existFile(String fileName) {
        Boolean exists = Files.exists(Paths.get(MediaDistributeConfig.mediaRootDir, fileName));
        return exists;
    }

}
