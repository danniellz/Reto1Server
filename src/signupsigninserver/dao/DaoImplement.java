/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package signupsigninserver.dao;

import exceptions.UserAlreadyExistException;
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
 * Class that Connect with the DB to make the SignIn or SignUp
 * 
 * @author Aritz Arrieta, Daniel Brizuela
 */
public class DaoImplement implements Signable {
    //LOGGER
    private static final Logger LOG = Logger.getLogger(SignUpSignInServer.class.getName());

    private Connection con;
    private PreparedStatement stmt;
    private CallableStatement cstmt;
    private PoolConnection pool = PoolConnection.getInstace();

    private final String SignIn = "{CALL Login(?, ?)}";
    private final String SignUp = "INSERT INTO user (login, email, fullName, user.User_Status, user.User_Privilege, user.passw,user.lastPasswordChange) VALUES (?,?,?,'enabled','user',?, NOW());";
    private final String UserExist = "SELECT user.login FROM user WHERE user.login= ?";
    /**
     * Method for the SignIn process
     * 
     * @param user contains the login info
     * @return the user object containing the data
     */
    @Override
    public User signIn(User user) {
        ResultSet rs = null;
        try {
            con = pool.getConnection();
        } catch (Exception ex) {
            Logger.getLogger(DaoImplement.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            LOG.info("Doing SQL FOR "+user.getLogin());
            cstmt = con.prepareCall(SignIn);
            cstmt.setString(1, user.getLogin());
            cstmt.setString(2, user.getPassword());
            cstmt.execute();
            rs = cstmt.getResultSet();
            if (rs.next()) {
                LOG.info("Saving User data...");
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
            LOG.log(Level.SEVERE, "SQL Error SingIn", ex);
        }
        
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException ex) {
                LOG.log(Level.SEVERE, "Error Closing RS in SignIn Process", ex);
            }
        }

        try {
            LOG.info("Closing SQL");
            pool.closeConnection(con);
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "SQL Error while Closing SignIn Process", ex);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error Closing Connection in SignIn Process", ex);
        }
        return user;
    }

    /**
     * Method for the SignUp process
     * 
     * @param user contains the register data
     * @return 
     */
    @Override
    public User signUp(User user) throws UserAlreadyExistException {
        
        Boolean exist = userExist(user.getLogin()); 
        
        try {
            con = pool.getConnection();
        } catch (Exception ex) {
            Logger.getLogger(DaoImplement.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
           
            stmt = con.prepareStatement(SignUp);
            stmt.setString(1, user.getLogin());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getFullName());
            stmt.setString(4, user.getPassword());
            //stmt.executeUpdate();
            if(!exist){
                stmt.executeUpdate();
            }else{
              throw new UserAlreadyExistException();
            }
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "SQL Error SingUp", ex);
        }
        try {
             pool.closeConnection(con);
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "SQL Error in SignUp Process", ex);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error Closing Connection in SignUp Process", ex);
        }
        return user;

    }
public Boolean userExist(String login){
    boolean exist = false;
    ResultSet rs= null;  
    try {
            con = pool.getConnection();
        } catch (Exception ex) {
            Logger.getLogger(DaoImplement.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
             //*****COMPROBACION DE USER YA EXISTE******  
              stmt = con.prepareStatement(UserExist);
              stmt.setString(1, login);
              rs = stmt.executeQuery();
                if(rs.next()){
                   exist= true; 
                }
             //***********FINAL DE COMPROBACION***********  
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
    
    
    return exist;

}
}
