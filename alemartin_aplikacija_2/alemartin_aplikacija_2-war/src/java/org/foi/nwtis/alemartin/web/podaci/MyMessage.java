/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.alemartin.web.podaci;

import java.util.Date;

/**
 *
 * @author TOSHIBA
 */
public class MyMessage {
    private int id;
    private Date sentDate;
    private Date recievedDate;
    private String from;
    private String subject;
    private String content;

    public MyMessage(int id, Date sentDate, Date recievedDate, String from, String subject, String content) {
        this.id = id;
        this.sentDate = sentDate;
        this.recievedDate = recievedDate;
        this.from = from;
        this.subject = subject;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getSentDate() {
        return sentDate;
    }

    public void setSentDate(Date sentDate) {
        this.sentDate = sentDate;
    }

    public Date getRecievedDate() {
        return recievedDate;
    }

    public void setRecievedDate(Date recievedDate) {
        this.recievedDate = recievedDate;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }      
}
