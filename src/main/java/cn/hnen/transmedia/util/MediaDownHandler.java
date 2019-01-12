package cn.hnen.transmedia.util;

import cn.hnen.transmedia.config.MediaDistributeConfig;
import cn.hnen.transmedia.entry.DownResultModel;
import cn.hnen.transmedia.entry.FileHostDownloadRole;
import cn.hnen.transmedia.entry.FileHostDownloadRoleVo;
import cn.hnen.transmedia.entry.ReciveResultModel;
import cn.hnen.transmedia.exception.MediaDownloadException;
import cn.hnen.transmedia.jpaentry.MediaTransInfoEntry;
import cn.hnen.transmedia.repository.MediaTransRepository;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
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
public class MediaDownHandler {


    @Autowired
    public RestTemplate restTemplate;

    @Autowired
    private MediaTransRepository mediaDownRepository;


    /**
     * 自写单文件下载，可以定义缓冲大小，默认1024000；
     *
     * @param vo
     * @return
     */
    public ReciveResultModel receiveMedia(FileHostDownloadRole vo) {

        long start = System.currentTimeMillis();
        log.info("开始下载 {}", vo.getFileName());

        ReciveResultModel resultModel = new ReciveResultModel();

        FileOutputStream outputStream = null;
        InputStream inputStream = null;

        File targetFile = new File(downloadMediaDir, vo.getFileName());

        try {
            if (targetFile.exists()) {
                if (targetFile.length() > 0) {
                    /*记录日志*/
                    log.info("文件已存在 文件名称:{}", vo.getFileName());

                    /*保存到数据库*/
                    MediaTransInfoEntry downloadInfoEntry = new MediaTransInfoEntry();
                    downloadInfoEntry.setFileId(vo.getId());
                    downloadInfoEntry.setCityId(vo.getCityId());
                    downloadInfoEntry.setDownloadMediaDir(downloadMediaDir);
                    downloadInfoEntry.setDownLoadResult(DOWN_RESULT_EXIST);
                    downloadInfoEntry.setDownloadType(DOWN_TYPE_FROM);
                    downloadInfoEntry.setFileName(vo.getFileName());
                    downloadInfoEntry.setBeginPlayTime(vo.getBeginPlayTime());
                    downloadInfoEntry.setDescribe("文件已存在!");
                    mediaDownRepository.save(downloadInfoEntry);

                    /*返回*/
                    resultModel.setResultCode(1);
                    resultModel.getExistedList().add(targetFile.getName());
                    /*汇报结果*/
                    this.doDownReport(vo);

                    return resultModel;
                }
            }

            String url = downloadApiPath + "?fileName=" + vo.getFileName();
            ResponseEntity<Resource> respEntry = restTemplate.getForEntity(url, Resource.class);

            if (200 != respEntry.getStatusCodeValue()) {
                /*记录日志*/
                log.error("上级文件不存在 文件名称:{}", vo.getFileName());
                /*保存到数据库*/
                MediaTransInfoEntry downloadInfoEntry = new MediaTransInfoEntry();
                downloadInfoEntry.setFileId(vo.getId());
                downloadInfoEntry.setCityId(vo.getCityId());
                downloadInfoEntry.setDownloadMediaDir(downloadMediaDir);
                downloadInfoEntry.setDownLoadResult(DOWN_RESULT_UNEXIST);
                downloadInfoEntry.setDownloadType(DOWN_TYPE_FROM);
                downloadInfoEntry.setFileName(vo.getFileName());
                downloadInfoEntry.setBeginPlayTime(vo.getBeginPlayTime());
                downloadInfoEntry.setDescribe("上级文件不存在!");
                mediaDownRepository.save(downloadInfoEntry);
                /*返回值*/
                resultModel.setResultCode(-1);
                resultModel.getFailedList().add(vo.getFileName());

                return resultModel;
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
            downloadInfoEntry.setDownloadMediaDir(downloadMediaDir);
            downloadInfoEntry.setDownLoadResult(DOWN_RESULT_SUCCESS);
            downloadInfoEntry.setFileName(vo.getFileName());
            downloadInfoEntry.setDownloadType(DOWN_TYPE_FROM);
            downloadInfoEntry.setBeginPlayTime(vo.getBeginPlayTime());
            downloadInfoEntry.setDownLoadDuration(stop - start);
            downloadInfoEntry.setDescribe("文件大小:" + targetFile.length() / 1024 + "K,耗时: " + (stop - start) + "毫秒");
            mediaDownRepository.save(downloadInfoEntry);

           /*汇报结果*/
            this.doDownReport(vo);
           /*返回值*/
            resultModel.setResultCode(0);
            resultModel.getDownedList().add(vo.getFileName());

        } catch (Exception e) {

            long stop = System.currentTimeMillis();
            /*记录日志*/
            log.error("下载失败  文件名称: {}, 失败原因: {},耗时{}", vo.getFileName(), e.getMessage(), stop - start);

            /*保存到数据库*/
            MediaTransInfoEntry downloadInfoEntry = new MediaTransInfoEntry();
            downloadInfoEntry.setFileId(vo.getId());
            downloadInfoEntry.setCityId(vo.getCityId());
            downloadInfoEntry.setDownloadMediaDir(downloadMediaDir);
            downloadInfoEntry.setDownLoadResult(DOWN_RESULT_FAILED);
            downloadInfoEntry.setFileName(vo.getFileName());
            downloadInfoEntry.setDownloadType(DOWN_TYPE_FROM);
            downloadInfoEntry.setBeginPlayTime(vo.getBeginPlayTime());
            downloadInfoEntry.setDownLoadDuration(stop - start);
            downloadInfoEntry.setDescribe(e.getMessage() != null ? e.getMessage() : e.toString());

            mediaDownRepository.save(downloadInfoEntry);

            /*返回*/
            resultModel.setResultCode(-2);
            resultModel.getFailedList().add(vo.getFileName());
            //e.printStackTrace();

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
        return resultModel;
    }


    /**
     * 异步下载
     * @param vo
     */
    @Async
    public void receiveMediaAsync(FileHostDownloadRole vo) {
        ReciveResultModel resultModel = receiveMedia(vo);
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
            downloadInfoEntry.setDownloadMediaDir(downloadMediaDir);
            downloadInfoEntry.setDownLoadResult(DOWN_RESULT_SUCCESS);
            downloadInfoEntry.setDownloadType(DOWN_TYPE_REPORT);
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
            downloadInfoEntry.setDownloadMediaDir(downloadMediaDir);
            downloadInfoEntry.setDownLoadResult(DOWN_RESULT_FAILED);
            downloadInfoEntry.setDownloadType(DOWN_TYPE_REPORT);
            downloadInfoEntry.setFileName(vo.getFileName());
            downloadInfoEntry.setBeginPlayTime(vo.getBeginPlayTime());
            downloadInfoEntry.setDescribe("下载文件 汇报失败!!");
            mediaDownRepository.save(downloadInfoEntry);
            /*打印错误堆栈*/
            e.printStackTrace();
        }

    }


    /**
     * 设备端下载
     *
     * @param fileName
     * @param response
     */
    public void downLoadMedia(String fileName, HttpServletResponse response) {


        long start = System.currentTimeMillis();
        log.info("设备端 开始下载 {}", fileName);
        FileInputStream fileInStream = null;
//        InputStream requInStream = null;
//        FileOutputStream fileOutStream = null;
        ServletOutputStream respOutStream = null;
        try {
            File downFile = new File(downloadMediaDir, fileName);
            if (!downFile.exists()) {
                long stop = System.currentTimeMillis();
                MediaTransInfoEntry downloadInfoEntry = new MediaTransInfoEntry();

                downloadInfoEntry.setDownloadMediaDir(downloadMediaDir);
                downloadInfoEntry.setDownLoadResult(DOWN_RESULT_FAILED);
                downloadInfoEntry.setFileName(fileName);
                downloadInfoEntry.setDownloadType(DOWN_TYPE_TO);
                downloadInfoEntry.setDescribe("下载失败,文件不存在!");
                downloadInfoEntry.setDownLoadDuration(stop - start);
                mediaDownRepository.save(downloadInfoEntry);

                log.error("设备端 下载失败！  文件名称: {};错误信息：{}", downFile.getName(), "文件不存在!");

                response.setStatus(501);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                DownResultModel downResultModel = new DownResultModel();
                downResultModel.setCode(-1).setMessage("下载失败,文件不存在!").setResult(false).setData(fileName);
                try {
                    response.getWriter().write(JSON.toJSONString(downResultModel));
                } catch (IOException e1) {
                    log.error("设备端 下载失败写出错误！  文件名称: {};错误信息：{}", downFile.getName(), "文件不存在!");
                    e1.printStackTrace();
                }


//                requInStream = restTemplate.getForEntity(downloadApiPath + "?fileName=" + fileName, Resource.class).getBody().getInputStream();
//                fileOutStream = new FileOutputStream(downFile);
//                respOutStream = response.getOutputStream();
//
//
//                response.setContentType("application/octet-stream");
//                response.setHeader("Content-Disposition", "attachment; filename="+  new String(fileName.getBytes("utf-8"),"iso-8859-1"));
//
//                byte[] buffer = new byte[downloadBufferSize];
//                int readed = 0;
//                while ((readed = requInStream.read(buffer)) != -1) {
//                    fileOutStream.write(buffer, 0, readed);
//                    respOutStream.write(buffer, 0, readed);
//                }
//                respOutStream.flush();
//                fileOutStream.flush();
//                long stop = System.currentTimeMillis();
//
//                MediaTransInfoEntry downloadInfoEntry = new MediaTransInfoEntry();
//
//                downloadInfoEntry.setDownloadMediaDir(downloadMediaDir);
//
//                downloadInfoEntry.setDownLoadResult(DOWN_RESULT_SUCCESS);
//                downloadInfoEntry.setFileName(fileName);
//                downloadInfoEntry.setDownloadType(DOWN_TYPE_ALL);
//                downloadInfoEntry.setDescribe("设备端+本地 下载完成  文件名称: " + fileName);
//                downloadInfoEntry.setDownLoadDuration(stop - start);
//
//                mediaDownRepository.save(downloadInfoEntry);
//
//                log.info("设备端+本地 下载完成  文件名称: {},文件大小:{},耗时 {}毫秒", fileName, downFile.length(), (stop - start));

            } else {
                fileInStream = new FileInputStream(downFile);
                respOutStream = response.getOutputStream();
                response.setContentType("application/octet-stream");
                response.setHeader("Content-Disposition", "attachment; filename=" + new String(fileName.getBytes("utf-8"), "iso-8859-1"));

                byte[] buffer = new byte[MediaDistributeConfig.downloadBufferSize];
                int readed = 0;
                while ((readed = fileInStream.read(buffer)) != -1) {
                    respOutStream.write(buffer, 0, readed);
                }
                respOutStream.flush();

                long stop = System.currentTimeMillis();

                MediaTransInfoEntry downloadInfoEntry = new MediaTransInfoEntry();

                downloadInfoEntry.setDownloadMediaDir(downloadMediaDir);
                downloadInfoEntry.setDownLoadResult(DOWN_RESULT_SUCCESS);
                downloadInfoEntry.setFileName(fileName);
                downloadInfoEntry.setDownloadType(DOWN_TYPE_TO);
                downloadInfoEntry.setDescribe("设备端 下载完成  文件名称: " + fileName);
                downloadInfoEntry.setDownLoadDuration(stop - start);

                mediaDownRepository.save(downloadInfoEntry);

                log.info("设备端 下载完成  文件名称: {},文件大小:{},耗时 {}毫秒", fileName, downFile.length(), (stop - start));
            }
        } catch (Exception e) {

            long stop = System.currentTimeMillis();
            MediaTransInfoEntry downloadInfoEntry = new MediaTransInfoEntry();

            downloadInfoEntry.setDownloadMediaDir(downloadMediaDir);
            downloadInfoEntry.setDownLoadResult(DOWN_RESULT_FAILED);
            downloadInfoEntry.setFileName(fileName);
            downloadInfoEntry.setDownloadType(DOWN_TYPE_TO);
            downloadInfoEntry.setDescribe(e.getMessage() != null ? e.getMessage() : e.toString());
            downloadInfoEntry.setDownLoadDuration(stop - start);
            mediaDownRepository.save(downloadInfoEntry);

            log.error("设备端 下载失败  文件名称: {};错误信息：{}", downloadMediaDir + fileName, e.getMessage() != null ? e.getMessage() : e.toString());

            response.setStatus(500);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            DownResultModel downResultModel = new DownResultModel();
            downResultModel.setCode(-1).setMessage("下载失败!").setResult(false).setData(fileName);
            try {
                response.getWriter().write(JSON.toJSONString(downResultModel));
            } catch (IOException e1) {
                e1.printStackTrace();
            }

        } finally {

            if (fileInStream != null) {
                try {
                    fileInStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (respOutStream != null) {
                try {
                    respOutStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }


    }


    //**jdk**私有方法源码
    public static long copy(InputStream source, OutputStream sink)
            throws IOException
    {
        long nread = 0L;
        byte[] buf = new byte[8192];
        int n;
        while ((n = source.read(buf)) > 0) {
            sink.write(buf, 0, n);
            nread += n;
        }
        return nread;
    }

}
