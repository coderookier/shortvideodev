package org.video.service;

import org.video.pojo.Users;

/**
 * @author gutongxue
 * @date 2019/11/13 15:36
 **/
public interface UserService {
    /**
     * 判断用户名是否存在
     * @param username
     * @return
     */
    public boolean queryUsernameIsExist(String username);

    /**
     * 存储用户注册信息信息
     * @param users
     */
    public void saveUser(Users users);

    /**
     * 用户登录，根据用户名和密码查询用户
     */
    public Users queryUserForLogin(String username, String password);

    /**
     * 用户修改信息
     */
    public void updateUserInfo(Users users);

    /**
     * 用户信息查询
     */
    public Users queryUserInfo(String userId);
}
