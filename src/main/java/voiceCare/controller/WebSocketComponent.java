package voiceCare.controller;

import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@ServerEndpoint("/websocket")
public class WebSocketComponent {
    private Session session;

    private CopyOnWriteArraySet<WebSocketComponent> webSocketComponentSet = new CopyOnWriteArraySet<>();

    @OnOpen
    public void onOpen(Session session) throws IOException {
        this.session = session;
        webSocketComponentSet.add(this);
        System.out.println("连接成功");
        //只要连接成功了，就每天定时发送消息

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        for(int i=1;i<5;i++){
            try {
                Thread.sleep(3 * 1000);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
            System.out.println(df.format(new Date()));// new Date()为获取当前系统时间
            this.sendMessage("发送给客户端的消息: 第"+i+"次 当前时间："+df.format(new Date()));
        }

    }

    @OnClose
    public void onClose(){
        webSocketComponentSet.remove(this);
        System.out.println("连接关闭");
    }

    @OnMessage
    public void onMessage(String message){
        System.out.println("【接收消息】："+message);
    }

    public void sendMessage(String message) throws IOException {
        System.out.println("【发送消息】："+message);
        this.session.getBasicRemote().sendText(message);
    }
}
