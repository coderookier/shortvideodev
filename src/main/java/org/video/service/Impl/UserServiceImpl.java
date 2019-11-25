package org.video.service.Impl;

import com.mysql.jdbc.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.video.common.org.n3r.idworker.Sid;
import org.video.mapper.UsersFansMapper;
import org.video.mapper.UsersLikeVideosMapper;
import org.video.mapper.UsersMapper;
import org.video.mapper.UsersReportMapper;
import org.video.pojo.Users;
import org.video.pojo.UsersFans;
import org.video.pojo.UsersLikeVideos;
import org.video.pojo.UsersReport;
import org.video.service.UserService;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

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

    @Autowired
    UsersLikeVideosMapper usersLikeVideosMapper;

    @Autowired
    UsersFansMapper usersFansMapper;

    @Autowired
    UsersReportMapper usersReportMapper;

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

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public boolean isUserLikeVideo(String userId, String videoId) {

        if (StringUtils.isNullOrEmpty(userId) || StringUtils.isNullOrEmpty(videoId)) {
            return false;
        }

        Example example = new Example(UsersLikeVideos.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId", userId);
        criteria.andEqualTo("videoId", videoId);
        List<UsersLikeVideos> list = usersLikeVideosMapper.selectByExample(example);
        if (list != null && list.size() > 0) {
            return true;
        }
        return false;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveUserFanRelation(String userId, String fanId) {
        //1. 保存关注关系到users_fans表中
        String reId = sid.nextShort();
        UsersFans usersFans = new UsersFans();
        usersFans.setId(reId);
        usersFans.setUserId(userId);
        usersFans.setFanId(fanId);
        usersFansMapper.insert(usersFans);

        //2. 在用户表中更新关注与粉丝数量
        usersMapper.addFansCount(userId);
        usersMapper.addFollersCount(fanId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void deleteUserFanRelation(String userId, String fanId) {
        //1. 删除users_fans表中对应数据
        Example example = new Example(UsersFans.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId", userId);
        criteria.andEqualTo("fanId", fanId);
        usersFansMapper.deleteByExample(example);

        //2. 在用户表中更新关注与粉丝数量
        usersMapper.reduceFansCount(userId);
        usersMapper.reduceFollersCount(fanId);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public boolean queryIfFollow(String userId, String fanId) {
        Example example = new Example(UsersFans.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId", userId);
        criteria.andEqualTo("fanId", fanId);
        List<UsersFans> list =  usersFansMapper.selectByExample(example);
        if (list != null && !list.isEmpty() && list.size() > 0) {
            return true;
        }
        return false;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void reportUser(UsersReport usersReport) {
        String urId = sid.nextShort();
        usersReport.setId(urId);
        usersReport.setCreateDate(new Date());

        usersReportMapper.insert(usersReport);
    }
}
