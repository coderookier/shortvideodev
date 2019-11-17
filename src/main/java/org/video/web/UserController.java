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
    public IMoocJSONResult query(String userId) throws Exception {
        if (StringUtils.isEmptyOrWhitespaceOnly(userId)) {
            return IMoocJSONResult.errorMsg("用户ID不能为空");
        }
        Users userInfo = userService.queryUserInfo(userId);
        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(userInfo, usersVO);
        return IMoocJSONResult.ok(usersVO);
    }
}
