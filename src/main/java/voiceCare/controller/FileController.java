package voiceCare.controller;

import com.alibaba.fastjson.JSONObject;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import springfox.documentation.spring.web.json.Json;
import voiceCare.model.entity.User;
import voiceCare.service.UserService;
import voiceCare.utils.JsonData;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("api/v1/file")
public class FileController {

    @Autowired
    private UserService userService;

    /**
     * 语音文件上传
     * 接受未知参数名的多个文件或者一个文件
     * @param request 请求
     * @return 返回
     */
    @PostMapping("audio_upload")
    public JsonData handleFileUpload(HttpServletRequest request) {
        System.out.println("获取Id："+request.getParameter("id"));
        int id = Integer.parseInt(request.getParameter("id"));
        Iterator<String> fileNames = ((MultipartHttpServletRequest) request).getFileNames();
        JSONObject result = null;
        while (fileNames.hasNext()) {
            String next = fileNames.next();
            MultipartFile file = ((MultipartHttpServletRequest) request).getFile(next);
            System.out.println("file.getName():" + file.getName());
            System.out.println("file.getOriginalFilename():" + file.getOriginalFilename());

            //判断是否有该文件夹，若没有则创建
            File filee = new File("C:\\VoiceBase\\"+id);
            if( !filee.exists() && !filee.isDirectory()){
                filee.mkdir();
            }
            String folder = "C:\\VoiceBase\\"+id+"\\";

            String picName = new Date().getTime() + ".mp3";
            File filelocal = new File(folder, picName);
            result = new JSONObject();
            result.put(picName, folder + picName);
            try {
                file.transferTo(filelocal);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return JsonData.buildSuccess("语音文件上传成功");
    }

    /**
     * 头像图片文件上传
     * @param request
     * @return
     */
    @PostMapping("headimg_upload")
    public JsonData headImgUpload(HttpServletRequest request) {
        System.out.println("获取Id："+request.getParameter("id"));
        int id = Integer.parseInt(request.getParameter("id"));
        String headImgUrl;  //头像路径
        Iterator<String> fileNames = ((MultipartHttpServletRequest) request).getFileNames();
        JSONObject result = null;
        while (fileNames.hasNext()) {
            String next = fileNames.next();
            MultipartFile file = ((MultipartHttpServletRequest) request).getFile(next);
            System.out.println("file.getName():" + file.getName());
            System.out.println("file.getOriginalFilename():" + file.getOriginalFilename());
            String folder = "C:\\Develop\\apache-tomcat-8.5.31\\webapps\\headImg";
            String urlName = "http://8.131.246.100:8080/headImg/";
            String picName = new Date().getTime() + ".jpg";
            headImgUrl = urlName + picName;
            System.out.println("头像url存储路径："+headImgUrl);
            File filelocal = new File(folder, picName);
            result = new JSONObject();
            result.put(picName, folder + picName);
            try {
                file.transferTo(filelocal);
                //存头像路径至数据库
                userService.uploadHeadImgUrl(id, headImgUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return JsonData.buildSuccess("头像上传成功");
    }

}