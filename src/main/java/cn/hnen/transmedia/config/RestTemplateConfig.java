package cn.hnen.transmedia.config;

import org.springframework.context.annotation.Bean;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * @author YSH
 * @create 201812
 * @desc 注入RestTemplate 并设置
 */
//@Configuration
public class RestTemplateConfig {


    @Bean
    public RestTemplate httpClientRestTemplate(){
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        RestTemplate restTemplate = new RestTemplate(factory);
        return restTemplate;
    }

   /*注入RestTemplate 并设置 factory */
//    @Bean
//    public RestTemplate restTemplate(ClientHttpRequestFactory factory) {
//        return new RestTemplate(factory);
//    }

//    @Bean
//    public ClientHttpRequestFactory simpleClientHttpRequestFactory() {
//       SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
////        SimpleClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
////        factory.setReadTimeout(60000);//单位为ms
////        factory.setConnectTimeout(60000);//单位为ms
//
//        return factory;
//    }
//
//    @Bean
//    public RestTemplate restTemplate(){
//        RestTemplate restTemplate = new RestTemplate();
//        return restTemplate;
//    }
//
//    @Bean("urlConnection")
//    public RestTemplate urlConnectionRestTemplate(){
//        RestTemplate restTemplate = new RestTemplate(new SimpleClientHttpRequestFactory());
//        return restTemplate;
//    }
//

//
//    @Bean("OKHttp3")
//    public RestTemplate OKHttp3RestTemplate(){
//        RestTemplate restTemplate = new RestTemplate(new OkHttp3ClientHttpRequestFactory());
//        return restTemplate;
//    }

}