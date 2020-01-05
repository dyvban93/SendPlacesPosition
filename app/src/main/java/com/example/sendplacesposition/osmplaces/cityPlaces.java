package com.example.sendplacesposition.osmplaces;

public class cityPlaces {


    private String name;
    private double longitude;
    private double latitude;
    private String countryCode;
    private String city;
    private String address;

    public cityPlaces(String name, double longitude, double latitude, String countryCode, String city, String address) {
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
        this.countryCode = countryCode;
        this.city = city;
        this.address = address;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
