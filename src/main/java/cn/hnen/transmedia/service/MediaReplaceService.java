package cn.hnen.transmedia.service;

import cn.hnen.transmedia.Config.FileDistributeConfig;
import cn.hnen.transmedia.entry.ResponseModel;
import cn.hnen.transmedia.util.MediaReplaceHandler;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
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

import static cn.hnen.transmedia.entry.BusinessEnum.*;
import static cn.hnen.transmedia.Config.FileDistributeConfig.downloadMediaDir;

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

    private static final  String api_root_path="http://192.168.1.112:8080/adback/";
    private static final  String api_download_path="api/adreplace/down";
    private static final String api_callback_path = "api/adreplace/callback";
    private static final String api_callback_url = api_root_path+api_callback_path;
    private static final String api_download_url= api_root_path+api_download_path;





    public  ResponseModel uploadMedia(MultipartFile file){
        ResponseModel responseModel = new ResponseModel();
        String fileName = file.getOriginalFilename();
        Path path = Paths.get( downloadMediaDir,fileName);
        if (Files.exists(path)){
             responseModel = ResponseModel.warp(EXISTED).setData(fileName);
        }else{
            try {
                 file.transferTo(path);
                 responseModel = ResponseModel.warp(SUCCESS).setData(fileName);
            } catch (IOException e) {
                 responseModel = ResponseModel.warp(FAILED).setData(fileName).setDetail(e.getMessage());
                e.printStackTrace();
            }
        }
        return responseModel;
    }

    @Async
    public  void uploadMediaAsync(MultipartFile file){
        ResponseModel responseModel = uploadMedia(file);
        replaceMediaReport(responseModel);
    }





    public void replaceMediaReport(ResponseModel model){
        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
        param.add("report",  JSON.toJSONString(model));
        ResponseModel responseModel = restTemplate.postForObject(api_callback_url, param, ResponseModel.class);
        log.info("结果汇报返回：>>>>{}",responseModel);
    }



    public ResponseModel downMedia(String fileName) {

        ResponseModel responseModel = new ResponseModel();
        Path targetPath = Paths.get(downloadMediaDir, fileName);
        if (Files.exists(targetPath)) {
            responseModel = ResponseModel.warp(EXISTED).setData(fileName);
        }else{
            try {
                MultiValueMap paramsMap = new LinkedMultiValueMap();
                paramsMap.add(FileDistributeConfig.downloadFilekey,fileName);
                ResponseEntity<Resource>  respEntity = restTemplate.postForEntity(api_download_url, paramsMap,Resource.class);
                InputStream inputStream = respEntity.getBody().getInputStream();
                Files.copy(inputStream, targetPath);
                responseModel= ResponseModel.warp(SUCCESS).setData(fileName);
            } catch (Exception e) {
                responseModel = ResponseModel.warp(FAILED).setData(fileName).setMessage(e.getMessage());
                e.printStackTrace();
            }
        }
        return responseModel;
    }

    @Async
    public void downMediaAsync(String fileName) {
        ResponseModel responseModel = downMedia(fileName);
        replaceMediaReport(responseModel);
    }



}
