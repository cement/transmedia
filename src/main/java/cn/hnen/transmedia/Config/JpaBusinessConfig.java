package cn.hnen.transmedia.Config;

import org.springframework.beans.factory.annotation.Value;

public class JpaBusinessConfig {

    @Value("${app.h2.record.overdue:365}")
    public  void setOverDueDays(String overDueDays) {
        JpaBusinessConfig.overDueDays = overDueDays;
    }

    public static String overDueDays;
}
