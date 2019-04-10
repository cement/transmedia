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

        fileHostDownloadRole0.setFileName("a.mp4");
        fileHostDownloadRole0.setId(1L);

        list.add(fileHostDownloadRole0);


        FileHostDownloadRole fileHostDownloadRole1 = new FileHostDownloadRole();
        fileHostDownloadRole1.setFileName("a.txt");
        fileHostDownloadRole1.setId(2L);

        list.add(fileHostDownloadRole1);

        FileHostDownloadRole fileHostDownloadRole2 = new FileHostDownloadRole();
        fileHostDownloadRole2.setFileName("b.txt");
        fileHostDownloadRole2.setId(3L);

        list.add(fileHostDownloadRole2);

        FileHostDownloadRole fileHostDownloadRole22 = new FileHostDownloadRole();
        fileHostDownloadRole22.setFileName("b.mp4");
        fileHostDownloadRole22.setId(3L);

        list.add(fileHostDownloadRole22);

        FileHostDownloadRole fileHostDownloadRole3 = new FileHostDownloadRole();
        fileHostDownloadRole3.setFileName("154477337607248866014ddcf423988077817ccb4e240.mp4");
        fileHostDownloadRole3.setId(4L);

        list.add(fileHostDownloadRole3);


        String listStr = JSON.toJSONString(list);

        HashMap<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("data",listStr);

        System.out.println(JSON.toJSONString(paramsMap));

//        ResponseEntity<String> reportResult = restTemplate.getForEntity("http://127.0.0.1:8008/api/distribute/receive?data={data}",  String.class,paramsMap);
//        String result = reportResult.getBody();

    }


}