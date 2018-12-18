package cn.hnen.transmedia.util;

import cn.hnen.transmedia.Config.FileDistributeConfig;
import cn.hnen.transmedia.entry.DownResultModel;
import cn.hnen.transmedia.entry.FileHostDownloadRole;
import cn.hnen.transmedia.entry.ReciveResultModel;
import cn.hnen.transmedia.entry.FileHostDownloadRoleVo;
import cn.hnen.transmedia.exception.MediaDownloadException;
import cn.hnen.transmedia.jpaentry.MediaDownloadInfoEntry;
import cn.hnen.transmedia.repository.MediaDownloadRepository;
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
import org.springframework.web.client.RestTemplate;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

import static cn.hnen.transmedia.Config.FileDistributeConfig.*;

/**
 * @author YSH
 * @create 20181203
 * @实现从上级下载及下级下载
 */
@Slf4j
@Component
public class FileDownHandler {


    @Autowired
    public RestTemplate restTemplate;

    @Autowired
    private MediaDownloadRepository mediaDownRepository;


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

        String fileName = vo.getFileName();
        File targetFile = new File(downloadMediaDir, fileName);

        try {
            if (targetFile.exists()) {
                if (targetFile.length() > 0) {
                    resultModel.getExistedList().add(targetFile.getName());
                    log.info("文件已存在 文件名称:{}", targetFile.getName());


                    MediaDownloadInfoEntry downloadInfoEntry = new MediaDownloadInfoEntry();
                    downloadInfoEntry.setFileId(vo.getId());
                    downloadInfoEntry.setCityId(vo.getCityId());
                    downloadInfoEntry.setDownloadMediaDir(downloadMediaDir);
                    downloadInfoEntry.setDownLoadResult(DOWN_RESULT_EXIST);
                    downloadInfoEntry.setDownloadType(DOWN_TYPE_FROM);
                    downloadInfoEntry.setFileName(vo.getFileName());
                    downloadInfoEntry.setBeginPlayTime(vo.getBeginPlayTime());
                    downloadInfoEntry.setDescribe("文件已存在!");
                    mediaDownRepository.save(downloadInfoEntry);

                    resultModel.setResultCode(0);

                    return resultModel;
                }
            }

            String url = downloadApiPath + "?fileName=" + fileName;


            ResponseEntity<Resource> respEntry = restTemplate.getForEntity(url, Resource.class);

            if ( 200 != respEntry.getStatusCodeValue()){
                MediaDownloadInfoEntry downloadInfoEntry = new MediaDownloadInfoEntry();
                downloadInfoEntry.setFileId(vo.getId());
                downloadInfoEntry.setCityId(vo.getCityId());
                downloadInfoEntry.setDownloadMediaDir(downloadMediaDir);
                downloadInfoEntry.setDownLoadResult(DOWN_RESULT_UNEXIST);
                downloadInfoEntry.setDownloadType(DOWN_TYPE_FROM);
                downloadInfoEntry.setFileName(vo.getFileName());
                downloadInfoEntry.setBeginPlayTime(vo.getBeginPlayTime());
                downloadInfoEntry.setDescribe("上级文件不存在!");
                mediaDownRepository.save(downloadInfoEntry);
                log.info("上级文件不存在 文件名称:{}", targetFile.getName());
                resultModel.setResultCode(-1);

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

            MediaDownloadInfoEntry downloadInfoEntry = new MediaDownloadInfoEntry();
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
            log.info("下载完成  文件名称: {},文件大小:{}K,耗时 {}毫秒", targetFile.getName(), targetFile.length() / 1024, (stop - start));


            resultModel.getDownedList().add(targetFile.getName());

            this.doDownReport(vo.getId());

        } catch (Exception e) {
            resultModel.getFailedList().add(targetFile.getName());
            long stop = System.currentTimeMillis();

            MediaDownloadInfoEntry downloadInfoEntry = new MediaDownloadInfoEntry();
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


            log.error("下载失败  文件名称: {}, 失败原因: {},耗时{}", targetFile.getName(), e.getMessage(), stop - start);
            //e.printStackTrace();
            //throw new MediaDownloadException(1, resultModel);
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
     * 使用 Spring内置方法下载，它的缓冲较小，4096，传输大文件 时影响速度。
     *
     * @param vo
     * @return
     */
    public ReciveResultModel springDownMedia(FileHostDownloadRoleVo vo) {

        long start = System.currentTimeMillis();
        log.info("开始下载 {}", vo.getFileName());

        ReciveResultModel downModel = new ReciveResultModel();

        String fileName = vo.getFileName();
        File targetFile = new File(downloadMediaDir, fileName);

        if (targetFile.exists() && targetFile.length() > 0) {
            log.info("文件已存在 {}", fileName);
            downModel.getExistedList().add(fileName);
            return downModel;
        }
        try {

            String url = downloadApiPath + "?fileName=" + fileName;
            InputStream inputStream = restTemplate.getForEntity(url, Resource.class).getBody().getInputStream();
            OutputStream outputStream = new FileOutputStream(targetFile);
//            FileCopyUtils.BUFFER_SIZE = 1024000;
            int byteCount = FileCopyUtils.copy(inputStream, outputStream);

            long stop = System.currentTimeMillis();
            downModel.getDownedList().add(fileName);
            log.info("下载完成  文件名称: {},文件大小:{}K,耗时 {}毫秒", fileName, byteCount / 1024, (stop - start));

            downModel.getDownedList().add(fileName);
        } catch (IOException e) {
            long stop = System.currentTimeMillis();
            log.info("下载失败  文件名称: {}, 失败原因: {},耗时{}", targetFile.getName(), e.getMessage(), stop - start);
            downModel.getFailedList().add(fileName);
            //e.printStackTrace();
            throw new MediaDownloadException(1, downModel);
        }
        return downModel;
    }


    /**
     * 异步下载
     *
     * @param vo
     */
    @Async
    public void receiveMediaAsync(FileHostDownloadRole vo) {
        ReciveResultModel resultModel = receiveMedia(vo);

//        for (int i = 0; i < 3; i++) {
//
//            resultModel = receiveMedia(vo);
//            if (resultModel.getResultCode() == 0){
//                return;
//            }
//        }

//        this.downReport(vo.getId());
    }


    /**
     * 从上级服务器下载情况汇报
     *
     * @param id
     */
    public void doDownReport(Long id) {

        MultiValueMap<String, Object> paramsMap = new LinkedMultiValueMap<String, Object>();
        paramsMap.add("id", id);
        ResponseEntity<String> reportResult = restTemplate.postForEntity(FileDistributeConfig.downloadReportUrl, paramsMap, String.class);
        String result = reportResult.getBody();
//        HashMap<String, Object> paramsMap = new HashMap<>();
//        paramsMap.put("id", id);
//        String post = HttpUtil.post(FileDistributeConfig.downloadReportUrl, paramsMap);
        log.info("发送报告>>>>  " + result);

    }

    /**
     * 设备端下载
     *
     * @param fileName
     * @param response
     */
    public void downLoadMedia2(String fileName, HttpServletResponse response) {


        FileInputStream fileInStream = null;
        ServletOutputStream respOutStream = null;
        try {
            File downFile = new File(FileDistributeConfig.downloadMediaDir, fileName);
            fileInStream = new FileInputStream(downFile);
            respOutStream = response.getOutputStream();

            byte[] buffer = new byte[FileDistributeConfig.downloadBufferSize];
            int readed = 0;
            while ((readed = fileInStream.read(buffer)) != -1) {
                respOutStream.write(buffer, 0, readed);
            }
        } catch (IOException e) {
            e.printStackTrace();
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
                MediaDownloadInfoEntry downloadInfoEntry = new MediaDownloadInfoEntry();

                downloadInfoEntry.setDownloadMediaDir(downloadMediaDir);
                downloadInfoEntry.setDownLoadResult(DOWN_RESULT_FAILED);
                downloadInfoEntry.setFileName(fileName);
                downloadInfoEntry.setDownloadType(DOWN_TYPE_TO);
                downloadInfoEntry.setDescribe("下载失败,文件不存在!");
                downloadInfoEntry.setDownLoadDuration(stop - start);
                mediaDownRepository.save(downloadInfoEntry);

                log.error("设备端 下载失败！  文件名称: {};错误信息：{}", downFile.getName(), "文件不存在!");

                response.setStatus(500);
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

//                return;
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

            } else {
                fileInStream = new FileInputStream(downFile);
                respOutStream = response.getOutputStream();
                response.setContentType("application/octet-stream");
                response.setHeader("Content-Disposition", "attachment; filename=" + new String(fileName.getBytes("utf-8"), "iso-8859-1"));

                byte[] buffer = new byte[FileDistributeConfig.downloadBufferSize];
                int readed = 0;
                while ((readed = fileInStream.read(buffer)) != -1) {
                    respOutStream.write(buffer, 0, readed);
                }
                respOutStream.flush();

                long stop = System.currentTimeMillis();

                MediaDownloadInfoEntry downloadInfoEntry = new MediaDownloadInfoEntry();

                downloadInfoEntry.setDownloadMediaDir(downloadMediaDir);

                downloadInfoEntry.setDownLoadResult(DOWN_RESULT_SUCCESS);
                downloadInfoEntry.setFileName(fileName);
                downloadInfoEntry.setDownloadType(DOWN_TYPE_TO);
                downloadInfoEntry.setDescribe("设备端 下载完成  文件名称: " + fileName);
                downloadInfoEntry.setDownLoadDuration(stop - start);

                mediaDownRepository.save(downloadInfoEntry);

                log.info("设备端 下载完成  文件名称: {},文件大小:{},耗时 {}毫秒", fileName, downFile.length(),(stop - start));
            }
        } catch (Exception e) {

            long stop = System.currentTimeMillis();
            MediaDownloadInfoEntry downloadInfoEntry = new MediaDownloadInfoEntry();

            downloadInfoEntry.setDownloadMediaDir(downloadMediaDir);
            downloadInfoEntry.setDownLoadResult(DOWN_RESULT_FAILED);
            downloadInfoEntry.setFileName(fileName);
            downloadInfoEntry.setDownloadType(DOWN_TYPE_TO);
            downloadInfoEntry.setDescribe(e.getMessage() != null ? e.getMessage() : e.toString());
            downloadInfoEntry.setDownLoadDuration(stop - start);
            mediaDownRepository.save(downloadInfoEntry);

            log.error("设备端 下载失败  文件名称: {};错误信息：{}", downloadMediaDir + fileName, e.getMessage() != null ? e.getMessage() : e.toString());

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            DownResultModel downResultModel = new DownResultModel();
            downResultModel.setCode(-1).setMessage("下载失败!").setResult(false).setData(fileName);
            try {
                response.getWriter().write(JSON.toJSONString(downResultModel));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
//            response.setHeader("Content-Disposition", "attachment; filename="+  new String(fileName.getBytes("utf-8"),"iso-8859-1"));
//            e.printStackTrace();
        } finally {

            if (fileInStream != null) {
                try {
                    fileInStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
//            if (fileOutStream != null) {
//                try {
//                    fileOutStream.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            if (requInStream != null) {
//                try {
//                    requInStream.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
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
