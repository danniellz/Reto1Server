package signupsigninserver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import message.Message;

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
     * @throws java.lang.ClassNotFoundException
     */
    public static void main(String[] args) throws ClassNotFoundException {
        ServerSocket serverSc;
        final int PORT = Integer.parseInt(ResourceBundle.getBundle("signupsigninserver.dao/config").getString("PORT"));
        Message mes;

        try {
            serverSc = new ServerSocket(PORT);
            Socket clientSc;
            LOG.info("SERVER > Initialized");
            ObjectInputStream inO;

            while(true){
                //Waiting for client request
                clientSc = serverSc.accept();
                LOG.info("SERVER > Client Connected!");
                
                inO = new ObjectInputStream(clientSc.getInputStream()); //recibir mensaje
                mes = (Message) inO.readObject();
                
                LOG.info(mes.getAccion().toString()+" REQUESTED FOR "+mes.getUser().getLogin());       
            }
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "An error occurred trying to connect with Client", ex);
        }
    }  
}
