package cn.hnen.transmedia.handler;

import cn.hnen.transmedia.annotation.RecordLog;
import cn.hnen.transmedia.config.MediaDistributeConfig;
import cn.hnen.transmedia.entry.BusinessEnum;
import cn.hnen.transmedia.entry.FileHostDownloadRole;
import cn.hnen.transmedia.entry.ResponseModel;
import cn.hnen.transmedia.exception.MediaReciveException;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import static cn.hnen.transmedia.config.MediaDistributeConfig.*;
import static cn.hnen.transmedia.entry.BusinessEnum.FAILED;

/**
 * @author YSH
 * @create 20181203
 * @实现从上级下载及下级下载
 */
@Slf4j
@Component
public class MediaDistributeHandler2 {


    @RecordLog
    @Retryable(value = MediaReciveException.class,maxAttempts = 10,backoff = @Backoff(delay = 120000L,multiplier = 2))
    public ResponseModel receiveMedia(FileHostDownloadRole vo){

        long start = System.currentTimeMillis();
        String fileName = vo.getFileName();
        Path targetPath = Paths.get(mediaRootDir, fileName);
        String fullUrl = downloadApiUrl + "?fileName=" + fileName;
        if (Files.exists(targetPath) && targetPath.toFile().length()>0) {
            log.info("从上级 文件已经存在，文件名：{}",fileName);
             this.doDownReport(vo);
            return ResponseModel.warp(BusinessEnum.EXISTED).setResult(fileName);
        } else {
            try {
                long length = HttpUtil.downloadFile(fullUrl, targetPath.toFile());
                this.doDownReport(vo);
                long stop = System.currentTimeMillis();
                log.info("从上级 下载完成，文件名：{}，文件大小/耗时：{}",fileName,length+"/"+(stop-start));
                return ResponseModel.warp(BusinessEnum.SUCCESS).setResult(fileName).setDetail(length+"/"+(stop-start));
            } catch (Exception e) {
                try {
                    Files.deleteIfExists(targetPath);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                log.info("从上级 下载失败，文件名：{}",fileName);
                /*重新抛出异常，以便@Retryable进行重试*/
                throw new MediaReciveException(e);
            }
        }
    }

    @Recover
    public ResponseModel recoverReceive(MediaReciveException e, FileHostDownloadRole vo){
        log.error(" receiveMediaSync  *** 重试失败！***; {}",vo.getFileName(),e.getMessage(),e);
        return ResponseModel.warp(FAILED).setResult(vo.getFileName());
    }












    /**
     * 从上级服务器下载情况汇报
     *
     */
    @RecordLog
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


