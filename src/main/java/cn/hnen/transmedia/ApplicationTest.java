package cn.hnen.transmedia;

import com.alibaba.fastjson.JSON;
import jdk.nashorn.internal.lookup.MethodHandleFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplateHandler;

import java.io.UnsupportedEncodingException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ApplicationTest {
    public static void main(String[] args) {
        Map<String,String> map = new HashMap();
        map.put("p1","1");
        map.put("p2","5");
        map.put("p3","5");
        map.put("p4","5");
        RestTemplate restTemplate = new RestTemplate();
//        restTemplate.setUriTemplateHandler(new UriTemplateHandler() {
//            @Override
//            public URI expand(String uriTemplate, Map<String, ?> uriVariables) {
//                StringBuffer buffer=new StringBuffer();
//                buffer.append("?");
//                for (Map.Entry<String,?> entry:uriVariables.entrySet()) {
//                    buffer.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
//                }
//                buffer.setLength(buffer.length()-1);
//                URI uri = null;
//                try {
//                    uri = new URI(uriTemplate + buffer.toString());
//                    System.out.println(uri);
//                } catch (URISyntaxException e) {
//                    e.printStackTrace();
//                }
//                return uri;
//            }
//
//            @Override
//            public URI expand(String uriTemplate, Object... uriVariables) {
//                return null;
//            }
//        });

//        MultiValueMap<String, Object> multiValueMap = new LinkedMultiValueMap<>();
//        multiValueMap.put("p1",map);

        HashMap<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("p1","1");
        paramsMap.put("p2","2");

        String result = restTemplate.postForObject("http://localhost:8008/test/test", paramsMap, String.class);
        System.out.println(result);

    }
}
