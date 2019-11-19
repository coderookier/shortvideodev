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
     */
    public void saveVideo(Videos videos);
}
