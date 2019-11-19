package org.video.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.video.common.org.n3r.idworker.Sid;
import org.video.mapper.VideosMapper;
import org.video.pojo.Videos;
import org.video.service.VideoService;

import java.util.List;

/**
 * @author gutongxue
 * @date 2019/11/17 20:00
 **/
@Service
public class VideoServiceImpl implements VideoService {

    @Autowired
    private Sid sid;

    @Autowired
    private VideosMapper videosMapper;

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveVideo(Videos videos) {
        //生成唯一主键
        String id = sid.nextShort();
        videos.setId(id);
        videosMapper.insertSelective(videos);
    }
}
