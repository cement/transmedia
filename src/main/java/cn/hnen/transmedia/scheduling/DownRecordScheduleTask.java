package cn.hnen.transmedia.scheduling;

import cn.hnen.transmedia.Config.JpaBusinessConfig;
import cn.hnen.transmedia.repository.MediaDownloadRepository;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * @author YSH
 * @create 201812
 * @desc  定时任务，目前没有使用，将来删除等业务需要
 */
@Component
public class DownRecordScheduleTask {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private MediaDownloadRepository downloadRepository;


//    @Scheduled(cron = "0/3 * * * * ?")
    public void getDefaultMedia() {
        System.out.println("This is a say method!"+new Date());
    }

//    @Scheduled(cron = "0/5 * * * * ?")
    //每周六一点执行一次
    @Scheduled(cron = "0 0 1 ? * 7")
    public void deleteDownInfo() {

//        Date date = new Date();//当前日期
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");//格式化对象
//        Calendar calendar = Calendar.getInstance();//日历对象
//        calendar.setTime(date);//设置当前日期
//        calendar.add(Calendar.MONTH, -1);//月份减一
//        Date preMonthDate = calendar.getTime();

        System.out.println("===================================="+JpaBusinessConfig.overDueDays);
      downloadRepository.deleteOutDateRecord(JpaBusinessConfig.overDueDays);
    }
}
