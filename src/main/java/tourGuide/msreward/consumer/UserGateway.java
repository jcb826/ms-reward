package tourGuide.msreward.consumer;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
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
    public ResponseEntity<Void> updateUser(User user){
        HttpEntity<User> request = new HttpEntity<>(user);
        return restTemplate.exchange("http://localhost:8091/user/"+user.getUserName(), HttpMethod.PUT,request,Void.class);
    }
}
