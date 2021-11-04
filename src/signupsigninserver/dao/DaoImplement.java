/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package signupsigninserver.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import signable.Signable;
import signupsigninserver.SignUpSignInServer;
import signupsigninserver.pool.PoolConnection;
import user.User;
import user.UserPrivilege;
import user.UserStatus;
/**
 *
 * @author Aritz Arrieta
 */
public class DaoImplement implements Signable {
    //LOGGER
    private static final Logger LOG = Logger.getLogger(SignUpSignInServer.class.getName());

    private Connection con;
    private PreparedStatement stmt;
    private CallableStatement cstmt;
    private PoolConnection pool = PoolConnection.getInstace();

    private final String SignIn = "CALL 'Login'(?,?)";
    private final String SignUp = "INSERT INTO user (login, email, fullName, user.User_Status, user.User_Privilege, user.PASSWORD) VALUES (?,?,?,'enabled','user',?);";

    @Override
    public User signIn(User user) {
        ResultSet rs = null;
        try {
            con = pool.getConnection();
        } catch (Exception ex) {
            Logger.getLogger(DaoImplement.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            stmt = con.prepareStatement(SignIn);
            stmt.setString(1, user.getLogin());
            stmt.setString(2, user.getPassword());
            LOG.info("Ejecucion del procedimiento");
            rs = stmt.executeQuery();
            LOG.info("Ejecucion del procedimiento exitosa");
            if (rs.next()) {
                LOG.info("Guardado de parametros dentro del objeto user");
                user.setId(rs.getInt(1));
                user.setLogin(rs.getString(2));
                user.setEmail(rs.getString(3));
                user.setFullName(rs.getString(4));
                user.setStatus(UserStatus.ENABLED);
                user.setPrivilege(UserPrivilege.USER);
                user.setPassword(rs.getString(7));
                user.setLastPasswordChange(rs.getTimestamp(8));
            }
        }catch(SQLException ex) {
             Logger.getLogger(DaoImplement.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("SQL ERROR");
        }
        
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException ex) {
                LOG.log(Level.SEVERE, "Error Closing RS in SignIn Process", ex);
            }
        }

        try {
            pool.closeConnection(con);
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "SQL Error in SignIn Process", ex);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error Closing Connection in SignIn Process", ex);
        }
        LOG.info(user.getFullName());
        return user;
    }

    @Override
    public void signUp(User user) {
        try {
            con = pool.getConnection();
        } catch (Exception ex) {
            Logger.getLogger(DaoImplement.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            stmt = con.prepareStatement(SignUp);
            stmt.setString(0, user.getLogin());
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getFullName());

            stmt.setString(5, user.getPassword());
        } catch (SQLException ex) {
            Logger.getLogger(DaoImplement.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
             pool.closeConnection(con);
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "SQL Error in SignUp Process", ex);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error Closing Connection in SignUp Process", ex);
        }

    }

}
