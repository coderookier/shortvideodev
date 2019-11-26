package org.video.web;

import com.mysql.jdbc.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.video.common.utils.IMoocJSONResult;
import org.video.pojo.Users;
import org.video.pojo.UsersReport;
import org.video.pojo.vo.PublisherVideo;
import org.video.pojo.vo.UsersVO;
import org.video.service.UserService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;


/**
 * @author gutongxue
 * @date 2019/11/13 14:28
 * 处理用户相关业务，如上传头像等
 **/
@RestController
@Api(value = "用户相关业务接口", tags = {"用户相关业务的controller"})
@RequestMapping("/user")
public class UserController extends BasicController {

    @Autowired
    private UserService userService;

    @ApiOperation(value = "用户上传头像", notes = "用户上传头像的接口")
    @ApiImplicitParam(name = "userId", value = "用户ID", required = true,
            dataType = "String", paramType = "query")
    @PostMapping("/uploadFace")
    public IMoocJSONResult uploadFace(String userId, @RequestParam("file") MultipartFile[] files) throws Exception {

        if (StringUtils.isEmptyOrWhitespaceOnly(userId)) {
            return IMoocJSONResult.errorMsg("用户ID不能为空");
        }

        //文件保存的命名空间
        String fileSpace = "D:/wxxcx/userfiles";
        //保存到数据库中的相对路径
        String uploadPathDB = "/" + userId + "/face";
        FileOutputStream fileOutputStream = null;
        InputStream inputStream;
        try {
            if (files != null && files.length > 0) {
                String fileName = files[0].getOriginalFilename();
                System.out.println(fileName);
                //这个忘记取非让我找了一下午错误，哭了
                if (!StringUtils.isEmptyOrWhitespaceOnly(fileName)) {
                    //文件上传的最终保存路径
                    String finalFacePath = fileSpace + uploadPathDB + "/" + fileName;

                    //数据库存储路径
                    uploadPathDB += ("/" + fileName);

                    File outFile = new File(finalFacePath);

                    if (outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()) {
                        // 创建父文件夹
                        outFile.getParentFile().mkdirs();
                    }

                    fileOutputStream = new FileOutputStream(outFile);
                    inputStream = files[0].getInputStream();
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

        Users users = new Users();
        users.setId(userId);
        users.setFaceImage(uploadPathDB);
        userService.updateUserInfo(users);

        return IMoocJSONResult.ok(uploadPathDB);
    }


    @ApiOperation(value = "用户信息查询", notes = "用户信息查询的接口")
    @ApiImplicitParam(name = "userId", value = "用户ID", required = true, dataType = "String", paramType = "query")
    @PostMapping("/query")
    public IMoocJSONResult query(String userId, String fanId) throws Exception {
        if (StringUtils.isEmptyOrWhitespaceOnly(userId)) {
            return IMoocJSONResult.errorMsg("用户ID不能为空");
        }
        Users userInfo = userService.queryUserInfo(userId);
        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(userInfo, usersVO);

        usersVO.setFollow(userService.queryIfFollow(userId, fanId));
        return IMoocJSONResult.ok(usersVO);
    }

    @ApiOperation(value = "视频详情页的发布者信息与点赞查询", notes = "视频详情页的发布者信息与点赞查询接口")
    @PostMapping("/queryPublisher")
    public IMoocJSONResult queryPublisher(String loginUserId, String videoId, String publishUserId) throws Exception {
        if (StringUtils.isNullOrEmpty(publishUserId)) {
            return IMoocJSONResult.errorMsg("");
        }
        //1. 查询视频发布者信息
        Users userInfo = userService.queryUserInfo(publishUserId);
        UsersVO publisher = new UsersVO();
        BeanUtils.copyProperties(userInfo, publisher);

        //2. 查询当前登录着与视频的点赞关系
        boolean userLikeVideo = userService.isUserLikeVideo(loginUserId, videoId);

        PublisherVideo publisherVideo = new PublisherVideo();
        publisherVideo.setPublisher(publisher);
        publisherVideo.setUserLikeVideo(userLikeVideo);

        return IMoocJSONResult.ok(publisherVideo);
    }

    @ApiOperation(value = "关注用户", notes = "关注用户接口")
    @PostMapping("/beyourfans")
    public IMoocJSONResult beyourfans(String userId, String fanId) throws Exception{
        if (StringUtils.isNullOrEmpty(userId) || StringUtils.isNullOrEmpty(fanId)) {
            return IMoocJSONResult.errorMsg("");
        }
        System.out.println("userId: " + userId + ", fanId: " + fanId);
        userService.saveUserFanRelation(userId, fanId);
        return IMoocJSONResult.ok("关注成功!");
    }

    @ApiOperation(value = "取消关注用户", notes = "取消关注用户接口")
    @PostMapping("/dontbeyourfans")
    public IMoocJSONResult dontbeyourfans(String userId, String fanId) throws Exception{
        if (StringUtils.isNullOrEmpty(userId) || StringUtils.isNullOrEmpty(fanId)) {
            return IMoocJSONResult.errorMsg("");
        }
        userService.deleteUserFanRelation(userId, fanId);
        return IMoocJSONResult.ok("取消关注成功!");
    }

    @ApiOperation(value = "举报用户", notes = "举报用户接口")
    @PostMapping("/reportUser")
    public IMoocJSONResult reportUser(@RequestBody UsersReport usersReport) throws Exception{
        userService.reportUser(usersReport);
        return IMoocJSONResult.ok("举报成功...");
    }
}
