package cn.hnen.transmedia;

import cn.hnen.transmedia.entry.ReciveResultModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static cn.hnen.transmedia.config.MediaDistributeConfig.downloadBufferSize;
import static cn.hnen.transmedia.config.MediaDistributeConfig.downloadMediaDir;

@Slf4j
@Component
public class MediaReceiveTest {
    @Autowired
    public RestTemplate restTemplate;
//    @Async
    public ReciveResultModel receiveMedia(String fileName) {

        long start = System.currentTimeMillis();
        log.info("开始下载 {}", fileName);

        ReciveResultModel resultModel = new ReciveResultModel();

        FileOutputStream outputStream = null;
        InputStream inputStream = null;

        File targetFile = new File(downloadMediaDir, fileName);

        try {
            if (targetFile.exists()) {
                if (targetFile.length() > 0) {
                    /*记录日志*/
                    log.info("文件已存在 文件名称:{}", fileName);

                    /*返回*/
                    resultModel.setResultCode(1);
                    resultModel.getExistedList().add(targetFile.getName());
                    /*汇报结果*/

                    return resultModel;
                }
            }

//            String url = downloadApiPath + "?fileName=" + fileName;
            String url = "http://192.168.1.150:8080/advertising/api/updateAdvert/downloadAdvert?fileName=" + fileName;
            ResponseEntity<Resource> respEntry = restTemplate.getForEntity(url, Resource.class);

            if (200 != respEntry.getStatusCodeValue()) {
                /*记录日志*/
                log.error("上级文件不存在 文件名称:{}", fileName);

                /*返回值*/
                resultModel.setResultCode(-1);
                resultModel.getFailedList().add(fileName);

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
            log.info("下载完成  文件名称: {},文件大小:{}K,耗时 {}毫秒", fileName, targetFile.length() / 1024, (stop - start));



            /*返回值*/
            resultModel.setResultCode(0);
            resultModel.getDownedList().add(fileName);

        } catch (Exception e) {

            long stop = System.currentTimeMillis();
            /*记录日志*/
            log.error("下载失败  文件名称: {}, 失败原因: {},耗时{}", fileName, e.getMessage(), stop - start);


            /*返回*/
            resultModel.setResultCode(-2);
            resultModel.getFailedList().add(fileName);
            e.printStackTrace();

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
}
