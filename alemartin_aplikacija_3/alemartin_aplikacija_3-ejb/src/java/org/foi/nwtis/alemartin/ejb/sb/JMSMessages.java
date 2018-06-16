/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.alemartin.ejb.sb;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import org.foi.nwtis.alemartin.konfiguracije.Konfiguracija;
import org.foi.nwtis.alemartin.konfiguracije.KonfiguracijaApstraktna;
import org.foi.nwtis.alemartin.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.alemartin.konfiguracije.NemaKonfiguracije;
import org.foi.nwtis.alemartin.web.podaci.EmailJMS;

/**
 *
 * @author TOSHIBA
 */
@Singleton
@LocalBean
@Startup
public class JMSMessages {
    private static final String APP_CONFIGURATION = "/META-INF/NWTiS.app.config.xml";
    private List<EmailJMS> emailMessages = new ArrayList<>();
    private String emailFile;

    public JMSMessages() {
        boolean b = true;
    }

    @PostConstruct
    public void init() {
        try {
            URL urlApp = getClass().getClassLoader().getResource(APP_CONFIGURATION);
            Konfiguracija config = KonfiguracijaApstraktna.preuzmiKonfiguraciju(urlApp.getFile());
            emailFile = config.dajPostavku("email.datoteka.serijalizacije");
            loadSerializedEmailMessages();
        } catch (NemaKonfiguracije | NeispravnaKonfiguracija ex) {
            Logger.getLogger(JMSMessages.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }

    public void addEmail(EmailJMS msg) {
        emailMessages.add(msg);
        //WebSocketSOPA.emailCallback();
    }

    public void deleteEmail(String msgId) {
        Iterator it = emailMessages.iterator();

        while (it.hasNext()) {
            EmailJMS msg = (EmailJMS) it.next();
            //Da li ce ova usporedba raditi
            if (msg.getId().equals(msgId)) {
                it.remove();
            }
        }
    }

    public void loadSerializedEmailMessages() {
        try (FileInputStream fis = new FileInputStream(emailFile);
                ObjectInputStream ois = new ObjectInputStream(fis)) {
            this.emailMessages = (ArrayList<EmailJMS>) ois.readObject();
        } catch (IOException ex) {
            Logger.getLogger(JMSMessages.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(JMSMessages.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void serializeEmailMessages() {
        try (FileOutputStream fos = new FileOutputStream(emailFile);
                ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(emailMessages);
        } catch (IOException ex) {
            Logger.getLogger(JMSMessages.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @PreDestroy
    public void preDestroy() {
        serializeEmailMessages();
    }
}
