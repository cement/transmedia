package cn.hnen.transmedia.service;

import cn.hnen.transmedia.handler.MediaDownloadHandler2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;


@Slf4j
@Service
public class MediaDownloadService {


    @Autowired
    public MediaDownloadHandler2 distributeHandler;



    /**
     * 设备端文件下载
     * @param fileName 文件名
     * @param response  HttpServletResponse
     */
    public void downLoadMedia(String fileName, HttpServletResponse response) {
        distributeHandler.downLoadMedia(fileName,response);
    }
}
