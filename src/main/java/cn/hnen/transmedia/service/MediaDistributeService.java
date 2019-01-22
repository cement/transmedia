package cn.hnen.transmedia.service;

import cn.hnen.transmedia.entry.FileHostDownloadRole;
import cn.hnen.transmedia.util.MediaDownHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author YSH
 * @create 201812
 * @desc  利用@Async注解实现异步下载
 */
@Slf4j
@Service
public class MediaDistributeService {

    @Autowired
    public MediaDownHandler downHandler;


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
    public void receiveMediaList(List<FileHostDownloadRole> vos) {
        for (int i = 0; i < vos.size(); i++) {
            //20190112 改同步:
//            downHandler.receiveMediaAsync(vos.get(i));
            downHandler.receiveMedia(vos.get(i));
        }

    }





    /**
     * 设备端文件下载
     * @param fileName 文件名
     * @param response  HttpServletResponse
     */
    public void downLoadMedia(String fileName, HttpServletResponse response) {
          downHandler.downLoadMedia(fileName,response);
    }


}

