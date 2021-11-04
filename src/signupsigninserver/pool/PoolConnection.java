/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package signupsigninserver.pool;

import java.sql.Connection;
import java.util.ResourceBundle;
import java.util.Stack;
import java.util.logging.Logger;
import org.apache.commons.dbcp2.BasicDataSource;

/**
 *
 * @author Jonathan Viñan, Daniel Brizuela, Aritz Arrieta
 */
public class PoolConnection {
    //LOGGER
    private static final Logger LOG = Logger.getLogger(PoolConnection.class.getName());

    private ResourceBundle resourceBundle = ResourceBundle.getBundle("signupsigninserver.pool/PoolData");
    private static PoolConnection dataSource;
    private static Stack<Connection> poolStack = new Stack<>();
    private Connection con;
    private final BasicDataSource basicDataSource;

    //Pool
    private PoolConnection() {
        basicDataSource = new BasicDataSource();
        basicDataSource.setDriverClassName(resourceBundle.getString("DRIVER"));
        basicDataSource.setUsername(resourceBundle.getString("USER"));
        basicDataSource.setPassword(resourceBundle.getString("PASS"));
        basicDataSource.setUrl(resourceBundle.getString("URL"));
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
     * @return
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
        con.close();
        poolStack.push(con);
    }
}
