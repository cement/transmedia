package cn.hnen.transmedia.controller;

import cn.hnen.transmedia.config.FileDistributeConfig;
import cn.hnen.transmedia.entry.ResponseModel;
import cn.hnen.transmedia.jpaentry.MediaTransInfoEntry;
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

import static cn.hnen.transmedia.config.FileDistributeConfig.DOWN_TYPE_REPLACE;
import static cn.hnen.transmedia.config.FileDistributeConfig.downloadMediaDir;


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
//    @ApiResponses({})
    @RequestMapping(value = "/upload", method = {RequestMethod.POST})
    public ResponseEntity<ResponseModel> uploadReplace(@RequestParam("file") MultipartFile file) {
        ResponseModel responseModel = uploadService.uploadMediaReport(file);
        return ResponseEntity.ok().body(responseModel);

    }

    @ApiOperation(value = "替换文件  下载方式", notes = "替换文件 下载,供主服务器调用")
//    @ApiResponses({})
    @RequestMapping(value = "/download", method = {RequestMethod.POST})
    public ResponseEntity<ResponseModel> downloadReplace(@RequestParam("fileName") String fileName) {


        ResponseModel responseModel = uploadService.downMediaReport(fileName);
        return ResponseEntity.ok().body(responseModel);

    }

    @ApiOperation(value = "文件是否已经下载", notes = "文件是否已经下载api接口")
    @RequestMapping(value = "/isexist", method = {RequestMethod.POST, RequestMethod.GET})
    public ResponseEntity<Boolean> existFile(@RequestParam("fileName") String fileName) {
        boolean exists = Files.exists(Paths.get(FileDistributeConfig.downloadMediaDir, fileName));

//        /*记录日志*/
//        log.info("替换上传 文件已存在,文件名：{}",fileName);
//        /*记录到数据库*/
//        MediaTransInfoEntry transInfo = new MediaTransInfoEntry();
//        transInfo.setDownloadMediaDir(downloadMediaDir);
//        transInfo.setDownLoadResult("替换上传 文件已存在");
//        transInfo.setFileName(fileName);
//        transInfo.setDownloadType(DOWN_TYPE_REPLACE);
////        transInfo.setDownLoadDuration(stop - start);
//        mediaDownRepository.save(transInfo);

        return ResponseEntity.ok(exists);
    }


}
