package cn.hnen.transmedia.appender;

import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.AppenderBase;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * @author YSH
 * @create 201812
 * @desc  在此实现UI信息（没有使用）
 */
public class UiViewAppender extends AppenderBase<LoggingEvent> {


    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//    @Autowired
//    private JobtaskWebSocket webSocket;
    @Override
    protected void append(LoggingEvent event) {
//        StringBuffer sbf =new StringBuffer();
//        sbf.append("[").append(sdf.format(new Date(event.getTimeStamp()))).append("] ");
//        sbf.append("[").append(event.getLevel()).append("]");
//        sbf.append("[").append(event.getThreadName()).append("]");
//        sbf.append("[").append(event.getLoggerName()).append("]");
//        sbf.append(" : ").append(event.getMessage()).append("\r\n");
//        String msg = sbf.toString();

       // TaskWebSocket.sendsMessage(msg);
        //        System.out.println("+3+"+event.getMessage());

    }
}
