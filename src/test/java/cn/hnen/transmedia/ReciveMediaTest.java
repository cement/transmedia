package cn.hnen.transmedia;

import cn.hnen.transmedia.entry.FileHostDownloadRole;
import cn.hnen.transmedia.entry.ReciveResultModel;
import cn.hnen.transmedia.jpaentry.MediaTransInfoEntry;
import cn.hnen.transmedia.repository.MediaTransRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static cn.hnen.transmedia.config.MediaDistributeConfig.downloadApiPath;
import static cn.hnen.transmedia.config.MediaDistributeConfig.downloadBufferSize;
import static cn.hnen.transmedia.config.MediaDistributeConfig.downloadMediaDir;
import static cn.hnen.transmedia.jpaentry.MediaTransInfoEntry.*;
import static cn.hnen.transmedia.jpaentry.MediaTransInfoEntry.DOWN_RESULT_FAILED;
import static cn.hnen.transmedia.jpaentry.MediaTransInfoEntry.DOWN_TYPE_FROM;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class ReciveMediaTest {


    @Autowired
    private MediaTransRepository mediaDownRepository;

    @Autowired
    MediaReceiveTest mediaReceiveTest;


    @Test
    public void ReciveMediaTest(){

//        receiveMedia("15471899898782180180e7e8946a59ed14237832d47ee.mp4");
//        receiveMedia("1547198509213380eb608374449ceaa4d91428c50f9a6.jpg");
//        receiveMedia("1547257313707c3927dfd2b834ef3858468b523b9e23a.mp4");

        List<String> failedFileList = mediaDownRepository.getFailedFileList();
//        System.out.println(failedFileList);
//        ArrayList<String> failedFileList = new ArrayList();
//        failedFileList.add("15471899898782180180e7e8946a59ed14237832d47ee.mp4");
//        failedFileList.add("1547198509213380eb608374449ceaa4d91428c50f9a6.jpg");
//        failedFileList.add("1547257313707c3927dfd2b834ef3858468b523b9e23a.mp4");
//        failedFileList.add("1547190804268f8714cb4ee6f4dc6aa0bd1efb60a9c18.mp4");
//        failedFileList.add("15450651353679e2b4c97788144c28f619acb536ca546.jpg");
//        failedFileList.add("15450651741020f8dacbe0dd3458bac563faf020085c7.jpg");
//        failedFileList.add("15449124585917db5fce6639c40e6be225172d9c69791.jpg");
//        failedFileList.add("15471891499548fea9a79536d44eaba30847564a86aea.jpg");
//        for (int i = 0; i <failedFileList.size() ; i++) {
//            mediaReceiveTest.receiveMedia(failedFileList.get(i));
//        }

       failedFileList.parallelStream().map(fileName -> mediaReceiveTest.receiveMedia(fileName)).forEach(a->{

       });

        System.out.println("接收完成！");
//        try {
//            Thread.sleep(200000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

    }



}
