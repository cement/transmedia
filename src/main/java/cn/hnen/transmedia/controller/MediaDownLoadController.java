package cn.hnen.transmedia.controller;

import cn.hnen.transmedia.service.MediaDistributeService;
import cn.hnen.transmedia.util.MyUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
    public void clientDownMedia(@RequestParam("fileName") String fileName, HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
        /*2019-03-11 改服务器跳转接口*/
//        downloadMediaService.downLoadMedia(fileName, response);
            request.getRequestDispatcher(mediaPathRef + fileName).forward(request, response);
            log.info("Client [{}]  downLoad finished:{}", MyUtil.getIpAddress(request),fileName);
    }




    @ApiOperation(value = "提供下载(服务转发)", notes = "提供下载api接口")
    @RequestMapping(value = "/fordown/{fileName:.+}", method = {RequestMethod.GET, RequestMethod.POST})
    public void downloadForward(HttpServletRequest request, HttpServletResponse response, @PathVariable("fileName") String fileName) {
        try {
            request.getRequestDispatcher(mediaPathRef + fileName).forward(request, response);
            log.info("forwarded1：{}", mediaPathRef + fileName);
        } catch (Exception e) {
            log.error("设备端 下载失败！{}", fileName, e.getMessage(), e);
        }
    }

    /**
     * @param fileName
     * @param response
     * @desc 根据文件名进行下载，没有就用json告诉设备端，有就写入流
     */
    @ApiOperation(value = "提供设备端下载(服务转发)", notes = "设备端文件下载webapi接口")
    @ApiResponses({})
    @RequestMapping(value = "/fordown", method = {RequestMethod.GET, RequestMethod.POST})
    public void downloadForward(@RequestParam("fileName") String fileName, HttpServletRequest request,HttpServletResponse response) {
        try {
            request.getRequestDispatcher(mediaPathRef + fileName).forward(request, response);
            log.info("forwarded2：{}", mediaPathRef + fileName);
        } catch (Exception e) {
            log.error("设备端 下载失败！{}", fileName, e.getMessage(), e);
        }
    }


}
