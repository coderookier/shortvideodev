package org.video;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author gutongxue
 * @date 2019/12/1 22:02
 **/

@Configuration
@EnableConfigurationProperties(ResourceConfig.class)
@ConfigurationProperties(prefix = "org.video")
@PropertySource("classpath:resource.properties")
public class ResourceConfig {
    private String zookeeperServer;
    private String bgmServer;
    private String fileSpace;

    public String getZookeeperServer() {
        return zookeeperServer;
    }

    public void setZookeeperServer(String zookeeperServer) {
        this.zookeeperServer = zookeeperServer;
    }

    public String getBgmServer() {
        return bgmServer;
    }

    public void setBgmServer(String bgmServer) {
        this.bgmServer = bgmServer;
    }

    public String getFileSpace() {
        return fileSpace;
    }

    public void setFileSpace(String fileSpace) {
        this.fileSpace = fileSpace;
    }
}
