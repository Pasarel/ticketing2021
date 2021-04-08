package com.ticketing;

import com.ticketing.service.Service;

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

        iabilet.removeClient(1234);

        Integer idMP = iabilet.newClient("Marian", "Preda");
        Integer idMD = iabilet.newClient("Mircea", "Dumitru");
        Integer idET = iabilet.newClient("Ecaterina", "Teodoroiu");
        Integer idLP = iabilet.newClient("Liviu", "Pop");
        Integer idEA = iabilet.newClient("Ecaterina", "Andronescu");

        Integer idSP = iabilet.newLocation("Sala Palatului");
        Integer idRO = iabilet.newLocation("Romexpo");
        Integer idNT = iabilet.newLocation("Nottara");

        Integer idTN = iabilet.newConcert("Timpuri Noi", idRO,  randomNumber(0, 100), randomDate());
        Integer idID = iabilet.newEvent("IDK", idRO,  randomNumber(0, 100), randomDate());
        Integer idFU = iabilet.newConcert("Fuego", idSP,  randomNumber(0, 100), randomDate());
        Integer idSH = iabilet.newPlay("Shakespeare", idNT,  randomNumber(0, 100), randomDate());

        iabilet.addCredit(idMP, 10000.2F);
        iabilet.addEventToClient(idMP, idTN);
        iabilet.addEventToClient(idMP, idFU);
        iabilet.addEventToClient(idMP, idSH);
        iabilet.removeEventFromClient(idMP, idFU);
        iabilet.addEventToClient(idMP, idID);
        iabilet.printClientEvents(idMP);

        iabilet.addCredit(idET, 10000.2F);
        iabilet.addCredit(idMD, 25.2F);
        iabilet.addEventToClient(idET, idSH);
        iabilet.addEventToClient(idMD, idSH);
        iabilet.addEventToClient(idLP, idSH);
        iabilet.addEventToClient(idET, idTN);
        iabilet.addEventToClient(idET, idID);
        iabilet.printClientInfo(idET);

        iabilet.printClientEvents(idET);
        iabilet.removeEvent(idTN);
        iabilet.printClientEvents(idET);
        System.out.println();
        iabilet.printEventInfo(idFU);
    }
}
