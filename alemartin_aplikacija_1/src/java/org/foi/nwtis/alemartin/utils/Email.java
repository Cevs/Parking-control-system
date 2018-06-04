/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.alemartin.utils;

import static com.oracle.jrockit.jfr.ContentType.Address;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletContext;
import org.foi.nwtis.alemartin.konfiguracije.Konfiguracija;
import org.foi.nwtis.alemartin.web.slusaci.SlusacAplikacije;

/**
 *
 * @author TOSHIBA
 */
public class Email {

    private static String serverIp;
    private static String posiljatelj;
    private static String primatelj;
    private static String naslov;
    private static Properties properties;

    public Email() {
        ServletContext sc = SlusacAplikacije.getServletContext();
        Konfiguracija konfig = (Konfiguracija) sc.getAttribute("konfiguracija_aplikacije");
        serverIp = konfig.dajPostavku("mail.server");
        posiljatelj = konfig.dajPostavku("mail.sender");
        primatelj = konfig.dajPostavku("mail.receiver");
        naslov = konfig.dajPostavku("mail.subjectEmail");
        properties = System.getProperties();
        properties.put("mail.smtp.host", serverIp);
        properties.put("mail.smtp.port", "25");
    }

    public static void posaljiEmail(String tekst) {
        try {
            Session session = Session.getInstance(properties, null);
            MimeMessage poruka = new MimeMessage(session);
            Address adresaPosiljatelja = new InternetAddress(posiljatelj);
            poruka.setFrom(adresaPosiljatelja);

            Address adresaPrimatelja = new InternetAddress(primatelj);
            poruka.setRecipient(Message.RecipientType.TO, adresaPrimatelja);

            poruka.setSubject(naslov);
            poruka.setText(tekst);

            Transport.send(poruka);
        } catch (AddressException ex) {
            Logger.getLogger(Email.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MessagingException ex) {
            Logger.getLogger(Email.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
