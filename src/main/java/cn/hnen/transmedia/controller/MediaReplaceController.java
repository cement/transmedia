package cn.hnen.transmedia.controller;

import cn.hnen.transmedia.config.MediaDistributeConfig;
import cn.hnen.transmedia.entry.BusinessEnum;
import cn.hnen.transmedia.entry.ResponseModel;
import cn.hnen.transmedia.service.MediaReplaceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Paths;


/**
 * @author YSH
 * @created 20190103
 * @desc 替换文件的上传、下载
 */
@Api(value = "替换文件", tags = {"替换文件webapi接口"})
@CrossOrigin
@Slf4j
@RestController
@RequestMapping("/api/replace")
public class MediaReplaceController {


    @Autowired
    private MediaReplaceService replaceService;


    //    private static final ExecutorService executor = Executors.newCachedThreadPool();
    @ApiOperation(value = "替换文件 上传方式", notes = "替换文件 上传,供主服务器调用")
    @RequestMapping(value = "/upload", method = {RequestMethod.POST})
    public ResponseEntity<ResponseModel> uploadReplace(@RequestParam("file") MultipartFile file, @RequestParam(value = "isAsync", defaultValue = "false") Boolean isAsync) {
        if (isAsync) {
//            executor.submit(()->uploadService.uploadMediaAsync(file));
            replaceService.uploadMediaAsync(file);
            ResponseModel responseModel = ResponseModel.warp(BusinessEnum.EXECUTING).setResult(file.getOriginalFilename());
            return ResponseEntity.ok().body(responseModel);
        } else {
            ResponseModel responseModel = replaceService.uploadMediaSync(file);
            return ResponseEntity.ok().body(responseModel);
        }

    }


    @ApiOperation(value = "替换文件  下载方式", notes = "替换文件 下载,供主服务器调用")
    @RequestMapping(value = "/download", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<ResponseModel> downloadReplace(@RequestParam("fileName") String fileName, @RequestParam(value = "isAsync", defaultValue = "false") Boolean isAsync) {
        if (isAsync) {
//            executor.submit(()->uploadService.downMediaAsync(fileName));
            replaceService.downMediaAsync(fileName);
            ResponseModel responseModel = ResponseModel.warp(BusinessEnum.EXECUTING).setResult(fileName);
            return ResponseEntity.ok().body(responseModel);
        } else {
            ResponseModel responseModel = replaceService.downMediaSync(fileName);
            return ResponseEntity.ok().body(responseModel);
        }

    }


    @ApiOperation(value = "文件是否已经下载/存在", notes = "文件是否已经下载/存在api接口", hidden = true)
    @RequestMapping(value = "/upload/isexist", method = {RequestMethod.POST, RequestMethod.GET})
    public ResponseEntity<Boolean> existFile(@RequestParam("fileName") String fileName) {
        Boolean exist = replaceService.existFile(fileName);
        return ResponseEntity.ok(exist);
    }


}
