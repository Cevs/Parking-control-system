/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.alemartin.utils;

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
            + "((DODAJ|AŽURIRAJ) (\"|')[aA-žŽ]*(\"|') (\"|')[aA-žŽ]*(\"|')|"
            + "((GRUPA DODAJ|GRUPA PREKID|GRUPA KRENI|GRUPA PAUZA|GRUPA STANJE)|"
            + "(PREKID|PAUZA|KRENI|PASIVNO|AKTIVNO|STANI|STANJE|LISTAJ)));";

    private KomandaUtils() {}

    public static String dohvatiKorisnickoIme(String komanda) {
        String[] komandaArray = rastaviKomandu(komanda);
        return komandaArray[1];
    }

    public static String dohvatiLozinku(String komanda) {
        String[] komandaArray = rastaviKomandu(komanda);
        return komandaArray[3];
    }

    public static String dohvatiAkcijuPosluzitelja(String komanda) {
        String podKomanda = vratiPodkomandu(komanda);
        String[] komandaArray = rastaviKomandu(podKomanda);
        String akcija = komandaArray[0].toLowerCase();
        return akcija;
    }

    public static String dohvatiAkcijuGrupe(String komanda) {
        String podKomanda = vratiPodkomandu(komanda);
        String[] komandaArray = rastaviKomandu(podKomanda);
        String tip = komandaArray[0].toLowerCase();
        String akcija = komandaArray[1].substring(0, 1).toUpperCase() + komandaArray[1].substring(1).toLowerCase();
        String puniNazivAkcije = tip + akcija;
        return puniNazivAkcije;
    }

    public static String[] rastaviKomandu(String komanda) {
        komanda = komanda.trim().replaceAll("[;']", "");
        String[] komandaArray = komanda.split(SPLIT_REGEX);
        return komandaArray;
    }

    public static String[] rastaviKomanduOrginalno(String komanda){
       String[] komandaArray = komanda.split(SPLIT_REGEX);
       return komandaArray;
    }

        
    private static String spojiKomandu(String[] komandaArray) {
        String komanda = "";
        for (String s : komandaArray) {
            if(!s.isEmpty()){
                 komanda += s + " ";
            }           
        }
        return komanda.trim();
    }

    public static String vratiPodkomandu(String komanda, int pocetak) throws IndexOutOfBoundsException {
        String[] komandaArray = komanda.split(SPLIT_REGEX);
        komandaArray = Arrays.copyOfRange(komandaArray, pocetak, komandaArray.length);
        String podKomanda = spojiKomandu(komandaArray);
        return podKomanda;
    }

    public static String vratiPodkomandu(String komanda, int pocetak, int kraj) throws IndexOutOfBoundsException {
        String[] komandaArray = komanda.split(SPLIT_REGEX);
        komandaArray = Arrays.copyOfRange(komandaArray, pocetak, kraj);
        String podKomanda = spojiKomandu(komandaArray);      
        return podKomanda;
    }

    /*
        Ista se cut-a za grupu i za posluzitelja
     */
    public static String vratiPodkomandu(String komanda) {
        String[] komandaArray = rastaviKomandu(komanda);
        komandaArray = Arrays.copyOfRange(komandaArray, 4, komandaArray.length);
        String podKomanda = spojiKomandu(komandaArray);
        return podKomanda;
    }

    public static boolean provjeriKomandu(String komanda) {
        Pattern uzorak = Pattern.compile(KOSTUR_KOMANDE);
        Matcher matcher = uzorak.matcher(komanda);
        if (matcher.matches()) {
            return true;
        }
        return false;
    }

    public static String odrediVrstuKomande(String komanda) {
        if (komanda.contains("GRUPA")) {
            return "grupa";
        } else {
            return "posluzitelj";
        }
    }

    public static String izbaciLozinku(String komanda) {
        String[] komandaArray = rastaviKomanduOrginalno(komanda);
        komandaArray[2] = "";
        komandaArray[3] = "";
        return spojiKomandu(komandaArray);
    }

}
