package cn.hnen.transmedia.exception;

/**
 * @author  YSH
 * @create  201812
 * @desc  自定义异常，用于同一异常处理
 */
public class MediaDownloadException extends MediaBaseException {


    public MediaDownloadException() {
        super();
    }

    public MediaDownloadException(String message) {
        super(message);
    }

    public MediaDownloadException(String message, Throwable cause) {
        super(message, cause);
    }

    public MediaDownloadException(Throwable cause) {
        super(cause);
    }

    protected MediaDownloadException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
