package voiceCare.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import voiceCare.model.entity.ToneJson;
import voiceCare.service.ClockService;
import voiceCare.utils.JsonData;

import java.io.IOException;

@RestController
@RequestMapping("api/v1/clock")
public class ClockController {

    @Autowired
    private ClockService clockService;

    @RequestMapping("websocket_login")
    public JsonData websocketLogin(@RequestBody ToneJson toneJson){
        int id = toneJson.getId();
        clockService.loginInState(id);


//        try {
//            WebSocketServer.sendInfo("controller已经知道你登录了",String.valueOf(id));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return JsonData.buildSuccess("调用成功");
    }

    @RequestMapping("websocket_quit")
    public JsonData websocketQuit(@RequestBody ToneJson toneJson){
        int id = toneJson.getId();
        clockService.quitState(id);

        return JsonData.buildSuccess("用户已退出");
    }
}
