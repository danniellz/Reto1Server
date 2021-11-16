package signupsigninserver.worker;

import exceptions.ConnectionException;
import exceptions.DatabaseNotFoundException;
import exceptions.IncorrectPasswordException;
import exceptions.MaxConnectionException;
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

            //Get the DaoImplement from the factory and save it into the Signable interface
            Signable sign = DaoFactory.getDao();

            //The user requested process to do, SignIn or SignUp
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
            LOG.severe("RUN FAIL");
        } catch (ClassNotFoundException ex) {
            LOG.severe("CLASS NOT FOUND");
        } catch (UserAlreadyExistException ex) {
            //Send the corresponding message if the user already exist
            LOG.info("Sending Message for 'User Already Exist' in DB");
            message.setAccion(Accion.USERALREADYEXIST);
            message.setUser(null);
        } catch (UserNotFoundException ex) {
            //Send the corresponding message if the user doesn't exist
            LOG.info("Sending Message for 'Incorrect User'");
            message.setAccion(Accion.USERNOTFOUND);
            message.setUser(null);
        } catch (IncorrectPasswordException ex) {
            //Send the corresponding message if the password is incorrect
            LOG.info("Sending Message for 'Incorrect Password'");
            message.setAccion(Accion.INVALIDPASSWORD);
            message.setUser(null);
        } catch (DatabaseNotFoundException ex) {
            //Send the corresponding message if there is an error with the database
            LOG.info("Sending Message for 'Database Error'");
            message.setAccion(Accion.DATABASENOTFOUND);
            message.setUser(null);
        } catch (ConnectionException | MaxConnectionException ex) {
            //Send the corresponding message if there is a coneection error
            LOG.info("Sending Message for 'Connection Error'");
            message.setAccion(Accion.CONNECTIONNOTFOUND);
            message.setUser(null);
        } finally {
            //remove a connection when done
            LOG.info("Releasing Connection...");
            int disconnect = 1;
            SignUpSignInServer freeConnection = new SignUpSignInServer(disconnect);
            try {
                //Send the message
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
