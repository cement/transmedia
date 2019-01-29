package cn.hnen.transmedia.entry;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * @author YSH
 * @crerate 201812
 * @desc 统一返回实体类
 */
@Data
@Component
public class ResponseModel{


    public int code;
    public String name;
    public String message;
    public String detail;
    public String log;



    public Object data;
    public Object result;
    public Object extra;



    public Object getData() {
        return data;
    }

    public ResponseModel setData(Object data) {
        this.data = data;
        return this;
    }

    public Object getExtra() {
        return extra;
    }

    public ResponseModel setExtra(Object extra) {
        this.extra = extra;
        return this;
    }


    public String getLog() {
        return log;
    }

    public ResponseModel setLog(String log) {
        this.log = log;
        return this;
    }

    public Object getResult() {
        return result;
    }

    public ResponseModel setResult(Object result) {
        this.result = result;
        return this;
    }


    public Throwable getException() {
        return exception;
    }

    public ResponseModel setException(Throwable exception) {
        this.exception = exception;
        return this;
    }

    public Throwable exception;


    public ResponseModel(int code, String message, Object result) {
        this.code = code;
        this.message = message;
        this.result = result;
    }

    public ResponseModel(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public ResponseModel(int code) {
        this.code = code;
    }
    public ResponseModel() {
    }

    public int getCode() {
        return code;
    }

    public ResponseModel setCode(int code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public ResponseModel setMessage(String message) {
        this.message = message;
        return this;
    }

    public String getDetail() {
        return detail;
    }

    public ResponseModel setDetail(String detail) {
        this.detail = detail;
        return this;
    }
    public String getName() {
        return name;
    }

    public ResponseModel setName(String name) {
        this.name = name;
        return this;
    }


    public static ResponseModel warp(BusinessEnum adEnum){
        ResponseModel model = new ResponseModel(adEnum.code,adEnum.cname);
        model.setName(adEnum.name()).setDetail(adEnum.detail);
       return model;
    }



}
