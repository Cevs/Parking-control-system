/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.alemartin.socket;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 *
 * @author TOSHIBA
 */
@ServerEndpoint("/websocket")
public class WebSocket {
    
    private static Set<Session> sessions = new LinkedHashSet<>();

    @OnOpen
    public void onOpen(Session session){
        sessions.add(session);
    }
    
    @OnClose
    public void onClose(Session session){
        sessions.remove(session);
    }
    
    @OnMessage
    public String onMessage(String message) {
        return "";
    }
    
    public static void sendMessage(String msg){
        for(Session s: sessions){
            try {
                s.getBasicRemote().sendText(msg);
            } catch (IOException ex) {
                Logger.getLogger(WebSocket.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
