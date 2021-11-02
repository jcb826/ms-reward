package tourGuide.msreward.consumer;
import tourGuide.msreward.model.Attraction;
import tourGuide.msreward.model.Location;

public class ProximityDTO {
    private Attraction attraction;
    private Location location;

    public ProximityDTO(Attraction attraction, Location location) {
        this.attraction = attraction;
        this.location = location;
    }

    public Attraction getAttraction() {
        return attraction;
    }

    public void setAttraction(Attraction attraction) {
        this.attraction = attraction;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
