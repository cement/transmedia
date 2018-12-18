package cn.hnen.transmedia.repository;

import cn.hnen.transmedia.jpaentry.MediaDownloadInfoEntry;
import org.hibernate.annotations.SQLDelete;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Date;

/**
 * @author YSH
 * @create 201812
 * @desc  H2数据库访问，jpa实现
 */
@Repository
public interface MediaDownloadRepository extends JpaRepository<MediaDownloadInfoEntry,Long> {




//    public Integer deleteByUpdataDateBefore(Date deleteBeforeDate);
   @Modifying
   @Transactional
   @Query(value = "DELETE  FROM T_MEDIA_DOWNLOAD_INFO WHERE UPDATA_DATE < DATEADD(day,-overDueDays,GETDATE())", nativeQuery=true)
   public void deleteOutDateRecord(@Param("overDueDays")String overDueDays);

}