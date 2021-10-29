/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package signupsigninserver.pool;

import java.sql.Connection;
import java.util.ResourceBundle;
import org.apache.commons.dbcp2.BasicDataSource;

/**
 *
 * @author Jonathan Viñan 
 */
public class PoolConection {

    private ResourceBundle resourceBundle = ResourceBundle.getBundle("signupsigninserver/pool/PoolConection");
    private static PoolConection dataSource;

    private BasicDataSource basicDataSource = null;

    private PoolConection() {
        basicDataSource = new BasicDataSource();
        basicDataSource.setDriverClassName(resourceBundle.getString("DRIVE"));
        basicDataSource.setUsername(resourceBundle.getString("USER"));
        basicDataSource.setPassword(resourceBundle.getString("PASSWORD"));
        basicDataSource.setUrl(resourceBundle.getString("URL"));

    }

    public static PoolConection getInstace() {
        if (dataSource == null) {
            dataSource = new PoolConection();
        }
        return dataSource;
    }

    public Connection getConection() throws Exception {
        return this.basicDataSource.getConnection();
    }

    public void closeConnection(Connection connection) throws Exception {
        connection.close();
    }
}
