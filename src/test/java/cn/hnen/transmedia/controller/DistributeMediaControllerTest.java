package cn.hnen.transmedia.controller;

import cn.hnen.transmedia.entry.FileHostDownloadRole;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class DistributeMediaControllerTest {

    @Autowired
    private RestTemplate restTemplate;
    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void receiveMedia() {
        List<FileHostDownloadRole> list = new ArrayList<>();
        FileHostDownloadRole fileHostDownloadRole0 = new FileHostDownloadRole();

        fileHostDownloadRole0.setFileName("15447546433643a370b6081bc466c92a6dd5e3855431d.jpg");
        fileHostDownloadRole0.setId(1L);

        list.add(fileHostDownloadRole0);


        FileHostDownloadRole fileHostDownloadRole1 = new FileHostDownloadRole();
        fileHostDownloadRole1.setFileName("1544772881119896f3bfe312e4e4294b38a16ef74c21a.mp4");
        fileHostDownloadRole1.setId(2L);

        list.add(fileHostDownloadRole1);

        FileHostDownloadRole fileHostDownloadRole2 = new FileHostDownloadRole();
        fileHostDownloadRole2.setFileName("15448189031659380605287fe42ea9dce867bc44af81a.mp4");
        fileHostDownloadRole2.setId(3L);

        list.add(fileHostDownloadRole2);

        FileHostDownloadRole fileHostDownloadRole3 = new FileHostDownloadRole();
        fileHostDownloadRole3.setFileName("154477337607248866014ddcf423988077817ccb4e240.mp4");
        fileHostDownloadRole3.setId(4L);

        list.add(fileHostDownloadRole3);


        String listStr = JSON.toJSONString(list);

        HashMap<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("data",listStr);

        ResponseEntity<String> reportResult = restTemplate.getForEntity("http://127.0.0.1:8001/api/distribute/receive?data={data}",  String.class,paramsMap);
        String result = reportResult.getBody();

    }

    @Test
    public void reportDownInfo() {
    }
}