package cn.hnen.transmedia.config;

import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

/**
 * @author YSH
 * @create 201812
 * @desc 注入RestTemplate 并设置
 */
@Configuration
public class RestTemplateConfig {

    @Value("${app.download.retry.count:3}")
    public int retryCount;

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder,ClientHttpRequestFactory clientHttpRequestFactory) {
        RestTemplate restTemplate = builder.build();
        restTemplate.setRequestFactory(clientHttpRequestFactory);
        // 使用 utf-8 编码集的 conver 替换默认的 conver（默认的 string conver 的编码集为"ISO-8859-1"）
//        List<HttpMessageConverter<?>> messageConverters = restTemplate.getMessageConverters();
//        Iterator<HttpMessageConverter<?>> iterator = messageConverters.iterator();
//        while (iterator.hasNext()) {
//            HttpMessageConverter<?> converter = iterator.next();
//            if (converter instanceof StringHttpMessageConverter) {
//                iterator.remove();
//            }
//        }
//        messageConverters.add(new StringHttpMessageConverter(Charset.forName("UTF-8")));

        return restTemplate;
    }




    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory(HttpClient httpClient) {
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        clientHttpRequestFactory.setHttpClient(httpClient);
//        clientHttpRequestFactory.setConnectTimeout(5000); // 连接超时，毫秒
//        clientHttpRequestFactory.setReadTimeout(5000); // 读写超时，毫秒
        return clientHttpRequestFactory;
    }




    @Bean
    public HttpClient httpClientBuilder(HttpClientConnectionManager  connectionManager,HttpRequestRetryHandler httpRequestRetryHandler) {
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        //设置HTTP连接管理器
        httpClientBuilder.setConnectionManager(connectionManager);
        httpClientBuilder.setRetryHandler(httpRequestRetryHandler);
        return httpClientBuilder.build();
    }

    @Bean
    public HttpClientConnectionManager poolingConnectionManager() {
        PoolingHttpClientConnectionManager poolingConnectionManager = new PoolingHttpClientConnectionManager();
        poolingConnectionManager.setMaxTotal(30); // 连接池最大连接数
        poolingConnectionManager.setDefaultMaxPerRoute(10); // 每个主机的并发
        return poolingConnectionManager;
    }

    @Bean
    public HttpRequestRetryHandler getHttpRequestRetryHandler(){
        return  new HttpRequestRetryHandler() {
            @Override
            public boolean retryRequest(IOException e, int retriCount, HttpContext httpContext) {
                return retriCount<retryCount;
            }
        };
    }


}
