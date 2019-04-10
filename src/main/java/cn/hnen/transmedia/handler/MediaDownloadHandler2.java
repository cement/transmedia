package cn.hnen.transmedia.handler;

import cn.hnen.transmedia.annotation.RecordLog;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import static cn.hnen.transmedia.config.MediaDistributeConfig.mediaRootDir;


@Slf4j
@Component
public class MediaDownloadHandler2 {

    @RecordLog
    public void downLoadMedia(String fileName, HttpServletResponse response) {

        long start = System.currentTimeMillis();
        Path srcPath = Paths.get(mediaRootDir, fileName);
        try (BufferedInputStream inputStream = FileUtil.getInputStream(srcPath);
             OutputStream outputStream = response.getOutputStream()){
            response.setContentType("application/octet-stream");
            response.setContentLengthLong(srcPath.toFile().length());
            response.setHeader("Content-Disposition", "attachment; filename=" + new String(fileName.getBytes("utf-8"), "iso-8859-1"));
            outputStream.flush();
            long length = IoUtil.copyByNIO(inputStream, outputStream, 1024 * 8, null);
            outputStream.flush();
            /*记录日志*/
            long stop = System.currentTimeMillis();
            log.info("设备端 下载完成  文件名称: {},文件大小:{},耗时 {}毫秒", fileName, length, (stop - start));
        } catch (IOException e) {
            /*记录日志*/
            long stop = System.currentTimeMillis();
            log.error("设备端 下载失败  文件名称: {},,耗时 {}毫秒", fileName,stop - start,e);
        }
    }
}
