package cn.hnen.transmedia.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JpaBusinessConfig {

    @Value("${app.h2.record.overdue:365}")
    public  void setOverDueDays(Integer overDueDays) {
        JpaBusinessConfig.overDueDays = -overDueDays;
    }

    public static Integer overDueDays;
}
