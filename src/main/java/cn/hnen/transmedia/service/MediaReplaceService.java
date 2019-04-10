package cn.hnen.transmedia.service;

import cn.hnen.transmedia.config.MediaDistributeConfig;
import cn.hnen.transmedia.entry.ResponseModel;
import cn.hnen.transmedia.exception.MediaDownloadException;
import cn.hnen.transmedia.exception.MediaUploadException;
import cn.hnen.transmedia.handler.MediaReplaceHandler;
import cn.hnen.transmedia.handler.MediaReplaceHandler2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Paths;

import static cn.hnen.transmedia.entry.BusinessEnum.FAILED;

/**
 * @author YSH
 * @create 20181203
 */
@Slf4j
@Service
public class MediaReplaceService {

    @Autowired
    public MediaReplaceHandler2 replaceHandler;


    @Retryable(value = MediaUploadException.class, maxAttempts = 3, backoff = @Backoff(delay = 2000, multiplier = 2))
    public ResponseModel uploadMediaSync(MultipartFile file) {
        return replaceHandler.uploadMedia(file);
    }

    /**
     * 此处关于异步的说明：
     * 1、网络接口本身就是多线程(或者NIO)，此处虽然方法名表示异步，但不需要,如果使用异步，有可能出现找不到中间临时文件的问题。
     * 2、本方法的目的是实现下载后向相关接口进行汇报。
     */
    /*不需要异步*/
//    @Async
    @Retryable(value = MediaUploadException.class, maxAttempts = 3, backoff = @Backoff(delay = 3000, multiplier = 3))
    public void uploadMediaAsync(MultipartFile file) {
        ResponseModel responseModel = replaceHandler.uploadMedia(file);
        replaceHandler.replaceMediaReport(responseModel);
    }


    @Recover
    public void recoverUploadAsync(MediaUploadException e, MultipartFile file) {
        log.error("upload async *** 重试失败！***; {},{}", file.getOriginalFilename(), e.getMessage(), e);
    }

    @Recover
    public ResponseModel recoverUploadSync(MediaUploadException e, MultipartFile file) {
        log.error(" upload sync*** 重试失败！***; {},{}", file.getOriginalFilename(), e.getMessage(), e);
        return ResponseModel.warp(FAILED).setResult(file.getOriginalFilename());
    }


    @Retryable(value = MediaDownloadException.class, maxAttempts = 3, backoff = @Backoff(delay = 2000, multiplier = 2))
    public ResponseModel downMediaSync(String fileName) {
        return replaceHandler.downMedia(fileName);
    }

    /**
     * 此处关于异步的说明：
     * 1、网络接口本身就是多线程(或者NIO)，此处虽然方法名表示异步，但不需要。
     * 2、本方法的目的是实现下载后向相关接口进行汇报。
     */
    /*不需要异步*/
//    @Async
    @Retryable(value = MediaDownloadException.class, maxAttempts = 3, backoff = @Backoff(delay = 3000, multiplier = 3))
    public void downMediaAsync(String fileName) {
        ResponseModel responseModel = replaceHandler.downMedia(fileName);
        replaceHandler.replaceMediaReport(responseModel);
    }


    @Recover
    public void recoverDownAsync(MediaDownloadException e, String fileName) {
        log.error(" download async *** 重试失败！***; {},{}", fileName, e.getMessage(), e);
    }

    @Recover
    public ResponseModel recoverDownSync(MediaDownloadException e, String fileName) {
        log.error(" download sync*** 重试失败！***; {},{}", fileName, e.getMessage(), e);
        return ResponseModel.warp(FAILED).setResult(fileName);
    }


    /**
     * retry 使用 注意事项：
     * 1、 使用了@Retryable的方法不能在本类被调用，不然重试机制不会生效。也就是要标记为@Service，然后在其它类使用@Autowired注入或者@Bean去实例才能生效。
     * 2 、要触发@Recover方法，那么在@Retryable方法上不能有返回值，只能是void才能生效,--->>>>>这句是错误的，经测试，@Recover方法必须与@Retryable方法返回值一致，第一入参为要重试的异常，其他参数与@Retryable保持一致，返回值也要一样，否则无法执行。
     *
     * 3 、非幂等情况下慎用
     * 4 、使用了@Retryable的方法里面不能使用try...catch包裹，要在方法上抛出异常，不然不会触发 --->>>RuntimeException直接抛出，并不在方法上throews,也是可以的。
     */


    public Boolean existFile(String fileName) {
        return replaceHandler.existFile(fileName);
    }


}
