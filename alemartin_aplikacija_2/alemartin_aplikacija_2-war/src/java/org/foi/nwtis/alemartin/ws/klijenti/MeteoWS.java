/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.alemartin.ws.klijenti;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.ws.Dispatch;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.Service;
import java.io.StringReader;

/**
 *
 * @author TOSHIBA
 */
public class MeteoWS {

   public static MeteoPodaci dajZadnjePreuzeteMeteoPodatke(java.lang.String korisnickoIme, java.lang.String lozinka, int id) {
	org.foi.nwtis.alemartin.ws.klijenti.GeoMeteoWS_Service service = new org.foi.nwtis.alemartin.ws.klijenti.GeoMeteoWS_Service();
	org.foi.nwtis.alemartin.ws.klijenti.GeoMeteoWS port = service.getGeoMeteoWSPort();
	return port.dajZadnjePreuzeteMeteoPodatke(korisnickoIme, lozinka, id);
    }
    
     public static MeteoPodaci dajVazeceMeteoPodatke(java.lang.String korisnickoIme, java.lang.String lozinka, int id) {
	org.foi.nwtis.alemartin.ws.klijenti.GeoMeteoWS_Service service = new org.foi.nwtis.alemartin.ws.klijenti.GeoMeteoWS_Service();
	org.foi.nwtis.alemartin.ws.klijenti.GeoMeteoWS port = service.getGeoMeteoWSPort();
	return port.dajVazeceMeteoPodatke(korisnickoIme, lozinka, id);
    }
}
