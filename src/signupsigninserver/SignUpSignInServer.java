package signupsigninserver;

import exceptions.ConnectionException;
import exceptions.MaxConnectionException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import message.Accion;
import message.Message;
import signupsigninserver.worker.Worker;

/**
 * class responsible for starting the application
 *
 * @author Daniel Brizuela
 * @version 1.0
 */
public class SignUpSignInServer {
    //LOGGER
    private static final Logger LOG = Logger.getLogger(SignUpSignInServer.class.getName());

    //Attributes
    private static int con = 0;

    /**
     * Main class, start the application
     *
     * @param args the command line arguments
     * @throws exceptions.ConnectionException if the connection with the client fails, error message
     * @throws exceptions.MaxConnectionException if there is no thread available, error message
     * @throws java.io.IOException general exception
     */
    public static void main(String[] args) throws ConnectionException, MaxConnectionException, IOException {
        //Attributes
        ServerSocket serverSc;
        Socket clientSc = null;
        Message mes = new Message();
        ObjectOutputStream outO;
        //Pool Properties
        final int PORT = Integer.parseInt(ResourceBundle.getBundle("signupsigninserver.pool/poolData").getString("PORT"));
        final int MAXCON = Integer.parseInt(ResourceBundle.getBundle("signupsigninserver.pool/poolData").getString("MAXCONNECTIONS"));

        //Init Server
        try {
            serverSc = new ServerSocket(PORT);
            LOG.info("SERVER > Initialized");

            //Waiting for client request,
            while (true) {
                clientSc = serverSc.accept();
                con++;
                System.out.println("CLIENT " + con + " CONNECTED!");

                //If a client request a Thread while there is none, send message and close the socket
                if (con > MAXCON) {
                    throw new MaxConnectionException();
                } else {
                    Worker worker = new Worker(clientSc);
                    worker.start();
                }
            }

        } catch (MaxConnectionException ex) {
            //Sending the message for the Max connection reached opening a writting channel
            LOG.info("Sending message for the 'Max Connection' limit Reached");
            outO = new ObjectOutputStream(clientSc.getOutputStream());
            mes.setAccion(Accion.MAXCONNECTION);
            mes.setUser(null);
            outO.writeObject(mes);
            outO.close();

            //Close socket of extra client
            clientSc.close();
            LOG.info("Client Socket Close");

        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "An error Occurred trying to connect with Client", ex);
        }
    }

    /**
     * Method that remove a connection when done to be able to use a socket
     *
     * @param freeConnection a integer with value 1
     */
    public SignUpSignInServer(int freeConnection) {
        con = con - freeConnection;
    }
}
