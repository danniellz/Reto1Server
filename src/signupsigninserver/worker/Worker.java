/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package signupsigninserver.worker;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import signupsigninserver.dao.DaoFactory;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import message.Message;
import signable.Signable;

/**
 * @author Mikel Matilla
 */
public class Worker extends Thread {
    
    private static final Logger LOG = Logger.getLogger(Worker.class.getName());
    
    protected Socket socket = null;
    protected Message message   = null;
    
    public Worker (Socket socket) {
        this.socket = socket;
    }
    
    public void run () {
        try {
            LOG.info("Sending info to DaoImplement");
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            message = (Message) ois.readObject();
            
            Signable sign = new DaoFactory().getDao();
            switch (message.getAccion()) {
                case SIGNUP:
                    oos.writeObject(sign.signUp(message.getUser()));
                    LOG.info("SignUp");
                    break;
                case SIGNIN:
                    oos.writeObject(sign.signIn(message.getUser()));
                    LOG.info("SignIn");
                    break;
                default:
                    LOG.severe("Unknown error");
                    break;
            }

            oos.close();
            ois.close();
        } catch (IOException ex) {
            Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
