package com.app.snapsapp.models;

public class SnapModelClass {
    private String id;
    private String description;
    private String imageUrl;
    private double latitude;
    private double longitude;

    public SnapModelClass() { }

    public SnapModelClass(String id, String description, String imageUrl, double latitude, double longitude) {
        this.id = id;
        this.description = description;
        this.imageUrl = imageUrl;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
