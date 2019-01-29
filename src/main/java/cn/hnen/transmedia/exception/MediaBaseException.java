package cn.hnen.transmedia.exception;

public class MediaBaseException extends RuntimeException {

    public Object result;

    public String log;



    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public MediaBaseException() {
    }

    public MediaBaseException(String message) {
        super(message);
    }

    public MediaBaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public MediaBaseException(Throwable cause) {
        super(cause);
    }

    public MediaBaseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
