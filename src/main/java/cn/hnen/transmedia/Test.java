package cn.hnen.transmedia;



import cn.hnen.transmedia.entry.ResponseModel;
import org.springframework.core.io.Resource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static cn.hnen.transmedia.config.FileDistributeConfig.downloadApiPath;
import static cn.hnen.transmedia.config.FileDistributeConfig.downloadBufferSize;


public class Test {


    public static RestTemplate restTemplate =  new RestTemplate();
    public static void main(String[] args) {


        long start = System.currentTimeMillis();
//        FileSystemResource resource = new FileSystemResource( Paths.get("D:\\advertfiles","c.mp4"));
        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
        param.add("fileName", "c.mp4");
        param.add("callback", 0);
        ResponseModel model = restTemplate.postForObject("http://192.168.1.112:8008/api/replace/download", param, ResponseModel.class);
        System.out.println(model);
        long stop =System.currentTimeMillis();

        System.out.println(stop-start);

    }

    public static void testFileCreate(){

        InputStream requInStream = null;
        FileOutputStream fileOutStream = null;
        try {
            String fileName = "aaa.jpg";
            File downFile = new File(downloadApiPath,fileName);
            RestTemplate restTemplate = new RestTemplate();


            requInStream = restTemplate.getForEntity("http:192.168.1.112/api/download/down?fileName=" + fileName, Resource.class).getBody().getInputStream();
            fileOutStream = new FileOutputStream(downFile);
//        respOutStream = response.getOutputStream();


//        response.setContentType("application/octet-stream");
//        response.setHeader("Content-Disposition", "attachment; filename="+  new String(fileName.getBytes("utf-8"),"iso-8859-1"));

            byte[] buffer = new byte[downloadBufferSize];
            int readed = 0;
            while ((readed = requInStream.read(buffer)) != -1) {
                fileOutStream.write(buffer, 0, readed);
    //            respOutStream.write(buffer, 0, readed);
            }
//        respOutStream.flush();
            fileOutStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (requInStream != null) {
                try {
                    requInStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileOutStream != null) {
                try {
                    fileOutStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
