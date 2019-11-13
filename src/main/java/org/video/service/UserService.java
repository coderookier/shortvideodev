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
}
