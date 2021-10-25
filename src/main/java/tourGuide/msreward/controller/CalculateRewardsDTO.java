package tourGuide.msreward.controller;

import tourGuide.msreward.model.User;
import tourGuide.msreward.model.VisitedLocation;


public class CalculateRewardsDTO {

    private User user;
    private VisitedLocation visitedLocation;

    public VisitedLocation getVisitedLocation() {
        return visitedLocation;
    }

    public void setVisitedLocation(VisitedLocation visitedLocation) {
        this.visitedLocation = visitedLocation;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
