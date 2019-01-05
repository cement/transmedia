package cn.hnen.transmedia.entry;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * @author YSH
 * @create 201812
 * @desc  接收文件下载返回实体类(没有用到)
 */
@Data
public class ReciveResultModel {

    private  int  resultCode = 0;
    private  List downedList = new ArrayList();
    private  List existedList = new ArrayList();
    private  List failedList = new ArrayList();
    public synchronized static ReciveResultModel mergeResult(ReciveResultModel result, ReciveResultModel result1){

        result.getDownedList().addAll(result1.getDownedList());
        result.getFailedList().addAll(result1.getFailedList());
        result.getExistedList().addAll(result1.getExistedList());
        return result;
    }

}
