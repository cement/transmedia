package cn.hnen.transmedia.controller;


import cn.hnen.transmedia.entry.FileHostDownloadRole;
import cn.hnen.transmedia.entry.ResponseModel;
import cn.hnen.transmedia.service.MediaDistributeService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
//        downloadMediaService.receiveMediaListAsync(fileHostDownloadRoles);
//        List<ResponseModel> resultList = fileHostDownloadRoles.stream().map(vo -> downloadMediaService.receiveMediaSync(vo)).collect(Collectors.toList());
        fileHostDownloadRoles.stream().forEach(vo -> downloadMediaService.receiveMediaAsync(vo));

        return "接收成功！";
    }



}
