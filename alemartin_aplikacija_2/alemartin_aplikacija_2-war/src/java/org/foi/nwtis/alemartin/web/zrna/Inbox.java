/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.alemartin.web.zrna;

import java.io.IOException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import org.foi.nwtis.alemartin.ejb.sb.EmailDretva;
import org.foi.nwtis.alemartin.konfiguracije.Konfiguracija;
import org.foi.nwtis.alemartin.web.podaci.Menu;
import org.foi.nwtis.alemartin.web.podaci.MyMessage;
import org.foi.nwtis.alemartin.web.utils.SessionUtils;

/**
 *
 * @author TOSHIBA
 */
@Named(value = "inbox")
@RequestScoped
public class Inbox implements Serializable {

    private String selectedInbox;
    private List<MyMessage> listOfMessages = new ArrayList<>();
    private List<Menu> listOfInboxes = new ArrayList<>();

    private String serverIp;
    private String userAccount;
    private String userPassword;

    private int numberOfMessages;
    private int pageIndex;
    private int lastPageIndex;
    private int recordsPerPage;

    private boolean showButtonPrevious;
    private boolean showButtonNext;

    private HttpSession session;

    public Inbox() {
        ServletContext sc = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        Konfiguracija config = (Konfiguracija) sc.getAttribute("konfiguracija_aplikacije");
        serverIp = config.dajPostavku("mail.server");
        userAccount = config.dajPostavku("mail.receiver");
        userPassword = config.dajPostavku("mail.passwordReceiver");
        recordsPerPage = Integer.parseInt(config.dajPostavku("stranicenje.brojZapisa"));
        session = SessionUtils.getSession();
        getSessionData();
        getInboxes();
        getInboxMessages();
        refreshView();
    }

    private void getSessionData() {
        if (session.getAttribute("pageIndexInbox") == null) {
            pageIndex = 0;
            session.setAttribute("pageIndexInbox", pageIndex);
        } else {
            pageIndex = (int) session.getAttribute("pageIndexInbox");
        }
        if (session.getAttribute("selectedInbox") == null) {
            selectedInbox = "INBOX";
            session.setAttribute("selectedInbox", selectedInbox);
        } else {
            selectedInbox = (String) session.getAttribute("selectedInbox");
        }
    }

    private void getInboxes() {
        try {
            listOfInboxes.clear();
            Store store = getStoreConnection();
            Folder folder = store.getDefaultFolder();
            Folder[] folderArray = folder.list(); // folder.list("*");
            for (Folder f : folderArray) {
                String folderName = f.getFullName() + " [" + f.getMessageCount() + "]";
                listOfInboxes.add(new Menu(folderName, f.getFullName()));
            }
            if (selectedInbox == null) {
                selectedInbox = listOfInboxes.get(0).getValue();
            }

            store.close();
        } catch (MessagingException ex) {
            Logger.getLogger(Inbox.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

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

    private void getInboxMessages() {
        try {
            listOfMessages.clear();
            Store store = getStoreConnection();
            Folder folder = store.getFolder(selectedInbox);
            folder.open(Folder.READ_WRITE);

            numberOfMessages = folder.getMessageCount();
            lastPageIndex = (int) Math.ceil((double) numberOfMessages / recordsPerPage);

            int start = 1 + (pageIndex * recordsPerPage);
            int end = start + recordsPerPage - 1;
            if (end > numberOfMessages) {
                end = numberOfMessages;
            }

            Message[] msgs = folder.getMessages(start, end);

            for (Message m : msgs) {
                Address[] fromAddresses = m.getFrom();
                InternetAddress from = (InternetAddress) fromAddresses[0];
                MyMessage myMessage = new MyMessage(m.getMessageNumber(), m.getSentDate(), m.getReceivedDate(),
                        from.getAddress(), m.getSubject(), m.getContent().toString());
                listOfMessages.add(myMessage);
                m.setFlag(Flags.Flag.SEEN, false);
            }
            folder.close(false);
            store.close();
        } catch (MessagingException | IOException ex) {
            Logger.getLogger(Inbox.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void refreshView() {
        getInboxMessages();
        if (pageIndex > 0 && pageIndex < lastPageIndex - 1) {
            showButtonPrevious = true;
            showButtonNext = true;
        } else if (lastPageIndex - 1 > 0 && !(pageIndex < lastPageIndex - 1)) {
            showButtonPrevious = true;
            showButtonNext = false;
        } else if (!(pageIndex > 0) && pageIndex < lastPageIndex - 1) {
            showButtonPrevious = false;
            showButtonNext = true;
        } else {
            showButtonPrevious = false;
            showButtonNext = false;
        }

    }

    public void deleteAllMessages() {
        try {
            Store store = getStoreConnection();
            Folder folder = store.getFolder(selectedInbox);
            folder.open(Folder.READ_WRITE);

            Message[] msgs = folder.getMessages();
            for (Message m : msgs) {
                m.setFlag(Flags.Flag.DELETED, true);
            }
            folder.close(true);
            store.close();
        } catch (MessagingException ex) {
            Logger.getLogger(Inbox.class.getName()).log(Level.SEVERE, null, ex);
        }
        session.setAttribute("pageIndexInbox", 0);
        getInboxes();
        refreshView();
    }

    public void changeInbox() {
        session.setAttribute("selectedInbox", selectedInbox);
        session.setAttribute("pageIndexInbox", 0);
        refreshView();

    }

    public void nextPage() {
        session.setAttribute("pageIndexInbox", ++pageIndex);
        refreshView();
    }

    public void previousPage() {
        session.setAttribute("pageIndexInbox", --pageIndex);
        refreshView();
    }

    public boolean isShowButtonPrevious() {
        return showButtonPrevious;
    }

    public void setShowButtonPrevious(boolean showButtonPrevious) {
        this.showButtonPrevious = showButtonPrevious;
    }

    public boolean isShowButtonNext() {
        return showButtonNext;
    }

    public void setShowButtonNext(boolean showButtonNext) {
        this.showButtonNext = showButtonNext;
    }

    public String getSelectedInbox() {
        return selectedInbox;
    }

    public void setSelectedInbox(String selectedInbox) {
        this.selectedInbox = selectedInbox;
    }

    public List<MyMessage> getListOfMessages() {
        return listOfMessages;
    }

    public void setListOfMessages(List<MyMessage> listOfMessages) {
        this.listOfMessages = listOfMessages;
    }

    public List<Menu> getListOfInboxes() {
        return listOfInboxes;
    }

    public void setListOfInboxes(List<Menu> listOfInboxes) {
        this.listOfInboxes = listOfInboxes;
    }

    /* Navigation */
    public String home() {
        return "home";
    }

    public String profile() {
        return "profile";
    }

    public String status() {
        return "status";
    }
    
    public String log(){
        return "log";
    }
    
    public String parkings(){
        return "parkings";
    }
    
    public String mqtt(){
        return "mqtt";
    }
}
