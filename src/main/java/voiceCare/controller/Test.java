package voiceCare.controller;

import com.baidu.aip.speech.AipSpeech;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
public class Test {

    public static final String AccessKey_ID = "LTAI5tKPo4qep5fFaENkYToX";
    public static final String AccessKey_Secret = "piX7oSjsIUxRMoSXV0kYJRHjDTENJb";
    public static void main(String[] args) {
        final String accessKeyId = "LTAI5tKPo4qep5fFaENkYToX";
        final String accessKeySecret = "piX7oSjsIUxRMoSXV0kYJRHjDTENJb";
        /**
         * 地域ID
         */
        final String regionId = "cn-shanghai";
        final String endpointName = "cn-shanghai";
        final String product = "nls-filetrans";
        final String domain = "filetrans.cn-shanghai.aliyuncs.com";

        IAcsClient client;
        // 设置endpoint
        try {
            DefaultProfile.addEndpoint(endpointName, regionId, product, domain);
        } catch (ClientException e) {
            e.printStackTrace();
        }
        // 创建DefaultAcsClient实例并初始化
        DefaultProfile profile = DefaultProfile.getProfile(regionId, accessKeyId, accessKeySecret);
        client = new DefaultAcsClient(profile);
        /**
         * 创建CommonRequest 设置请求参数
         */
        CommonRequest postRequest = new CommonRequest();
        postRequest.setDomain("filetrans.cn-shanghai.aliyuncs.com"); // 设置域名，固定值。
        postRequest.setVersion("2018-08-17");         // 设置API的版本号，固定值。
        postRequest.setAction("SubmitTask");          // 设置action，固定值。
        postRequest.setProduct("nls-filetrans");      // 设置产品名称，固定值。
        // 设置录音文件识别请求参数，以JSON字符串的格式设置到请求Body中。
        JSONObject taskObject = new JSONObject();
        taskObject.put("appkey", "skhe3m4z3XRU1prF");    // 项目的Appkey
        taskObject.put("file_link", "http://8.131.246.100:8080/headImg/123.wav");  // 设置录音文件的链接
        //        taskObject.put(KEY_VERSION, "4.0");  // 新接入请使用4.0版本，已接入（默认2.0）如需维持现状，请注释掉该参数设置。
        String task = taskObject.toJSONString();
        postRequest.putBodyParameter("Task", task);  // 设置以上JSON字符串为Body参数。
        postRequest.setMethod(MethodType.POST);      // 设置为POST方式请求。
        /**
         * 提交录音文件识别请求
         */
        String taskId = "";   // 获取录音文件识别请求任务的ID，以供识别结果查询使用。
        CommonResponse postResponse = null;
        try {
            postResponse = client.getCommonResponse(postRequest);
        } catch (ClientException e) {
            e.printStackTrace();
        }
        if (postResponse.getHttpStatus() == 200) {
            JSONObject result = JSONObject.parseObject(postResponse.getData());
            String statusText = result.getString("StatusText");
            if ("SUCCESS".equals(statusText)) {
                System.out.println("录音文件识别请求成功响应： " + result.toJSONString());
                taskId = result.getString("TaskId");
            }
            else {
                System.out.println("录音文件识别请求失败： " + result.toJSONString());
                return;
            }
        }
        else {
            System.err.println("录音文件识别请求失败，Http错误码：" + postResponse.getHttpStatus());
            System.err.println("录音文件识别请求失败响应：" + JSONObject.toJSONString(postResponse));
            return;
        }
    }
}