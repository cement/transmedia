package cn.hnen.transmedia.service;

import cn.hnen.transmedia.entry.FileHostDownloadRole;
import cn.hnen.transmedia.util.FileDownHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author YSH
 * @create 201812
 * @desc  利用@Async注解实现异步下载
 */
@Slf4j
@Service
public class DistributeMediaService {

    @Autowired
    public FileDownHandler downHandler;


    /**
     * 异步下载多个文件
     * @param vos 文件实体集合
     */
    @Async
    public void receiveMediaListAsync(List<FileHostDownloadRole> vos) {
//        ExecutorService executorService = ThreadUtil.newExecutor(10);
        for (int i = 0; i < vos.size(); i++) {

            downHandler.receiveMediaAsync(vos.get(i));
        }

    }



//    public void downLoadMediaListAsync(String fileNameList, HttpServletResponse response) {
//        for (int i = 0; i < fileNameList.size(); i++) {
//            response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", fileNameList.get(i)));
//            downHandler.downLoadMediaAsync(fileNameList.get(i),response);
//        }
//    }

    /**
     * 设备端文件下载
     * @param fileName 文件名
     * @param response  HttpServletResponse
     */
    public void downLoadMedia(String fileName, HttpServletResponse response) {
//        response.setContentType("application/force-download");
//        try {
//            response.setContentType("application/octet-stream");
//            response.setHeader("Content-Disposition", "attachment; filename="+  new String(fileName.getBytes("utf-8"),"iso-8859-1"));
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
          downHandler.downLoadMedia(fileName,response);
    }

}

