package org.video;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author gutongxue
 * @date 2019/11/13 13:42
 **/

/**
 * 此处不能使用注解@Component(...)，否则会使得swagger2页面不显示
 */
@SpringBootApplication
@MapperScan(basePackages = "org.video.mapper")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
