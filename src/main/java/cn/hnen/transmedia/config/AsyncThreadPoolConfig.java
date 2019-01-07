package cn.hnen.transmedia.config;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * @author YSH
 * @create 201812
 * @Async线程池配置(目前使用默认)
 */
//@Configuration
//@ComponentScan("cn.hnen.transmedia.service")
//@EnableAsync
public class AsyncThreadPoolConfig implements AsyncConfigurer{





        @Override
        public Executor getAsyncExecutor() {
            ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
            executor.setThreadNamePrefix("async-pool-");
//            executor.setCorePoolSize(5);
//            executor.setMaxPoolSize(10);
//            executor.setQueueCapacity(25);
            executor.initialize();
            return executor;
        }

        @Override
        public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
            AsyncUncaughtExceptionHandler asyncUncaughtExceptionHandler = new AsyncUncaughtExceptionHandler() {

                @Override
                public void handleUncaughtException(Throwable throwable, Method method, Object... objects) {

                }
            };
            return asyncUncaughtExceptionHandler;

        }


    @Bean("taskExecutor")
    public Executor taskExecutor() {


        ExecutorService executorService = Executors.newCachedThreadPool();
        return executorService;

//        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//        executor.setCorePoolSize(10);
//        executor.setMaxPoolSize(20);
//        executor.setQueueCapacity(200);
//        executor.setKeepAliveSeconds(60);
//        executor.setThreadNamePrefix("taskExecutor-");
//        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
//        return executor;
    }




}
