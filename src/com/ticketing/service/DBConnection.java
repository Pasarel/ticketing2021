package com.ticketing.service;

import com.ticketing.service.*;
import java.sql.*;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;

public class DBConnection {
    private static DBConnection instance = null;
    private static Connection connection;
    private PreparedStatement insertJunction;
    private PreparedStatement readJunctions;
    private PreparedStatement removeJunction;
    private PreparedStatement insertClient;
    private PreparedStatement selectClient;
    private PreparedStatement selectClients;
    private PreparedStatement updateClientFName;
    private PreparedStatement updateClientSurname;
    private PreparedStatement updateClientCredit;
    private PreparedStatement removeClient;
    private PreparedStatement insertEvent;
    private PreparedStatement selectEvent;
    private PreparedStatement selectEvents;
    private PreparedStatement updateEventName;
    private PreparedStatement updateEventCost;
    private PreparedStatement updateEventDate;
    private PreparedStatement updateEventLocation;
    private PreparedStatement removeEvent;
    private PreparedStatement insertLoc;
    private PreparedStatement selectLoc;
    private PreparedStatement selectLocs;
    private PreparedStatement updateLoc;
    private PreparedStatement removeLoc;

    public static DBConnection getInstance() throws SQLException {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }
    public static Boolean isInstanced() {
        return (instance != null);
    }
    private DBConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/pao_project";
        String user = "root";
        String password = "";
        connection = DriverManager.getConnection(url, user, password);

        insertClient = connection.prepareStatement("insert into clients values (?,?,?,?)"); // id,nume,prenume,credit
        selectClients = connection.prepareStatement("select * from clients");
        selectClient = connection.prepareStatement("select id,nume,prenume,credit from clients where id =?"); // id
        updateClientFName = connection.prepareStatement("update clients set prenume = ? where id = ?"); // prenume,id
        updateClientSurname = connection.prepareStatement("update clients set nume = ? where id = ?"); // nume,id
        updateClientCredit = connection.prepareStatement("update clients set credit = ? where id = ?"); // credit,id
        removeClient = connection.prepareStatement("delete from clients where id = ?"); // id

        insertEvent = connection.prepareStatement("insert into events values (?,?,?,?,?,?)"); // id,nume,locID,data,pret,type
        selectEvents= connection.prepareStatement("select * from events"); // id
        selectEvent = connection.prepareStatement("select id,nume,loc_id,data_pret,type from events where id =?"); // id
        updateEventName = connection.prepareStatement("update events set nume = ? where id = ?"); // nume,id
        updateEventLocation = connection.prepareStatement("update events set loc_id = ? where id = ?"); // loc_id,id
        updateEventDate = connection.prepareStatement("update events set data = ? where id = ?"); // data,id
        updateEventCost = connection.prepareStatement("update events set pret = ? where id = ?"); // cost,id
        removeEvent = connection.prepareStatement("delete from events where id = ?"); // id

        insertLoc = connection.prepareStatement("insert into locations values (?,?)"); // id,nume
        selectLocs= connection.prepareStatement("select * from locations"); // id
        selectLoc = connection.prepareStatement("select id,nume from locations where id =?"); // id
        updateLoc = connection.prepareStatement("update locations set nume = ? where id = ?"); // nume,id
        removeLoc = connection.prepareStatement("delete from locations where id = ?"); // id

        insertJunction  = connection.prepareStatement("insert into clients_events_junction values (?,?,?)");
        readJunctions   = connection.prepareStatement("select id_event from clients_events_junction where id_client =?");
        removeJunction = connection.prepareStatement("delete from clients_events_junction where id_junction = ?"); // id
    }
    public void close() throws SQLException {
        insertClient.close();
        selectClient.close();
        updateClientFName.close();
        updateClientSurname.close();
        updateClientCredit.close();
        removeClient.close();
        insertEvent.close();
        selectEvent.close();
        updateEventName.close();
        updateEventLocation.close();
        updateEventDate.close();
        updateEventCost.close();
        removeEvent.close();
        insertLoc.close();
        selectLoc.close();
        updateLoc.close();
        removeLoc.close();
        connection.close();
    }
    // CRUD for clients
    public void createClient(Client c) throws SQLException {
        insertClient.setInt(1, c.getId());
        insertClient.setString(2, c.getSurname());
        insertClient.setString(3, c.getName());
        insertClient.setFloat(4, c.getCredit());
        insertClient.executeUpdate();
        for (Event e: c.getEvents()) {
            insertJunction.setString(1, c.getId().toString() + "-" + e.getId().toString());
            insertJunction.setInt(2, c.getId());
            insertJunction.setInt(3, e.getId());
            insertJunction.executeUpdate();
        }
    }
    private Client newClient(String prenume, String nume, Float credit, int id, TreeMap<Integer, Event> all_events) throws SQLException {
        Client new_client = new Client("place", "holder");
        TreeSet<Event> client_events = new TreeSet();
        readJunctions.setInt(1, id);
        ResultSet res = readJunctions.executeQuery();
        while (res.next()) {
            Integer id_event = res.getInt("id_event");
            client_events.add(all_events.get(id_event));
        }
        new_client.set(prenume, nume, credit, client_events, id);
        return new_client;
    }
    public Client readClient(int id, TreeMap<Integer, Event> events) throws SQLException {
        selectClient.setInt(1, id);
        ResultSet res = selectClient.executeQuery();
        return newClient(res.getString("prenume"), res.getString("nume"), res.getFloat("credit"), res.getInt("id"), events);
    }
    public TreeMap<Integer, Client> readClients(TreeMap<Integer, Event> events) throws  SQLException {
        TreeMap<Integer, Client> clients = new TreeMap<Integer, Client>();
        ResultSet res = selectClients.executeQuery();
        while (res.next()) {
            Integer id = res.getInt("id");
            clients.put(id, newClient(res.getString("prenume"), res.getString("nume"), res.getFloat("credit"), id, events));
        }
        return clients;
    }
    public void modifyClientFName(int id, String fname) throws SQLException {
        updateClientFName.setInt(2, id);
        updateClientFName.setString(1, fname);
        updateClientFName.executeUpdate();
    }
    public void modifyClientSurname(int id, String name) throws SQLException {
        updateClientSurname.setInt(2, id);
        updateClientSurname.setString(1, name);
        updateClientSurname.executeUpdate();
    }
    public void modifyClientCredit(int id, float credit) throws SQLException {
        updateClientCredit.setFloat(1, credit);
        updateClientCredit.setInt(2, id);
        updateClientCredit.execute();
    }
    public void deleteClient(int id, TreeMap<Integer, Client> clients) throws SQLException {
        Client c = clients.get(id);
        removeClient.setInt(1, id);
        removeClient.executeUpdate();
        for (Event e: c.getEvents()) {
            removeJunction.setString(1, Integer.valueOf(id).toString() + "-" + Integer.valueOf(e.getId()).toString());
            removeJunction.executeUpdate();
        }
    }

    //CRUD for events
    public void createEvent(Event e) throws SQLException {
        insertEvent.setInt(1,e.getId());
        insertEvent.setString(2,e.getName());
        insertEvent.setInt(3,e.getLocID());
        insertEvent.setLong(4,e.getDate().getTime());
        insertEvent.setFloat(5,e.getPrice());
        if (e instanceof Concert) {
            insertEvent.setString(6, "concert");
        }
        else if (e instanceof Play) {
            insertEvent.setString(6, "play");
        }
        else {
            insertEvent.setString(6, "event");
        }
        insertEvent.executeUpdate();
    }
    private Event newEvent(String name, Location loc, Float pret, Date data, String type, int ide) {
        Event e;
        if (type == "concert") {
            e = new Concert("placeholder", null, pret, data);
        }
        else if (type == "play") {
            e = new Play("placeholder", null, pret, data);
        }
        else {
            e = new Event("placeholder", null, pret, data);
        }
        e.set(name, loc, pret, data, ide);
        return e;
    }
    public Event readEvent(int id, TreeMap<Integer, Location> locations) throws SQLException {
        selectEvent.setInt(1,id);
        ResultSet res = selectEvent.executeQuery();
        int ide = res.getInt("id");
        String name = res.getString("nume");
        Location loc = locations.get(res.getInt("loc_id"));
        Date data = new Date(res.getLong("data"));
        float pret = res.getFloat("pret");
        String type = res.getString("type");
        return newEvent(name, loc, pret, data, type, ide);
    }
    public TreeMap<Integer, Event> readEvents(TreeMap<Integer, Location> locations) throws  SQLException {
        TreeMap<Integer, Event> events = new TreeMap<Integer, Event>();
        ResultSet res = selectEvents.executeQuery();
        while (res.next()) {
            int ide = res.getInt("id");
            String name = res.getString("nume");
            Location loc = locations.get(res.getInt("loc_id"));
            Date data = new Date(res.getLong("data"));
            float pret = res.getFloat("pret");
            String type = res.getString("type");
            events.put(ide, newEvent(name, loc, pret, data, type, ide));
        }
        return events;
    }
    public void modifyEventName(int id, String nume) throws SQLException {
        updateEventName.setString(1, nume);
        updateEventName.setInt(2, id);
        updateEventName.executeUpdate();
    }
    public void modifyEventLocation(int id, int loc_id) throws SQLException {
        updateEventName.setInt(1, loc_id);
        updateEventName.setInt(2, id);
        updateEventName.executeUpdate();
    }
    public void modifyEventDate(int id, long data) throws SQLException {
        updateEventName.setLong(1, data);
        updateEventName.setInt(2, id);
        updateEventName.executeUpdate();
    }
    public void modifyEventCost(int id, float cost) throws SQLException {
        updateEventName.setFloat(1, cost);
        updateEventName.setInt(2, id);
        updateEventName.executeUpdate();
    }
    public void deleteEvent(int id) throws SQLException {
        removeEvent.setInt(1, id);
        removeEvent.executeUpdate();
    }

    //CRUD for locations
    public void createLocation(Location l) throws SQLException {
        insertLoc.setInt(1,l.getId());
        insertLoc.setString(2,l.getName());
        insertLoc.executeUpdate();
    }
    private Location newLocation(String name, int idl) {
        Location l = new Location("placeholder");
        l.set(name, idl);
        return l;
    }
    public Location readLocation(int id) throws SQLException {
        selectLoc.setInt(1,id);
        ResultSet res = selectLoc.executeQuery();
        int idl = res.getInt("id");
        String name = res.getString("nume");
        return newLocation(name, idl);
    }
    public TreeMap<Integer, Location> readLocations() throws  SQLException {
        TreeMap<Integer, Location> locations = new TreeMap<Integer, Location>();
        ResultSet res = selectLocs.executeQuery();
        while (res.next()) {
            int idl = res.getInt("id");
            String name = res.getString("nume");
            locations.put(idl, newLocation(name, idl));
        }
        return locations;
    }
    public void modifyLocation(int id, String name) throws SQLException {
        updateLoc.setString(1, name);
        updateLoc.setInt(2, id);
        updateLoc.executeUpdate();
    }
    public void deleteLocation(int id) throws SQLException {
        removeLoc.setInt(1, id);
        removeLoc.executeUpdate();
    }

    public void addJunction(int id_client, int id_event) throws SQLException {
        insertJunction.setString(1, Integer.valueOf(id_client).toString() + "-" + Integer.valueOf(id_event).toString());
        insertJunction.setInt(2, id_client);
        insertJunction.setInt(3, id_event);
        insertJunction.executeUpdate();
    }

    public void removeJunction(int id_client, int id_event) throws SQLException {
        removeJunction.setString(1, Integer.valueOf(id_client).toString() + "-" + Integer.valueOf(id_event).toString());
        removeJunction.executeUpdate();
    }
}
