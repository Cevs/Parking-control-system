/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.alemartin.ws.klijenti;

/**
 *
 * @author alemartin
 */
public class ParkiranjeWSKlijenti {

    public static Boolean registrirajGrupu(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka) {
        org.foi.nwtis.alemartin.ws.klijenti.Parkiranje_Service service = new org.foi.nwtis.alemartin.ws.klijenti.Parkiranje_Service();
        org.foi.nwtis.alemartin.ws.klijenti.Parkiranje port = service.getParkiranjePort();
        return port.registrirajGrupu(korisnickoIme, korisnickaLozinka);
    }

    public static Boolean deregistrirajGrupu(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka) {
        org.foi.nwtis.alemartin.ws.klijenti.Parkiranje_Service service = new org.foi.nwtis.alemartin.ws.klijenti.Parkiranje_Service();
        org.foi.nwtis.alemartin.ws.klijenti.Parkiranje port = service.getParkiranjePort();
        return port.deregistrirajGrupu(korisnickoIme, korisnickaLozinka);
    }

    public static Boolean aktivirajGrupu(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka) {
        org.foi.nwtis.alemartin.ws.klijenti.Parkiranje_Service service = new org.foi.nwtis.alemartin.ws.klijenti.Parkiranje_Service();
        org.foi.nwtis.alemartin.ws.klijenti.Parkiranje port = service.getParkiranjePort();
        return port.aktivirajGrupu(korisnickoIme, korisnickaLozinka);
    }

    public static Boolean blokirajGrupu(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka) {
        org.foi.nwtis.alemartin.ws.klijenti.Parkiranje_Service service = new org.foi.nwtis.alemartin.ws.klijenti.Parkiranje_Service();
        org.foi.nwtis.alemartin.ws.klijenti.Parkiranje port = service.getParkiranjePort();
        return port.blokirajGrupu(korisnickoIme, korisnickaLozinka);
    }

    public static StatusKorisnika dajStatusGrupe(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka) {
        org.foi.nwtis.alemartin.ws.klijenti.Parkiranje_Service service = new org.foi.nwtis.alemartin.ws.klijenti.Parkiranje_Service();
        org.foi.nwtis.alemartin.ws.klijenti.Parkiranje port = service.getParkiranjePort();
        return port.dajStatusGrupe(korisnickoIme, korisnickaLozinka);
    }

    public static java.util.List<org.foi.nwtis.alemartin.ws.klijenti.Parkiraliste> dajSvaParkiralistaGrupe(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka) {
        org.foi.nwtis.alemartin.ws.klijenti.Parkiranje_Service service = new org.foi.nwtis.alemartin.ws.klijenti.Parkiranje_Service();
        org.foi.nwtis.alemartin.ws.klijenti.Parkiranje port = service.getParkiranjePort();
        return port.dajSvaParkiralistaGrupe(korisnickoIme, korisnickaLozinka);
    }

    public static boolean obrisiParkiralisteGrupe(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka, int idParkiraliste) {
        org.foi.nwtis.alemartin.ws.klijenti.Parkiranje_Service service = new org.foi.nwtis.alemartin.ws.klijenti.Parkiranje_Service();
        org.foi.nwtis.alemartin.ws.klijenti.Parkiranje port = service.getParkiranjePort();
        return port.obrisiParkiralisteGrupe(korisnickoIme, korisnickaLozinka, idParkiraliste);
    }

    public static java.util.List<org.foi.nwtis.alemartin.ws.klijenti.Vozilo> dajSvaVozilaParkiralistaGrupe(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka, int idParkiraliste) {
        org.foi.nwtis.alemartin.ws.klijenti.Parkiranje_Service service = new org.foi.nwtis.alemartin.ws.klijenti.Parkiranje_Service();
        org.foi.nwtis.alemartin.ws.klijenti.Parkiranje port = service.getParkiranjePort();
        return port.dajSvaVozilaParkiralistaGrupe(korisnickoIme, korisnickaLozinka, idParkiraliste);
    }
   
}
