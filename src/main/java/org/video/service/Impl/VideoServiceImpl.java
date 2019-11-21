package org.video.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.video.common.org.n3r.idworker.Sid;
import org.video.common.utils.PagedResult;
import org.video.mapper.SearchRecordsMapper;
import org.video.mapper.VideosMapper;
import org.video.mapper.VideosMapperCustom;
import org.video.pojo.SearchRecords;
import org.video.pojo.Videos;
import org.video.pojo.vo.VideosVO;
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

    @Autowired
    private VideosMapperCustom videosMapperCustom;

    @Autowired
    private SearchRecordsMapper searchRecordsMapper;

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public String saveVideo(Videos videos) {
        //生成唯一主键
        String id = sid.nextShort();
        videos.setId(id);
        videosMapper.insertSelective(videos);
        return id;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateVideo(String videoId, String coverPath) {
        Videos videos = new Videos();
        videos.setId(videoId);
        videos.setCoverPath(coverPath);
        videosMapper.updateByPrimaryKeySelective(videos);

    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public PagedResult getAllVideos(Videos videos, Integer isSaveRecord, Integer page, Integer pageSize) {

        //保存热搜词
        String desc = videos.getVideoDesc();
        if (isSaveRecord != null && isSaveRecord == 1) {
            SearchRecords searchRecords = new SearchRecords();
            String recordId = sid.nextShort();
            searchRecords.setId(recordId);
            searchRecords.setContent(desc);
            searchRecordsMapper.insert(searchRecords);
        }

        //需要进行分页
        PageHelper.startPage(page, pageSize);

        //进行全表查询
        List<VideosVO> list = videosMapperCustom.queryAllVideos(desc);

        //页面信息
        PageInfo<VideosVO> pageInfo = new PageInfo<VideosVO>(list);

        PagedResult pagedResult = new PagedResult();
        pagedResult.setPage(page);
        pagedResult.setTotal(pageInfo.getPages());
        pagedResult.setRows(list);
        pagedResult.setRecords(pageInfo.getTotal());

        return pagedResult;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<String> getHotWords() {
        return searchRecordsMapper.getHotwords();
    }
}
