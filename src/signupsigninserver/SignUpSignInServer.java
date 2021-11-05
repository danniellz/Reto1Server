package signupsigninserver;

import exceptions.ConnectionException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    private static int con = 0;

    /**
     * Main class, start the application
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException, ConnectionException {
        //Attributes
        ServerSocket serverSc;
        final int PORT = Integer.parseInt(ResourceBundle.getBundle("signupsigninserver.pool/poolData").getString("PORT"));
        final int MAXCON = Integer.parseInt(ResourceBundle.getBundle("signupsigninserver.pool/poolData").getString("MAXCONNECTIONS"));

        //Init Server
        try {
            serverSc = new ServerSocket(PORT);
            Socket clienteSc;
            LOG.info("SERVER > Initialized");

            while (true) {
                //Waiting for client request, 
                clienteSc = serverSc.accept();
                con++;
                System.out.println("CLIENT " + con + " CONNECTED!");

                //If a client request a connection while there is none, close the socket
                if (con > MAXCON) {
                    System.out.println("There is not connections available, please try again later");
                    clienteSc.close();
                } else {
                    Worker worker = new Worker(clienteSc);
                    worker.start();
                }
            }
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "An error Occurred trying to connect with Client", ex);
        }
    }

    /**
     *
     * @param freeConnection if a connection ends, free it
     */
    public SignUpSignInServer(int freeConnection) {
        con = con - freeConnection;
    }
}
