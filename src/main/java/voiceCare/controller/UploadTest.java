package voiceCare.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.Random;

@RestController
@RequestMapping("api/t2/file")
public class UploadTest {
    /**
     * 接受未知参数名的多个文件或者一个文件
     *
     * @param request 请求
     * @return 返回
     */
    @PostMapping("/upload")
    public JSONObject handleFileUpload(HttpServletRequest request) {
        Iterator<String> fileNames = ((MultipartHttpServletRequest) request).getFileNames();
        JSONObject result = null;
        while (fileNames.hasNext()) {
            String next = fileNames.next();
            MultipartFile file = ((MultipartHttpServletRequest) request).getFile(next);
            System.out.println("file.getName():" + file.getName());
            System.out.println("file.getOriginalFilename():" + file.getOriginalFilename());
            String folder = "E:\\upload\\received\\";
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
     * 知道参数名的文件上传
     *
     * @param multipartFile 文件
     * @return 返回
     * @throws IOException
     */
    @PostMapping("/uploadCommon")
    //public JSONObject upload(MultipartFile multipartFile) throws IOException {
    public JSONObject upload(@RequestParam("A") MultipartFile multipartFile) throws IOException {
        String name = multipartFile.getName();//上传文件的参数名
        String originalFilename = multipartFile.getOriginalFilename();//上传文件的文件路径名
        long size = multipartFile.getSize();//文件大小
        String folder = "E:\\upload\\received\\";
        String picName = new Date().getTime() + ".jpg";
        File filelocal = new File(folder, picName);
        multipartFile.transferTo(filelocal);
       /* {
            "reason": "success",
                "result": {
            "D": "/upload/order/files/2016/a72750ad-8950-4949-b04a-37e69aff0d23.jpg",
                    "A": "/upload/order/files/2016/6842811a-eb76-453b-a2f3-488e2bb4500e.jpg",
                    "B": "/upload/order/files/2016/ccc96347-3cb8-4e2e-99a3-0c697b57eb88.jpg",
                    "C": "/upload/order/files/2016/d470d533-a54b-406a-a0f9-bbf82c314755.jpg"
        },
            "error_code": 0
        }*/
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("error_code", 223805);
        jsonObject.put("reason", "文件过大或上传发生错误");
        Random random = new Random();
        if (random.nextInt(10) > 3) {
            jsonObject.put("error_code", 0);
            jsonObject.put("reason", "success");
            JSONObject result = new JSONObject();
            result.put(name, folder + picName);
            jsonObject.put("result", result);
        }
        return jsonObject;
    }

}
