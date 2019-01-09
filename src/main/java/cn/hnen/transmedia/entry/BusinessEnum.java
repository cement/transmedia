package cn.hnen.transmedia.entry;

public enum BusinessEnum {

    EXISTED(1,"文件已存在"),SUCCESS(2,"成功"),FAILED(3,"失败"),EXCUTING(4,"正在执行"),UNEXIST(5,"源文件不存在");;

    public  int code;
    public  String cname;
    public  String detail;
    BusinessEnum(int code, String cname) {
          this.code=code;
          this.cname=cname;
    }




}
