package signupsigninserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    public static void main(String[] args) {
        ServerSocket serverSc;
        final int PORT = Integer.parseInt(ResourceBundle.getBundle("signupsigninserver.dao/config").getString("PORT"));

        try {
            serverSc = new ServerSocket(PORT);
            Socket clienteSc;
            LOG.info("SERVER > Initialized");

            while(true){
                //Waiting for client request
                clienteSc = serverSc.accept();
                LOG.info("SERVER > Client Connected!");

            }
        } catch (IOException ex) {
            System.out.println("Ha ocurrido un error al conectar con el cliente");
            Logger.getLogger(SignUpSignInServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
