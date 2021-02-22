package voiceCare.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.iflytek.msp.lfasr.LfasrClient;
import com.iflytek.msp.lfasr.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import voiceCare.controller.UserController;
import voiceCare.mapper.AudioMapper;
import voiceCare.model.entity.Audio;
import voiceCare.model.entity.ChatJson;
import voiceCare.model.entity.KeDa;
import voiceCare.model.entity.TuringResult;
import voiceCare.model.entity.TuringResultBag.Results;
import voiceCare.model.entity.TuringResultBag.Values;
import voiceCare.service.AudioService;
import com.baidu.aip.speech.AipSpeech;
import com.baidu.aip.speech.TtsResponse;
import com.baidu.aip.util.Util;

import java.io.File;
import java.io.IOException;

import java.util.concurrent.TimeUnit;

@Service
public class AudioServiceImpl implements AudioService {

    /**
     * 科大讯飞api key
     */
    private static final String APP_ID = "5f92b6b6";
    private static final String SECRET_KEY = "57e6303621c43b0064f54ea07bce2f01";

    /**
     * 图灵机器人api key
     */
    public static final String T_API_KEY = "3b966c3ba00a4e4e9b46a0889e66330c";

    /**
     * 百度语音api key
     */
    //设置APPID/AK/SK
    public static final String B_APP_ID = "18557398";
    public static final String B_API_KEY = "ShjQ8zyXPlSijEx3hq5jMpZt";
    public static final String B_SECRET_KEY = "ntX3GkzRV7VVoaAIEE6EXhz44pIWqhxG";

//    @Autowired
//    private AudioMapper audioMapper;

    /**
     * 科大讯飞api LfasrSDK 语音转文字
     * @param audio_file_path
     * @return
     */
    @Override
    public String audio2word(String audio_file_path){

        String result = null;
        try {
            result = standard(audio_file_path);
            System.out.println("我：" + result);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 图灵机器人api 分析文字 文字转文字
     * @param audio_word_result
     * @return
     */
    @Override
    public String word2word(String audio_word_result) {

        String result = null;
        String res = null;
        try{
            //api url地址
            String url = "http://openapi.tuling123.com/openapi/api/v2";
            //post请求
            HttpMethod method =HttpMethod.POST;

            ChatJson chatJson = new ChatJson();
            chatJson.setReqType(0);
//            System.out.println("图灵机器人接收到我说的audio_word_result : "+audio_word_result);
            chatJson.perception.inputText.setText(audio_word_result);    //输入的文字
            chatJson.userInfo.setUserId(String.valueOf(9));         //用户id
            chatJson.userInfo.setApiKey(T_API_KEY);

            String jsonString = JSONObject.toJSONString(chatJson);
            JSONObject postData = JSONObject.parseObject(jsonString);

            //发送http请求并返回结果
            result = UserController.HttpRestClient(url,method,postData);
//            System.out.println("接收图灵机器人result：" + result);
            res = getTextValue(result);      //解析其中的text
            System.out.println("图灵机器人："+res+"   ");

        }catch (Exception e){
        }

        return res;
    }

    @Override
    public String word2Audio(String turing_word_result) {

        // 初始化一个AipSpeech
        AipSpeech client = new AipSpeech(B_APP_ID, B_API_KEY, B_SECRET_KEY);

        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);

        // 调用接口
        TtsResponse res = client.synthesis(turing_word_result, "zh", 1, null);
        byte[] data = res.getData();
        org.json.JSONObject res1 = res.getResult();
        if (data != null) {
            try {
                Util.writeBytesToFileSystem(data, "output.mp3");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (res1 != null) {
            System.out.println(res1.toString(2));
        }

        return "F:\\WangChen2628\\IDEA\\VoiceCare\\output.mp3";
    }

    @Override
    public void fileDir(String name) {
        File file = new File("C:\\Audio\\"+name);
        if(!file.exists() && !file.isDirectory()){
            System.out.println(name+"文件夹不存在，创建该文件夹");
            file.mkdir();
        }else{
            System.out.println(name+"该文件夹存在");
        }
    }


    public String getTextValue(String res) {

        TuringResult turingResult = JSONObject.parseObject(res, TuringResult.class);
        Results[] results = turingResult.getResults();
        Values values = results[0].getValues();
        String text1 = values.getText();
        return text1;
    }


    private static String standard(String AUDIO_FILE_PATH) throws InterruptedException {
        //1、创建客户端实例
        LfasrClient lfasrClient = LfasrClient.getInstance(APP_ID, SECRET_KEY);

        //2、上传
        Message task = lfasrClient.upload(AUDIO_FILE_PATH);
        String taskId = task.getData();
        System.out.println("转写任务 taskId：" + taskId);

        //3、查看转写进度
        int status = 0;
        while (status != 9) {
            Message message = lfasrClient.getProgress(taskId);
            JSONObject object = JSON.parseObject(message.getData());
            status = object.getInteger("status");
            System.out.println(message.getData());
            TimeUnit.SECONDS.sleep(2);
        }
        //4、获取结果
        Message result = lfasrClient.getResult(taskId);
        System.out.println("转写结果: \n" + result.getData());

        //5、fastjson获取其中要说的text
        String text = result.getData();
        String txt = text.substring(1,text.length()-1);
        KeDa keDa = JSON.parseObject(txt, KeDa.class);
        String realtext = keDa.getOnebest();

        return realtext;

        //退出程序，关闭线程资源，仅在测试main方法时使用。
//        System.exit(0);
    }
}
