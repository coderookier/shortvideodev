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

	/**
	 * 增加粉丝数量
	 */
	public void addFansCount(String userId);

	/**
	 * 减少粉丝数量
	 */
	public void reduceFansCount(String userId);

	/**
	 * 增加关注数量
	 */
	public void addFollersCount(String userId);

	/**
	 * 减少关注数量
	 */
	public void reduceFollersCount(String userId);

}