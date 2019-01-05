package cn.hnen.transmedia.controller;

import cn.hnen.transmedia.service.MediaDistributeService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;

/**
 * @auther YSH
 * @create 20181203
 * 设备端媒体文件(广告)下载接口
 *
 */


@Api(value="设备端下载",tags={"设备端下载 接口"})
@CrossOrigin
@Slf4j
@Controller
@RequestMapping("/api/download")
public class MediaDownLoadController {


    @Autowired
    private MediaDistributeService downloadMediaService;


    /**
     * @desc  根据文件名进行下载，没有就用json告诉设备端，有就写入流
     * @param fileName
     * @param response
     */
    @ApiOperation( value = "设备端文件下载", notes="设备端文件下载webapi接口" )
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "fileName", value = "文件名", required = true, dataType = "String", paramType = "query"),
            }
    )
    @ApiResponses({ })


    @RequestMapping(value = "/down")
    public void clientDownMedia(@RequestParam("fileName")String  fileName, HttpServletResponse response){
         downloadMediaService.downLoadMedia(fileName, response);
    }

}
