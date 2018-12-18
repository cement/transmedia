package cn.hnen.transmedia.entry;

import lombok.Data;

/**
 * @author xcl
 * 文件服务器下发策略到下游服务器(废弃)
 * @date 2018/12/6
 **/
@Data
public class FileHostDownloadRoleVo {

    public FileHostDownloadRoleVo(Long id, String fileName, String beginPlayTime) {
        this.id = id;
        this.fileName = fileName;
        this.beginPlayTime = beginPlayTime;
    }

    public FileHostDownloadRoleVo() {

    }

    /**
     * id
     */
    private Long id;
    /**
     * 要下发的文件名
     */
    private String fileName;

    /**
     * 在设备上开始播放的日期
     */
    private String beginPlayTime;

    private boolean distributeResult;

}
