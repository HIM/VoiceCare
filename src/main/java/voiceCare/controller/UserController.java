package voiceCare.controller;

import com.alibaba.fastjson.JSON;
import it.sauronsoftware.jave.AudioAttributes;
import it.sauronsoftware.jave.EncodingAttributes;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import voiceCare.config.AudioTransWord;
import voiceCare.model.entity.*;
import voiceCare.model.entity.AudioTransWord.AliJson;
import voiceCare.model.entity.AudioTransWord.Sentences;
import voiceCare.model.request.LoginRequest;
import voiceCare.service.AudioService;
import voiceCare.service.ClockService;
import voiceCare.service.NewsService;
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
    @Autowired
    private NewsService newsService;

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
        user.setFamilyName(userService.getFamilyName(id));
//        return token == null ?JsonData.buildError("登录失败，账号密码错误"): JsonData.buildSuccess(token);
        return JsonData.buildSuccess(user);
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
        userService.setStateOn(userId);//强制使用户登录状态为1
        user.setFamilyName(userService.getFamilyName(userId));
        return JsonData.buildSuccess(user);
    }

    /**
     * 家庭列表
     * @return
     */
    @GetMapping("family")
    public JsonData findUserListByFamilyId(HttpServletRequest request){

        int id = (Integer) request.getAttribute("user_id");
        String familyId = userService.getFamilyId(id);
        if(familyId.equals("0")){
            return JsonData.buildError("no family");
        }

        if(familyId == null){
            return JsonData.buildError("查询失败");
        }

        List<User> userList = userService.findListFamily(familyId,id);
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

    /**
     * 加入家庭
     * @param family
     * @param request
     * @return
     */
    @RequestMapping("join_family")
    public JsonData joinFamily(@RequestBody Family family, HttpServletRequest request){
        String familyId = family.getFamilyId();
        int id = (Integer)request.getAttribute("user_id");
        if(userService.findFamilyIdExist(familyId) == 1){
            userService.joinFamily(familyId, id);
            return JsonData.buildSuccess("成功加入家庭");
        }else {
            return JsonData.buildError("此家庭不存在");
        }
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
//        Thread.sleep(10000);
        /*
        * 服务器上接收音频文件用FileController
        * 这里暂时用监听文件夹的方式，监听文件夹：F:\WangChen2628\IDEA
        *
        */
//        String file = ListenFile.filen;//获取音频地址
//        String AUDIO_FILE_PATH = file;"F:\\WangChen2628\\IDEA"
        String AUDIO_FILE_PATH = "F:\\Code\\1.wav";
        String AUDIO_WORD_RESULT = audioService.audio2word(AUDIO_FILE_PATH);
        System.out.println(AUDIO_WORD_RESULT);
//        String TURING_WORD_RESULT = audioService.word2word(AUDIO_WORD_RESULT);
//        String TURING_WORD_RESULT = audioService.word2word("今天天气怎么样");
//        String AUDIO_URL = audioService.word2Audio(TURING_WORD_RESULT);
//        System.out.println("AUDIO_URL："+AUDIO_URL);
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
     * 修改音色
     * @param exid
     * @param request
     * @return
     */
    @RequestMapping("exchange")
    public JsonData exchangeVoice(@RequestBody Exid exid, HttpServletRequest request){

        int id = exid.getId();
        Integer userId = (Integer) request.getAttribute("user_id");
        System.out.println("用户"+userId+"切换成了"+id+"的声音");
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
        userService.setStateOn(id);//强制使用户登录状态为1
        return JsonData.buildSuccess(clocks);
    }

    /**
     * 随心听 发送
     * @param audioWord
     * @param request
     * @return
     */
    @RequestMapping("listen_word")
    public JsonData listenWordSend(@RequestBody AudioWord audioWord, HttpServletRequest request){
        String word = audioWord.getContext();
        int id = (Integer)request.getAttribute("user_id");
        int tone_id = userService.getToneId(id);
        AudioWord audioWord1 = new AudioWord();
        audioWord1.setId(id);
        audioWord1.setToneId(tone_id);
        audioWord1.setContext(word);
        String audioWordJson = JSONObject.toJSONString(audioWord1);
        System.out.println("“随心听”发送给Python: "+audioWordJson);
        JSONObject postData = JSONObject.parseObject(audioWordJson);  //请求json
        String url = "http://127.0.0.1:80/zdy";
        HttpMethod method = HttpMethod.POST;
        try {
            String result = UserController.HttpRestClient(url, method,postData);
            System.out.println("result: "+ result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return JsonData.buildSuccess("随心听发送并合成成功");
    }

    /**
     * 随心听 播放
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("listen_play")
    public String listenWordPlay(HttpServletRequest request, HttpServletResponse response){
        Integer userId = (Integer) request.getAttribute("user_id");

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

    /**
     * 新闻列表
     * @param request
     * @return
     */
    @RequestMapping("news_list")
    public JsonData newsList(HttpServletRequest request){
        int id = (Integer)request.getAttribute("user_id");
        List<News> news = newsService.showNews();
        System.out.println(news.toString());
        return JsonData.buildSuccess(news);
    }

    /**
     * 新闻详情页
     * @param news
     * @param request
     * @return
     */
    @RequestMapping("news_details")
    public JsonData newsDetails(@RequestBody News news, HttpServletRequest request){
        int newsId = news.getId();
        int id = (Integer)request.getAttribute("user_id");
        News news1 = newsService.getDetailNews(newsId);
        return JsonData.buildSuccess(news1);
    }

    /**
     * 播放新闻
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("news_play")
    public String newsPlay(HttpServletRequest request, HttpServletResponse response){
        int id = Integer.parseInt(request.getParameter("id"));//从？后获取新闻Id
        int userId = (Integer) request.getAttribute("user_id");
        int tone_id = userService.getToneId(userId);
//        String audioUrl = "C:\\Users\\Administrator\\Desktop\\news_audio\\tacotron_inference_output\\"+userId+"\\"+newsId+".wav";
//        String audioUrl = "http://8.131.246.100:8080/headImg/"+tone_id+"/"+newsId+".wav";
//        String audioUrl = "F:\\FFOutput\\xcz.mp3";
        String audioUrl = "C:\\Users\\Administrator\\Desktop\\news_audio\\tacotron_inference_output\\"+tone_id+"\\"+id+".mp3";
        System.out.println("audioUrl："+audioUrl);
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

    /**
     * 切换简洁模式
     * @param request
     * @return
     */
    @RequestMapping("role_play")
    public JsonData selectRole(HttpServletRequest request){
        int id = (Integer) request.getAttribute("user_id");
        String familyId = userService.getFamilyId(id);
        if(familyId.equals("0")){
            return JsonData.buildError("no family");
        }
        if(familyId == null){
            return JsonData.buildError("查询失败");
        }
        List<User> userList = userService.simply(familyId,id);
        return JsonData.buildSuccess(userList);
    }

    /**
     * 简洁模式下点头像播放
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("simply_play")
    public String simplyPlay(HttpServletRequest request, HttpServletResponse response){
        int tone_id = Integer.parseInt(request.getParameter("id"));//从？后获取新闻Id
        String audioUrl = "C:\\Users\\Administrator\\Desktop\\news_audio\\tacotron_inference_output\\"+tone_id+"\\"+(int)(Math.random()*10+1)+".mp3";
        System.out.println("audioUrl："+audioUrl);
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

    /**
     * 不部署至服务器  音频转文字 阿里云
     * @return
     */
    @RequestMapping("chat_receive")
    public String chatTest(){
        try {
            AudioTransWord audioTransWord = new AudioTransWord();
//            audioTransWord.setUrl("http://8.131.246.100:8080/headImg/12.wav");
            audioTransWord.setUrl("http://8.131.246.100:8080/headImg/chat/12s.wav");
            AliJson aliJson = JSON.toJavaObject(audioTransWord.getInfo(),AliJson.class);
            Sentences[] sentences = aliJson.getResult().getSentences();
            String text = sentences[0].getText();
            System.out.println(text);

//            String TURING_WORD_RESULT = audioService.word2word(text);   //图灵机器人返回值"
//            System.out.println(TURING_WORD_RESULT);

            return text;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 聊天记录
     * @param request
     * @return
     */
    @RequestMapping("chat_record")
    public JsonData chatRecord(HttpServletRequest request){
        int id = (Integer)request.getAttribute("user_id");
        List<ChatRecord> chatRecords = userService.findChatRecord(id);
        return JsonData.buildSuccess(chatRecords);
    }

}
