package org.video;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author gutongxue
 * @date 2019/12/1 14:18
 **/

@Component
public class ZKCuratorClient {

    //zookeeper客户端
    private CuratorFramework client = null;

    final static Logger logger = LoggerFactory.getLogger(ZKCuratorClient.class);

    public static final String ZOOKEEPER_SERVER = "121.248.55.164:2181";

    public void init() {
        if (client != null) {
            return;
        }
        //重连策略
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(10000, 5);
        //创建zk客户端
        client = CuratorFrameworkFactory.builder().connectString(ZOOKEEPER_SERVER)
                .sessionTimeoutMs(100000).retryPolicy(retryPolicy).namespace("admin").build();
        //启动客户端
        client.start();

        try {
//            String testNodeData = new String(client.getData().forPath("/bgm/1911307GKNW2HYRP"));
//            logger.info("测试节点的数据为：{}",testNodeData);
            addChildWatch("/bgm");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 对nodePath下所有子节点进行监听
     * @param nodePath
     */
    public void addChildWatch(String nodePath) throws Exception{
        //监听缓存
        final PathChildrenCache cache = new PathChildrenCache(client, nodePath, true);
        cache.start();
        //获取监听器列表
        cache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
                if (pathChildrenCacheEvent.getType().equals(PathChildrenCacheEvent.Type.CHILD_ADDED)) {
                    logger.info("监听到事件CHILD_ADDED");
                }
            }
        });
    }
}
