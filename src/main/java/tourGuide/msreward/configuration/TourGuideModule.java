package tourGuide.msreward.configuration;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import rewardCentral.RewardCentral;
import tourGuide.msreward.consumer.GpsGateway;


@Configuration
public class TourGuideModule {


	@Bean
	public RestTemplate getRestemplate() {
		return new RestTemplate();
	}
	@Bean
	public RewardCentral getRewardCentral() {
		return new RewardCentral();
	}

}
