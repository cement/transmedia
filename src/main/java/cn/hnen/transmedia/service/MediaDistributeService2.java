package cn.hnen.transmedia.service;

import cn.hnen.transmedia.entry.FileHostDownloadRole;
import cn.hnen.transmedia.util.MediaDownHandler;
import cn.hutool.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static cn.hnen.transmedia.config.MediaDistributeConfig.downloadApiPath;

/**
 * @author YSH
 * @create 201812
 * @desc  利用@Async注解实现异步下载
 */
@Slf4j
@Service
public class MediaDistributeService2 {


    @Value("${app.media.rootdir.name:download}")
    private String mediaRootdirName;



    /*从上端服务下载*/
    public void receiveMedia(String fileName) {
        String url = downloadApiPath + "?fileName=" + fileName;
        Path srcPath = Paths.get("e:/test/downNew", fileName);
        if (Files.exists(srcPath)){
            log.info("文件： {} 已经存在！",fileName);
        }else{
            try {
                HttpUtil.downloadFile(url,"e:/test/downNew/"+fileName);
                log.info("文件： {} 下载成功！",fileName);
            } catch (Exception e) {
                log.error("下载失败！{}",fileName,e.getMessage(),e);
                e.printStackTrace();
            }
        }

    }



    /*设备端下载，直接交给tomcat DefaultServlet处理*/
    public void downLoadMedia(String fileName, HttpServletRequest request,HttpServletResponse response) {
        try {
            request.getRequestDispatcher(mediaRootdirName+ File.separator+fileName).forward(request, response);
            log.info("下载完成：{}",fileName);
        } catch (ServletException e) {
            log.error("下载失败！{}",fileName,e.getMessage(),e);
            e.printStackTrace();
        } catch (IOException e) {
            log.error("下载失败！{}",fileName,e.getMessage(),e);
            e.printStackTrace();
        }
    }
}

