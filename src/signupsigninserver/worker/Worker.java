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
import message.Accion;
import message.Message;
import signable.Signable;
import signupsigninserver.dao.DaoFactory;
import user.User;

/**
 * @author Mikel Matilla
 */
public class Worker extends Thread {

    private static final Logger LOG = Logger.getLogger(Worker.class.getName());

    protected Socket socket = null;
    protected Message message = null;
    protected User user = null;

    public Worker(Socket socket) {
        this.socket = socket;

    }

    public void run() {
        try {
            LOG.info("Sending info to DaoImplement");
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            message = (Message) ois.readObject();
            LOG.info(message.getUser().getLogin());
            Signable sign = new DaoFactory().getDao();
            switch (message.getAccion()) {
                case SIGNUP:
                    sign.signUp(message.getUser());
                    LOG.info("SignUp");
                    break;
                case SIGNIN:
                    user = sign.signIn(message.getUser());
                    LOG.info("SignIn");
                    break;
                default:
                    LOG.severe("Unknown error");
                    break;
            }
            LOG.info("SENDIND MESSAGE FOR " + user.getFullName());
            message.setAccion(Accion.OK);
            message.setUser(user);
            oos.writeObject(message);

            oos.close();
            ois.close();
        } catch (IOException ex) {
            LOG.info("RUN FAIL");
        } catch (ClassNotFoundException ex) {
            LOG.info("CLASS NOT FOUND");
        }
    }

}
