package cn.hnen.transmedia.controller;

import cn.hnen.transmedia.config.MediaDistributeConfig;
import cn.hnen.transmedia.service.MediaDistributeService;
import io.swagger.annotations.*;
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
    @ApiResponses({ })
    @RequestMapping(value = "/down",method = {RequestMethod.GET,RequestMethod.POST})
    public void clientDownMedia(@RequestParam("fileName")String  fileName, HttpServletResponse response){
         downloadMediaService.downLoadMedia(fileName, response);
    }



    @ApiOperation( value = "提供下载",notes = "提供下载api接口")
    @RequestMapping(value = "/fordown/{filename:.+}", method = {RequestMethod.GET, RequestMethod.POST})
    public void forDown(HttpServletRequest request, HttpServletResponse response, @PathVariable("filename") String fileName){
        try {
            request.getRequestDispatcher(mediaPathRef +fileName).forward(request, response);
            log.info("forwarded：{},{}",fileName,mediaPathRef);
        } catch (Exception e) {
            log.error("下载失败！{}",fileName,e.getMessage(),e);
        }





//        Path srcPath = Paths.get(downloadMediaDir, filename);
//        Path srcPath = Paths.get("e:/test/download", filename);
//
//
//        try (OutputStream outStream = response.getOutputStream()){
////                response.setContentType("application/octet-stream");
////                response.setHeader("Content-Disposition", "attachment; filename=" + new String(filename.getBytes("utf-8"), "iso-8859-1"));
////                String filname = Charset.forName("iso-8859-1").decode(Charset.forName("utf-8").encode(filename)).toString();
//                response.setContentType(Files.probeContentType(srcPath));
//                response.setHeader("Content-Disposition", "attachment; filename=" + filename);
//
//                Files.copy(srcPath, outStream);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

    }
}
