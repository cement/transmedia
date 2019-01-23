package cn.hnen.transmedia.controller;

import cn.hnen.transmedia.config.MediaDistributeConfig;
import cn.hnen.transmedia.entry.BusinessEnum;
import cn.hnen.transmedia.entry.ResponseModel;
import cn.hnen.transmedia.repository.MediaTransRepository;
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


@Api(value = "替换文件", tags = {"替换文件webapi接口"})
@CrossOrigin
@Slf4j
@RestController
@RequestMapping("/api/replace")
public class MediaReplaceController {


    @Autowired
    private MediaReplaceService uploadService;

    @Autowired
    private MediaTransRepository mediaDownRepository;

    @ApiOperation(value = "替换文件 上传方式", notes = "替换文件 上传,供主服务器调用")
    @RequestMapping(value = "/upload", method = {RequestMethod.POST})
    public ResponseEntity<ResponseModel> uploadReplace(@RequestParam("file") MultipartFile file,@RequestParam(value = "isAsync",defaultValue = "false") Boolean isAsync) {
        if (isAsync){
            uploadService.uploadMediaAsync(file);
            ResponseModel responseModel = ResponseModel.warp(BusinessEnum.EXECUTING).setData(file.getOriginalFilename());
            return ResponseEntity.ok().body(responseModel);
        }else {
            ResponseModel responseModel = uploadService.uploadMedia(file);
            return ResponseEntity.ok().body(responseModel);
        }

    }


    @ApiOperation(value = "替换文件  下载(同步)方式", notes = "替换文件 下载,供主服务器调用")
    @RequestMapping(value = "/download", method = {RequestMethod.GET,RequestMethod.POST})
    public ResponseEntity<ResponseModel> downloadReplace(@RequestParam("fileName") String fileName,@RequestParam(value = "isAsync",defaultValue = "false") Boolean isAsync) {
        if (isAsync){
            uploadService.downMediaAsync(fileName);
            ResponseModel responseModel = ResponseModel.warp(BusinessEnum.EXECUTING).setData(fileName);
            return ResponseEntity.ok().body(responseModel);
        }else{
            ResponseModel responseModel = uploadService.downMedia(fileName);
            return ResponseEntity.ok().body(responseModel);
        }

    }



    @ApiOperation(value = "文件是否已经下载", notes = "文件是否已经下载api接口")
    @RequestMapping(value = "/isexist", method = {RequestMethod.POST, RequestMethod.GET})
    public ResponseEntity<Boolean> existFile(@RequestParam("fileName") String fileName) {
        boolean exists = Files.exists(Paths.get(MediaDistributeConfig.mediaRootDir, fileName));
        return ResponseEntity.ok(exists);
    }





}
