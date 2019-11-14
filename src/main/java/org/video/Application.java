package org.video;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author gutongxue
 * @date 2019/11/13 13:42
 **/
@SpringBootApplication
@MapperScan(basePackages = "org.video.mapper")
@ComponentScan(basePackages = "org.video.common.org.n3r.idworker")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
