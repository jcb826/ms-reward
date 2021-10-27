package tourGuide.msreward;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import rewardCentral.RewardCentral;
import tourGuide.msreward.consumer.GpsGateway;
import tourGuide.msreward.consumer.UserGateway;
import tourGuide.msreward.model.Attraction;
import tourGuide.msreward.model.UserReward;
import tourGuide.msreward.service.RewardsService;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@SpringBootTest
public class RewardsServiceTest {

    @Autowired
    GpsGateway gpsGateway;
    @Autowired
    UserGateway userGateway;
    @Test
    public void isWithinAttractionProximity() {

        Attraction[] attractions = gpsGateway.getAttractions().getBody();
        RewardsService rewardsService = new RewardsService(gpsGateway, userGateway, new RewardCentral(), Arrays.stream(attractions).toList());

        Attraction attraction = gpsGateway.getAttractions().getBody()[0];
        Assertions.assertTrue(rewardsService.isWithinAttractionProximity(attraction, attraction));
    }

}
