package voiceCare.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.*;
import voiceCare.model.entity.BaiduAudio.BaiduRequest;
import voiceCare.model.entity.BaiduAudio.QueryBody;
import voiceCare.model.entity.User;
import voiceCare.service.AudioService;
import voiceCare.service.UserService;
import voiceCare.utils.JsonData;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

import static voiceCare.utils.BaiduAccessToken.getAuth;

@RestController
@RequestMapping("api/v1/pub/audio")
public class AudioController {

    @Autowired
    private UserService userService;

    @GetMapping("audioplay")
    public String getVideos(HttpServletRequest request, HttpServletResponse response)
    {
        Integer userId = (Integer) request.getAttribute("user_id");

        User user =  userService.findByUserId(userId);
        String audioUrl = user.getAudioUrl();
        System.out.println(audioUrl);

//        String audiourl = "";
//        audiourl = userService.findAudioUrlByUserId(userId);

        try {
            FileInputStream fis = null;
            OutputStream os = null ;
//            url = "F:\\FFOutput\\xcz.mp3";
            String url = audioUrl;
            fis = new FileInputStream(url);
            System.out.println(fis);
            int size = fis.available(); // 得到文件大小
            byte data[] = new byte[size];
            fis.read(data); // 读数据
            fis.close();
            fis = null;
            response.setContentType("audio/mpeg"); // 设置返回的文件类型
            os = response.getOutputStream();
            os.write(data);
            os.flush();
            os.close();
            os = null;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //已弃用，百度语音转文字
    @PostMapping("chatRequest")
    public JsonData chatVoice(){

        String accsee_token = getAuth();
        System.out.println(accsee_token);

        try{

            /**
             * 创建音频转写任务
             */
            String url = "https://aip.baidubce.com/rpc/2.0/aasr/v1/create?access_token=" + accsee_token;
            HttpMethod method = HttpMethod.POST;
            String speech_url = "F:\\WangChen2628\\IDEA\\VoiceCare\\output.mp3";

            BaiduRequest baiduRequest = new BaiduRequest();
            baiduRequest.setSpeech_url(speech_url);
            baiduRequest.setFormat("pcm");
            baiduRequest.setPid(80001);
            baiduRequest.setRate(16000);

            String jsonRequest = JSONObject.toJSONString(baiduRequest);
            JSONObject postData = JSONObject.parseObject(jsonRequest);  //请求json

            String result = UserController.HttpRestClient(url, method, postData);
            JSONObject returnjson = JSONObject.parseObject(result);
            String task_id = returnjson.getString("task_id");

            /**
             * 已获取task_id，查询音频转写任务结果
             */
            String queryurl = "https://aip.baidubce.com/rpc/2.0/aasr/v1/query?access_token=" + accsee_token;
            QueryBody queryBody = new QueryBody();
            queryBody.setTask_ids(task_id);
            String queryreturn = JSONObject.toJSONString(baiduRequest);

            JSONObject postQuery = JSONObject.parseObject(queryreturn);  //查询json
            String resultQuery = UserController.HttpRestClient(queryurl, method, postQuery);
            System.out.println(resultQuery);

        }catch (Exception e){
        }

        return JsonData.buildSuccess();
    }


}
