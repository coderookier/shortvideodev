package org.video.service;

import org.video.pojo.Bgm;
import org.video.pojo.Videos;

import java.util.List;

/**
 * @author gutongxue
 * @date 2019/11/17 20:00
 **/
public interface VideoService {

    /**
     * 保存视频到数据库
     * 返回视频主键ID用于后面上传封面到数据库
     */
    public String saveVideo(Videos videos);


    /**
     * 修改视频的封面
     */
    public void updateVideo(String videoId, String coverPath);
}
