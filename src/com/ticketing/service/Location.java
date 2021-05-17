package com.ticketing.service;

public class Location implements Factory<Location> {
    private static int count = 0;
    int id;
    String name;

    public Location(String name) {
        this.name = name;
        id = count;
        count++;
    }
    void set(String name, Integer ID) {
        this.name = name;
        this.id = ID;
        count = id;
        count++;
    }

    public Location() {

    }

    @Override
    public String toString() {
        return name;
    }
    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public Location create() {
        return new Location();
    }
}