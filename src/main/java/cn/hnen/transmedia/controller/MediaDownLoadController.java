package cn.hnen.transmedia.controller;

import cn.hnen.transmedia.service.MediaDistributeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static cn.hnen.transmedia.config.MediaDistributeConfig.mediaPathRef;

/**
 * @auther YSH
 * @create 20181203
 * 设备端媒体文件(广告)下载接口
 */


@Api(value = "设备端下载", tags = {"设备端下载 接口"})
@CrossOrigin
@Slf4j
@Controller
@RequestMapping("/api/download")
public class MediaDownLoadController {


    @Autowired
    private MediaDistributeService downloadMediaService;


    /**
     * @param fileName
     * @param response
     * @desc 根据文件名进行下载，没有就用json告诉设备端，有就写入流
     */
    @ApiOperation(value = "设备端文件下载", notes = "设备端文件下载webapi接口")
    @ApiResponses({})
    @RequestMapping(value = "/down", method = {RequestMethod.GET, RequestMethod.POST})
    public void clientDownMedia(@RequestParam("fileName") String fileName, HttpServletResponse response) {
        downloadMediaService.downLoadMedia(fileName, response);
    }

//
//    @ApiOperation(value = "提供下载(输出流)", notes = "提供下载api接口")
//    @RequestMapping(value = "/fordown", method = {RequestMethod.GET, RequestMethod.POST})
//    public void forDown(HttpServletRequest request, HttpServletResponse response, @RequestParam("fileName") String fileName) {
//        Path srcPath = Paths.get("E:/Test/advertfiles", fileName);
//        try (OutputStream outStream = response.getOutputStream()) {
////                response.setContentType("application/octet-stream");
////                response.setHeader("Content-Disposition", "attachment; filename=" + new String(filename.getBytes("utf-8"), "iso-8859-1"));
////                String filname = Charset.forName("iso-8859-1").decode(Charset.forName("utf-8").encode(filename)).toString();
//            response.setContentType(Files.probeContentType(srcPath));
//            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
//
//            Files.copy(srcPath, outStream);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }



    @ApiOperation(value = "提供下载(服务转发)", notes = "提供下载api接口")
    @RequestMapping(value = "/fordown/{fileName:.+}", method = {RequestMethod.GET, RequestMethod.POST})
    public void downloadForward(HttpServletRequest request, HttpServletResponse response, @PathVariable("fileName") String fileName) {
        try {
            request.getRequestDispatcher(mediaPathRef + fileName).forward(request, response);
            log.info("forwarded：{},{}", mediaPathRef + fileName, mediaPathRef);
        } catch (Exception e) {
            log.error("下载失败！{}", fileName, e.getMessage(), e);
        }
    }

}
