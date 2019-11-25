package org.video.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.video.common.org.n3r.idworker.Sid;
import org.video.common.utils.PagedResult;
import org.video.common.utils.TimeAgoUtils;
import org.video.mapper.*;
import org.video.pojo.Comments;
import org.video.pojo.SearchRecords;
import org.video.pojo.UsersLikeVideos;
import org.video.pojo.Videos;
import org.video.pojo.vo.CommentsVO;
import org.video.pojo.vo.VideosVO;
import org.video.service.VideoService;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
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

    @Autowired
    private UsersLikeVideosMapper usersLikeVideosMapper;

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private CommentsMapper commentsMapper;

    @Autowired
    private CommentsMapperCustom commentsMapperCustom;

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
        String userId = videos.getUserId();
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
        List<VideosVO> list = videosMapperCustom.queryAllVideos(desc, userId);

        //页面信息
        PageInfo<VideosVO> pageInfo = new PageInfo<>(list);

        PagedResult pagedResult = new PagedResult();
        pagedResult.setPage(page);
        pagedResult.setTotal(pageInfo.getPages());
        pagedResult.setRows(list);
        pagedResult.setRecords(pageInfo.getTotal());

        return pagedResult;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedResult queryMyLikeVideos(String userId, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        List<VideosVO> list = videosMapperCustom.queryMylikeVideos(userId);

        PageInfo<VideosVO> pageList = new PageInfo<>(list);

        PagedResult pagedResult = new PagedResult();
        pagedResult.setTotal(pageList.getPages());
        pagedResult.setRows(list);
        pagedResult.setPage(page);
        pagedResult.setRecords(pageList.getTotal());

        return pagedResult;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedResult queryMyFollowVideos(String userId, Integer page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        List<VideosVO> list = videosMapperCustom.queryMyFollowVideos(userId);

        PageInfo<VideosVO> pageList = new PageInfo<>(list);

        PagedResult pagedResult = new PagedResult();
        pagedResult.setTotal(pageList.getPages());
        pagedResult.setRows(list);
        pagedResult.setPage(page);
        pagedResult.setRecords(pageList.getTotal());

        return pagedResult;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<String> getHotWords() {
        return searchRecordsMapper.getHotwords();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void userLikeVideo(String userId, String videoId, String VideoCreaterId) {
        //1. 保存用户和视频的点赞关联关系表
        String likeId = sid.nextShort();
        UsersLikeVideos usersLikeVideos = new UsersLikeVideos();
        usersLikeVideos.setId(likeId);
        usersLikeVideos.setUserId(userId);
        usersLikeVideos.setVideoId(videoId);
        usersLikeVideosMapper.insert(usersLikeVideos);

        //2. 视频喜欢数量累加
        videosMapperCustom.addVideoLikeCount(videoId);

        //3. 用户受喜欢数量累加
        usersMapper.addReceiveLikeCount(VideoCreaterId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void userUnlikeVideo(String userId, String videoId, String VideoCreaterId) {
        //1. 删除用户和视频的点赞关联关系表
        Example example = new Example(UsersLikeVideos.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId", userId);
        criteria.andEqualTo("videoId", videoId);
        usersLikeVideosMapper.deleteByExample(example);

        //2. 视频喜欢数量累减
        videosMapperCustom.reduceVideoLikeCount(videoId);

        //3. 用户受喜欢数量减
        usersMapper.reduceReceiveLikeCount(VideoCreaterId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveComment(Comments comments) {
        String id = sid.nextShort();
        comments.setId(id);
        comments.setCreateTime(new Date());
        commentsMapper.insert(comments);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedResult getAllComments(String videoId, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        List<CommentsVO> list = commentsMapperCustom.queryComments(videoId);
        for (CommentsVO c : list) {
            String timeAgo = TimeAgoUtils.format(c.getCreateTime());
            c.setTimeAgoStr(timeAgo);
        }
        PageInfo<CommentsVO> pageList = new PageInfo<>(list);
        PagedResult grid = new PagedResult();
        grid.setTotal(pageList.getPages());
        grid.setRows(list);
        grid.setPage(page);
        grid.setRecords(pageList.getTotal());
        return grid;
    }
}
