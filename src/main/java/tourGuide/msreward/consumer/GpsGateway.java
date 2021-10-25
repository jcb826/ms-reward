package tourGuide.msreward.consumer;


import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import tourGuide.msreward.model.Attraction;
import tourGuide.msreward.model.VisitedLocation;
import java.util.UUID;

@Component
public class GpsGateway {

    private final RestTemplate restTemplate;


    public GpsGateway(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<VisitedLocation> getUserLocation(UUID id) {

        return restTemplate.getForEntity("http://localhost:8090/gps/{uuid}/" + id.toString(), VisitedLocation.class);
    }

    public ResponseEntity<Attraction[]> getAttractions() {

        return restTemplate.getForEntity("http://localhost:8090/gps/attractions", Attraction[].class);
    }


}
