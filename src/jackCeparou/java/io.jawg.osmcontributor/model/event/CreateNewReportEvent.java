package io.jawg.osmcontributor.model.event;

/**
 * This event carries the request for a new report to be created with all the information needed
 */
public class CreateNewReportEvent {

    String title;
    String description;
    double latitude;
    double longitude;
    String imageFilePath;

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getImageFilePath() {
        return imageFilePath;
    }

    public CreateNewReportEvent(String title, String description, double latitude, double longitude, String imageFilePath) {

        this.title = title;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imageFilePath = imageFilePath;
    }
}
