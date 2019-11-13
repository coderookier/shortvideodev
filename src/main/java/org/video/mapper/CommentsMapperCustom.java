package org.video.mapper;


import java.util.List;
import org.video.common.utils.MyMapper;
import org.video.pojo.Comments;
import org.video.pojo.vo.CommentsVO;

public interface CommentsMapperCustom extends MyMapper<Comments> {
	
	public List<CommentsVO> queryComments(String videoId);
}