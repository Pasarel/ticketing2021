package com.ticketing.service;

public class Location {
    private static int count = 0;
    private String name;
    int id;

    public Location(String name) {
        this.name = name;
        id = count;
        count++;
    }
    @Override
    public String toString() {
        return name;
    }

    public int getId() {
        return id;
    }
}