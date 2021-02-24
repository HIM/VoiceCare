package voiceCare;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@MapperScan("voiceCare.mapper")
@EnableTransactionManagement
public class VoiceCareApplication {

	public static void main(String[] args) {
		SpringApplication.run(VoiceCareApplication.class, args);
	}

}
