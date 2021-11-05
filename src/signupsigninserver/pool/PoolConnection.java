package signupsigninserver.pool;

import java.sql.Connection;
import java.util.ResourceBundle;
import java.util.Stack;
import java.util.logging.Logger;
import org.apache.commons.dbcp2.BasicDataSource;

/**
 * Class that manage connections
 * 
 * @author Jonathan Viñan, Daniel Brizuela, Aritz Arrieta
 */
public class PoolConnection {
    //LOGGER
    private static final Logger LOG = Logger.getLogger(PoolConnection.class.getName());

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
     * 
     * @return a connection from the stack
     * @throws Exception 
     */
    public synchronized Connection getConnection() throws Exception {
        LOG.info("GETTING CONNECTION");
        if (poolStack.isEmpty()) {
            LOG.info("Pool empty, Getting new Connection");
            con = basicDataSource.getConnection();
            poolStack.push(con);
        }
        return poolStack.pop();
    }

    /**
     * 
     * @param connection
     * @throws Exception 
     */
    public synchronized void closeConnection(Connection connection) throws Exception {
        LOG.info("CLOSING AND SAVING CONNECTION");
        connection.close();
        poolStack.push(connection);
    }
}
