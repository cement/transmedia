package cn.hnen.transmedia.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.File;

/**
 * @author YSH
 * @create 201812
 * @desc 文件下发下载配置
 */
@Configuration
public class MediaDistributeConfig {


    @Value("${app.media.root.dir}")
    public void setMediaRootDir(String mediaRootDir) {
        MediaDistributeConfig.mediaRootDir = mediaRootDir.endsWith(File.separator) ? mediaRootDir : mediaRootDir + File.separator;
    }

    @Value("${app.media.path.ref:media}")
    public void setMediaPathRef(String mediaPath) {

        mediaPathRef = mediaPath.startsWith("/") ?mediaPath : "/"+mediaPath;
        mediaPathRef = mediaPathRef.endsWith("/") ?mediaPathRef : mediaPathRef+"/";
    }

    @Value("${app.download.report.url}")
    public void setDownloadReportUrl(String downloadReportUrl) {
        MediaDistributeConfig.downloadReportUrl = downloadReportUrl;
    }


    @Value("${app.download.buffer.size:1024000}")
    public void setDownloadBufferSize(int downloadBufferSize) {
        MediaDistributeConfig.downloadBufferSize = downloadBufferSize;
    }

    @Value("${app.download.api.path}")
    public void setDownloadApiPath(String downloadApiPath) {
        MediaDistributeConfig.downloadApiPath = downloadApiPath;
    }

    @Value("${app.upload.filekey}")
    public void setUploadFilekey(String uploadFilekey) {
        MediaDistributeConfig.uploadFilekey = uploadFilekey;
    }

    @Value("${app.download.filekey}")
    public void setDownloadFilekey(String downloadFilekey) {
        MediaDistributeConfig.downloadFilekey = downloadFilekey;
    }


    @Value("${app.replace.download.api}")
    public  void setReplaceDownloadApi(String replaceDownloadApi) {
        MediaDistributeConfig.replaceDownloadApi = replaceDownloadApi;
    }

    @Value("${app.replace.download.report.api}")
    public  void setReplaceDownloadReportApi(String replaceDownloadReportApi) {
        MediaDistributeConfig.replaceDownloadReportApi = replaceDownloadReportApi;
    }
//    public static String downloadMediaDir;  //上传文件夹
    public static String mediaRootDir;  //媒体文件存储文件夹
    public static String mediaPathRef;  //媒体文件路径映射

    public static String downloadApiPath;  //分发下载路径
    public static String downloadReportUrl;  //下载文件结果汇报地址
    public static int downloadBufferSize;//复制文件缓冲区大小,单位:byte；

    public static String uploadFilekey;
    public static String downloadFilekey;

    public static String replaceDownloadApi;  //替换下载接口
    public static String replaceDownloadReportApi;  //替换下载文件结果汇报接口


}
