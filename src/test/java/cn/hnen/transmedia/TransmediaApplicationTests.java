package cn.hnen.transmedia;

import cn.hnen.transmedia.Config.FileDistributeConfig;
import cn.hnen.transmedia.entry.FileHostDownloadRoleVo;
import cn.hnen.transmedia.jpaentry.MediaDownloadInfoEntry;
import cn.hnen.transmedia.repository.MediaDownloadRepository;
import cn.hutool.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class TransmediaApplicationTests {


    @Autowired
    RestTemplate restTemplate;

    @Autowired
    private MediaDownloadRepository mediaDownRepository;
    @Test
    public void contextLoads() {
    }

    @Test
    public void receiveTest() {

//        Map<String,Object> paramsMap = new HashMap<>();


        FileHostDownloadRoleVo vo1 = new FileHostDownloadRoleVo(8787878L, "a.jpg", "2018-01-01");
        FileHostDownloadRoleVo vo2 = new FileHostDownloadRoleVo(9898989L, "b.mpg", "2018-01-01");
        FileHostDownloadRoleVo vo3 = new FileHostDownloadRoleVo(32323432L, "bbb.mp4", "2018-01-01");
        FileHostDownloadRoleVo vo4 = new FileHostDownloadRoleVo(989892389L, "名字篇.mp4", "2018-01-01");
        FileHostDownloadRoleVo vo5= new FileHostDownloadRoleVo(932898989L, "谈吐篇.mpg", "2018-01-01");


        List<FileHostDownloadRoleVo> list = new ArrayList<>();
        list.add(vo1);
        list.add(vo2);
        list.add(vo3);
        list.add(vo4);
        list.add(vo5);

        String listStr = JSONArray.toJSONString(list);
        MultiValueMap<String, String> paramsMap= new LinkedMultiValueMap<String, String>();
        paramsMap.add("data",listStr);

        HttpHeaders headers = new HttpHeaders();
//        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(paramsMap, headers);


        String result = restTemplate.postForObject("http://localhost:8000/api/distribute/receive", paramsMap, String.class);


        log.info("  测试  接收 返回 >>>>  "+result);



    }

    @Test
    public void reportTest() {
//        HashMap<String, Object> paramsMap = new HashMap<>();
//        paramsMap.put("id",000000L);

        MultiValueMap<String, String> paramsMap= new LinkedMultiValueMap<String, String>();
        paramsMap.add("id","000006");

        String result = restTemplate.postForObject(FileDistributeConfig.downloadReportUrl, paramsMap, String.class);


//        HttpHeaders headers = new HttpHeaders();
//        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(paramsMap, headers);


//        String result = restTemplate.postForObject(FileDistributeConfig.downloadReportUrl, paramsMap, String.class);

//        ResponseEntity<String> response = restTemplate.exchange(FileDistributeConfig.downloadReportUrl, HttpMethod.POST, requestEntity, String.class);


//        ResponseEntity<String> reportResult = restTemplate.getForEntity(FileDistributeConfig.downloadReportUrl+"?id=999999",String.class);
//        String result = response.getBody();
        log.info("  测试  发送报告 返回 >>>>  "+result);
    }

    @Test
    public void saveTest() {
        MediaDownloadInfoEntry downloadInfoEntry= new MediaDownloadInfoEntry();
        downloadInfoEntry.setFileId(33333333L);
        downloadInfoEntry.setDownLoadResult(FileDistributeConfig.DOWN_RESULT_SUCCESS);
        downloadInfoEntry.setDownloadType(FileDistributeConfig.DOWN_TYPE_FROM);
        downloadInfoEntry.setFileName("dcq.txt");
        downloadInfoEntry.setBeginPlayTime(null);
        mediaDownRepository.save(downloadInfoEntry);
    }

}
