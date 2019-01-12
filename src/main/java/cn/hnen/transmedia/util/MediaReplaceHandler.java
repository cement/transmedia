package cn.hnen.transmedia.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static cn.hnen.transmedia.config.MediaDistributeConfig.downloadMediaDir;
@Component
public class MediaReplaceHandler {

    @Autowired
    public RestTemplate restTemplate;



    public static Path fileUpload(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        Path path = Paths.get( downloadMediaDir+ fileName);
        file.transferTo(path);
        return path;
    }






}
