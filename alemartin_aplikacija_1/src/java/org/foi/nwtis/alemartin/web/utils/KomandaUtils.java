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
    private static final String kosturPodKomandeKorisnik = "(DODAJ (\"|')[aA-zZ]*(\"|') (\"|')[aA-zZ]*(\"|')|PAUZA|KRENI|PASIVNO|AKTIVNO|STANI|STANJE|LISTAJ);";
    private static final String kosturKomandeAutorizacije = "KORISNIK [aA-zZ0-9]*; LOZINKA [aA-zZ0-9]*; [aA-zZ0-9;'\" ]*";
    public final static KomandaUtils INSTANCE = new KomandaUtils();
    private KomandaUtils(){}
      
    public String[] rastaviKomandu(String komanda){
        komanda = komanda.trim().replaceAll("[;']", "");
        String[] komandaArray = komanda.split(SPLIT_REGEX);
        return komandaArray;
    }    
    
    public String[] rastaviKomandu(String komanda, int pocetak) throws IndexOutOfBoundsException{
        String[] komandaArray = rastaviKomandu(komanda);
        return Arrays.copyOfRange(komandaArray, pocetak, komandaArray.length);
    }
    
    public String[] rastaviKomandu(String komanda, int pocetak, int kraj) throws IndexOutOfBoundsException{
        String[] komandaArray = rastaviKomandu(komanda);
        return Arrays.copyOfRange(komandaArray, pocetak, kraj);
    }
        
    public String vratiPodkomandu(String komanda, int pocetak) throws IndexOutOfBoundsException{
        String[] komandaArray = komanda.split(SPLIT_REGEX);
        komandaArray = Arrays.copyOfRange(komandaArray, pocetak, komandaArray.length);
        String podKomanda = "";
        for(String k : komandaArray){
            podKomanda += k+" ";
        }
        return podKomanda.trim();
    }
    
    public String vratiPodkomandu(String komanda, int pocetak, int kraj)throws IndexOutOfBoundsException { 
        String[] komandaArray = komanda.split(SPLIT_REGEX);
        komandaArray =  Arrays.copyOfRange(komandaArray, pocetak, kraj);
        String podKomanda = "";
        for(String k : komandaArray){
            podKomanda += k+" ";
        }
        return podKomanda.trim();
    }
    
    public boolean provjeriPodKomanduKorisnik(String komanda){
        Pattern uzorak = Pattern.compile(kosturPodKomandeKorisnik);
        Matcher matcher = uzorak.matcher(komanda);
        if(matcher.matches()){
            return true;
        }
        return false;
    }
    
    public boolean provjeriKomanduAutorizacije(String komanda){
        Pattern uzorak = Pattern.compile(kosturKomandeAutorizacije);
        Matcher matcher = uzorak.matcher(komanda);
        if(matcher.matches()){
            return true;
        }
        return false;
    }
}
