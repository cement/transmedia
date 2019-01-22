package cn.hnen.transmedia.controller;

import cn.hnen.transmedia.config.MediaDistributeConfig;
import cn.hnen.transmedia.entry.ResponseModel;
import cn.hnen.transmedia.repository.MediaTransRepository;
import cn.hnen.transmedia.service.MediaReplaceService;
import cn.hutool.http.HttpUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import static cn.hnen.transmedia.config.MediaDistributeConfig.mediaPathRef;


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
        ResponseModel responseModel = uploadService.uploadMediaAndReport(file);
        return ResponseEntity.ok().body(responseModel);

    }

    @ApiOperation(value = "替换文件  下载方式", notes = "替换文件 下载,供主服务器调用")
//    @ApiResponses({})
    @RequestMapping(value = "/download", method = {RequestMethod.POST})
    public ResponseEntity<ResponseModel> downloadReplace(@RequestParam("fileName") String fileName) {
        ResponseModel responseModel = uploadService.downMediaAndReport(fileName);
        return ResponseEntity.ok().body(responseModel);

    }

    @ApiOperation(value = "文件是否已经下载", notes = "文件是否已经下载api接口")
    @RequestMapping(value = "/isexist", method = {RequestMethod.POST, RequestMethod.GET})
    public ResponseEntity<Boolean> existFile(@RequestParam("fileName") String fileName) {
        boolean exists = Files.exists(Paths.get(MediaDistributeConfig.mediaRootDir, fileName));
        return ResponseEntity.ok(exists);
    }


//    @Autowired
//    private RestTemplate restTemplate;
//
//
//    @ApiOperation( value = "从网络下载",notes = "从网络下载api接口")
//    @RequestMapping(value = "/obtain", method = {RequestMethod.GET, RequestMethod.POST})
//    public void obtain(@RequestParam("urls") String... urls){
//        Arrays.stream(urls).parallel().peek(url->log.info(url)).forEach(url->{
//
//            String filename = url.substring(url.lastIndexOf('/')+1);
////            Path target = Paths.get(downloadMediaDir, filename);
//            Path srcPath = Paths.get("e:/test/downNew", filename);
//            if (Files.exists(srcPath)){
//                log.info("文件： {}已经存在！",filename);
//            }else{
//                try {
//                    HttpUtil.downloadFile(url,"e:/test/downNew/"+filename);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//
//    }




}
