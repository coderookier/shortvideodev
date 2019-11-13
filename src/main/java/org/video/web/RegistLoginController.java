package org.video.web;

import com.mysql.jdbc.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.video.common.utils.IMoocJSONResult;
import org.video.pojo.Users;

/**
 * @author gutongxue
 * @date 2019/11/13 14:28
 **/
@RestController
public class RegistLoginController {

    @PostMapping("/regist")
    public IMoocJSONResult regist(@RequestBody Users user)
    {
        /**
         * 1. 判断用户名和密码必须不为空
         * 2. 判断用户名是否存在
         * 3. 保存用户注册信息
         */
        if (StringUtils.isEmptyOrWhitespaceOnly(user.getUsername())
                || StringUtils.isEmptyOrWhitespaceOnly(user.getPassword())) {
            return IMoocJSONResult.errorMsg("用户名和密码不能为空");
        }

        return IMoocJSONResult.ok();
    }
}
