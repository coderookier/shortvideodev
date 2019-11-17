package org.video.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.video.common.org.n3r.idworker.Sid;
import org.video.mapper.UsersMapper;
import org.video.pojo.Users;
import org.video.service.UserService;
import tk.mybatis.mapper.entity.Example;

/**
 * @author gutongxue
 * @date 2019/11/13 15:41
 **/
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private Sid sid;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public boolean queryUsernameIsExist(String username) {

        Users users = new Users();
        users.setUsername(username);
        Users result = usersMapper.selectOne(users);
        return result == null ? false : true;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveUser(Users users) {
        String userId = sid.nextShort();
        users.setId(userId);
        usersMapper.insert(users);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users queryUserForLogin(String username, String password) {
        Users users = new Users();
        users.setUsername(username);
        users.setPassword(password);
        return usersMapper.selectOne(users);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public void updateUserInfo(Users users) {
        Example userExample = new Example(Users.class);
        Example.Criteria criteria = userExample.createCriteria();
        criteria.andEqualTo("id", users.getId());
        usersMapper.updateByExampleSelective(users, userExample);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users queryUserInfo(String userId) {
        Example userExample = new Example(Users.class);
        Example.Criteria criteria = userExample.createCriteria();
        criteria.andEqualTo("id", userId);
        Users users = usersMapper.selectOneByExample(userExample);
        return users;
    }
}
