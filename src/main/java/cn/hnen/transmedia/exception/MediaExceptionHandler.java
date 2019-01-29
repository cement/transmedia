package cn.hnen.transmedia.exception;

import cn.hnen.transmedia.entry.ResponseModel;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;


/**
 * @author  YSH
 * @create 201812
 * @desc  统一异常处理
 */
@ResponseBody
@ControllerAdvice
public class MediaExceptionHandler {


    @ExceptionHandler(MediaBaseException.class)
    public ResponseModel mediaUploadExceptionHandler(Exception e, HttpServletResponse response) {
           ResponseModel model = new ResponseModel(1);

        return model;
    }



}
