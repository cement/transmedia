package cn.hnen.transmedia.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;


@Configuration
public class JpaBusinessConfig {

    @Value("${app.h2.record.overdue:365}")
    public  void setOverDueDays(Integer overDueDays) {
        JpaBusinessConfig.overDueDays = -overDueDays;
    }

    public static Integer overDueDays;


//    @Bean(name = "primaryDataSource")
//    @Qualifier("primaryDataSource")
//    @ConfigurationProperties(prefix="spring.datasource.primary")
//    public DataSource primaryDataSource() {
//        return DataSourceBuilder.create().build();
//    }
//
//    @Bean(name = "secondaryDataSource")
//    @Qualifier("secondaryDataSource")
//    @Primary
//    @ConfigurationProperties(prefix="spring.datasource.secondary")
//    public DataSource secondaryDataSource() {
//        return DataSourceBuilder.create().build();
//    }



//    @Bean     //声明其为Bean实例
//    public DataSource buildDataSource() {
//        return DataSourceBuilder.create().type(null).driverClassName("org.h2.Driver").username("user").password("pass").url("jdbc:h2:file:./H2/embeddb;AUTO_SERVER=TRUE").build();
//    }


}
