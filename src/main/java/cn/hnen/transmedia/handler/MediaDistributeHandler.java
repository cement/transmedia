package cn.hnen.transmedia.handler;

import cn.hnen.transmedia.config.MediaDistributeConfig;
import cn.hnen.transmedia.entry.*;
import cn.hnen.transmedia.exception.MediaReciveException;
import cn.hnen.transmedia.jpaentry.MediaTransInfoEntry;
import cn.hnen.transmedia.repository.MediaTransRepository;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

import static cn.hnen.transmedia.config.MediaDistributeConfig.*;
import static cn.hnen.transmedia.jpaentry.MediaTransInfoEntry.*;

/**
 * @author YSH
 * @create 20181203
 * @实现从上级下载及下级下载
 */
@Slf4j
@Component
public class MediaDistributeHandler {


    @Autowired
    public RestTemplate restTemplate;

    @Autowired
    private MediaTransRepository mediaDownRepository;


    /**
     * 自写单文件下载，可以定义缓冲大小，默认102400；
     *
     * @param vo
     * @return
     */
    public ResponseModel receiveMedia(FileHostDownloadRole vo) {

        long start = System.currentTimeMillis();
        log.info("开始下载 {}", vo.getFileName());


        FileOutputStream outputStream = null;
        InputStream inputStream = null;

        File targetFile = new File(mediaRootDir, vo.getFileName());

        try {
            if (targetFile.exists()) {
                if (targetFile.length() > 0) {
                    /*记录日志*/
                    log.info("文件已存在 文件名称:{}", vo.getFileName());

                    /*保存到数据库*/
                    MediaTransInfoEntry downloadInfoEntry = new MediaTransInfoEntry();
                    downloadInfoEntry.setFileId(vo.getId());
                    downloadInfoEntry.setCityId(vo.getCityId());
                    downloadInfoEntry.setDownloadMediaDir(mediaRootDir);
                    downloadInfoEntry.setDownLoadResult(RESULT_DOWN_EXIST);
                    downloadInfoEntry.setDownloadType(TYPE_DOWN_FROM);
                    downloadInfoEntry.setFileName(vo.getFileName());
                    downloadInfoEntry.setBeginPlayTime(vo.getBeginPlayTime());
                    downloadInfoEntry.setDescribe("文件已存在!");
                    mediaDownRepository.save(downloadInfoEntry);

                    /*汇报结果*/
                    this.doDownReport(vo);

                    /*返回*/
                    return ResponseModel.warp(BusinessEnum.EXISTED).setResult(vo.getFileName());
                }
            }

            String url = downloadApiUrl + "?fileName=" + vo.getFileName();
            ResponseEntity<Resource> respEntry = restTemplate.getForEntity(url, Resource.class);

            if (200 != respEntry.getStatusCodeValue()) {
                /*记录日志*/
                log.error("上级文件不存在 文件名称:{}", vo.getFileName());
                /*保存到数据库*/
                MediaTransInfoEntry downloadInfoEntry = new MediaTransInfoEntry();
                downloadInfoEntry.setFileId(vo.getId());
                downloadInfoEntry.setCityId(vo.getCityId());
                downloadInfoEntry.setDownloadMediaDir(mediaRootDir);
                downloadInfoEntry.setDownLoadResult(RESULT_DOWN_UNEXIST);
                downloadInfoEntry.setDownloadType(TYPE_DOWN_FROM);
                downloadInfoEntry.setFileName(vo.getFileName());
                downloadInfoEntry.setBeginPlayTime(vo.getBeginPlayTime());
                downloadInfoEntry.setDescribe("上级文件不存在!");
                mediaDownRepository.save(downloadInfoEntry);

                /*返回值*/
                return ResponseModel.warp(BusinessEnum.FAILED).setResult(vo.getFileName());
            }

            inputStream = respEntry.getBody().getInputStream();
            outputStream = new FileOutputStream(targetFile);

            byte[] buf = new byte[downloadBufferSize];
            int readed = 0;
            while ((readed = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, readed);
            }
            outputStream.flush();

            long stop = System.currentTimeMillis();
            /*记录日志*/
            log.info("下载完成  文件名称: {},文件大小:{}K,耗时 {}毫秒", vo.getFileName(), targetFile.length() / 1024, (stop - start));

            /*保存到数据库*/
            MediaTransInfoEntry downloadInfoEntry = new MediaTransInfoEntry();
            downloadInfoEntry.setFileId(vo.getId());
            downloadInfoEntry.setCityId(vo.getCityId());
            downloadInfoEntry.setDownloadMediaDir(mediaRootDir);
            downloadInfoEntry.setDownLoadResult(RESULT_DOWN_SUCCESS);
            downloadInfoEntry.setFileName(vo.getFileName());
            downloadInfoEntry.setDownloadType(TYPE_DOWN_FROM);
            downloadInfoEntry.setBeginPlayTime(vo.getBeginPlayTime());
            downloadInfoEntry.setDownLoadDuration(stop - start);
            downloadInfoEntry.setDescribe("文件大小:" + targetFile.length() / 1024 + "K,耗时: " + (stop - start) + "毫秒");
            mediaDownRepository.save(downloadInfoEntry);

           /*汇报结果*/
            this.doDownReport(vo);
           /*返回值*/
            return ResponseModel.warp(BusinessEnum.SUCCESS).setResult(vo.getFileName());

        } catch (Exception e) {

            /*如果存在未下载完成的文件，则删除*/
            if (targetFile.exists()){
                targetFile.delete();
            }

            long stop = System.currentTimeMillis();
            /*记录日志*/
            log.error("下载失败  文件名称: {}, 失败原因: {},耗时{}", vo.getFileName(), e.getMessage(), stop - start);

            /*保存到数据库*/
            MediaTransInfoEntry downloadInfoEntry = new MediaTransInfoEntry();
            downloadInfoEntry.setFileId(vo.getId());
            downloadInfoEntry.setCityId(vo.getCityId());
            downloadInfoEntry.setDownloadMediaDir(mediaRootDir);
            downloadInfoEntry.setDownLoadResult(RESULT_DOWN_FAILED);
            downloadInfoEntry.setFileName(vo.getFileName());
            downloadInfoEntry.setDownloadType(TYPE_DOWN_FROM);
            downloadInfoEntry.setBeginPlayTime(vo.getBeginPlayTime());
            downloadInfoEntry.setDownLoadDuration(stop - start);
            downloadInfoEntry.setDescribe(e.getMessage() != null ? e.getMessage() : e.toString());

            mediaDownRepository.save(downloadInfoEntry);

            /*重新抛出异常，以便@Retryable进行重试*/
            throw new MediaReciveException(e);

        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();

                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
        }

    }





    /**
     * 从上级服务器下载情况汇报
     * @param vo 文件实体
     */
    public void doDownReport(FileHostDownloadRole vo) {

        try {
            MultiValueMap<String, Object> paramsMap = new LinkedMultiValueMap<String, Object>();
            paramsMap.add("id", vo.getId());
            ResponseEntity<String> reportResult = restTemplate.postForEntity(MediaDistributeConfig.downloadReportUrl, paramsMap, String.class);
            String result = reportResult.getBody();
//        HashMap<String, Object> paramsMap = new HashMap<>();
//        paramsMap.put("id", id);
//        String post = HttpUtil.post(FileDistributeConfig.downloadReportUrl, paramsMap);
            /*记录日志*/
            log.info("下载文件 汇报成功, 文件名称:{},返回信息:{}", vo.getFileName(),result);
            /*保存到本地数据库*/
            MediaTransInfoEntry downloadInfoEntry = new MediaTransInfoEntry();
            downloadInfoEntry.setFileId(vo.getId());
            downloadInfoEntry.setCityId(vo.getCityId());
            downloadInfoEntry.setDownloadMediaDir(mediaRootDir);
            downloadInfoEntry.setDownLoadResult(RESULT_DOWN_SUCCESS);
            downloadInfoEntry.setDownloadType(TYPE_DOWN_REPORT);
            downloadInfoEntry.setFileName(vo.getFileName());
            downloadInfoEntry.setBeginPlayTime(vo.getBeginPlayTime());
            downloadInfoEntry.setDescribe("下载文件 汇报失败!!");
            mediaDownRepository.save(downloadInfoEntry);

        } catch (RestClientException e) {
            /*记录日志*/
            log.error("下载文件 汇报失败, 文件名称:{}", vo.getFileName());

            /*保存到本地数据库*/
            MediaTransInfoEntry downloadInfoEntry = new MediaTransInfoEntry();
            downloadInfoEntry.setFileId(vo.getId());
            downloadInfoEntry.setCityId(vo.getCityId());
            downloadInfoEntry.setDownloadMediaDir(mediaRootDir);
            downloadInfoEntry.setDownLoadResult(RESULT_DOWN_FAILED);
            downloadInfoEntry.setDownloadType(TYPE_DOWN_REPORT);
            downloadInfoEntry.setFileName(vo.getFileName());
            downloadInfoEntry.setBeginPlayTime(vo.getBeginPlayTime());
            downloadInfoEntry.setDescribe("下载文件 汇报失败!!");
            mediaDownRepository.save(downloadInfoEntry);
            /*打印错误堆栈*/
            e.printStackTrace();
        }

    }






}
