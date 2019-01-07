package cn.hnen.transmedia.jpaentry;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

/**
 * @author YSH
 * @create 201812
 * @desc   记录文件下载（从服务端下载）+（客户端下载）信息记录
 */
@Entity
@Table(name = "T_MEDIA_DOWNLOAD_INFO")
@EntityListeners(AuditingEntityListener.class)
@Data
public class MediaTransInfoEntry {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;




   @Column(name = "file_id")
   private Long fileId;
   // 要下发的文件名
    @Column(name = "file_name",length = 1024)
    private String fileName;

    //在设备上开始播放的日期
    @Column(name = "begin_play_time")
    private String beginPlayTime;


    //在设备上结束播放的日期
    @Column(name = "end_play_time")
    private String endPlayTime;

    //下载结果
    @Column(name = "down_load_result")
    private String downLoadResult;

//
//    //下载耗时
//    @Column(name = "down_load_time")
//    private Long downLoadTime;

    //下载日期时间戳
    @Column(name = "down_load_date" )
    @CreatedDate
//    @Temporal(TemporalType.TIMESTAMP)

    private Date downLoadDate;

    //修改日期时间戳
    @Column(name = "updata_date" )
    @LastModifiedDate
//    @Temporal(TemporalType.TIMESTAMP)

    private Date updataDate;

    //下载类型
    @Column(name = "down_load_type" )
    private String downloadType;

    //下载描述
    @Column(name = "down_info_describe",length = 10000)
    private String describe;

    //下载耗时
    @Column(name = "down_load_duration" )
    private Long downLoadDuration;

     //市州Id
    @Column(name = "city_id")
    private String cityId;

    //广告文件存储目录
    @Column(name = "down_media_dir")
    private String downloadMediaDir;
}
