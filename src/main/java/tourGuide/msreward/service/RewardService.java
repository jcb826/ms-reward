package tourGuide.msreward.service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import org.springframework.stereotype.Service;
import rewardCentral.RewardCentral;
import tourGuide.msreward.consumer.GpsGateway;
import tourGuide.msreward.model.Attraction;
import tourGuide.msreward.model.User;
import tourGuide.msreward.model.UserReward;
import tourGuide.msreward.model.VisitedLocation;
import tourGuide.user.User;
import tourGuide.user.UserReward;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// dans ms-reward
@Service
public class RewardService {
    private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;
    private final GpsGateway gpsGateway;
    private final RewardCentral rewardsCentral;
    // proximity in miles
    private int defaultProximityBuffer = 10;
    private int proximityBuffer = defaultProximityBuffer;
    private int attractionProximityRange = 200;

    public RewardService(GpsGateway gpsGateway, RewardCentral rewardCentral) {
        this.gpsGateway = gpsGateway;

        this.rewardsCentral = rewardCentral;
    }

    public void setProximityBuffer(int proximityBuffer) {
        this.proximityBuffer = proximityBuffer;
    }

    public void setDefaultProximityBuffer() {
        proximityBuffer = defaultProximityBuffer;
    }

    //  dans ms-user
    public User calculateRewards(User user) {
        List<VisitedLocation> userLocations = new ArrayList<>(user.getVisitedLocations());
        List<Attraction> attractions = Arrays.asList(gpsGateway.getAttractions().getBody());

        for (VisitedLocation visitedLocation : userLocations) {
            for (Attraction attraction : attractions) {
                if (user.getUserRewards().stream().noneMatch(r -> r.attraction.attractionName.equals(attraction.attractionName))) {
                    if (nearAttraction(visitedLocation, attraction)) {
                        user.addUserReward(new UserReward(visitedLocation, attraction, getRewardPoints(attraction, user)));
                    }
                }
            }
        }
        return user;
    }

    // user
    public boolean isWithinAttractionProximity(Attraction attraction, Location location) {
        return (getDistance(attraction, location) < attractionProximityRange);
    }

    // user
    private boolean nearAttraction(VisitedLocation visitedLocation, Attraction attraction) {
        return getDistance(attraction, visitedLocation.location) > proximityBuffer ? false : true;
    }

    //reward
    private int getRewardPoints(Attraction attraction, User user) {
        return rewardsCentral.getAttractionRewardPoints(attraction.attractionId, user.getUserId());
    }

    // user
    public double getDistance(Location loc1, Location loc2) {
        double lat1 = Math.toRadians(loc1.latitude);
        double lon1 = Math.toRadians(loc1.longitude);
        double lat2 = Math.toRadians(loc2.latitude);
        double lon2 = Math.toRadians(loc2.longitude);

        double angle = Math.acos(Math.sin(lat1) * Math.sin(lat2)
                + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

        double nauticalMiles = 60 * Math.toDegrees(angle);
        double statuteMiles = STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles;
        return statuteMiles;
    }

}
