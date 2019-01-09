package cn.hnen.transmedia.repository;

import cn.hnen.transmedia.jpaentry.MediaTransInfoEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

/**
 * @author YSH
 * @create 201812
 * @desc  H2数据库访问，jpa实现
 */
@Repository
public interface MediaTransRepository extends JpaRepository<MediaTransInfoEntry,Long> {




//    public Integer deleteByUpdataDateBefore(Date deleteBeforeDate);
    //原生传参两种形式：  1   :name  2  ?1
   @Modifying
   @Transactional
   @Query(value = "DELETE  FROM T_MEDIA_DOWNLOAD_INFO WHERE DOWN_LOAD_DATE < DATEADD(day,:overDueDays,GETDATE())", nativeQuery=true)
   public void deleteOutDateRecord(@Param("overDueDays")Integer overDueDays);

}