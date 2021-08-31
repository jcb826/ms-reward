package tourGuide.msreward.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tourGuide.msreward.model.User;
import tourGuide.msreward.service.RewardsService;


@RestController
public class RewardController {

    @Autowired
    RewardsService rewardService;
	
    @RequestMapping("/")
    public String index() {
        return "Greetings from TourGuide!";
    }
    
    @RequestMapping("/getLocation") 
    public String getLocation(@RequestParam String userName) {
        /*

    	VisitedLocation visitedLocation = tourGuideService.getUserLocation(getUser(userName));
		return JsonStream.serialize(visitedLocation.location);

         */
        return "toto";
    }
    @PostMapping("/reward")
    public User getRewards(@RequestBody User user) {
        return rewardService.calculateRewards(user);
    }

}