package com.example.nfcpro;

public class BoothData {
    private String boothId;
    private String name;
    private String description;
    private String location;

    public BoothData(String boothId, String name, String description, String location) {
        this.boothId = boothId;
        this.name = name;
        this.description = description;
        this.location = location;
    }

    public String getBoothId() { return boothId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getLocation() { return location; }
}