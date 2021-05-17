package com.ticketing;

import com.ticketing.service.*;

import java.io.IOException;
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


    public static void main(String[] args) {
        Service iabilet = new Service();
        try {
            iabilet.startlogging("log");
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

            iabilet.addEventToClient(2,3);
            iabilet.printClientEvents(2);
            iabilet.addEventToClient(1,3); // should throw exception
            iabilet.stoplogging();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                iabilet.stoplogging();
            } catch (IOException f) {
                f.printStackTrace();
            }
        }
    }
    // Below: initial creation of the files:
/*
        try {
            //iabilet.removeClient(1234);
            Service.GenericSingletonWriter<Location> l = (Service.GenericSingletonWriter<Location>) (Service.GenericSingletonWriter.getInstance());
            l.setFactory(new Location());
            l.setFile("/Users/alania/IdeaProjects/proiect/src/locations.csv");

            Integer idMP = iabilet.newClient("Marian", "Preda");
            Integer idMD = iabilet.newClient("Mircea", "Dumitru");
            Integer idET = iabilet.newClient("Ecaterina", "Teodoroiu");
            Integer idLP = iabilet.newClient("Liviu", "Pop");
            Integer idEA = iabilet.newClient("Ecaterina", "Andronescu");

            Integer idSP = iabilet.newLocation("Sala Palatului");
            Integer idRO = iabilet.newLocation("Romexpo");
            Integer idNT = iabilet.newLocation("Nottara");

            Integer idTN = iabilet.newConcert("Timpuri Noi", idRO, randomNumber(0, 100), randomDate());
            Integer idID = iabilet.newEvent("IDK", idRO, randomNumber(0, 100), randomDate());
            Integer idFU = iabilet.newConcert("Fuego", idSP, randomNumber(0, 100), randomDate());
            Integer idSH = iabilet.newPlay("Shakespeare", idNT, randomNumber(0, 100), randomDate());

            iabilet.addCredit(idMP, 10000.2F);
            iabilet.addEventToClient(idMP, idTN);
            iabilet.addEventToClient(idMP, idFU);
            iabilet.removeEventFromClient(idMP, idFU);
            iabilet.addEventToClient(idMP, idID);

            iabilet.addCredit(idET, 10000.2F);
            iabilet.addCredit(idMD, 25.2F);
            iabilet.addEventToClient(idET, idSH);
            iabilet.addEventToClient(idET, idTN);
            iabilet.addEventToClient(idET, idID);

            l.writeAll();
            l.closeFile();


            Service.GenericSingletonWriter<Event> e = (Service.GenericSingletonWriter<Event>) (Service.GenericSingletonWriter.getInstance());
            e.setFactory(new Event());
            e.setFile("/Users/alania/IdeaProjects/proiect/src/events.csv");
            e.writeAll();
            e.closeFile();

            Service.GenericSingletonWriter<Client> c = (Service.GenericSingletonWriter<Client>) (Service.GenericSingletonWriter.getInstance());
            c.setFactory(new Client());
            c.setFile("/Users/alania/IdeaProjects/proiect/src/clients.csv");
            c.writeAll();
            c.closeFile();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
 */
}