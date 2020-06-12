package com.edge2.registration.models;

import java.util.Arrays;

public class User {
    private String _id;
    private String name;
    private String email;
    private String stream;
    private String year;
    private String instituteName;
    private String contact;
    private boolean verified;
    private Event [] events;
    private double total;
    //private String photo;
    private String receipt;

    public User() {
    }

    public String get_id() {
        return _id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getStream() {
        return stream;
    }

    public String getYear() {
        return year;
    }

    public String getInstituteName() {
        return instituteName;
    }

    public String getContact() {
        return contact;
    }

    public boolean isVerified() {
        return verified;
    }

    public Event[] getEvents() {
        return events;
    }

    public double getTotal() {
        return total;
    }

    /*public String getPhoto() {
        return photo;
    }*/

    public String getReceipt() {
        return receipt;
    }

    @Override
    public String toString() {
        return "User{" +
                "_id='" + _id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", stream='" + stream + '\'' +
                ", year='" + year + '\'' +
                ", instituteName='" + instituteName + '\'' +
                ", contact='" + contact + '\'' +
                ", verified=" + verified +
                ", events=" + Arrays.toString(events) +
                ", total=" + total +
                //", photo='" + photo + '\'' +
                ", receipt='" + receipt + '\'' +
                '}';
    }
}
