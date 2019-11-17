package org.video.service;

import org.video.pojo.Bgm;

import java.util.List;

/**
 * @author gutongxue
 * @date 2019/11/17 20:00
 **/
public interface BgmService {

    /**
     * 查询背景音乐列表
     */
    public List<Bgm> queryBgmList();
}
