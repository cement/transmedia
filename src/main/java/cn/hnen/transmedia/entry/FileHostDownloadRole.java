package cn.hnen.transmedia.entry;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author xcl
 * 文件服务器下发策略
 * @date 2018/12/6
 **/
//@Entity
@Data
//@AllArgsConstructor
//@NoArgsConstructor
//@Table(name = "AD.T_FILEHOST_DOWNLOAD_ROLE")
public class FileHostDownloadRole {


    /**
     *  id
     */
//    @Id
//    @Column(name = "ID")
    private Long id;
    /**
     *  要下发的文件名
     */
//    @Column(name = "FILENAME")
    private String fileName;
    /**
     *  市州Id
     */
//    @Column(name = "CITYID")
    private String cityId;
    /**
     *  在设备上开始播放的日期
     */
//    @Column(name = "BEGINPLAYTIME")
    private String beginPlayTime;

    /**
     *  是否成功下发
     */
//    @Column(name = "SENDED")
    private Boolean sended;
}
