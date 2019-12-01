package org.video;

import com.mysql.jdbc.StringUtils;
import org.apache.commons.io.FileUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.video.common.enums.BGMOperatorTypeEnum;
import org.video.common.utils.JsonUtils;
import org.video.pojo.Bgm;
import org.video.service.BgmService;

import java.io.File;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @author gutongxue
 * @date 2019/12/1 14:18
 **/

@Component
public class ZKCuratorClient {

    @Autowired
    private BgmService bgmService;

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

                    //1. 从数据库中查询Bgm对象，获取路径path
                    //1.1获取节点路径
                    String path = pathChildrenCacheEvent.getData().getPath();
                    //1.2获取节点类型
                    String operatorObjStr = new String(pathChildrenCacheEvent.getData().getData(),"gbk");
                    Map<String, String> map = JsonUtils.jsonToPojo(operatorObjStr, HashMap.class);

                    String operatorType = map.get("operType");
                    String bgmPath = map.get("path");

                    //1.3切割路径得到bgmId
//                    String[] arr = path.split("/");
//                    String bgmId = arr[arr.length - 1];

                    //1.4根据bgmId得到bgm对象
//                    Bgm bgm = bgmService.queryBgmById(bgmId);
//                    if (bgm == null) {
//                        return;
//                    }
                    //bgm所在的相对路径
                    String songPath = bgmPath;

                    //2. 定义保存到本地的bgm路径
                    String filePath = "D:\\wxxcx\\userfiles" + songPath;

                    //3. 定义下载的路径（播放url）
                    String[] arrPath = songPath.split("\\\\");
                    String finalPath = "";
                    //3.1 处理url的斜杠和编码
                    for (int i = 0; i < arrPath.length; i++) {
                        if (!StringUtils.isNullOrEmpty(arrPath[i])) {
                            finalPath += "/";
                            finalPath += URLEncoder.encode(arrPath[i], "UTF-8");
                        }
                    }
                    String bgmUrl = "http://121.248.54.185:8080/mvc" + finalPath;

                    //添加bgm后下载
                    if (operatorType.equals(BGMOperatorTypeEnum.ADD.type)) {
                        URL url = new URL(bgmUrl);
                        File file = new File(filePath);
                        FileUtils.copyURLToFile(url, file);
                        //将zookeeper服务器上节点中数据删除
                        client.delete().forPath(path);
                        //删除bgm
                    } else if (operatorType.equals(BGMOperatorTypeEnum.DELETE.type)) {
                        File file = new File(filePath);
                        FileUtils.forceDelete(file);
                        client.delete().forPath(path);
                    }

                }
            }
        });
    }
}
