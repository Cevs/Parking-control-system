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
 * @author alemarti
 */
public class JMS implements Serializable {

    private static int id;
    private Date previousJMSDate;
    private Date currentJMSDate;
    private long iterationDuration;
    private int msgsNumber;

    public JMS(Date previousJMSDate, Date currentJMSDate, long iterationDuration, int msgsNumber) {
        id++;
        this.previousJMSDate = previousJMSDate;
        this.currentJMSDate = currentJMSDate;
        this.iterationDuration = iterationDuration;
        this.msgsNumber = msgsNumber;
    }

    public static int getId() {
        return id;
    }

    public Date getPreviousJMSDate() {
        return previousJMSDate;
    }

    public Date getCurrentJMSDate() {
        return currentJMSDate;
    }

    public long getIterationDuration() {
        return iterationDuration;
    }

    public int getMsgsNumber() {
        return msgsNumber;
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
