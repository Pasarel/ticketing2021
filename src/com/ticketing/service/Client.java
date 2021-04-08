package com.ticketing.service;

import java.util.TreeSet;

public class Client {
    private static Integer count = 0;
    private Integer id;
    private String name, surname;
    TreeSet<Event> events;
    private Float credit;

    public Client(String name, String surname) {
        this.id = count;
        this.name = name;
        this.surname = surname;
        this.credit = 0F;
        events = new TreeSet();
        count++;
    }
    public Integer getId() {
        return id;
    }
    public String getFullName() {
        return name + " " + surname;
    }
    public Float getCredit() {
        return credit;
    }
    void addCredit(Float credit) {
        this.credit += credit;
    }
    private int removeCredit(Float credit) {
        if (credit > this.credit)
            return -1;
        this.credit -= credit;
        return 1;
    }
    int addEvent(Event e) {
       if (removeCredit(e.getPrice()) == -1) {
           return -1;
       }
       events.add(e);
       return 1;
    }
    void removeEvent(Event e) {
        addCredit(e.getPrice());
        events.remove(e);
    }
}