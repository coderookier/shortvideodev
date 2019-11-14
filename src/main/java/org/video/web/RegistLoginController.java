package org.video.web;

import com.mysql.jdbc.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.video.common.utils.IMoocJSONResult;
import org.video.common.utils.MD5Utils;
import org.video.pojo.Users;
import org.video.service.UserService;

/**
 * @author gutongxue
 * @date 2019/11/13 14:28
 **/
@RestController
@Api(value = "用户注册登录接口", tags = {"注册和登录的controller"})
public class RegistLoginController {

    @Autowired
    private UserService userService;

    @ApiOperation(value = "用户登录", notes = "用户注册的接口")
    @PostMapping("/regist")
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

        return IMoocJSONResult.ok();
    }
}
