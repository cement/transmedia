package cn.hnen.transmedia.handler;

import cn.hnen.transmedia.config.MediaDistributeConfig;
import cn.hnen.transmedia.entry.DownResultModel;
import cn.hnen.transmedia.jpaentry.MediaTransInfoEntry;
import cn.hnen.transmedia.repository.MediaTransRepository;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static cn.hnen.transmedia.config.MediaDistributeConfig.mediaRootDir;
import static cn.hnen.transmedia.jpaentry.MediaTransInfoEntry.*;
import static cn.hnen.transmedia.jpaentry.MediaTransInfoEntry.TYPE_DOWN_TO;

@Slf4j
@Component
public class MediaDownloadHandler {

    @Autowired
    public RestTemplate restTemplate;

    @Autowired
    private MediaTransRepository mediaDownRepository;
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
            File downFile = new File(mediaRootDir, fileName);
            if (!downFile.exists()) {
                long stop = System.currentTimeMillis();

                MediaTransInfoEntry downloadInfoEntry = new MediaTransInfoEntry();
                downloadInfoEntry.setDownloadMediaDir(mediaRootDir);
                downloadInfoEntry.setDownLoadResult(RESULT_DOWN_FAILED);
                downloadInfoEntry.setFileName(fileName);
                downloadInfoEntry.setDownloadType(TYPE_DOWN_TO);
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

            } else {
                fileInStream = new FileInputStream(downFile);
                respOutStream = response.getOutputStream();

                response.setContentType("application/octet-stream");
                response.setContentLengthLong(downFile.length());
                response.setHeader("Content-Disposition", "attachment; filename=" + new String(fileName.getBytes("utf-8"), "iso-8859-1"));

                byte[] buffer = new byte[MediaDistributeConfig.downloadBufferSize];
                int readed = 0;
                while ((readed = fileInStream.read(buffer)) != -1) {
                    respOutStream.write(buffer, 0, readed);
                }
                respOutStream.flush();

                long stop = System.currentTimeMillis();

                MediaTransInfoEntry downloadInfoEntry = new MediaTransInfoEntry();

                downloadInfoEntry.setDownloadMediaDir(mediaRootDir);
                downloadInfoEntry.setDownLoadResult(RESULT_DOWN_SUCCESS);
                downloadInfoEntry.setFileName(fileName);
                downloadInfoEntry.setDownloadType(TYPE_DOWN_TO);
                downloadInfoEntry.setDescribe("设备端 下载完成  文件名称: " + fileName);
                downloadInfoEntry.setDownLoadDuration(stop - start);

                mediaDownRepository.save(downloadInfoEntry);

                log.info("设备端 下载完成  文件名称: {},文件大小:{},耗时 {}毫秒", fileName, downFile.length(), (stop - start));
            }
        } catch (Exception e) {

            long stop = System.currentTimeMillis();
            MediaTransInfoEntry downloadInfoEntry = new MediaTransInfoEntry();

            downloadInfoEntry.setDownloadMediaDir(mediaRootDir);
            downloadInfoEntry.setDownLoadResult(RESULT_DOWN_FAILED);
            downloadInfoEntry.setFileName(fileName);
            downloadInfoEntry.setDownloadType(TYPE_DOWN_TO);
            downloadInfoEntry.setDescribe(e.getMessage() != null ? e.getMessage() : e.toString());
            downloadInfoEntry.setDownLoadDuration(stop - start);
            mediaDownRepository.save(downloadInfoEntry);

            log.error("设备端 下载失败  文件名称: {};错误信息：{}", mediaRootDir + fileName, e.getMessage() != null ? e.getMessage() : e.toString());


            try {
                response.setStatus(500);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                DownResultModel downResultModel = new DownResultModel();
                downResultModel.setCode(-1).setMessage("下载失败!").setResult(false).setData(fileName);
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
}
