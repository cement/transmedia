package cn.hnen.transmedia.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Api(value = "替换文件", tags = {"替换文件webapi接口"})
@CrossOrigin
@Slf4j
@RestController
@RequestMapping("/test")
public class TestController {

    @RequestMapping("/test")
    public String test(@RequestParam("p1") String p1){


        log.info("p1:{}");
        return p1;
    }
}
