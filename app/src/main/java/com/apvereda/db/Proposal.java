package com.apvereda.db;

public class Proposal extends Trip{
    private String sender;

    public Proposal(double originlat, double originlon, double destinationlat, double destinationlon, String date, String time, String sender) {
        super(originlat, originlon, destinationlat, destinationlon, date, time);
        this.sender = sender;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
