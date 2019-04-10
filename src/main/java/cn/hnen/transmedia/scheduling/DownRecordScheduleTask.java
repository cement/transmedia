package cn.hnen.transmedia.scheduling;

import cn.hnen.transmedia.config.JpaBusinessConfig;
import cn.hnen.transmedia.repository.MediaTransRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Date;


/**
 * @author YSH
 * @create 201812
 * @desc 定时任务，目前没有使用，将来删除等业务需要
 */
@Slf4j
@Component
public class DownRecordScheduleTask {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private MediaTransRepository downloadRepository;

    private static Thread.UncaughtExceptionHandler uncaughtExceptionHandler = new Thread.UncaughtExceptionHandler() {
        @Override
        public void uncaughtException(Thread t, Throwable e) {
            log.error("删除媒体文件{}发生错误！错误信息：{}", e.getMessage(), e);
        }
    };

    //    @Scheduled(cron = "0/3 * * * * ?")
    public void getDefaultMedia() {
        System.out.println("This is a say method!" + new Date());
    }

    //    @Scheduled(cron = "0/5 * * * * ?")
    //每周六一点执行一次
//    @Scheduled(cron = "0 0 1 ? * 7")
    public void deleteDBDownInfo() {

//        Date date = new Date();//当前日期
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");//格式化对象
//        Calendar calendar = Calendar.getInstance();//日历对象
//        calendar.setTime(date);//设置当前日期
//        calendar.add(Calendar.MONTH, -1);//月份减一
//        Date preMonthDate = calendar.getTime();

        System.out.println("====================================" + JpaBusinessConfig.overDueDays);
        downloadRepository.deleteOutDateRecord(JpaBusinessConfig.overDueDays);
    }

    /*暂时注释。根据删除规则在使用*/
//    @Scheduled(cron = "0 0 1 ? * 7")
    public static void deleteOverdueMediaFiles() throws IOException {
        Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler);

    }

    //
    public static void main(String[] args) throws IOException {
            deleteOverdueMediaFiles();
//        FileUtil.copyContent(FileUtil.file("F:\\maven\\repository"),FileUtil.file("e:/test/repository"),false);
//        FileUtil.copyContent(FileUtil.file("E:/Test/advertfiles/"), FileUtil.file("e:/test/download"), false);
//         FileCopier.create(FileUtil.file("E:/Test/advertfiles/"), FileUtil.file("e:/test/download")).setOverride(false).setCopyContentIfDir(true).copy();

//        String str = RuntimeUtil.execForStr("ping");
//        System.out.println(str);
//        Validator.validateChinese();
//        BeanUtil.isBean();

//        try {
//            Map<String, PropertyDescriptor> propertyDescriptorMap = BeanUtil.getPropertyDescriptorMap(MediaTransInfoEntry.class, true);
//            System.out.println(propertyDescriptorMap);
//        } catch (BeanException e) {
//            e.printStackTrace();
//        }


    }


}
