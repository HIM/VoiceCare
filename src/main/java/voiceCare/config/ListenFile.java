package voiceCare.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.util.Timer;
import java.util.TimerTask;

@Component
public class ListenFile implements ApplicationRunner {

    public static String filen;

        //项目启动后执行的方法
        @Override
        public void run(ApplicationArguments applicationArguments) throws Exception {
            getFile();//监听新增文件
        }

        private static String path = "F:\\WangChen2628\\IDEA";
        public static void getFile() throws FileNotFoundException, IOException {
            final Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                public void run() {
                    WatchKey key;
                    try {
                        WatchService watchService = FileSystems.getDefault().newWatchService();
                        Paths.get(path).register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
                        while (true) {
                            File file = new File(path);//path为监听文件夹
                            File[] files = file.listFiles();
                            System.out.println("等待中...");
                            key = watchService.take();//没有文件增加时，阻塞在这里
                            for (WatchEvent<?> event : key.pollEvents()) {
                                String fileName = path+"\\"+event.context();
                                filen = fileName;
                                System.out.println("增加文件的文件夹路径 :"+fileName);
                                System.out.println("增加文件的名称 :"+event.context());
                            }if (!key.reset()) {
                                break; //中断循环
                            }
                        }
                    }catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }, 2000 , 3000);//第一个数字2000表示，2000ms以后开启定时器,第二个数字3000，表示3000ms后运行一次run
        }
}
