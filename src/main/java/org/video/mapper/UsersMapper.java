package org.video.mapper;


import org.video.common.utils.MyMapper;
import org.video.pojo.Users;

public interface UsersMapper extends MyMapper<Users> {
	
	/**
	 * 用户受喜欢数累加
	 */
	public void addReceiveLikeCount(String userId);
	
	/**
	 * 用户受喜欢数累减
	 */
	public void reduceReceiveLikeCount(String userId);

}