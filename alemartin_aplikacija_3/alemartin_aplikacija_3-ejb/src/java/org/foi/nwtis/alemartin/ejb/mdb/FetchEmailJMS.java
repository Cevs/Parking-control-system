/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.alemartin.ejb.mdb;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import org.foi.nwtis.alemartin.ejb.sb.JMSMessages;
import org.foi.nwtis.alemartin.web.podaci.EmailJMS;

/**
 *
 * @author TOSHIBA
 */
@MessageDriven(mappedName = "jms/NWTiS_alemartin_1",
        activationConfig = {
            @ActivationConfigProperty(
                    propertyName = "acknowledgeMode",
                    propertyValue = "Auto-acknowledge")
            ,
@ActivationConfigProperty(
                    propertyName = "destinationType",
                    propertyValue = "javax.jms.Queue")})

public class FetchEmailJMS implements MessageListener {

    @EJB
    private JMSMessages jmsMsg;

    public FetchEmailJMS() {
    }

    @Override
    public void onMessage(Message message) {
        try {
            ObjectMessage msg = (ObjectMessage) message;
            String id = msg.getJMSMessageID();
            EmailJMS emailJMS = (EmailJMS) msg.getObject();
            emailJMS.setId(id);
            jmsMsg.addEmail(emailJMS);
        } catch (JMSException ex) {
            Logger.getLogger(FetchEmailJMS.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
