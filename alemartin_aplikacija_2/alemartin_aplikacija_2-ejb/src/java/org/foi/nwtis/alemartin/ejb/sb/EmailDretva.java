/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.alemartin.ejb.sb;

import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.stream.JsonParsingException;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import org.foi.nwtis.alemartin.konfiguracije.Konfiguracija;
import org.foi.nwtis.alemartin.konfiguracije.KonfiguracijaApstraktna;
import org.foi.nwtis.alemartin.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.alemartin.konfiguracije.NemaKonfiguracije;

/**
 *
 * @author alemartin
 */
@Singleton
@Startup
@LocalBean
public class EmailDretva {

    private String serverIp;
    private String userAccount;
    private String userPassword;
    private String messageSubject;
    private String folderName;
    private int interval;
    private volatile boolean work;
    private Thread thread;

    private static final String APP_CONFIGURATION = "/META-INF/NWTiS.app.config.xml";

    /**
     * Initial settings for working with James. Starts thread
     */
    @PostConstruct
    private void init() {
        try {
            URL urlApp = getClass().getClassLoader().getResource(APP_CONFIGURATION);
            Konfiguracija konfiguracija = KonfiguracijaApstraktna.preuzmiKonfiguraciju(urlApp.getFile());
            serverIp = konfiguracija.dajPostavku("mail.server");
            userAccount = konfiguracija.dajPostavku("mail.receiver");
            userPassword = konfiguracija.dajPostavku("mail.passwordReceiver");
            messageSubject = konfiguracija.dajPostavku("mail.subjectEmail");
            interval = Integer.parseInt(konfiguracija.dajPostavku("mail.timeSecThreadCycle"));
            folderName = konfiguracija.dajPostavku("mail.folderName");
            work = true;

            Store store = getStoreConnection();
            createFolder(store, folderName);
            store.close();

        } catch (NemaKonfiguracije | NeispravnaKonfiguracija ex) {
            Logger.getLogger(EmailDretva.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MessagingException ex) {
            Logger.getLogger(EmailDretva.class.getName()).log(Level.SEVERE, null, ex);
        }

        Runnable runnable = () -> {
            while (work) {
                try {
                    processInbox();
                    Thread.sleep(interval * 1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(EmailDretva.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        thread = new Thread(runnable);
        thread.start();
        //deleteAllMessages();
    }

    /**
     * Create folder in store with given name if one doesn't exists
     *
     * @param store
     * @param nazivFoldera
     */
    private void createFolder(Store store, String folderName) {
        try {
            Folder root = store.getDefaultFolder();
            Folder newFolder = root.getFolder(folderName);
            if (!newFolder.exists()) {
                newFolder.create(Folder.HOLDS_MESSAGES);
            }
        } catch (MessagingException ex) {
            Logger.getLogger(EmailDretva.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Set properties and return Store object that represent connection to store
     *
     * @return
     */
    public Store getStoreConnection() {
        try {
            Properties properties = System.getProperties();
            properties.put("mail.smtp.host", serverIp);
            Session session = Session.getInstance(properties, null);
            Store store = session.getStore("imap");
            store.connect(serverIp, userAccount, userPassword);
            return store;
        } catch (MessagingException ex) {
            Logger.getLogger(EmailDretva.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Go through user inbox and check for new messages.
     */
    public void processInbox() {
        try {
            Store store = getStoreConnection();
            Folder folder = store.getFolder("INBOX");
            folder.open(Folder.READ_WRITE);
            Message[] messages = folder.getMessages();
            List<Message> nwtisMessages = new ArrayList<>();
            for (Message msg : messages) {
                if (!msg.isSet(Flags.Flag.SEEN)) {
                    if (isNwtisMessage(msg)) {
                        msg.setFlag(Flags.Flag.SEEN, true);
                        nwtisMessages.add(msg);
                    } else {
                        msg.setFlag(Flags.Flag.SEEN, false);
                    }
                }
            }
            if (nwtisMessages.size() > 0) {
                moveMessagesToInbox(store, nwtisMessages, folder, folderName);
                deleteMessagesFromInbox(nwtisMessages);
            }
            folder.close(true);
            store.close();
        } catch (MessagingException ex) {
            Logger.getLogger(EmailDretva.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
  
    /**
     * Check if message is of type NWTIS
     *
     * @param msg
     * @return
     */
    public boolean isNwtisMessage(Message msg) {
        try {
            String subject = msg.getSubject();
            if (subject.equals(messageSubject)) {
                String content = msg.getContent().toString();

                JsonReader jsonReader = Json.createReader(new StringReader(content));
                JsonObject jsonObject = jsonReader.readObject();

                if (jsonObject.containsKey("id") && jsonObject.containsKey("komanda") && jsonObject.containsKey("vrijeme")) {
                    return true;
                }
            }
        } catch (MessagingException ex) {
            Logger.getLogger(EmailDretva.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(EmailDretva.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JsonParsingException ex) {
        }

        return false;
    }

    /**
     * Move messages from one folder to another
     *
     * @param store
     * @param listaPoruka
     * @param inbox
     * @param nazivOdredista
     */
    private void moveMessagesToInbox(Store store, List<Message> listaPoruka, Folder inbox, String nazivOdredista) {
        try {
            Message[] nwtisPoruke = listaPoruka.toArray(new Message[listaPoruka.size()]);
            Folder odrediste = store.getFolder(nazivOdredista);
            inbox.copyMessages(nwtisPoruke, odrediste);
        } catch (MessagingException ex) {
            Logger.getLogger(EmailDretva.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void deleteMessagesFromInbox(List<Message> nwtisPoruke) {
        for (Message msg : nwtisPoruke) {
            try {
                msg.setFlag(Flags.Flag.DELETED, true);
            } catch (MessagingException ex) {
                Logger.getLogger(EmailDretva.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /*
        Before destruction of Singleton bean, stop thread 
     */
    @PreDestroy
    public void stopThread() {
        work = false;
        thread.interrupt();
    }

    /*
        This method is not needed in production version
     */
    private void deleteAllMessages() {
        try {
            Store store = getStoreConnection();
            Folder folder = store.getFolder("INBOX");
            folder.open(Folder.READ_WRITE);
            Message[] msg = folder.getMessages();
            for (Message m : msg) {
                m.setFlag(Flags.Flag.DELETED, true);
            }
            folder.close(true);

            folder = store.getFolder(folderName);
            folder.open(Folder.READ_WRITE);
            msg = folder.getMessages();
            for (Message m : msg) {
                m.setFlag(Flags.Flag.DELETED, true);
            }
            folder.close(true);
            store.close();
        } catch (MessagingException ex) {
            Logger.getLogger(EmailDretva.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
