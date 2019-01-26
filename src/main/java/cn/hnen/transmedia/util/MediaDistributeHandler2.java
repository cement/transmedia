package cn.hnen.transmedia.util;

import cn.hnen.transmedia.config.MediaDistributeConfig;
import cn.hnen.transmedia.entry.BusinessEnum;
import cn.hnen.transmedia.entry.DownResultModel;
import cn.hnen.transmedia.entry.FileHostDownloadRole;
import cn.hnen.transmedia.entry.ResponseModel;
import cn.hnen.transmedia.exception.MediaReciveException;
import cn.hnen.transmedia.jpaentry.MediaTransInfoEntry;
import cn.hnen.transmedia.repository.MediaTransRepository;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.StreamProgress;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.channels.Channels;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import static cn.hnen.transmedia.config.MediaDistributeConfig.*;
import static cn.hnen.transmedia.jpaentry.MediaTransInfoEntry.*;

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

    /**
     * 自写单文件下载，可以定义缓冲大小，默认1024000；
     *
     * @return
     */
//    public ResponseModel receiveMedia(FileHostDownloadRole vo) {
//
////        log.info(" 测试Retry 开始..............{}",vo.getFileName());
////        if (testCount++%2!=0) throw new MediaReciveException("测试 retry  >>>>>>>>>>>>>>>>>>");
//
//        long start = System.currentTimeMillis();
//        log.info("开始下载 {}", vo.getFileName());
//
//
//        FileOutputStream outputStream = null;
//        InputStream inputStream = null;
//
//        File targetFile = new File(mediaRootDir, vo.getFileName());
//
//        try {
//            if (targetFile.exists() && targetFile.length() > 0) {
//                    /*记录日志*/
//                    log.info("文件已存在 文件名称:{}", vo.getFileName());
//
//                    /*保存到数据库*/
//                    MediaTransInfoEntry downloadInfoEntry = new MediaTransInfoEntry();
//                    downloadInfoEntry.setFileId(vo.getId());
//                    downloadInfoEntry.setCityId(vo.getCityId());
//                    downloadInfoEntry.setDownloadMediaDir(mediaRootDir);
//                    downloadInfoEntry.setDownLoadResult(RESULT_DOWN_EXIST);
//                    downloadInfoEntry.setDownloadType(TYPE_DOWN_FROM);
//                    downloadInfoEntry.setFileName(vo.getFileName());
//                    downloadInfoEntry.setBeginPlayTime(vo.getBeginPlayTime());
//                    downloadInfoEntry.setDescribe("文件已存在!");
//                    mediaDownRepository.save(downloadInfoEntry);
//                    /*汇报结果*/
//                    this.doDownReport(vo);
//                    /*返回*/
//                    return ResponseModel.warp(BusinessEnum.EXISTED).setData(vo.getFileName());
//            }
//
//            String url = downloadApiPath + "?fileName=" + vo.getFileName();
//            ResponseEntity<Resource> respEntry = restTemplate.getForEntity(url, Resource.class);
//
//            if (200 != respEntry.getStatusCodeValue()) {
//                /*记录日志*/
//                log.error("上级文件不存在 文件名称:{}", vo.getFileName());
//                /*保存到数据库*/
//                MediaTransInfoEntry downloadInfoEntry = new MediaTransInfoEntry();
//                downloadInfoEntry.setFileId(vo.getId());
//                downloadInfoEntry.setCityId(vo.getCityId());
//                downloadInfoEntry.setDownloadMediaDir(mediaRootDir);
//                downloadInfoEntry.setDownLoadResult(RESULT_DOWN_UNEXIST);
//                downloadInfoEntry.setDownloadType(TYPE_DOWN_FROM);
//                downloadInfoEntry.setFileName(vo.getFileName());
//                downloadInfoEntry.setBeginPlayTime(vo.getBeginPlayTime());
//                downloadInfoEntry.setDescribe("上级文件不存在!");
//                mediaDownRepository.save(downloadInfoEntry);
//
//                /*返回值*/
//                return ResponseModel.warp(BusinessEnum.FAILED).setData(vo.getFileName());
//            }
//
//            inputStream = respEntry.getBody().getInputStream();
//            outputStream = new FileOutputStream(targetFile);
//
//            byte[] buf = new byte[downloadBufferSize];
//            int readed = 0;
//            while ((readed = inputStream.read(buf)) != -1) {
//                outputStream.write(buf, 0, readed);
//            }
//            outputStream.flush();
//
//            long stop = System.currentTimeMillis();
//            /*记录日志*/
//            log.info("下载完成  文件名称: {},文件大小:{}K,耗时 {}毫秒", vo.getFileName(), targetFile.length() / 1024, (stop - start));
//
//            /*保存到数据库*/
//            MediaTransInfoEntry downloadInfoEntry = new MediaTransInfoEntry();
//            downloadInfoEntry.setFileId(vo.getId());
//            downloadInfoEntry.setCityId(vo.getCityId());
//            downloadInfoEntry.setDownloadMediaDir(mediaRootDir);
//            downloadInfoEntry.setDownLoadResult(RESULT_DOWN_SUCCESS);
//            downloadInfoEntry.setFileName(vo.getFileName());
//            downloadInfoEntry.setDownloadType(TYPE_DOWN_FROM);
//            downloadInfoEntry.setBeginPlayTime(vo.getBeginPlayTime());
//            downloadInfoEntry.setDownLoadDuration(stop - start);
//            downloadInfoEntry.setDescribe("文件大小:" + targetFile.length() / 1024 + "K,耗时: " + (stop - start) + "毫秒");
//            mediaDownRepository.save(downloadInfoEntry);
//
//           /*汇报结果*/
//            this.doDownReport(vo);
//           /*返回值*/
//            return ResponseModel.warp(BusinessEnum.SUCCESS).setData(vo.getFileName());
//
//        } catch (Exception e) {
//
//            long stop = System.currentTimeMillis();
//            /*记录日志*/
//            log.error("下载失败  文件名称: {}, 失败原因: {},耗时{}", vo.getFileName(), e.getMessage(), stop - start);
//
//            /*保存到数据库*/
//            MediaTransInfoEntry downloadInfoEntry = new MediaTransInfoEntry();
//            downloadInfoEntry.setFileId(vo.getId());
//            downloadInfoEntry.setCityId(vo.getCityId());
//            downloadInfoEntry.setDownloadMediaDir(mediaRootDir);
//            downloadInfoEntry.setDownLoadResult(RESULT_DOWN_FAILED);
//            downloadInfoEntry.setFileName(vo.getFileName());
//            downloadInfoEntry.setDownloadType(TYPE_DOWN_FROM);
//            downloadInfoEntry.setBeginPlayTime(vo.getBeginPlayTime());
//            downloadInfoEntry.setDownLoadDuration(stop - start);
//            downloadInfoEntry.setDescribe(e.getMessage() != null ? e.getMessage() : e.toString());
//
//            mediaDownRepository.save(downloadInfoEntry);
//
//            /*重新抛出异常，以便@Retryable进行重试*/
//            throw new MediaReciveException(e);
//
//            /*返回*/
////            return ResponseModel.warp(BusinessEnum.FAILED).setData(vo.getFileName());
//            //e.printStackTrace();
//
//        } finally {
//            if (inputStream != null) {
//                try {
//                    inputStream.close();
//                } catch (IOException e1) {
//                    e1.printStackTrace();
//
//                }
//            }
//            if (outputStream != null) {
//                try {
//                    outputStream.close();
//                } catch (IOException e2) {
//                    e2.printStackTrace();
//                }
//            }
//        }
//
//    }
    public ResponseModel receiveMedia(String fileName) {

        Path targetPath = Paths.get(mediaRootDir, fileName);
        String fullUrl = downloadApiPath + "?fileName=" + fileName;
        if (Files.exists(targetPath)) {
            return ResponseModel.warp(BusinessEnum.EXISTED).setData(fileName);
        } else {
            try {
                long length = HttpUtil.downloadFile(fullUrl, targetPath.toFile());
                return ResponseModel.warp(BusinessEnum.SUCCESS).setData(fileName).setDetail(String.valueOf(length));
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseModel.warp(BusinessEnum.FAILED).setData(fileName);
            }
        }
    }







    public void downLoadMedia(String fileName, HttpServletResponse response) {

        long start = System.currentTimeMillis();
        Path srcPath = Paths.get(mediaRootDir, fileName);
        try (BufferedInputStream inputStream = FileUtil.getInputStream(srcPath);
             OutputStream outputStream = response.getOutputStream()){
            long length = IoUtil.copyByNIO(inputStream, outputStream, 1024 * 4, null);
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


