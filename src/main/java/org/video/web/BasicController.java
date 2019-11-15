package org.video.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.video.common.utils.RedisOperator;

/**
 * @author gutongxue
 * @date 2019/11/15 16:51
 **/
@RestController
public class BasicController {

    //注入操作redis数据库的类
    @Autowired
    public RedisOperator redisOperator;

    public static final String USER_REDIS_SESSION = "user-redis-session";
}
