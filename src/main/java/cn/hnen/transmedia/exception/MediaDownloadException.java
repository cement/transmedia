package cn.hnen.transmedia.exception;

/**
 * @author  YSH
 * @create  201812
 * @desc  自定义异常，用于同一异常处理
 */
public class MediaDownloadException extends RuntimeException {


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    public int code;
    public Object obj;

    public MediaDownloadException(int code,Object obj) {
        this.code = code;
        this.obj = obj;
    }
    public MediaDownloadException(int code) {
        this.code = code;
    }
    public MediaDownloadException(Throwable cause) {
        super(cause);
    }

}
