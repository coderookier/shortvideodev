package org.video.web;

import com.mysql.jdbc.StringUtils;
import io.swagger.annotations.*;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.video.common.enums.VideoStatusEnum;
import org.video.common.utils.FetchVideoCover;
import org.video.common.utils.IMoocJSONResult;
import org.video.common.utils.MergeVideoMp3;
import org.video.pojo.Bgm;
import org.video.pojo.Videos;
import org.video.service.BgmService;
import org.video.service.VideoService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

/**
 * @author gutongxue
 * @date 2019/11/17 19:59
 **/

@RestController
@Api(value = "视频相关业务的接口", tags = {"视频相关业务的controller"})
@RequestMapping("/video")
public class VideoController extends BasicController{

    @Autowired
    private BgmService bgmService;

    @Autowired
    private VideoService videoService;


    @ApiOperation(value = "上传视频", notes = "上传视频的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户ID", required = true,
                dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "bgmId", value = "背景音乐ID", required = false,
                    dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "videoSeconds", value = "视频播放长度", required = true,
                    dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "videoWidth", value = "视频宽度", required = true,
                    dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "videoHeight", value = "视频高度", required = true,
                    dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "desc", value = "视频描述", required = false,
                    dataType = "String", paramType = "form")
    })
    @PostMapping(value="/upload", headers="content-type=multipart/form-data")
    public IMoocJSONResult upload(String userId, String bgmId, double videoSeconds,
                                  int videoWidth, int videoHeight, String desc,
                                  @RequestParam("file")MultipartFile file) throws Exception {

        if (StringUtils.isEmptyOrWhitespaceOnly(userId)) {
            return IMoocJSONResult.errorMsg("用户ID不能为空");
        }

        //文件保存的命名空间
        //String fileSpace = "D:/wxxcx/userfiles";
        //视频保存到数据库中的相对路径
        String uploadPathDB = "/" + userId + "/video";
        //封面保存到数据库的相对路径
        String coverPathDB = "/" + userId + "/video";
        String finalVideoPath = "";
        FileOutputStream fileOutputStream = null;
        InputStream inputStream;
        try {
            if (file != null) {
                //视频名称
                String fileName = file.getOriginalFilename();
                //取视频名称前缀作为图片名称前缀
                String fileNamePrefix = fileName.split("\\.")[0];
                System.out.println(fileName);
                if (!StringUtils.isEmptyOrWhitespaceOnly(fileName)) {
                    //文件上传的最终保存路径
                    finalVideoPath = FILE_SPACE + uploadPathDB + "/" + fileName;

                    //数据库存储路径
                    uploadPathDB += ("/" + fileName);
                    coverPathDB = coverPathDB + "/" + fileNamePrefix + ".jpg";

                    File outFile = new File(finalVideoPath);

                    if (outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()) {
                        // 创建父文件夹
                        outFile.getParentFile().mkdirs();
                    }

                    fileOutputStream = new FileOutputStream(outFile);
                    inputStream = file.getInputStream();
                    IOUtils.copy(inputStream, fileOutputStream);
                }
            } else {
                return IMoocJSONResult.errorMsg("上传出错");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return IMoocJSONResult.errorMsg("上传出错");
        } finally {
            if (fileOutputStream != null) {
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        }

        //判断bgmId是否为空，如果不为空，则查询bgm信息，并合并视频与bgm生成新的视频
        if (!StringUtils.isEmptyOrWhitespaceOnly(bgmId)) {
            Bgm bgm = bgmService.queryBgmById(bgmId);
            //bgm所在的全路径
            String mp3InputPath = FILE_SPACE + bgm.getPath();

            String videoInputPath = finalVideoPath;

            MergeVideoMp3 mergeVideoMp3 = new MergeVideoMp3(FFMPEG_EXE);
            //生成合并后的文件名
            String videoOutputName = UUID.randomUUID().toString() + ".mp4";
            uploadPathDB = "/" + userId + "/video" + "/" + videoOutputName;
            //合并后视频最终保存的路径
            finalVideoPath = FILE_SPACE + uploadPathDB;

            mergeVideoMp3.convertor(videoInputPath, mp3InputPath, videoSeconds, finalVideoPath);
        }
        System.out.println("uploadPathDB:" + uploadPathDB);
        System.out.println("finalVideoPath:" + finalVideoPath);
        Videos videos = null;

        //对视频进行截图，并存储在文件夹中
        FetchVideoCover fetchVideoCover = new FetchVideoCover(FFMPEG_EXE);
        fetchVideoCover.getCover(finalVideoPath, FILE_SPACE + coverPathDB);

        //保存视频到数据库
        videos = new Videos();
        videos.setAudioId(bgmId);
        videos.setUserId(userId);
        videos.setVideoSeconds((float)videoSeconds);
        System.out.println(videoSeconds);
        System.out.println("videoHeight: " + videoHeight);
        videos.setVideoHeight(videoHeight);
        videos.setVideoWidth(videoWidth);
        videos.setVideoDesc(desc);
        videos.setVideoPath(uploadPathDB);
        videos.setCoverPath(coverPathDB);
        videos.setStatus(VideoStatusEnum.SUCCESS.value);
        videos.setCreateTime(new Date());

        String videoId = videoService.saveVideo(videos);

        return IMoocJSONResult.ok(videoId);
    }


    @ApiOperation(value = "上传视频封面", notes = "上传视频封面的接口")

    @ApiImplicitParams({
            @ApiImplicitParam(name = "videoId", value = "视频主键ID", required = true,
                    dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "userId", value = "用户ID", required = true,
                    dataType = "String", paramType = "form"),

    })

    /**
     * 这部分由于手机端无效，所以上传封面集成到上一部分内容
     */
    @PostMapping(value="/uploadCover", headers="content-type=multipart/form-data")
    public IMoocJSONResult uploadCover(String videoId, String userId, @RequestParam("file")MultipartFile file) throws Exception {

        if (StringUtils.isEmptyOrWhitespaceOnly(videoId) || StringUtils.isEmptyOrWhitespaceOnly(userId)) {
            return IMoocJSONResult.errorMsg("视频ID和用户ID不能为空");
        }

        //文件保存的命名空间
        //String fileSpace = "D:/wxxcx/userfiles";
        //视频保存到数据库中的相对路径
        String uploadPathDB = "/" + userId + "/video";
        String finalCoverPath = "";
        FileOutputStream fileOutputStream = null;
        InputStream inputStream;
        try {
            if (file != null) {
                String fileName = file.getOriginalFilename();
                System.out.println(fileName);
                if (!StringUtils.isEmptyOrWhitespaceOnly(fileName)) {
                    //文件上传的最终保存路径
                    finalCoverPath = FILE_SPACE + uploadPathDB + "/" + fileName;

                    //数据库存储路径
                    uploadPathDB += ("/" + fileName);

                    File outFile = new File(finalCoverPath);

                    if (outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()) {
                        // 创建父文件夹
                        outFile.getParentFile().mkdirs();
                    }

                    fileOutputStream = new FileOutputStream(outFile);
                    inputStream = file.getInputStream();
                    IOUtils.copy(inputStream, fileOutputStream);
                }
            } else {
                return IMoocJSONResult.errorMsg("上传出错");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return IMoocJSONResult.errorMsg("上传出错");
        } finally {
            if (fileOutputStream != null) {
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        }

        videoService.updateVideo(videoId, uploadPathDB);

        return IMoocJSONResult.ok();
    }
}
