package signupsigninserver.pool;

import java.sql.Connection;
import java.util.ResourceBundle;
import java.util.Stack;
import java.util.logging.Logger;
import org.apache.commons.dbcp2.BasicDataSource;

/**
 * Class that manage connections
 *
 * @author Jonathan Viñan, Aritz Arrieta
 * @version 1.0
 */
public class PoolConnection {

    //LOGGER
    private static final Logger LOG = Logger.getLogger(PoolConnection.class.getName());
    
    //Attibutes
    private ResourceBundle poolData = ResourceBundle.getBundle("signupsigninserver.pool/poolData");
    private static PoolConnection dataSource;
    private static Stack<Connection> poolStack = new Stack<>();
    private Connection con;
    private final BasicDataSource basicDataSource;

    //Pool
    private PoolConnection() {
        basicDataSource = new BasicDataSource();
        basicDataSource.setDriverClassName(poolData.getString("DRIVER"));
        basicDataSource.setUsername(poolData.getString("USER"));
        basicDataSource.setPassword(poolData.getString("PASS"));
        basicDataSource.setUrl(poolData.getString("URL"));
        basicDataSource.setMaxTotal(Integer.parseInt(poolData.getString("MAXCONNECTIONS")));
    }

    /**
     * Pool Singleton
     *
     * @return a new PoolConnection
     */
    public static PoolConnection getInstace() {
        if (dataSource == null) {
            dataSource = new PoolConnection();
        }
        return dataSource;
    }

    /**
     * Get a Connection from the stack if is not empty or create a new Connection
     * 
     * @return a connection from the stack
     * @throws Exception when failed to get a connection
     */
    public synchronized Connection getConnection() throws Exception {
        LOG.info("GETTING CONNECTION");
        if (poolStack.isEmpty()) {
            LOG.info("Pool Empty, Getting New Connection");
            con = basicDataSource.getConnection();
        } else {
            con = poolStack.pop();
        }
        return con;
    }

    /**
     * Method that save the connection in a Stack when a client is done using it
     *
     * @param connection contains a connection form DaoImplement
     * @throws Exception if saving the connection fails
     */
    public synchronized void closeConnection(Connection connection) throws Exception {
        LOG.info("SAVING CONNECTION");
        poolStack.push(connection);
    }
}
