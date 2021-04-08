package com.ticketing.service;
import java.util.Date;

public class Event implements Comparable<Event> {
    private static Integer count = 0;
    private Integer id;
    private String name;
    private Location loc;
    private Date date;
    private Float price;

    public Event(String name, Location loc, Float price, Date date) {
        this.id = count;
        this.name  = name;
        this.loc   = loc;
        this.date  = date;
        this.price = price;
        count++;
    }
    @Override
    public int compareTo(Event event) {
        return this.date.compareTo(event.date);
    }
    public String toString() {
        return date + ": " + name + ", " + loc;
    }
    public Integer getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public Float getPrice() {
        return price;
    }
    public Location getLoc() {
        return loc;
    }
    public Date getDate() {
        return date;
    }
    void setPrice(Float price) {
        this.price = price;
    }
}

class Concert extends Event {
    public Concert(String name, Location loc, Float price, Date date) {
        super(name, loc, price, date);
    }
}

class Play extends Event {
    public Play(String name, Location loc, Float price, Date date) {
        super(name, loc, price, date);
    }
}