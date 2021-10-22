package tourGuide.msreward.service;


import org.apache.catalina.Executor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import rewardCentral.RewardCentral;
import tourGuide.msreward.consumer.GpsGateway;
import tourGuide.msreward.consumer.UserGateway;
import tourGuide.msreward.model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.stream.Collectors;

// dans ms-reward
@Service
public class RewardsService {
    private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;
    private final GpsGateway gpsGateway;
    private final UserGateway userGateway;
    private final RewardCentral rewardsCentral;
    private final List<Attraction> attractions;
    // proximity in miles
    private int defaultProximityBuffer = 10;
    private int proximityBuffer = defaultProximityBuffer;
    private int attractionProximityRange = 200;
    private ExecutorService executorService = Executors.newFixedThreadPool(100);

    public RewardsService(GpsGateway gpsGateway, UserGateway userGateway, RewardCentral rewardCentral, List<Attraction> attractions) {
        this.gpsGateway = gpsGateway;
        this.userGateway = userGateway;

        this.rewardsCentral = rewardCentral;
        this.attractions = attractions;
    }


    public void setProximityBuffer(int proximityBuffer) {
        this.proximityBuffer = proximityBuffer;
    }

    public void setDefaultProximityBuffer() {
        proximityBuffer = defaultProximityBuffer;
    }

    //  dans ms-user
    public void calculateRewards(User user, Attraction[] attractions) {

        List<VisitedLocation> userLocations = new ArrayList<>(user.getVisitedLocations());

        List<Attraction> attractions2 = Arrays.stream(attractions)
                .filter(attraction -> userDidntGotReward(user.getUserRewards(), attraction)).collect(Collectors.toList());

        List<Pair<VisitedLocation, Attraction>> collect = userLocations.stream().flatMap(l -> attractions2.parallelStream().map(a -> Pair.of(l, a)))

                .filter(p -> nearAttraction(p.getLeft(), p.getRight())).collect(Collectors.toList());
        CompletableFuture.runAsync(() ->
        {
            updateUser(user, collect);
        }, executorService);


    }

    private void updateUser(User user, List<Pair<VisitedLocation, Attraction>> collect) {

        collect
                .forEach(p -> user.addUserReward(new UserReward(p.getLeft(), p.getRight(), getRewardPoints(p.getRight(), user))));
        userGateway.updateUser(user);

    }

    public User calculateRewards(User user, VisitedLocation visitedLocation) {
        // all attractions
        List<Attraction> attractions = Arrays.stream(gpsGateway.getAttractions().getBody())
                // je retire les attractions pour lequels deja recompensé
                .filter(attraction -> userDidntGotReward(user.getUserRewards(), attraction)).collect(Collectors.toList());
        attractions.parallelStream()
                // on check si il est passé a proximité d'une des attraction restantes si oui on le récompense
                .filter(a -> nearAttraction(visitedLocation, a))
                .findFirst()
                .ifPresent(a -> user.addUserReward(new UserReward(visitedLocation, a, getRewardPoints(a, user))));

        return user;
    }


    private boolean userDidntGotReward(List<UserReward> userRewards, Attraction attraction) {

        return userRewards.stream().noneMatch(r -> r.attraction.attractionName.equals(attraction.attractionName));
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
