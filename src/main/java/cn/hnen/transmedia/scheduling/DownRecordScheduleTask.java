package cn.hnen.transmedia.scheduling;

import cn.hnen.transmedia.config.JpaBusinessConfig;
import cn.hnen.transmedia.config.MediaDistributeConfig;
import cn.hnen.transmedia.jpaentry.MediaTransInfoEntry;
import cn.hnen.transmedia.repository.MediaTransRepository;
import cn.hutool.core.bean.BeanException;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.file.FileCopier;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.sun.deploy.net.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.RestTemplate;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;


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
    @Scheduled(cron = "0 0 1 ? * 7")
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
        //TODO

        Path rootPath = Paths.get("F:\\android-sdk");
        Files.list(rootPath).parallel().filter(path -> !path.equals(rootPath) && !path.toString().endsWith(".mp4"))
                .peek(path -> log.info("path：{},count:{}", path,path.getNameCount()))
//                .forEach(path ->  Files.deleteIfExists(path));
             .forEach(path ->{

                         try {
                             FileUtil.del(path.toFile());
                         } catch (IORuntimeException e) {
                             e.printStackTrace();
                         }
                     }
             );


//        Files.walk(Paths.get("e:/test/download/"),1).parallel().peek(path-> System.out.println(path)).collect(Collectors.toList());

    }

    //
    public static void main(String[] args) throws IOException {
            deleteOverdueMediaFiles();
//        FileUtil.copyContent(FileUtil.file("F:\\maven\\repository"),FileUtil.file("e:/test/repository"),false);
//       FileUtil.copyContent(FileUtil.file("E:/Test/advertfiles/"), FileUtil.file("e:/test/download"), false);
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
