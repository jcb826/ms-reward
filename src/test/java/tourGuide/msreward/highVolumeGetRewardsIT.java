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

import java.util.*;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class highVolumeGetRewardsIT {


    @Autowired
    GpsGateway gpsGateway;
    @Autowired
    UserGateway userGateway;
    @Autowired
    RewardsService rewardsService;

    @Test
    void contextLoads() {
    }

    @Test
    public void highVolumeGetRewards() throws InterruptedException {
        StopWatch stopWatch = new StopWatch();
        Attraction[] attractions = gpsGateway.getAttractions().getBody();
        RewardsService rewardsService = new RewardsService(gpsGateway, userGateway, new RewardCentral(), Arrays.stream(attractions).toList());

        // Users should be incremented up to 100,000, and test finishes within 20 minutes


        Attraction attraction = Arrays.stream(gpsGateway.getAttractions().getBody()).toList().get(0);

        List<User> allUsers = Arrays.stream(userGateway.getAllUsers().getBody()).toList();
        System.out.println(allUsers.size());
        allUsers.parallelStream().forEach(u -> u.addToVisitedLocations(new VisitedLocation(u.getUserId(), attraction, new Date())));
        stopWatch.start();
        allUsers.parallelStream().forEach(u -> rewardsService.calculateRewards(u, attractions));
        for (User user : allUsers) {
            while (user.getUserRewards().size() == 0) {
                TimeUnit.MILLISECONDS.sleep(200);
            }
        }

        stopWatch.stop();

        for (User user : allUsers) {
            Assertions.assertTrue(user.getUserRewards().size() > 0);
        }

        System.out.println("highVolumeGetRewards: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");
        Assertions.assertTrue(TimeUnit.MINUTES.toSeconds(20) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
    }




}
