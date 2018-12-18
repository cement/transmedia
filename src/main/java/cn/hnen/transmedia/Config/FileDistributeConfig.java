package cn.hnen.transmedia.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author YSH
 * @create 201812
 * @desc 文件下发下载配置
 */
@Configuration
public class FileDistributeConfig {


    public static final String DOWN_TYPE_FROM = "from";
    public static final String DOWN_TYPE_TO= "to";

    public static final String DOWN_RESULT_SUCCESS ="success";
    public static final String DOWN_RESULT_FAILED = "failed";
    public static final String DOWN_RESULT_EXIST= "existed";
    public static final String DOWN_RESULT_UNEXIST= "unexisted";




    @Value("${app.download.media.dir}")
    public  void setDownloadMediaDir(String downloadMediaDir) {
        FileDistributeConfig.downloadMediaDir = downloadMediaDir.endsWith("/")?downloadMediaDir:downloadMediaDir+"/";
    }

    @Value("${app.download.report.url}")
    public  void setDownloadReportUrl(String downloadReportUrl) {
        FileDistributeConfig.downloadReportUrl = downloadReportUrl;
    }


    @Value("${app.download.buffer.size:1024000}")
    public  void setDownloadBufferSize(int downloadBufferSize) {
        FileDistributeConfig.downloadBufferSize = downloadBufferSize;
    }

    @Value("${app.download.api.path}")
    public  void setDownloadApiPath(String downloadApiPath) {
        FileDistributeConfig.downloadApiPath = downloadApiPath;
    }

    public static String downloadMediaDir;  //上传文件夹



    public static String downloadApiPath;  //分发下载路径



    public static String downloadReportUrl;  //下载文件结果汇报地址



    public static int  downloadBufferSize;//复制文件缓冲区大小,单位:byte；
    public static long mediaKeepTime ;

//    public static String extraPrefix = "user:";
//    public static String specifyPalyTime ="specifyPalyTime";  //记录文件播放时间，固定


}
