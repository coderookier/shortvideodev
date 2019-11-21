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
	public List<VideosVO> queryAllVideos();
}