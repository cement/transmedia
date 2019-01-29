package cn.hnen.transmedia.exception;

public class MediaReciveException extends MediaBaseException {


    public MediaReciveException() {
    }

    public MediaReciveException(String message) {
        super(message);
    }

    public MediaReciveException(String message, Throwable cause) {
        super(message, cause);
    }

    public MediaReciveException(Throwable cause) {
        super(cause);
    }

    public MediaReciveException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
