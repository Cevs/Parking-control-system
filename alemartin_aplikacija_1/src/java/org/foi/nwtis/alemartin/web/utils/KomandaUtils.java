/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.alemartin.web.utils;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author TOSHIBA
 */
public class KomandaUtils {

    private static final String SPLIT_REGEX = " ";
    private static final String KOSTUR_KOMANDE = "KORISNIK [aA-žŽ0-9]*; LOZINKA [aA-žŽ0-9]*; "
            + "((DODAJ (\"|')[aA-žŽ]*(\"|') (\"|')[aA-žŽ]*(\"|'))|"
            + "((|GRUPA )(DODAJ|PREKID|PAUZA|KRENI|PASIVNO|AKTIVNO|STANI|STANJE|LISTAJ)));";
    public final static KomandaUtils INSTANCE = new KomandaUtils();

    private KomandaUtils() {
    }

    public String dohvatiKorisnickoIme(String komanda) {
        String[] komandaArray = rastaviKomandu(komanda);
        return komandaArray[1];
    }

    public String dohvatiLozinku(String komanda) {
        String[] komandaArray = rastaviKomandu(komanda);
        return komandaArray[3];
    }

    public String dohvatiAkcijuPosluzitelja(String komanda) {
        String podKomanda = vratiPodkomandu(komanda);
        String[] komandaArray = rastaviKomandu(podKomanda);
        String akcija = komandaArray[0].toLowerCase();
        return akcija;
    }

    public String dohvatiAkcijuGrupe(String komanda) {
        String podKomanda = vratiPodkomandu(komanda);
        String[] komandaArray = rastaviKomandu(podKomanda);
        String tip = komandaArray[0].toLowerCase();
        String akcija = komandaArray[1].substring(0, 1).toUpperCase() + komandaArray[1].substring(1).toLowerCase();
        String puniNazivAkcije = tip + akcija;
        return puniNazivAkcije;
    }

    public String[] rastaviKomandu(String komanda) {
        komanda = komanda.trim().replaceAll("[;']", "");
        String[] komandaArray = komanda.split(SPLIT_REGEX);
        return komandaArray;
    }

    public String[] rastaviKomandu(String komanda, int pocetak) throws IndexOutOfBoundsException {
        String[] komandaArray = rastaviKomandu(komanda);
        return Arrays.copyOfRange(komandaArray, pocetak, komandaArray.length);
    }

    public String[] rastaviKomandu(String komanda, int pocetak, int kraj) throws IndexOutOfBoundsException {
        String[] komandaArray = rastaviKomandu(komanda);
        return Arrays.copyOfRange(komandaArray, pocetak, kraj);
    }

    public String vratiPodkomandu(String komanda, int pocetak) throws IndexOutOfBoundsException {
        String[] komandaArray = komanda.split(SPLIT_REGEX);
        komandaArray = Arrays.copyOfRange(komandaArray, pocetak, komandaArray.length);
        String podKomanda = "";
        for (String k : komandaArray) {
            podKomanda += k + " ";
        }
        return podKomanda.trim();
    }

    public String vratiPodkomandu(String komanda, int pocetak, int kraj) throws IndexOutOfBoundsException {
        String[] komandaArray = komanda.split(SPLIT_REGEX);
        komandaArray = Arrays.copyOfRange(komandaArray, pocetak, kraj);
        String podKomanda = "";
        for (String k : komandaArray) {
            podKomanda += k + " ";
        }
        return podKomanda.trim();
    }

    /*
        Ista se cut-a za grupu i za posluzitelja
     */
    public String vratiPodkomandu(String komanda) {
        String[] komandaArray = rastaviKomandu(komanda);
        komandaArray = Arrays.copyOfRange(komandaArray, 4, komanda.length());
        String podKomanda = "";
        for (String k : komandaArray) {
            podKomanda += k + " ";
        }
        return podKomanda.trim();
    }

    public String vratiPodkomanduGrupe() {
        return "";
    }
  
    public boolean provjeriKomandu(String komanda){
        Pattern uzorak = Pattern.compile(KOSTUR_KOMANDE);
        Matcher matcher = uzorak.matcher(komanda);
        if(matcher.matches()){
            return true;
        }
        return false;
    }

    public String odrediVrstuKomande(String komanda) {
        if (komanda.contains("GRUPA")) {
            return "grupa";
        } else {
            return "posluzitelj";
        }
    }
}
