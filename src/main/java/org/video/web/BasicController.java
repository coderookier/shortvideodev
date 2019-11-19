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

    //文件保存的命名空间
    public static final String FILE_SPACE = "D:/wxxcx/userfiles";

    //ffmpeg所在目录
    public static final String FFMPEG_EXE = "D:\\ffmpeg\\bin\\ffmpeg.exe";
}
