package com.ticketing;

import com.ticketing.service.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

public class Main {

    public static Float randomNumber(long min, long max) {
        return min + (float) Math.random() * (max - min);
    }

    public static Date randomDate() {
        Date d = new Date();
        long randomEpoch = ThreadLocalRandom.current().nextLong(d.getTime(), d.getTime() + 100000000000L);
        return new Date(randomEpoch);
    }

    public static void populateCSV(Service iabilet) throws Service.TicketingException, IOException, SQLException {//iabilet.removeClient(1234);
        Service.GenericSingletonWriter<Location> l = (Service.GenericSingletonWriter<Location>) (Service.GenericSingletonWriter.getInstance());
        l.setFactory(new Location());
        l.setFile("src/locations.csv");

        initializations(iabilet);

        l.writeAll();
        l.closeFile();

        Service.GenericSingletonWriter<Event> e = (Service.GenericSingletonWriter<Event>) (Service.GenericSingletonWriter.getInstance());
        e.setFactory(new Event());
        e.setFile("src/events.csv");
        e.writeAll();
        e.closeFile();

        Service.GenericSingletonWriter<Client> c = (Service.GenericSingletonWriter<Client>) (Service.GenericSingletonWriter.getInstance());
        c.setFactory(new Client());
        c.setFile("src/clients.csv");
        c.writeAll();
        c.closeFile();
    }

    public static void manipulateCSV(Service iabilet) throws IOException, ParseException, Service.TicketingException, SQLException {
        Service.GenericSingletonReader<Location> l = (Service.GenericSingletonReader<Location>) (Service.GenericSingletonReader.getInstance());
        l.setFactory(new Location());
        l.setFile("src/locations.csv");
        l.readAll();
        l.closeFile();

        Service.GenericSingletonReader<Event> e = (Service.GenericSingletonReader<Event>) (Service.GenericSingletonReader.getInstance());
        e.setFactory(new Event());
        e.setFile("src/events.csv");
        e.readAll();
        e.closeFile();

        Service.GenericSingletonReader<Client> c = (Service.GenericSingletonReader<Client>) (Service.GenericSingletonReader.getInstance());
        c.setFactory(new Client());
        c.setFile("src/clients.csv");
        c.readAll();
        c.closeFile();
    }

    public static void initializations(Service iabilet) throws SQLException, IOException, Service.TicketingException {
        Integer idMarianPreda = iabilet.newClient("Marian", "Preda");
        Integer idMirceaDumitru = iabilet.newClient("Mircea", "Dumitru");
        Integer idTeodoriu = iabilet.newClient("Ecaterina", "Teodoroiu");
        Integer idPop = iabilet.newClient("Liviu", "Pop");
        Integer idAndronescu = iabilet.newClient("Ecaterina", "Andronescu");

        Integer idSala = iabilet.newLocation("Sala Palatului");
        Integer idRomexpo = iabilet.newLocation("Romexpo");
        Integer Nottara = iabilet.newLocation("Nottara");

        Integer idEu = iabilet.newConcert("Eu", idRomexpo, randomNumber(0, 100), randomDate());
        Integer idIDK = iabilet.newEvent("IDK", idRomexpo, randomNumber(0, 100), randomDate());
        Integer idFuego = iabilet.newConcert("Fuego", idSala, randomNumber(0, 100), randomDate());
        Integer idShakespeare = iabilet.newPlay("Shakespeare", Nottara, randomNumber(0, 100), randomDate());

        iabilet.addCredit(idMarianPreda, 10000.2F);
        iabilet.addEventToClient(idMarianPreda, idEu);
        iabilet.addEventToClient(idMarianPreda, idFuego);
        iabilet.removeEventFromClient(idMarianPreda, idFuego);
        iabilet.addEventToClient(idMarianPreda, idFuego);

        iabilet.addCredit(idTeodoriu, 10000.2F);
        iabilet.addCredit(idMirceaDumitru, 25.2F);
        iabilet.addEventToClient(idTeodoriu, idShakespeare);
        iabilet.addEventToClient(idTeodoriu, idEu);
        iabilet.addEventToClient(idTeodoriu, idTeodoriu);
    }

    public static void operations(Service iabilet) throws Service.TicketingException, SQLException, IOException {
        iabilet.printEventInfo(3);
        iabilet.addEventToClient(2, 3);
        iabilet.printClientEvents(2);
        iabilet.printClientInfo(1);
        iabilet.printClientInfo(2);
        try {
            iabilet.addEventToClient(1, 3); // should throw exception
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        /*
        try {
            iabilet.removeClient(50);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        iabilet.removeClient(3);
        iabilet.removeEventFromClient(1, 3);
        iabilet.removeEvent(2);
         */
    }

    public static void mixed() {
        Service iabilet = new Service();
        try {
            iabilet.openDB();
            iabilet.startlogging("log");
            populateCSV(iabilet);
            manipulateCSV(iabilet);
            operations(iabilet);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                iabilet.stoplogging();
                iabilet.closeDB();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void csv_only() {
        Service iabilet = new Service();
        try {
            iabilet.startlogging("log");
            populateCSV(iabilet);
            manipulateCSV(iabilet);
            operations(iabilet);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                iabilet.stoplogging();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void db_only() {
        Service iabilet = new Service();
        try {
            iabilet.openDB();
            iabilet.readDB();
            iabilet.startlogging("log");
            initializations(iabilet);
            operations(iabilet);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                iabilet.stoplogging();
                iabilet.closeDB();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        csv_only();
        //mixed();
        //db_only();
    }
}