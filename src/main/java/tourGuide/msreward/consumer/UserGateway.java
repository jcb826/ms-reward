package tourGuide.msreward.consumer;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import tourGuide.msreward.model.Attraction;
import tourGuide.msreward.model.User;

import java.util.List;

@Component
public class UserGateway {

    private final RestTemplate restTemplate;


    public UserGateway(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<User[]> getAllUsers(){
        // appel du micro service
        return restTemplate.getForEntity("http://localhost:8091/user/users",User[].class);
    }
}
