package com.edge2.registration.models;

public class Event {
    private String _id;
    private String name;
    private String description;
    private double fees;
    private boolean couponAvailable;

    public Event() {
    }

    public String get_id() {
        return _id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getFees() {
        return fees;
    }

    public boolean isCouponAvailable() {
        return couponAvailable;
    }
}
