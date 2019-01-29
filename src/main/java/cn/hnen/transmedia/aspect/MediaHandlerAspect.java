package cn.hnen.transmedia.aspect;

import cn.hnen.transmedia.entry.ResponseModel;
import cn.hnen.transmedia.exception.MediaBaseException;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Objects;

@Slf4j
@Aspect
@Component
public class MediaHandlerAspect {


    @Pointcut("@annotation(cn.hnen.transmedia.handler.RecordLog)")
    public void mediaHandlerPoint() {}

    @Before("mediaHandlerPoint()")
    public void doBefore(JoinPoint joinPoint)  {

        log.info("开始{},{}",joinPoint.getSignature().getName(),joinPoint.getArgs()[0]);
    }
    @After("mediaHandlerPoint()")
    public void doAfter(JoinPoint joinPoint){
        log.info("...doAfter... {}",joinPoint.getSignature().toLongString());
    }

    @AfterReturning( pointcut = "mediaHandlerPoint()",returning = "model")
    public void doAfterReturning(JoinPoint joinPoint, ResponseModel model)  {
        // 处理完请求，返回内容
        if (Objects.nonNull(model)){
           log.info("...doAfterReturning...log：{}",model.getLog());
        }else{
            log.info("...doAfterReturning...{},{}",joinPoint,model);
        }
    }

    //后置异常通知
    @AfterThrowing(pointcut="mediaHandlerPoint()",throwing = "e")
    public void doAfterThrowing(JoinPoint jp,Exception e){
        if (e instanceof MediaBaseException){
            MediaBaseException mex = (MediaBaseException) e;
            log.error("...doAfterThrowing...{}", mex.getLog(),e);
        }else{
            log.error("...doAfterThrowing...", e);
        }
    }

    /*注意：用@Around这个注解，@Retryable 不生效。测试好长时间，郁闷！*/

//    //环绕通知,环绕增强，相当于MethodInterceptor
//    @Around("mediaHandlerPoint()")
//    public Object arround(ProceedingJoinPoint pjp) {
//        System.out.println("方法环绕start.....");
//        try {
//            Object o =  pjp.proceed();
//            System.out.println("方法环绕proceed，结果是 :" + o);
//            return o;
//        } catch (Throwable e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
}
