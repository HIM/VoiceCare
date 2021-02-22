package voiceCare.controller;

import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import sun.misc.IOUtils;
import voiceCare.config.ListenFile;
import voiceCare.model.entity.BaiduAudio.BaiduRequest;
import voiceCare.model.entity.BaiduAudio.QueryBody;
import voiceCare.model.entity.ChatJson;
import voiceCare.model.entity.User;
import voiceCare.model.request.LoginRequest;
import voiceCare.service.AudioService;
import voiceCare.service.UserService;
import voiceCare.utils.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sound.sampled.*;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

import static voiceCare.utils.BaiduAccessToken.getAuth;

@RestController
@RequestMapping("api/v1/pri/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AudioService audioService;

    @Autowired
    private RestTemplate restTemplate;

    public static String HttpRestClient(String url, HttpMethod method, JSONObject json) throws IOException {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(10*10000);
        requestFactory.setReadTimeout(10*10000);
        RestTemplate client = new RestTemplate(requestFactory);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON_UTF8);
        HttpEntity<String> requestEntity = new HttpEntity<String>(json.toString(), headers);
        //  执行HTTP请求
        ResponseEntity<String> response = client.exchange(url, method, requestEntity, String.class);
        return response.getBody();
    }

    /**
     * 注册接口
     * @param userInfo
     * @return
     */
    @PostMapping("register")
    public JsonData register(@RequestBody Map<String,String> userInfo ){

        int rows = userService.save(userInfo);

        return rows == 1 ? JsonData.buildSuccess(): JsonData.buildError("注册失败，请重试");

    }

    /**
     * 登录接口
     * @param loginRequest
     * @return
     */
    @PostMapping("login")
    public JsonData login(@RequestBody LoginRequest loginRequest){

        String token = userService.findByPhoneAndPwd(loginRequest.getPhone(), loginRequest.getPwd());

        return token == null ?JsonData.buildError("登录失败，账号密码错误"): JsonData.buildSuccess(token);

    }

    /**
     * 根据用户id查询用户信息
     * @param request
     * @return
     */
    @GetMapping("find_by_token")
    public JsonData findUserInfoByToken(HttpServletRequest request){

        Integer userId = (Integer) request.getAttribute("user_id");
        if(userId == null){
            return JsonData.buildError("查询失败");
        }
        User user =  userService.findByUserId(userId);

        return JsonData.buildSuccess(user);
    }

    /**
     * 根据用户family_id查询拥有同样family_id的人，即家庭的人
     * @return
     */
    @GetMapping("family")
    public JsonData findUserListByFamilyId(HttpServletRequest request){

        String familyId = (String) request.getAttribute("family_id");

        if(familyId == null){
            return JsonData.buildError("查询失败");
        }

        List<User> userList = userService.findListFamily(familyId);

        return JsonData.buildSuccess(userList);
    }

    /**
     * 创建家庭
     * @return
     */
    @PostMapping("create")
    public JsonData createFamily(@RequestBody Map<String,String> familyIdObj){
        int rows = userService.create(familyIdObj);
        return rows == 1 ? JsonData.buildSuccess(): JsonData.buildError("注册失败，请重试");
    }

    /**
     * 播放新闻
     * @param request
     * @param response
     * @return
     */
    @GetMapping("audioplay")
    public String getVideos(HttpServletRequest request, HttpServletResponse response) {

        Integer userId = (Integer) request.getAttribute("user_id");

        User user =  userService.findByUserId(userId);
        String audioUrl = user.getAudioUrl();
        System.out.println("audioUrl: " + audioUrl);
        try {
            FileInputStream fis = null;
            OutputStream os = null ;
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

    /**
     * 连续聊天
     * @param request
     * @param response
     * @return
     * @throws InterruptedException
     */
    @GetMapping("chat")
    public String chatTuring(HttpServletRequest request, HttpServletResponse response) throws InterruptedException {

        System.out.println("chat start...");
        Integer userId = (Integer) request.getAttribute("user_id");

        Thread.sleep(7000);
        String file = ListenFile.filen;//获取音频地址
        String AUDIO_FILE_PATH = file;

        String AUDIO_WORD_RESULT = audioService.audio2word(AUDIO_FILE_PATH);
        String TURING_WORD_RESULT = audioService.word2word(AUDIO_WORD_RESULT);
        String AUDIO_URL = audioService.word2Audio(TURING_WORD_RESULT);
        System.out.println(AUDIO_URL);

        return null;
    }

    /**
     * 获取返回聊天音频
     * @param request
     * @param response
     * @return
     */
    @GetMapping("con_chat")
    public String continuousChat(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        Integer userId = (Integer) request.getAttribute("user_id");
        System.out.println("start return chat message------------------------- ");

        String MUSIC_FILE = "F:\\WangChen2628\\IDEA\\VoiceCare\\output.mp3";

        response.setHeader("Content-Type", "audio/mpeg");
        File file = new File("F:\\FFOutput\\xcz.mp3");
        int len_l = (int) file.length();
        byte[] buf = new byte[2048];
        FileInputStream fis = new FileInputStream(file);
        OutputStream out = response.getOutputStream();
        len_l = fis.read(buf);
        while (len_l != -1) {
            out.write(buf, 0, len_l);
            len_l = fis.read(buf);
        }
        out.flush();
        out.close();
        fis.close();

        return null;
    }
    

}
