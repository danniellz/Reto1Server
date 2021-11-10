package signupsigninserver.worker;

import exceptions.ConnectionException;
import exceptions.DatabaseNotFoundException;
import exceptions.MaxConnectionException;
import exceptions.UserAlreadyExistException;
import exceptions.UserPasswordException;
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
 * Class that control the simultaneus connection with Threads
 *
 * @author Mikel Matilla
 * @version 1.0
 */
public class Worker extends Thread {

    //LOGGER
    private static final Logger LOG = Logger.getLogger(Worker.class.getName());

    //Attributes
    private Socket socket = null;
    private Message message = null;
    private User user = null;
    private ObjectInputStream inO = null;
    private ObjectOutputStream outO = null;

    /**
     * Worker constructor
     *
     * @param socket the client socket
     */
    public Worker(Socket socket) {
        this.socket = socket;

    }

    @Override
    public void run() {
        try {
            //sleep(10000);
            LOG.info("Sending info to DaoImplement");
            inO = new ObjectInputStream(socket.getInputStream());
            outO = new ObjectOutputStream(socket.getOutputStream());
            message = (Message) inO.readObject();

            Signable sign = new DaoFactory().getDao();

            switch (message.getAccion()) {
                case SIGNUP:
                    user = sign.signUp(message.getUser());
                    LOG.info("SignUp Process Done!");
                    break;
                case SIGNIN:
                    user = sign.signIn(message.getUser());
                    LOG.info("SignIn Process Done!");
                    break;
                default:
                    LOG.severe("Unknown error");
                    break;
            }
            LOG.info("SENDIND MESSAGE FOR " + user.getFullName());
            message.setAccion(Accion.OK);
            message.setUser(user);

        } catch (IOException ex) {
            LOG.info("RUN FAIL");
        } catch (ClassNotFoundException ex) {
            LOG.info("CLASS NOT FOUND");
        } catch (UserAlreadyExistException ex) {
            LOG.info("Sending Message for 'User Already Exist' in DB");
            message.setAccion(Accion.USERALREADYEXIST);
            message.setUser(null);
        } catch (UserPasswordException ex) {
            LOG.info("Sending Message for 'Incorrect User or Password'");
            message.setAccion(Accion.INVALIDUSERORPASSWORD);
            message.setUser(null);
        } catch (DatabaseNotFoundException ex) {
            LOG.info("Sending Message for 'Database Error'");
            message.setAccion(Accion.DATABASENOTFOUND);
            message.setUser(null);
        } catch (ConnectionException | MaxConnectionException ex) {
            LOG.info("Sending Message for 'Connection Error'");
            message.setAccion(Accion.CONNECTIONNOTFOUND);
            message.setUser(null);
        //} catch (InterruptedException ex) {
        //    Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            //remove a connection when done
            LOG.info("Releasing Connection...");
            int disconnect = 1;
            SignUpSignInServer freeConnection = new SignUpSignInServer(disconnect);
            try {
                outO.writeObject(message);

                //Close channels
                outO.close();
                inO.close();
            } catch (IOException ex) {
                Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, "Error in Worker, try-catch in finally", ex);
            }

        }
    }

}
