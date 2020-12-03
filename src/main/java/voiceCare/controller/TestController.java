package voiceCare.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.http.HttpMethod;
import voiceCare.model.entity.BaiduAudio.BaiduRequest;
import voiceCare.model.entity.BaiduAudio.QueryBody;

import java.io.IOException;

import static voiceCare.utils.BaiduAccessToken.getAuth;

public class TestController {
    public static void main(String[] args) {
        String accsee_token = getAuth();

        System.out.println(accsee_token);

        String url = "https://aip.baidubce.com/rpc/2.0/aasr/v1/create?access_token=" + accsee_token;
        HttpMethod method = HttpMethod.POST;
        String speech_url = "F:\\WangChen2628\\IDEA\\VoiceCare\\output.mp3";

        BaiduRequest baiduRequest = new BaiduRequest();
        baiduRequest.setSpeech_url(speech_url);
        baiduRequest.setFormat("pcm");
        baiduRequest.setPid(80001);
        baiduRequest.setRate(16000);

        String jsonRequest = JSONObject.toJSONString(baiduRequest);
        System.out.println(jsonRequest);

        JSONObject postData = JSONObject.parseObject(jsonRequest);  //请求json

        try {
            String result = UserController.HttpRestClient(url, method, postData);
            System.out.println(result);
            JSONObject returnjson = JSONObject.parseObject(result);
            String task_id = returnjson.getString("task_id");
            System.out.println(task_id);

            /**
             * 已获取task_id，查询音频转写任务结果
             */
            String queryurl = "https://aip.baidubce.com/rpc/2.0/aasr/v1/query?access_token=" + accsee_token;
            QueryBody queryBody = new QueryBody();
            queryBody.setTask_ids(task_id);
            String queryreturn = JSONObject.toJSONString(queryBody);
            System.out.println("请求json："+queryreturn);
            JSONObject postQuery = JSONObject.parseObject(queryreturn);  //请求json
            String resultQuery = UserController.HttpRestClient(queryurl, method, postQuery);
            System.out.println(resultQuery);
            Thread.sleep(5000);
            String resultQuery2 = UserController.HttpRestClient(queryurl, method, postQuery);
            System.out.println(resultQuery2);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

}
