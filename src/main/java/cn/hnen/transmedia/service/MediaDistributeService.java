package cn.hnen.transmedia.service;

import cn.hnen.transmedia.entry.BusinessEnum;
import cn.hnen.transmedia.entry.FileHostDownloadRole;
import cn.hnen.transmedia.entry.ResponseModel;
import cn.hnen.transmedia.exception.MediaReciveException;
import cn.hnen.transmedia.handler.MediaDistributeHandler2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

import static cn.hnen.transmedia.entry.BusinessEnum.FAILED;

/**
 * @author YSH
 * @create 201812
 * @desc  利用@Async注解实现异步下载
 */
@Slf4j
@Service
public class MediaDistributeService {

    @Autowired
    public MediaDistributeHandler2 distributeHandler;

    @Autowired
    public RestTemplate restTemplate;

    @Value("${app.media.rootdir.name:download}")
    private String mediaRootdirName;

    /**
     * 异步下载多个文件
     * @param vos 文件实体集合
     */
    @Async
    public void receiveMediaListAsync(List<FileHostDownloadRole> vos) {
       receiveMediaList(vos);
    }

    public List<ResponseModel> receiveMediaList(List<FileHostDownloadRole> vos) {
//        List<ResponseModel> resultList = new ArrayList<>();
//        for (int i = 0; i < vos.size(); i++) {
//            //20190112 改同步:
//            //downHandler.receiveMediaAsync(vos.get(i));
//            ResponseModel responseModel = downHandler.receiveMediaSync(vos.get(i));
//            resultList.add(responseModel);
//        }
        /*另一种写法*/
        List<ResponseModel> resultList = vos.stream().map(vo -> receiveMediaSync(vo)).collect(Collectors.toList());
        return resultList;
    }



    @Retryable(value = MediaReciveException.class,maxAttempts =3,backoff = @Backoff(delay = 5000,multiplier = 3))
    public ResponseModel receiveMediaSync(FileHostDownloadRole vo) {
        ResponseModel responseModel = distributeHandler.receiveMedia(vo.getFileName());
        return responseModel;
    }

    /**
     * 异步下载
     * @param vo
     */
    @Async
    @Retryable(value = MediaReciveException.class,maxAttempts =3,backoff = @Backoff(delay = 5000,multiplier = 3))
    public void receiveMediaAsync(FileHostDownloadRole vo) {
        ResponseModel responseModel = distributeHandler.receiveMedia(vo.getFileName());
        if (responseModel.getCode()== BusinessEnum.SUCCESS.code ||responseModel.getCode()==BusinessEnum.EXISTED.code){
            distributeHandler.doDownReport(vo);
        }

    }

    @Recover
    public ResponseModel recoverReceive(MediaReciveException e, FileHostDownloadRole vo){
        log.error(" receiveMediaSync  *** 重试失败！***; {}",vo.getFileName(),e.getMessage(),e);
        return ResponseModel.warp(FAILED).setResult(vo.getFileName());
    }



    /**
     * 设备端文件下载
     * @param fileName 文件名
     * @param response  HttpServletResponse
     */
    public void downLoadMedia(String fileName, HttpServletResponse response) {
        distributeHandler.downloadMedia(fileName,response);
    }


}

