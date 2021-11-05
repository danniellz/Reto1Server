/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package signupsigninserver.worker;

import exceptions.ConnectionException;
import exceptions.DatabaseNotFoundException;
import exceptions.IncorrectPasswordException;
import exceptions.InvalidEmailFormatException;
import exceptions.UserAlreadyExistException;
import exceptions.UserNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import message.Accion;
import message.Message;
import signable.Signable;
import signupsigninserver.SignUpSignInServer;
import signupsigninserver.dao.DaoFactory;
import user.User;

/**
 * @author Mikel Matilla
 */
public class Worker extends Thread {

    private static final Logger LOG = Logger.getLogger(Worker.class.getName());

    private Socket socket = null;
    private Message message = null;
    private User user = null;
    private ObjectInputStream inO = null;
    private ObjectOutputStream outO = null;

    public Worker(Socket socket) {
        this.socket = socket;

    }

    @Override
    public void run() {
        try {
            sleep(10000);
            LOG.info("Sending info to DaoImplement");
            inO = new ObjectInputStream(socket.getInputStream());
            outO = new ObjectOutputStream(socket.getOutputStream());
            message = (Message) inO.readObject();

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
            outO.writeObject(message);

        } catch (IOException ex) {
            LOG.info("RUN FAIL");
        } catch (ClassNotFoundException ex) {
            LOG.info("CLASS NOT FOUND");
        } catch (UserAlreadyExistException ex) {
            Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
            message.setAccion(Accion.USERALREADYEXIST);
            message.setUser(null);
        } catch (UserNotFoundException ex) {
            Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
            message.setAccion(Accion.USERNOTFOUND);
            message.setUser(null);
        } catch (DatabaseNotFoundException ex) {
            Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
            message.setAccion(Accion.DATABASENOTFOUND);
            message.setUser(null);
        } catch (ConnectionException ex) {
            Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
            message.setAccion(Accion.CONNECTIONNOTFOUND);
            message.setUser(null);
        } catch (IncorrectPasswordException ex) {
            Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
            message.setAccion(Accion.INCORRECTPASSWORD);
            message.setUser(null);
        } catch (InvalidEmailFormatException ex) {
            Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
            message.setAccion(Accion.EMAILNOTVALID);
            message.setUser(null);
        } catch (InterruptedException ex) {
            Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            int disconnect = 1;
            SignUpSignInServer freeConnection = new SignUpSignInServer(disconnect);
            try {
                outO.writeObject(message);

                outO.close();
                inO.close();
            } catch (IOException ex) {
                Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, "Error en WriteObjet Worker", ex);
            }
        }
    }

}
