package com.ticketing.service;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

public class Service {
    private TreeMap<Integer, Client> clients;
    private TreeMap<Integer, Event> events;
    private TreeMap<Integer, Location> locations;

    public Service() {
        clients = new TreeMap<Integer, Client>();
        events = new TreeMap<Integer, Event>();
        locations = new TreeMap<Integer, Location>();
    }
    public Integer newClient(String name, String surname) {
        Client newC = new Client(name, surname);
        Integer id = newC.getId();
        clients.put(id, newC);
        return id;
    }
    public int removeClient(Integer id) {
        // GDPR duh
        if (clients.get(id) == null) {
            System.out.println("com.ticketing.service removeClient(" + id + "): Client doesn't exist");
            return -1;
        }
        clients.remove(id);
        return 1;
    }
    public int addEventToClient(Integer clientid, Integer eventid) {
        Client c = clients.get(clientid);
        Event  e = events.get(eventid);
        if (c == null || e == null) {
            System.out.println("com.ticketing.service: Client or event doesn't exist");
            return -1;
        }
        if (c.addEvent(e) == -1) {
            System.out.println("com.ticketing.service: Not enough credit to add event " + e.getName() + " to client " + c.getFullName());
        }
        return 1;
    }
    public int removeEventFromClient(Integer clientid, Integer eventid) {
        Client c = clients.get(clientid);
        Event  e = events.get(eventid);
        if (c == null || e == null) {
            System.out.println("com.ticketing.service: Client or event doesn't exist");
            return -1;
        }
        c.removeEvent(e);
        return 1;
    }
    public int addCredit(Integer clientid, Float credit) {
        Client c = clients.get(clientid);
        if (c == null) {
            System.out.println("com.ticketing.service addCredit: " + clientid + " client doesn't exist");
            return -1;
        }
        c.addCredit(credit);
        return 1;
    }
    public int printClientEvents(Integer clientid) {
        Client c = clients.get(clientid);
        if (c == null) {
            System.out.println("com.ticketing.service printClientEvents: " + clientid + " client doesn't exist");
            return -1;
        }
        System.out.println(c.getFullName() + ": ");
        TreeSet<Event> events = c.events;
        for (Event e : events) {
            System.out.println(e);
        }
        return 1;
    }
    public int printClientInfo(Integer clientid) {
        Client c = clients.get(clientid);
        if (c == null) {
            System.out.println("com.ticketing.service printClientInfo: " + clientid + " client doesn't exist");
            return -1;
        }
        System.out.println(c.getFullName());
        System.out.println(c.getCredit());
        System.out.println("Next event: " + c.events.first());
        return 1;
    }
    public Integer newEvent(String name, Integer locID, Float price, Date date) {
        Location loc = locations.get(locID);
        if (loc == null) {
            System.out.println("com.ticketing.service newEvent: " + locID + " location doesn't exist");
            return null;
        }
        Event newE = new Event(name, loc, price, date);
        Integer id = newE.getId();
        events.put(id, newE);
        return id;
    }
    public Integer newConcert(String name, Integer locID, Float price, Date date) {
        Location loc = locations.get(locID);
        if (loc == null) {
            System.out.println("com.ticketing.service newEvent: " + locID + " location doesn't exist");
            return null;
        }
        Concert newE = new Concert(name, loc, price, date);
        Integer id = newE.getId();
        events.put(id, newE);
        return id;
    }
    public Integer newPlay(String name, Integer locID, Float price, Date date) {
        Location loc = locations.get(locID);
        if (loc == null) {
            System.out.println("com.ticketing.service newEvent: " + locID + " location doesn't exist");
            return null;
        }
        Play newE = new Play(name, loc, price, date);
        Integer id = newE.getId();
        events.put(id, newE);
        return id;
    }
    public int removeEvent(Integer eventid) {
        //  social distancing duh
        Event e = events.get(eventid);
        if (e == null) {
            System.out.println("com.ticketing.service removeEvent: " + eventid + " client doesn't exist");
            return -1;
        }
        for (Map.Entry<Integer, Client> entry : clients.entrySet()) {
            TreeSet<Event> eventTree = (entry.getValue()).events;
            eventTree.remove(e);
        }
        events.remove(eventid);
        return 1;
    }
    public int printEventInfo(Integer eventid) {
        Event e = events.get(eventid);
        if (e == null) {
            System.out.println("com.ticketing.service removeEvent: " + eventid + " client doesn't exist");
            return -1;
        }
        System.out.println(e);
        return 1;
    }

    public Integer newLocation(String name) {
        Location newL = new Location(name);
        Integer id = newL.getId();
        locations.put(id, newL);
        return id;
    }
}
