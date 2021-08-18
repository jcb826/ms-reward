package tourGuide.msreward;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Locale;

@SpringBootApplication
public class MsRewardApplication {

	public static void main(String[] args) {
		Locale.setDefault(new Locale("en", "US"));
		SpringApplication.run(MsRewardApplication.class, args);
	}

}
