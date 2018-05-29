/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.alemartin.web.slusaci;

import org.foi.nwtis.alemartin.web.dretve.PreuzmiMeteoPodatke;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.foi.nwtis.alemartin.konfiguracije.Konfiguracija;
import org.foi.nwtis.alemartin.konfiguracije.KonfiguracijaApstraktna;
import org.foi.nwtis.alemartin.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.alemartin.konfiguracije.NemaKonfiguracije;
import org.foi.nwtis.alemartin.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.alemartin.web.dretve.Posluzitelj;

/**
 * Web application lifecycle listener.
 *
 * @author TOSHIBA
 */
public class SlusacAplikacije implements ServletContextListener {

    private PreuzmiMeteoPodatke preuzmiMeteoPodatke = null;
    private Posluzitelj posluzitelj = null;
    private static ServletContext servletContext;
    @Override
    public void contextInitialized(ServletContextEvent sce){      
        try {
            servletContext = sce.getServletContext();
            String putanja = servletContext.getRealPath("/WEB-INF") + java.io.File.separator;
            String bazaDatoteka = servletContext.getInitParameter("baza_konfiguracija");
            String aplikacijaDatoteka = servletContext.getInitParameter("aplikacija_konfiguracija");
            Konfiguracija konfig = KonfiguracijaApstraktna.preuzmiKonfiguraciju(putanja + aplikacijaDatoteka);
            BP_Konfiguracija bpk = new BP_Konfiguracija(putanja + bazaDatoteka);
            servletContext.setAttribute("konfiguracija_aplikacije", konfig);
            servletContext.setAttribute("konfiguracija_baze", bpk);
        } catch (NemaKonfiguracije | NeispravnaKonfiguracija ex) {
            Logger.getLogger(SlusacAplikacije.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //Start thread
        posluzitelj = new Posluzitelj();
        preuzmiMeteoPodatke = new PreuzmiMeteoPodatke();
        preuzmiMeteoPodatke.start();
        posluzitelj.start();
        
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }

    public static ServletContext getServletContext() {
        return servletContext;
    }
    
    
}
