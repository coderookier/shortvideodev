package org.video.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.video.common.utils.MyMapper;
import org.video.pojo.Videos;
import org.video.pojo.vo.VideosVO;


public interface VideosMapperCustom extends MyMapper<Videos> {
	
	/**
	 * @Description: 关联查询
	 */
	public List<VideosVO> queryAllVideos(@Param("videoDesc") String videoDesc);


	/**
	 * 对视频获得的点赞数进行累加
	 */
	public void addVideoLikeCount(String videoId);

	/**
	 * 对视频获得的点赞数减一
	 */
	public void reduceVideoLikeCount(String videoId);
}