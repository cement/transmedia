package cn.hnen.transmedia.service;

import cn.hnen.transmedia.entry.FileHostDownloadRole;
import cn.hnen.transmedia.entry.ResponseModel;
import cn.hnen.transmedia.handler.MediaDistributeHandler2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;

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


    /**
     * 下载
     * @param vo
     */
    public ResponseModel receiveMedia(FileHostDownloadRole vo) {
        ResponseModel responseModel = distributeHandler.receiveMedia(vo);
        return responseModel;
    }

    /**
     * 异步下载
     * @param vo
     */
    @Async
    public void receiveMediaAsync(FileHostDownloadRole vo) {
        distributeHandler.receiveMedia(vo);
    }







}

