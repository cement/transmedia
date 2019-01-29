package cn.hnen.transmedia.handler;

import cn.hnen.transmedia.config.MediaDistributeConfig;
import cn.hnen.transmedia.entry.BusinessEnum;
import cn.hnen.transmedia.entry.FileHostDownloadRole;
import cn.hnen.transmedia.entry.ResponseModel;
import cn.hnen.transmedia.repository.MediaTransRepository;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import static cn.hnen.transmedia.config.MediaDistributeConfig.*;

/**
 * @author YSH
 * @create 20181203
 * @实现从上级下载及下级下载
 */
@Slf4j
@Component
public class MediaDistributeHandler2 {



    @Autowired
    private MediaTransRepository mediaDownRepository;

    public int testCount;

    public ResponseModel receiveMedia(String fileName) {

        long start = System.currentTimeMillis();
        Path targetPath = Paths.get(mediaRootDir, fileName);
        String fullUrl = downloadApiPath + "?fileName=" + fileName;
        if (Files.exists(targetPath)) {
            log.info("从上级 文件已经存在，文件名：{}",fileName);
            return ResponseModel.warp(BusinessEnum.EXISTED).setResult(fileName);
        } else {
            try {
                long length = HttpUtil.downloadFile(fullUrl, targetPath.toFile());
                long stop = System.currentTimeMillis();
                log.info("从上级 下载完成，文件名：{}，文件大小/耗时：{}",fileName,length+"/"+(stop-start));
                return ResponseModel.warp(BusinessEnum.SUCCESS).setResult(fileName).setDetail(length+"/"+(stop-start));
            } catch (Exception e) {

                log.info("从上级 下载失败，文件名：{}",fileName);
                return ResponseModel.warp(BusinessEnum.FAILED).setResult(fileName);
            }
        }
    }







    @RecordLog
    public void downloadMedia(String fileName, HttpServletResponse response) {

        long start = System.currentTimeMillis();
        Path srcPath = Paths.get(mediaRootDir, fileName);
        try (BufferedInputStream inputStream = FileUtil.getInputStream(srcPath);
             OutputStream outputStream = response.getOutputStream()){
            long length = IoUtil.copyByNIO(inputStream, outputStream, 1024 * 8, null);
            /*记录日志*/
            long stop = System.currentTimeMillis();
            log.info("设备端 下载完成  文件名称: {},文件大小:{},耗时 {}毫秒", fileName, length, (stop - start));
        } catch (IOException e) {
            /*记录日志*/
            long stop = System.currentTimeMillis();
            log.error("设备端 下载失败  文件名称: {},,耗时 {}毫秒", fileName,stop - start,e);
        }
    }






    /**
     * 从上级服务器下载情况汇报
     *
     */
    public void doDownReport(FileHostDownloadRole vo) {

        HashMap<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("id", vo.getId());
        try {
            String result = HttpUtil.post(MediaDistributeConfig.downloadReportUrl, paramsMap);
           log.info("从上级下载文件 汇报成功, 文件名称:{},返回信息:{}", vo.getFileName(), result);
        } catch (Exception e) {
            log.info("从上级下载文件 汇报失败, 文件名称:{}",vo.getFileName(), e);
        }

    }

}


