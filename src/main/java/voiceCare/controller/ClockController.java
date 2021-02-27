package voiceCare.controller;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import voiceCare.model.entity.ToneJson;
import voiceCare.utils.JsonData;

@RestController
@RequestMapping("api/v1/clock")
public class ClockController {
    @RequestMapping("websocket_update")
    public JsonData websocketUpdate(@RequestBody ToneJson toneJson){
        toneJson.toString();
        return JsonData.buildSuccess("调用成功");
    }
}
