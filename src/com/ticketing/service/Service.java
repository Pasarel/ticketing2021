package com.ticketing.service;
import com.sun.source.tree.Tree;

import javax.lang.model.element.Element;
import javax.naming.Name;
import java.io.*;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

public class Service {
    class TicketingException extends Exception {
        TicketingException(String str) {
            super(str);
        }
    }

    private BufferedWriter logging;
    static private TreeMap<Integer, Client> clients;
    static private TreeMap<Integer, Event> events;
    static private TreeMap<Integer, Location> locations;

    public Service() {
        clients = new TreeMap<Integer, Client>();
        events = new TreeMap<Integer, Event>();
        locations = new TreeMap<Integer, Location>();
    }
    private void logaction(String name) throws IOException {
        if (logging != null) {
            Date date = new Date();
            Long time = new Timestamp(date.getTime()).getTime();
            logging.write(name + "," + time + "\n");
        }
    }
    public void startlogging(String name) throws IOException {
        logging = new BufferedWriter(new FileWriter(name));
    }
    public void stoplogging() throws IOException {
        logging.close();
    }
    public Integer newClient(String name, String surname) throws IOException {
        Client newC = new Client(name, surname);
        Integer id = newC.getId();
        clients.put(id, newC);
        logaction("newClient");
        return id;
    }
    public void removeClient(Integer id) throws TicketingException, IOException {
        // GDPR duh
        if (clients.get(id) == null) {
            throw new TicketingException("com.ticketing.service removeClient(" + id + "): Client doesn't exist");
        }
        logaction("removeClient");
        clients.remove(id);
    }
    public void addEventToClient(Integer clientid, Integer eventid) throws TicketingException, IOException {
        Client c = clients.get(clientid);
        Event  e = events.get(eventid);
        if (c == null || e == null) {
            throw new TicketingException("com.ticketing.service: Client or event doesn't exist");
        }
        if (c.addEvent(e) == -1) {
            throw new TicketingException("com.ticketing.service: Not enough credit to add event " + e.getName() + " to client " + c.getFullName());
        }
        logaction("addEventToClient");
    }
    public void removeEventFromClient(Integer clientid, Integer eventid) throws TicketingException, IOException {
        Client c = clients.get(clientid);
        Event  e = events.get(eventid);
        if (c == null || e == null) {
            throw new TicketingException("com.ticketing.service: Client or event doesn't exist");
        }
        logaction("removeEventFromClient");
        c.removeEvent(e);
    }
    public void addCredit(Integer clientid, Float credit) throws TicketingException, IOException {
        Client c = clients.get(clientid);
        if (c == null) {
            throw new TicketingException("com.ticketing.service addCredit: " + clientid + " client doesn't exist");
        }
        logaction("addCredit");
        c.addCredit(credit);
    }
    public void printClientEvents(Integer clientid) throws TicketingException, IOException {
        Client c = clients.get(clientid);
        if (c == null) {
            throw new TicketingException("com.ticketing.service printClientEvents: " + clientid + " client doesn't exist");
        }
        System.out.println(c.getFullName() + ": ");
        TreeSet<Event> events = c.events;
        for (Event e : events) {
            System.out.println(e);
        }
        logaction("printClientEvents");
    }
    public void printClientInfo(Integer clientid) throws TicketingException, IOException {
        Client c = clients.get(clientid);
        if (c == null) {
            throw new TicketingException("com.ticketing.service printClientInfo: " + clientid + " client doesn't exist");
        }
        System.out.println(c.getFullName());
        System.out.println(c.getCredit());
        System.out.println("Next event: " + c.events.first());
        logaction("printClientInfo");
    }
    public Integer newEvent(String name, Integer locID, Float price, Date date) throws TicketingException, IOException {
        Location loc = locations.get(locID);
        if (loc == null) {
            throw new TicketingException("com.ticketing.service newEvent: " + locID + " location doesn't exist");
        }
        Event newE = new Event(name, loc, price, date);
        Integer id = newE.getId();
        events.put(id, newE);
        logaction("newEvent");
        return id;
    }
    public Integer newConcert(String name, Integer locID, Float price, Date date) throws TicketingException, IOException {
        Location loc = locations.get(locID);
        if (loc == null) {
            throw new TicketingException("com.ticketing.service newEvent: " + locID + " location doesn't exist");
        }
        Concert newE = new Concert(name, loc, price, date);
        Integer id = newE.getId();
        events.put(id, newE);
        logaction("newConcert");
        return id;
    }
    public Integer newPlay(String name, Integer locID, Float price, Date date) throws TicketingException, IOException {
        Location loc = locations.get(locID);
        if (loc == null) {
            throw new TicketingException("com.ticketing.service newEvent: " + locID + " location doesn't exist");
        }
        Play newE = new Play(name, loc, price, date);
        Integer id = newE.getId();
        events.put(id, newE);
        logaction("newPlay");
        return id;
    }
    public void removeEvent(Integer eventid) throws TicketingException, IOException {
        //  social distancing duh
        Event e = events.get(eventid);
        if (e == null) {
            throw new TicketingException("com.ticketing.service removeEvent: " + eventid + " client doesn't exist");
        }
        for (Map.Entry<Integer, Client> entry : clients.entrySet()) {
            TreeSet<Event> eventTree = (entry.getValue()).events;
            eventTree.remove(e);
        }
        events.remove(eventid);
        logaction("removeEvent");
    }
    public void printEventInfo(Integer eventid) throws TicketingException, IOException {
        Event e = events.get(eventid);
        if (e == null) {
            throw new TicketingException("com.ticketing.service removeEvent: " + eventid + " client doesn't exist");
        }
        System.out.println(e);
        logaction("printEventInfo");
    }

    public Integer newLocation(String name) throws IOException {
        Location newL = new Location(name);
        Integer id = newL.getId();
        locations.put(id, newL);
        logaction("newLocation");
        return id;
    }

    public void newLocation(Location loc) {
        locations.put(loc.getId(), loc);
    }

    public static class GenericSingletonReader<T> {
        private static Object instance = null;
        private Factory<T> factory;
        FileReader fileReader;
        BufferedReader reader = null;

        private GenericSingletonReader() {
        }

        public static Object getInstance() {
            if (instance == null) {
                instance = new GenericSingletonReader();
            }
            return instance;
        }

        public void setFactory(Factory<T> factory) {
            this.factory = factory;
        }

        public void setFile(String str) throws FileNotFoundException {
            fileReader = new FileReader(str);
            reader = new BufferedReader(fileReader);
        }

        public void closeFile() throws IOException {
            reader.close();
        }

        public T read() throws IOException, ParseException, EOFException {
            if (reader == null) {
                throw new IOException("GSR doesn't point to any stream!");
            }

            String line = reader.readLine();
            if (line == null) {
                throw new EOFException();
            }
            String readVals[] = line.split(",");
            T whatamireading = factory.create();

            try {
                if (whatamireading instanceof Location) {
                    if (readVals.length != 2) {
                        throw new ParseException("Invalid input file structure!", 0);
                    }
                    Integer id = Integer.parseInt(readVals[0]);
                    String name = readVals[1];
                    ((Location) whatamireading).set(name, id);
                } else if (whatamireading instanceof Event) {
                    if (readVals.length != 5) {
                        throw new ParseException("Invalid input file structure!", 0);
                    }
                    Integer id = Integer.parseInt(readVals[0]);
                    String Name = readVals[1];
                    Integer LocID = Integer.parseInt(readVals[2]);
                    Location Loc = locations.get(LocID);
                    if (Loc == null) throw new ParseException("Unrecognized location ID " + LocID.toString(), 0);
                    Date Data = new SimpleDateFormat("dd/MM/yyyy").parse(readVals[3]);
                    Float Price = Float.parseFloat(readVals[4]);
                    ((Event) whatamireading).set(Name, Loc, Price, Data, id);
                } else if (whatamireading instanceof Client) {
                    if (readVals.length < 5 || readVals.length != Integer.parseInt(readVals[4]) + 5) {
                        throw new ParseException("Invalid input file structure!", 0);
                    }
                    Integer id = Integer.parseInt(readVals[0]);
                    String Surname = readVals[1];
                    String Name = readVals[2];
                    Float Credit = Float.parseFloat(readVals[3]);
                    Integer eventCount = Integer.parseInt(readVals[4]);
                    TreeSet<Event> clientEvents = new TreeSet<Event>();
                    for (int i = 0; i < eventCount; i++) {
                        Integer idEvent = Integer.parseInt(readVals[i + 5]);
                        Event e = events.get(idEvent);
                        if (e == null) throw new ParseException("Unrecognized event ID " + idEvent.toString(), 0);
                        clientEvents.add(e);
                    }
                    ((Client) whatamireading).set(Name, Surname, Credit, clientEvents, id);
                }
            } catch (ParseException | NumberFormatException e) {
                throw e;
            }
            return whatamireading;
        }

        public void readAll() throws IOException, ParseException {
            try {
                T whatami = factory.create();
                if (whatami instanceof Location) {
                    while (true) {
                        Location read = (Location) (read());
                        locations.put(read.getId(), read);
                    }
                } else if (whatami instanceof Event) {
                    while (true) {
                        Event read = (Event) read();
                        events.put(read.getId(), read);
                    }
                } else if (whatami instanceof Client) {
                    while (true) {
                        Client read = (Client) read();
                        clients.put(read.getId(), read);
                    }
                }
            } catch (EOFException e) {
                return;
            }
            catch (IOException | ParseException e) {
                throw e;
            }
        }
    }
    public static class GenericSingletonWriter<T> {
        private static Object instance = null;
        private Factory<T> factory;
        FileWriter fileWriter;
        BufferedWriter writer = null;

        private GenericSingletonWriter() {
        }
        public static Object getInstance() {
            if (instance == null) {
                instance = new GenericSingletonWriter();
            }
            return instance;
        }
        public void setFactory(Factory<T> factory) {
            this.factory = factory;
        }

        public void setFile(String str) throws IOException {
            fileWriter = new FileWriter(str);
            writer = new BufferedWriter(fileWriter);
        }
        public void closeFile() throws IOException {
            writer.close();
        }

        public void write(int id) throws IOException {
            if (writer == null) {
                throw new IOException("GSW doesn't point to any stream!");
            }
            T whatami = factory.create();

            try {
                if (whatami instanceof Location) {
                    Location object = locations.get(id);
                    writer.write(object.getId().toString() + ',' + object.getName() + '\n');
                } else if (whatami instanceof Event) {
                    Event object = events.get(id);
                    String date = new SimpleDateFormat("dd/MM/yyyy").format(object.getDate());
                    writer.write(object.getId().toString() + ',' + ((Event) object).getName() + ',' + ((Event) object).getLocID() + ','
                            + date + ',' + ((Event) object).getPrice() + '\n');
                } else if (whatami instanceof Client) {
                    Client object = clients.get(id);
                    Integer size = ((Client) object).events.size();
                    writer.write(((Client) object).getId().toString() + ',' + ((Client) object).getFullName() + ',' +
                            ((Client) object).getCredit().toString() + ',' + size.toString() + ',');
                    for (Event e : ((Client) object).events) {
                        writer.write(e.getId().toString() + ',');
                    }
                    writer.write("\n");
                }
            } catch (IOException e) {
                throw e;
            }
        }
        public void write(T object) throws IOException {
            if (writer == null) {
                throw new IOException("GSW doesn't point to any stream!");
            }
            T whatami = factory.create();

            try {
                if (whatami instanceof Location) {
                    writer.write(((Location) object).getId().toString() + ',' + ((Location) object).getName() + '\n');
                } else if (whatami instanceof Event) {
                    String date = new SimpleDateFormat("dd/MM/yyyy").format(((Event) object).getDate());
                    writer.write(((Event) object).getId().toString() + ',' + ((Event) object).getName() + ',' + ((Event) object).getLocID() + ','
                            + date + ',' + ((Event) object).getPrice() + '\n');
                } else if (whatami instanceof Client) {
                    Integer size = ((Client) object).events.size();
                    writer.write(((Client) object).getId().toString() + ',' + ((Client) object).getFullName() + ',' +
                            ((Client) object).getCredit().toString() + ',' + size.toString() + ',');
                    for (Event e : ((Client) object).events) {
                        writer.write(e.getId().toString() + ',');
                    }
                    writer.write("\n");
                }
            } catch (IOException e) {
                throw e;
            }
        }
        public void writeAll() throws IOException {
            T whatami = factory.create();
            if (whatami instanceof Location) {
                for (Map.Entry<Integer, Location> entry: locations.entrySet()) {
                    Location loc = entry.getValue();
                    write((T) (loc));
                }
            }
            else if (whatami instanceof Event) {
                for (Map.Entry<Integer, Event> entry : events.entrySet()) {
                    Event e = entry.getValue();
                    write((T) (e));
                }
            }
            else if (whatami instanceof Client) {
                for (Map.Entry<Integer, Client> entry : clients.entrySet()) {
                    Client c = entry.getValue();
                    write((T) (c));
                }
            }
        }
    }
}