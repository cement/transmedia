package cn.hnen.transmedia.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import static cn.hnen.transmedia.config.MediaDistributeConfig.mediaPathRef;
import static cn.hnen.transmedia.config.MediaDistributeConfig.mediaRootDir;


/**
 * 配置媒体文件映射路径
 * WebMvcConfigurerAdapter 已经过时，经测试还可以使用，新版本建议使用 WebMvcConfigurationSupport
 * */
@Configuration
public class MediaResourceConfig extends WebMvcConfigurationSupport {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(mediaPathRef+"**").addResourceLocations("file:"+mediaRootDir);
        //*以下配置是为了 swagger-bootstrap-ui*/
        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");

    }

}



