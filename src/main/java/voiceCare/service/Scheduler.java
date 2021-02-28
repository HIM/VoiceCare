package voiceCare.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import voiceCare.controller.WebSocketServer;
import voiceCare.model.entity.Clock;
import voiceCare.model.entity.User;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
public class Scheduler {

    @Autowired
    private ClockService clockService;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH-mm-ss");
    SimpleDateFormat df = new SimpleDateFormat("HH:mm");//设置日期格式
    static List<User> userList;
    static String time = "";

    @Scheduled(fixedRate = 5*1000)
    public void showClock(){

        userList = clockService.findLoginUser();
        if(userList!=null){
            for(User user : userList){
                String nowTime = df.format(new Date());
                System.out.println("当前时间："+nowTime);
                System.out.println("当前登录的用户有："+user.getId());
                List<Clock> clockList = clockService.findLoginUserClock(user.getId());
                for(Clock clock : clockList){
                    /**
                     * params: nowTime, clock
                     * return: boolean
                     * 判断当前时间和闹钟所定时间是否相等，若相等，则返回true
                     */
                    if(nowTime.equals(clock.getTime()) && !nowTime.equals(time) && clock.getState() == 1){
                        time = nowTime;
                        try {
                            WebSocketServer.sendInfo(clock.getTime()+" 备注："+clock.getComment(),String.valueOf(user.getId()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }else {
            System.out.println("当前无用户登录（每隔1分钟刷新一次）");
        }

    }
}
