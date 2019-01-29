package cn.hnen.transmedia.exception;

/**
 * @author  YSH
 * @create  201812
 * @desc  自定义异常，用于同一异常处理
 */
public class MediaUploadException extends MediaBaseException {


    public MediaUploadException() {
        super();
    }

    public MediaUploadException(String message) {
        super(message);
    }

    public MediaUploadException(String message, Throwable cause) {
        super(message, cause);
    }

    public MediaUploadException(Throwable cause) {
        super(cause);
    }

    protected MediaUploadException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
