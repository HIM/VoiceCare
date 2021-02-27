package voiceCare.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import voiceCare.model.entity.User;

import java.text.SimpleDateFormat;
import java.util.List;

@Component
//@Service
public class Scheduler {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH-mm-ss");
    static List<User> userList;
    public void savaUser(int id){
        userList.add(new User(id));
    }
    @Scheduled(fixedRate = 60*1000)
    public void showClock(){

        if(userList!=null){
            for(User user : userList){
                System.out.println("当前登录的用户有："+user.getId());
            }
        }else {
            System.out.println("当前无用户登录（每隔1分钟刷新一次）");
        }

        System.out.println("666");
    }
}
