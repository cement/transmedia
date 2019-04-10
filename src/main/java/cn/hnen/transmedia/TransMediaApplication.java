package cn.hnen.transmedia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableJpaAuditing
@EnableAsync
@EnableScheduling
@SpringBootApplication(exclude = ErrorMvcAutoConfiguration.class)
@EnableRetry
public class TransMediaApplication {

    public static void main(String[] args) {
        SpringApplication.run(TransMediaApplication.class, args);
    }
}
