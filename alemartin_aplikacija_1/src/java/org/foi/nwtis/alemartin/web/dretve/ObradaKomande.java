/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.alemartin.web.dretve;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.Socket;
import org.foi.nwtis.alemartin.web.utils.KomandaUtils;

/**
 *
 * @author TOSHIBA
 */
public class ObradaKomande implements Runnable {

    String komanda;
    Socket socket;

    public ObradaKomande(String komanda, Socket socket) {
        this.komanda = komanda;
        this.socket = socket;
    }

    @Override
    public void run() {     
        try {
            if (!KomandaUtils.INSTANCE.provjeriPodKomanduKorisnik(komanda)) {
                vratiOdgovorKlijentu("Komanda nije ispravna");
            }
            String[] komandaArray = KomandaUtils.INSTANCE.rastaviKomandu(komanda);
            String akcija = komandaArray[0].toLowerCase();
            Method metoda = this.getClass().getDeclaredMethod(akcija);
            metoda.setAccessible(true);
            String odovor = (String)metoda.invoke(this);
            //vratiOdgovorKlijentu((String)metoda.invoke(this));
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void vratiOdgovorKlijentu(String odgovor) throws IOException {
        OutputStream out = socket.getOutputStream();
        out.write(odgovor.getBytes());
        out.flush();
        socket.shutdownOutput();
    }

    private String dodaj() {
        return "dodaj";
    }

    private String pauza() {
        return "pauza";
    }

    private String kreni() {
        return "kreni";
    }

    private String pasivno() {
        return "pasivno";
    }

    private String aktivno() {
        return "aktivno";
    }

    private String stani() {
        return "stani";
    }

    private String stanje() {
        return "stanje";
    }

    private String listaj() {
        return "listaj";
    }

}
