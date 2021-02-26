package voiceCare;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@MapperScan("voiceCare.mapper")
@EnableTransactionManagement
@EnableScheduling//开启定时任务支持
public class VoiceCareApplication {

	public static void main(String[] args) {
		SpringApplication.run(VoiceCareApplication.class, args);
	}

}
