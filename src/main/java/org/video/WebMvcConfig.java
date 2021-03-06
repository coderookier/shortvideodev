package org.video;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.video.web.interceptor.MiniInterceptor;

/**
 * @author gutongxue
 * @date 2019/11/17 10:22
 **/
@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/META-INF/resources/")
                .addResourceLocations("file:D:/wxxcx/userfiles/");
    }

    /**
     * 注册拦截器到spring中
     * @return
     */
    @Bean
    public MiniInterceptor miniInterceptor() {
        return new MiniInterceptor();
    }

    /**
     * 注册zookeeper客户端到spring中
     * @return
     */
    @Bean(initMethod = "init")
    public ZKCuratorClient zkCuratorClient() {
        return new ZKCuratorClient();
    }


    /**
     * 将自定义的拦截器注册到拦截器中心
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //通过registry进行注册
        registry.addInterceptor(miniInterceptor()).addPathPatterns("/user/**")
                .addPathPatterns("/video/upload", "/video/uploadCover",
                        "/video/userLike", "/video/userUnlike", "/video/userUnlike")
                                                  .addPathPatterns("/bgm/**")
                                                  .excludePathPatterns("/user/queryPublisher");
        super.addInterceptors(registry);
    }
}
