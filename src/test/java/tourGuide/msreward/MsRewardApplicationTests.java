package tourGuide.msreward;

import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import rewardCentral.RewardCentral;
import tourGuide.msreward.consumer.GpsGateway;
import tourGuide.msreward.consumer.UserGateway;
import tourGuide.msreward.model.Attraction;
import tourGuide.msreward.model.User;
import tourGuide.msreward.model.VisitedLocation;
import tourGuide.msreward.service.RewardsService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class MsRewardApplicationTests {


    @Autowired
    GpsGateway gpsGateway;
    @Autowired
    UserGateway userGateway;

    @Test
    void contextLoads() {
    }

    @Test
    public void highVolumeGetRewards() {

        RewardsService rewardsService = new RewardsService(gpsGateway, new RewardCentral());

        // Users should be incremented up to 100,000, and test finishes within 20 minutes
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        //	TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

        Attraction attraction = Arrays.stream(gpsGateway.getAttractions().getBody()).toList().get(0);

        List<User> allUsers = Arrays.stream(userGateway.getAllUsers().getBody()).toList();

        allUsers.forEach(u -> u.addToVisitedLocations(new VisitedLocation(u.getUserId(), attraction, new Date())));

        allUsers.forEach(u -> rewardsService.calculateRewards(u));

        for (User user : allUsers) {
            Assertions.assertTrue(user.getUserRewards().size() > 0);
        }
        stopWatch.stop();

        System.out.println("highVolumeGetRewards: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");
        Assertions.assertTrue(TimeUnit.MINUTES.toSeconds(20) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
    }

}
