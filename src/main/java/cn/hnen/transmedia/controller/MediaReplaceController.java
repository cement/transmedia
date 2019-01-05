package cn.hnen.transmedia.controller;

import cn.hnen.transmedia.entry.ResponseModel;
import cn.hnen.transmedia.service.MediaReplaceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;


@Api(value="替换文件",tags={"替换文件webapi接口"})
@CrossOrigin
@Slf4j
@RestController
@RequestMapping("/api/replace")
public class MediaReplaceController {


    @Autowired
    private MediaReplaceService uploadService;

    @ApiOperation(value = "替换文件上传", notes = "替换文件上传,供主服务器调用,当且仅当callback为true时,为异步上传，立即返回，否则同步返回")
//    @ApiResponses({})
    @RequestMapping(value = "/upload",method = {RequestMethod.POST})
    public ResponseEntity<ResponseModel> uploadReplace(@RequestParam("file") MultipartFile file,@RequestParam(value = "callback",required = false) Boolean callback){

        if (Objects.nonNull(callback) &&callback){
            uploadService.uploadMediaAsync(file);
           return null;
        }else {
            ResponseModel responseModel = uploadService.uploadMedia(file);
            return ResponseEntity.ok().body(responseModel);
        }
    }

    @ApiOperation(value = "替换文件下载", notes = "替换文件下载,供主服务器调用，当且仅当callback为true时,为异步上传，立即返回，否则同步返回")
//    @ApiResponses({})
    @RequestMapping(value = "/download",method = {RequestMethod.POST})
    public ResponseEntity<ResponseModel> downloadReplace(@RequestParam("fileName") String fileName,@RequestParam(value = "callback",required = false) Boolean callback){

        if (Objects.nonNull(callback) && callback){
            uploadService.downMediaAsync(fileName);
            return null;
        }else{
            ResponseModel responseModel = uploadService.downMedia(fileName);
            return ResponseEntity.ok().body(responseModel);
        }
    }



}
