/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.alemartin.web.slusaci;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.foi.nwtis.alemartin.konfiguracije.Konfiguracija;
import org.foi.nwtis.alemartin.konfiguracije.KonfiguracijaApstraktna;
import org.foi.nwtis.alemartin.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.alemartin.konfiguracije.NemaKonfiguracije;

/**
 * Web application lifecycle listener.
 *
 * @author alemartin
 */
public class SlusacAplikacije implements ServletContextListener {
    
    public static ServletContext servletContext;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            servletContext = sce.getServletContext();
            String fileName = servletContext.getInitParameter("konfiguracija_aplikacije");
            String path = servletContext.getRealPath("/WEB-INF") + java.io.File.separator;
            String fullPath = path+fileName;
            
            Konfiguracija konfiguracija = KonfiguracijaApstraktna.preuzmiKonfiguraciju(fullPath);
            servletContext.setAttribute("konfiguracija_aplikacije", konfiguracija);
        } catch (NemaKonfiguracije | NeispravnaKonfiguracija ex) {
            Logger.getLogger(SlusacAplikacije.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ServletContext sc = sce.getServletContext();
        sc.removeAttribute("konfiguracija_aplikacije");
    }
}
