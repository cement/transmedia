package cn.hnen.transmedia.entry;

/**
 * @author YSH
 * @create 201812
 * @desc  客户端下载返回实体类
 */
public class DownResultModel {


    private int code;
    private String message;
    private boolean result;
    private Object data;

    public int getCode() {
        return code;
    }

    public DownResultModel setCode(int code) {
        this.code = code;
        return this;
    }





    public DownResultModel setResult(boolean result) {
        this.result = result;
        return this;
    }

    public Object getData() {
        return data;
    }

    public DownResultModel setData(Object data) {
        this.data = data;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public DownResultModel setMessage(String message) {
        this.message = message;
        return this;
    }

    public boolean isResult() {
        return result;
    }
}
