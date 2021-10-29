/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package signupsigninserver.worker;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import message.Message;

/**
 *
 * @author 2dam
 */
public class Worker extends Thread {
    
    protected Socket socket = null;
    protected Message message   = null;
    
    public Worker (Socket socket, Message message) {
        this.socket = socket;
        this.message = message;
    }
    
    public void run () {
        try {
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
        message = (Message) ois.readObject();
        //DUDA
        switch (message.getAccion()) {
            case SIGNUP:
                break;
            case SIGNIN:
                break;
            case LOGOUT:
                break;
            default:
                break;
        }
        
        oos.close();
        ois.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
