package voiceCare.controller;

import io.swagger.models.auth.In;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import voiceCare.config.ListenFile;
import voiceCare.model.entity.*;
import voiceCare.model.request.LoginRequest;
import voiceCare.service.AudioService;
import voiceCare.service.ClockService;
import voiceCare.service.UserService;
import voiceCare.utils.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

@RestController
@RequestMapping("api/v1/pri/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private AudioService audioService;
    @Autowired
    private ClockService clockService;

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
        int id = userService.findIdByPhone(loginRequest.getPhone());
        User user =  userService.findByUserId(id);
        user.setToken(token);
        user_s = user;
//        return token == null ?JsonData.buildError("登录失败，账号密码错误"): JsonData.buildSuccess(token);
        return JsonData.buildSuccess(user);
    }

    public static User user_s;

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
        user_s = user;

        return JsonData.buildSuccess(user);
    }

    /**
     * 根据用户family_id查询拥有同样family_id的人，即家庭的人
     * @return
     */
    @GetMapping("family")
    public JsonData findUserListByFamilyId(HttpServletRequest request){

        String familyId = (String) request.getAttribute("family_id");
        Integer userId = (Integer) request.getAttribute("user_id");

        if(familyId == null){
            return JsonData.buildError("查询失败");
        }

        List<User> userList = userService.findListFamily(familyId,userId);

        return JsonData.buildSuccess(userList);
    }

    /**
     * 创建家庭
     * @return
     */
    @PostMapping("create_family")
    public JsonData createFamily(@RequestBody Family family, HttpServletRequest request){
        String familyId = userService.createFamily(family.getFamilyName(), (Integer)request.getAttribute("user_id"));
        return JsonData.buildSuccess(familyId);
    }

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
     * 播放新闻
     * @param request
     * @param response
     * @return
     */
    @GetMapping("audioplay")
    public String getAudio(HttpServletRequest request, HttpServletResponse response) {
        Integer userId = (Integer) request.getAttribute("user_id");

        System.out.println("start ------------------------- ");
        String url = "http://127.0.0.1:80/bofang";
        HttpMethod method = HttpMethod.POST;

        ToneJson toneJson = new ToneJson();
        toneJson.setId((Integer)request.getAttribute("user_id"));
        toneJson.setTone_id((Integer)request.getAttribute("tone_id"));
        System.out.println("user_id: "+(Integer)request.getAttribute("user_id")+" ,tone_id: "+(Integer)request.getAttribute("tone_id"));
        String jsonRequest = JSONObject.toJSONString(toneJson);
        System.out.println("当前给python："+jsonRequest);
        JSONObject postData = JSONObject.parseObject(jsonRequest);  //请求json

        try {
            String result = UserController.HttpRestClient(url, method,postData);
            System.out.println("result= "+ result);
        } catch (IOException e) {
            e.printStackTrace();
        }

        User user =  userService.findByUserId(userId);
        String audioUrl = user.getAudioUrl();
        System.out.println("audioUrl= "+audioUrl);

        try {
            FileInputStream fis = null;
            OutputStream os = null ;

            String urll = audioUrl;
            fis = new FileInputStream(urll);
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

    @GetMapping("chat")
    public String chatTuring(HttpServletRequest request, HttpServletResponse response) throws InterruptedException {

        System.out.println("chat接口start...");
        Integer userId = (Integer) request.getAttribute("user_id");
        System.out.println("当前用户id："+userId);
        Thread.sleep(10000);
        String file = ListenFile.filen;//获取音频地址
        String AUDIO_FILE_PATH = file;

        String AUDIO_WORD_RESULT = audioService.audio2word(AUDIO_FILE_PATH);
        String TURING_WORD_RESULT = audioService.word2word(AUDIO_WORD_RESULT);
        String AUDIO_URL = audioService.word2Audio(TURING_WORD_RESULT);
        System.out.println("AUDIO_URL："+AUDIO_URL);
        return null;
    }

    @GetMapping("con_chat")
    public String continuousChat(HttpServletRequest request, HttpServletResponse response) {
        Integer userId = (Integer) request.getAttribute("user_id");

        System.out.println("start return chat message------------------------- ");

        try {
            FileInputStream fis = null;
            OutputStream os = null ;

            String urll = "C:\\Develop\\IDEA_WorkSpace\\VoiceCare\\output.mp3";
            fis = new FileInputStream(urll);
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
     * 接受未知参数名的多个文件或者一个文件
     *
     * @param request 请求
     * @return 返回
     */
    @PostMapping("upload")
    public JSONObject handleFileUpload(HttpServletRequest request) {
        Iterator<String> fileNames = ((MultipartHttpServletRequest) request).getFileNames();
        JSONObject result = null;
//        int user_id = user_s.getId();
        int user_id = (Integer) request.getAttribute("user_id");
        System.out.println("从token中获取Id为："+user_id);
        while (fileNames.hasNext()) {
            String next = fileNames.next();
            MultipartFile file = ((MultipartHttpServletRequest) request).getFile(next);
            System.out.println("file.getName():" + file.getName());
            System.out.println("file.getOriginalFilename():" + file.getOriginalFilename());
            //判断是否有该文件夹，若没有则创建
            File filee = new File("C:\\VoiceBase\\"+user_id);
            if( !filee.exists() && !filee.isDirectory()){
                filee.mkdir();
            }
            String folder = "C:\\VoiceBase\\"+user_id+"\\";
            String picName = new Date().getTime() + ".mp3";
            File filelocal = new File(folder, picName);
            result = new JSONObject();
            result.put(picName, folder + picName);
            System.out.println("*************存储语音模块结束***********");
            try {
                file.transferTo(filelocal);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("error_code", 223805);
        jsonObject.put("reason", "文件过大或上传发生错误");
        Random random = new Random();
        if (random.nextInt(10) > 3) {
            jsonObject.put("error_code", 0);
            jsonObject.put("reason", "success");

            jsonObject.put("result", result);
        }
        return jsonObject;
    }


    /**
     * 修改音色
     * @param exid
     * @param request
     * @return
     */
    @RequestMapping("exchange")
    public JsonData exchangeVoice(@RequestBody Exid exid, HttpServletRequest request){

        int id = exid.getId();
        Integer userId = (Integer) request.getAttribute("user_id");
        System.out.println("post传过来是："+id);
        System.out.println("request查到的是："+userId);
        if(userId == null){
            return JsonData.buildError("查询失败");
        }
        userService.exchangeToneId(id, userId);

        return JsonData.buildSuccess();
    }

    /**
     * 添加提醒（闹钟）
     * @param clock
     * @param request
     * @return
     */
    @RequestMapping("add_clock")
    public JsonData addClock(@RequestBody Clock clock, HttpServletRequest request){
        Integer id = (Integer)request.getAttribute("user_id");
        clock.setUserId(id);
        int rows = clockService.save(clock);
        return rows == 1?JsonData.buildSuccess():JsonData.buildError("新提醒添加失败");
    }

    /**
     * 修改提醒
     * @param clock
     * @param request
     * @return
     */
    @RequestMapping("update_clock")
    public JsonData updateClock(@RequestBody Clock clock, HttpServletRequest request){
        Integer id = (Integer)request.getAttribute("user_id");
        clock.setUserId(id);
        int rows = clockService.update(clock);
        return rows == 1?JsonData.buildSuccess():JsonData.buildError("提醒修改失败");
    }

    /**
     * 删除提醒
     * @param clock
     * @param request
     * @return
     */
    @RequestMapping("delete_clock")
    public JsonData deleteClock(@RequestBody Clock clock, HttpServletRequest request){
        Integer id = (Integer)request.getAttribute("user_id");
        String createTime = clock.getCreateTime();
        int rows = clockService.delete(id, createTime);
        return rows == 1?JsonData.buildSuccess():JsonData.buildError("提醒修改失败");
    }

    /**
     * 展示所有提醒
     * @param request
     * @return
     */
    @RequestMapping("show_clocks")
    public JsonData showClocks(HttpServletRequest request){
        Integer id = (Integer)request.getAttribute("user_id");
        if(id == null){
            return JsonData.buildError("查询失败");
        }
        List<Clock> clocks = clockService.showClocks(id);
        System.out.println(clocks.toString());
        return JsonData.buildSuccess(clocks);
    }


}
