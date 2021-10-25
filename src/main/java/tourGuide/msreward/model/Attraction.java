package tourGuide.msreward.model;


public class Attraction extends Location {
    public String attractionName;
    public String city;
    public String state;
    public String attractionId;
    public Double latitude;
    public Double longitude;

    public Attraction() {

    }

    public Attraction(double latitude, double longitude, String attractionName, String city, String state, String attractionId) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.attractionName = attractionName;
        this.city = city;
        this.state = state;
        this.attractionId = attractionId;
    }

    public String getAttractionName() {
        return attractionName;
    }

    public void setAttractionName(String attractionName) {
        this.attractionName = attractionName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getAttractionId() {
        return attractionId;
    }

    public void setAttractionId(String attractionId) {
        this.attractionId = attractionId;
    }
}