package voiceCare.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import springfox.documentation.spring.web.json.Json;
import voiceCare.config.AudioTransWord;
import voiceCare.model.entity.AudioTransWord.AliJson;
import voiceCare.model.entity.AudioTransWord.Sentences;
import voiceCare.model.entity.AudioWord;
import voiceCare.model.entity.User;
import voiceCare.service.AudioService;
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
    @Autowired
    private AudioService audioService;

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

    /**
     * 聊天，接收音频文件
     * @param request 请求
     * @return 返回
     */
    @PostMapping("chat_upload")
    public JsonData ChatFileUpload(HttpServletRequest request) {
        System.out.println("当前用户"+request.getParameter("id")+"发起了聊天..");
        int id = Integer.parseInt(request.getParameter("id"));
        Iterator<String> fileNames = ((MultipartHttpServletRequest) request).getFileNames();
        JSONObject result = null;
        while (fileNames.hasNext()) {
            String next = fileNames.next();
            MultipartFile file = ((MultipartHttpServletRequest) request).getFile(next);
            System.out.println("file.getName():" + file.getName());
            System.out.println("file.getOriginalFilename():" + file.getOriginalFilename());

            //判断是否有该文件夹，若没有则创建
            File filee = new File("C:\\Develop\\apache-tomcat-8.5.31\\webapps\\headImg\\chat\\"+id);
            if( !filee.exists() && !filee.isDirectory()){
                filee.mkdir();
            }
            String folder = "C:\\Develop\\apache-tomcat-8.5.31\\webapps\\headImg\\chat\\"+id+"\\";

            String picName = new Date().getTime() + ".wav";
            File filelocal = new File(folder, picName);
            result = new JSONObject();
            result.put(picName, folder + picName);
            try {
                file.transferTo(filelocal);
            } catch (IOException e) {
                e.printStackTrace();
            }
            /**
             * 阿里云 语音转文字
             */
            AudioTransWord audioTransWord = new AudioTransWord();
            String audiourl = "http://8.131.246.100:8080/headImg/chat/"+picName;
            audioTransWord.setUrl(audiourl);//音频文件地址
            AliJson aliJson = null;
            try {
                aliJson = JSON.toJavaObject(audioTransWord.getInfo(), AliJson.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Sentences[] sentences = aliJson.getResult().getSentences();
            String text = sentences[0].getText();   //获取用户说的话
            System.out.println("用户"+id+"说了："+text);

            String TURING_WORD_RESULT = audioService.word2word(text);   //图灵机器人返回值"
            System.out.println("图灵机器人说："+TURING_WORD_RESULT);

            int tone_id = userService.getToneId(id);
            AudioWord audioWord1 = new AudioWord();
            audioWord1.setId(id);
            audioWord1.setToneId(tone_id);
            audioWord1.setContext(text);
            String audioWordJson = JSONObject.toJSONString(audioWord1);
            System.out.println("“聊天”发送给Python: "+audioWordJson);
            JSONObject postData = JSONObject.parseObject(audioWordJson);  //请求json
            String url = "http://127.0.0.1:80/zdy";
            HttpMethod method = HttpMethod.POST;
            try {
                String res = UserController.HttpRestClient(url, method,postData);
                System.out.println("result: "+ res);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return JsonData.buildSuccess("音频处理成功");
    }
}