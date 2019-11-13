package org.video.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author gutongxue
 * @date 2019/11/13 14:28
 **/
@RestController
public class HelloController {
    @RequestMapping("/hello")
    public String Hello() {
        return "Hello SpringBoot~";
    }
}
