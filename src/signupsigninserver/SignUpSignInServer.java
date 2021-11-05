package signupsigninserver;

import exceptions.ConnectionException;
import java.io.IOException;
import static java.lang.Thread.sleep;
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
 */
public class SignUpSignInServer {
    //LOGGER
    private static final Logger LOG = Logger.getLogger(SignUpSignInServer.class.getName());

    /**
     * Main class, start the application 
     * 
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException, ConnectionException {
       ServerSocket serverSc;
        final int PORT = Integer.parseInt(ResourceBundle.getBundle("signupsigninserver.pool/poolData").getString("PORT"));
        final int MAXCON = Integer.parseInt(ResourceBundle.getBundle("signupsigninserver.pool/poolData").getString("MAXCONNECTIONS"));
        

        try {
            serverSc = new ServerSocket(PORT);
            Socket clienteSc;
            int con;
            LOG.info("SERVER > Initialized");

            while(true){
                //Waiting for client request
                for (con= 1; con <= MAXCON; con++) {
                    clienteSc = serverSc.accept();
                    System.out.println("CLIENT " + con + " CONNECTED!");

                    Worker worker = new Worker(clienteSc);
                    worker.start();
                }
                
                /*if(con>MAXCON){
                    sleep(100);
                    System.out.println("LIMITE DE CONEXIONES ALCANZADO");
                    throw new ConnectionException();
                }*/
            }
        } catch (IOException ex) {
            LOG.info("An error Occurred trying to connect with Client");
        }
    }
    
}
