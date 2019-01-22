package cn.hnen.transmedia.controller;


import cn.hnen.transmedia.entry.FileHostDownloadRole;
import cn.hnen.transmedia.entry.ReciveResultModel;
import cn.hnen.transmedia.entry.ResponseModel;
import cn.hnen.transmedia.service.MediaDistributeService;
import cn.hnen.transmedia.util.MediaDownHandler;
import com.alibaba.fastjson.JSONArray;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @auther YSH
 * @create 20181203
 * 媒体文件(广告)分发接口
 *
 */

@CrossOrigin
@Slf4j
@RestController
@RequestMapping("/api/distribute")
@Api(value="媒体文件分发",tags={"媒体文件分发 接口"})
public class MediaDistributeController {


    @Autowired
    private MediaDistributeService downloadMediaService;

    @Autowired
    public MediaDownHandler downHandler;

    /**
     *  @desc  请求—>根据路径启动一个线程下载->成功向服务器汇报(原来采用启动多线程下载，测试 发现有异常)
     * @param paramsJson  参数json ,有文件名，文件id等信息，请参考FileHostDownloadRole实体类
     * @return
     */
    @ApiOperation( value = "媒体文件接收",notes = "媒体文件接收api接口")
    @RequestMapping(value = "/receive",method = {RequestMethod.GET,RequestMethod.POST})
    public String receiveMedia(@RequestParam("data") String paramsJson){
        List<FileHostDownloadRole> fileHostDownloadRoles = JSONArray.parseArray(paramsJson, FileHostDownloadRole.class);
        log.info("{}",fileHostDownloadRoles);
        downloadMediaService.receiveMediaListAsync(fileHostDownloadRoles);
        return "接收成功！";
    }

//    @ApiOperation( value = "媒体文件接收",notes = "媒体文件接收api接口", hidden = true)
//    @RequestMapping(value = "/receive2",method = {RequestMethod.GET,RequestMethod.POST})
//    public ResponseEntity<ResponseModel> receiveMedia2(@RequestParam("data") String paramsJson){
//
//        ResponseModel model = new ResponseModel();
//        List<FileHostDownloadRole> fileVos = JSONArray.parseArray(paramsJson, FileHostDownloadRole.class);
//        List<ReciveResultModel> resultList = fileVos.parallelStream().map(fileVo -> downHandler.receiveMedia(fileVo)).collect(Collectors.toList());
//        model.setCode(1).setMessage("下载完成！").setData(resultList);
//       return ResponseEntity.ok(model);
//    }

}
