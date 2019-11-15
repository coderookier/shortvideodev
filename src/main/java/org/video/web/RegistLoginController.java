package org.video.web;

import com.mysql.jdbc.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.video.common.utils.IMoocJSONResult;
import org.video.common.utils.MD5Utils;
import org.video.pojo.Users;
import org.video.pojo.vo.UsersVO;
import org.video.service.UserService;

import java.util.UUID;

/**
 * @author gutongxue
 * @date 2019/11/13 14:28
 **/
@RestController
@Api(value = "用户注册登录注销接口", tags = {"注册和登录和注销的controller"})
public class RegistLoginController extends BasicController {

    @Autowired
    private UserService userService;

    @ApiOperation(value = "用户注册", notes = "用户注册的接口")
    @PostMapping("/regist")
    /**
     * users为前端传入的json对象
     */
    public IMoocJSONResult regist(@RequestBody Users users) throws Exception {
        /**
         * 1. 判断用户名和密码必须不为空
         * 2. 判断用户名是否存在
         * 3. 保存用户注册信息
         */

        // 1.判断用户名和密码必须不为空
        if (StringUtils.isEmptyOrWhitespaceOnly(users.getUsername())
                || StringUtils.isEmptyOrWhitespaceOnly(users.getPassword())) {
            return IMoocJSONResult.errorMsg("用户名和密码不能为空");
        }

        // 2.判断用户名是否存在
        boolean usernameIsExist = userService.queryUsernameIsExist(users.getUsername());

        // 3.保存用户注册信息
        if (!usernameIsExist) {
            users.setNickname(users.getUsername());
            users.setPassword(MD5Utils.getMD5Str(users.getPassword()));
            users.setFansCounts(0);
            users.setReceiveLikeCounts(0);
            users.setFollowCounts(0);
            userService.saveUser(users);
        }
        else {
            return IMoocJSONResult.errorMsg("用户名已存在，请换一个试试");
        }
        UsersVO usersVO = setUserRedisSessionToken(users);
        return IMoocJSONResult.ok(usersVO);
    }

    /**
     * 建立redis-session
     * @param users
     * @return
     */
    public UsersVO setUserRedisSessionToken(Users users) {
        //生成用户唯一ID
        String uniqueToken = UUID.randomUUID().toString();
        redisOperator.set(USER_REDIS_SESSION + ":" + users.getId(), uniqueToken, 1000 * 60 * 30);
        //生成此对象包含唯一token，将该对象返回到前端
        UsersVO usersVO = new UsersVO();
        //将users对象属性传入usersvo中
        BeanUtils.copyProperties(users, usersVO);
        //将唯一token赋值到usersvo中
        usersVO.setUserToken(uniqueToken);
        return usersVO;
    }

    @ApiOperation(value = "用户登录", notes = "用户登录的接口")
    @PostMapping("/login")
    public IMoocJSONResult login(@RequestBody Users users) throws Exception {
        String username = users.getUsername();
        String password = users.getPassword();

        //1.判断用户名和密码不能为空
        if (StringUtils.isEmptyOrWhitespaceOnly(username) || StringUtils.isEmptyOrWhitespaceOnly(password)) {
            return IMoocJSONResult.errorMsg("用户名或密码不能为空");
        }

        //2.判断用户是否存在
        Users userResult = userService.queryUserForLogin(username, MD5Utils.getMD5Str(password));

        //3.返回用户信息
        if (userResult != null) {
            UsersVO usersVO = setUserRedisSessionToken(userResult);
            return IMoocJSONResult.ok(usersVO);
        }
        else {
            return IMoocJSONResult.errorMsg("用户名或密码不正确，请重试");
        }
    }

    @ApiOperation(value = "用户注销", notes = "用户注销的接口")
    @ApiImplicitParam(name = "userId", value = "用户ID", required = true,
            dataType = "String", paramType = "query")
    @PostMapping("/logout")
    public IMoocJSONResult logout(String userId) throws Exception {
        redisOperator.del(USER_REDIS_SESSION + ":" + userId);
        return IMoocJSONResult.ok();
    }
}
