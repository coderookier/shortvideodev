package org.video.web.interceptor;

import com.mysql.jdbc.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.video.common.utils.IMoocJSONResult;
import org.video.common.utils.JsonUtils;
import org.video.common.utils.RedisOperator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 * @author gutongxue
 * @date 2019/11/22 13:39
 * 拦截器
 **/
public class MiniInterceptor implements HandlerInterceptor {

    @Autowired
    public RedisOperator redisOperator;

    public static final String USER_REDIS_SESSION = "user-redis-session";

    /**
     * 拦截请求，在controller调用之前
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        /**
         * 返回false代表请求被拦截
         * 返回true表示请求放行
         */
        String userId = request.getHeader("headeruserId");
        //登录成功后生成的唯一token
        String userToken = request.getHeader("headerUserToken");
        if (!StringUtils.isNullOrEmpty(userId) && !StringUtils.isNullOrEmpty(userToken)) {
            String uniqueToken = redisOperator.get(USER_REDIS_SESSION + ":" + userId);
            if (StringUtils.isNullOrEmpty(uniqueToken)) {
                System.out.println("会话已过期，请登录");
                //将信息返回到前端
                returnErrorResponse(response, IMoocJSONResult.errorTokenMsg("请登录..."));
                return false;
            } else {
                if (!uniqueToken.equals(userToken)) {
                    System.out.println("账号在别的地方登录");
                    returnErrorResponse(response, IMoocJSONResult.errorTokenMsg("其他位置登录..."));
                    return false;
                }
            }
        } else {
            System.out.println("请登录...");
            returnErrorResponse(response, IMoocJSONResult.errorTokenMsg("请登录..."));
            return false;
        }
        return true;
    }

    /**
     * 将拦截信息以json形式返回到前端进行显示
     */
    public void returnErrorResponse(HttpServletResponse response, IMoocJSONResult result) throws IOException, UnsupportedEncodingException {
        OutputStream outputStream = null;
        try {
            response.setCharacterEncoding("utf-8");
            response.setContentType("text/json");
            outputStream = response.getOutputStream();
            outputStream.write(JsonUtils.objectToJson(result).getBytes("utf-8"));
            outputStream.flush();
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }

    /**
     * 拦截请求在controller之后，渲染视图之前
     */
    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    /**
     * 拦截请求在controller之后，渲染视图之后
     */
    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
