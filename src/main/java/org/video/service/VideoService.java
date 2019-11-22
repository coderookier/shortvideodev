package org.video.service;

import org.video.common.utils.PagedResult;
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

    /**
     * 分页查询视频列表
     * page当前显示的页数，pageSize为每页记录数
     */
    public PagedResult getAllVideos(Videos videos, Integer isSaveRecord, Integer page, Integer pageSize);

    /**
     * 获取热搜词列表
     * @return
     */
    public List<String> getHotWords();


    /**
     * 用户点赞视频
     */
    public void userLikeVideo(String userId, String videoId, String VideoCreaterId);

    /**
     * 用户取消点赞
     */
    public void userUnlikeVideo(String userId, String videoId, String VideoCreaterId);

}
