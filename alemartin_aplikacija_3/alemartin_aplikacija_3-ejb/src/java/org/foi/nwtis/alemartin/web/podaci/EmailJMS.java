/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.alemartin.web.podaci;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author alemartin
 */
public class EmailJMS implements Serializable {

    private String id;
    private Date previousJMSDate;
    private Date currentJMSDate;
    private long iterationDuration;
    private int msgsNumber;

    public EmailJMS(Date previousJMSDate, Date currentJMSDate, long iterationDuration, int msgsNumber) {
        this.previousJMSDate = previousJMSDate;
        this.currentJMSDate = currentJMSDate;
        this.iterationDuration = iterationDuration;
        this.msgsNumber = msgsNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getPreviousJMSDate() {
        return previousJMSDate;
    }

    public void setPreviousJMSDate(Date previousJMSDate) {
        this.previousJMSDate = previousJMSDate;
    }

    public Date getCurrentJMSDate() {
        return currentJMSDate;
    }

    public void setCurrentJMSDate(Date currentJMSDate) {
        this.currentJMSDate = currentJMSDate;
    }

    public long getIterationDuration() {
        return iterationDuration;
    }

    public void setIterationDuration(long iterationDuration) {
        this.iterationDuration = iterationDuration;
    }

    public int getMsgsNumber() {
        return msgsNumber;
    }

    public void setMsgsNumber(int msgsNumber) {
        this.msgsNumber = msgsNumber;
    }

   
    

    @Override
    public String toString() {
        return "EmailJMS{"
                + "redniBroj=" + id + ", "
                + "vrijemePrethodnePoruke=" + previousJMSDate + ", "
                + "vrijemeTrenutnePoruke=" + currentJMSDate + ", "
                + "vrijemeTrajanjaIteracije=" + iterationDuration + ", "
                + "brojNwtisMailova=" + msgsNumber + "}";
    }
}
