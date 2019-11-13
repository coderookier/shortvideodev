package org.video.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.video.mapper.UsersMapper;
import org.video.pojo.Users;
import org.video.service.UserService;

/**
 * @author gutongxue
 * @date 2019/11/13 15:41
 **/
public class UserServiceImpl implements UserService {

    @Autowired
    private UsersMapper usersMapper;

    @Override
    public boolean queryUsernameIsExist(String username) {

        Users users = new Users();
        users.setUsername(username);
        Users result = usersMapper.selectOne(users);
        return result == null ? false : true;
    }

    @Override
    public void saveUser(Users users) {

    }
}
