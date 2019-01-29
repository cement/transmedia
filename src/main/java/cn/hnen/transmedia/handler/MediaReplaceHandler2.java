package cn.hnen.transmedia.handler;

import cn.hnen.transmedia.config.MediaDistributeConfig;
import cn.hnen.transmedia.entry.BusinessEnum;
import cn.hnen.transmedia.entry.ResponseModel;
import cn.hnen.transmedia.exception.MediaDownloadException;
import cn.hnen.transmedia.exception.MediaUploadException;
import cn.hnen.transmedia.jpaentry.MediaTransInfoEntry;
import cn.hnen.transmedia.repository.MediaTransRepository;
import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static cn.hnen.transmedia.config.MediaDistributeConfig.*;
import static cn.hnen.transmedia.entry.BusinessEnum.*;
import static cn.hnen.transmedia.jpaentry.MediaTransInfoEntry.*;


/**
 * @author  YSH
 * @create 20181203
 */
@Slf4j
@Component
public class MediaReplaceHandler2 {

    @Autowired
    public RestTemplate restTemplate;
    @Autowired
    private MediaTransRepository mediaDownRepository;
    private int testCount;

    @RecordLog
    public ResponseModel uploadMedia(MultipartFile file) {


        long start = System.currentTimeMillis();
        ResponseModel responseModel = null;
        String fileName = file.getOriginalFilename();
        Path path = Paths.get( mediaRootDir,fileName);
        if (Files.exists(path)){
            String log = "替换上传 文件已经存在！：文件名："+fileName;
            responseModel = ResponseModel.warp(EXISTED).setLog(log);
        }else{
            try {
                file.transferTo(path);
                /*返回值*/
                long stop = System.currentTimeMillis();
                String log = "替换下载 成功！：文件名："+fileName+",文件大小/耗时："+file.getSize()+"/"+(stop-start);
                responseModel = ResponseModel.warp(SUCCESS).setLog(log);
            } catch (IOException e) {
                long stop = System.currentTimeMillis();
                String log = "替换下载 失败！：文件名："+fileName+",文件大小/耗时："+file.getSize()+"/"+(stop-start);
                responseModel = ResponseModel.warp(FAILED).setLog(log);
                MediaUploadException ex = new MediaUploadException(e);
                ex.setResult(responseModel);
                throw ex;
            }
        }
        return responseModel;
    }




    @RecordLog
    public ResponseModel downMedia(String fileName){



        long start = System.currentTimeMillis();
        ResponseModel responseModel = null;
        Path targetPath = Paths.get(mediaRootDir, fileName);
        if (Files.exists(targetPath)) {
            /*返回值*/
            String log = "替换下载 文件已经存在！：文件名："+fileName;
            responseModel = ResponseModel.warp(EXISTED).setResult(fileName).setLog(log);
        }else{
            try {
                String url = replaceDownloadApi + "/" + fileName;
                File destFile = FileUtil.file(mediaRootDir, fileName);
                long length = HttpUtil.downloadFile(url, destFile);
                long stop = System.currentTimeMillis();
                String log = "替换下载 成功！：文件名："+fileName+",文件大小/耗时："+length+"/"+(stop-start);
                responseModel= ResponseModel.warp(SUCCESS).setResult(fileName).setLog(log);
            } catch (Exception e) {
                long stop = System.currentTimeMillis();
                String log = "替换下载 失败！：文件名："+fileName+",耗时："+(stop-start);
                responseModel= ResponseModel.warp(FAILED).setResult(fileName).setLog(log);
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
