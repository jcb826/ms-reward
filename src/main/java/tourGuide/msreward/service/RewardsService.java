package tourGuide.msreward.service;


import org.springframework.stereotype.Service;
import rewardCentral.RewardCentral;
import tourGuide.msreward.consumer.GpsGateway;
import tourGuide.msreward.model.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

// dans ms-reward
@Service
public class RewardsService {
    private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;
    private final GpsGateway gpsGateway;
    private final RewardCentral rewardsCentral;
    // proximity in miles
    private int defaultProximityBuffer = 10;
    private int proximityBuffer = defaultProximityBuffer;
    private int attractionProximityRange = 200;

    public RewardsService(GpsGateway gpsGateway, RewardCentral rewardCentral) {
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
        List<Attraction> attractions = Arrays.stream(gpsGateway.getAttractions().getBody())
                .filter(attraction -> userDidntGotReward(user,attraction)).collect(Collectors.toList());

        for (VisitedLocation visitedLocation : userLocations) {
            for (Attraction attraction : attractions) {
                    if (nearAttraction(visitedLocation, attraction)) {
                        user.addUserReward(new UserReward(visitedLocation, attraction, getRewardPoints(attraction, user)));
                    }

            }
        }
        return user;
    }
    private boolean userDidntGotReward(User user,Attraction attraction){

        return user.getUserRewards().stream().noneMatch(r -> r.attraction.attractionName.equals(attraction.attractionName));
    }

    // user
    public boolean isWithinAttractionProximity(Attraction attraction, Attraction location) {
        return (getDistance(attraction, location) < attractionProximityRange);
    }

    // user
    private boolean nearAttraction(VisitedLocation visitedLocation, Attraction attraction) {
        return getDistance(attraction, visitedLocation.location) > proximityBuffer ? false : true;
    }

    //reward
    private int getRewardPoints(Attraction attraction, User user) {
        return rewardsCentral.getAttractionRewardPoints(UUID.fromString(attraction.getAttractionId()), user.getUserId());
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
