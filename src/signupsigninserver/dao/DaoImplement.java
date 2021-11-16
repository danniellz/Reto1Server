package signupsigninserver.dao;

import exceptions.UserAlreadyExistException;
import exceptions.DatabaseNotFoundException;
import exceptions.IncorrectPasswordException;
import exceptions.UserNotFoundException;
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
 * @version 1.0
 */
public class DaoImplement implements Signable {

    //LOGGER
    private static final Logger LOG = Logger.getLogger(SignUpSignInServer.class.getName());

    //Attributes
    private Connection con;
    private PreparedStatement stmt;
    private CallableStatement cstmt;
    private final PoolConnection pool = PoolConnection.getInstace();

    //SQL sentences
    private final String SignIn = "{CALL Login(?, ?)}";
    private final String SignUp = "INSERT INTO user (login, email, fullName, user.User_Status, user.User_Privilege, user.passw,user.lastPasswordChange) VALUES (?,?,?,'enabled','User',?, NOW());";
    private final String UserExist = "SELECT user.login FROM user WHERE user.login= ?";

    /**
     * Method for the SignIn process
     *
     * @param user contains the login info
     * @throws exceptions.DatabaseNotFoundException if an error with the
     * DBoccurred, error message
     * @throws exceptions.UserNotFoundException
     * @throws exceptions.IncorrectPasswordException
     * @return the user object containing the data
     */
    @Override
    public User signIn(User user) throws DatabaseNotFoundException, UserNotFoundException, IncorrectPasswordException {
        ResultSet rs = null;
        String pass = "";
        Boolean exist = userExist(user.getLogin());

        //Get a connection from the pool
        try {
            con = pool.getConnection();
        } catch (Exception ex) {
            Logger.getLogger(DaoImplement.class.getName()).log(Level.SEVERE, null, ex);
            throw new DatabaseNotFoundException();
        }

        try {
            //SQL procedure
            LOG.info("Doing SQL FOR " + user.getLogin());
            cstmt = con.prepareCall(SignIn);
            cstmt.setString(1, user.getLogin());
            cstmt.setString(2, user.getPassword());
            LOG.info("Checking if User Exist...");

            //Check if the UsernameExist
            if (exist) {
                LOG.info("User Exist!");
                cstmt.execute();
                rs = cstmt.getResultSet();

                //Save the BD password of the user
                if (rs.next()) {
                    pass = rs.getString(7);
                }

                LOG.info("Checking if Password is correct...");
                //Compare if the user password introduced is the same, if not throw an exception
                if (user.getPassword().equals(pass)) {
                    LOG.info("Password Correct!");
                    LOG.info("Saving User data...");
                    user.setId(rs.getInt(1));
                    user.setLogin(rs.getString(2));
                    user.setEmail(rs.getString(3));
                    user.setFullName(rs.getString(4));
                    user.setStatus(UserStatus.ENABLED);
                    user.setPrivilege(UserPrivilege.USER);
                    user.setPassword(rs.getString(7));
                } else {
                    //if the password is incorrect, throw an error  
                    throw new IncorrectPasswordException();
                }

            } else {
                //if user doesn't exist, throw an error 
                throw new UserNotFoundException();
            }

        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "SQL Error SingIn - DB not found or Incorrect Sintax", ex);
            throw new DatabaseNotFoundException();
        } finally {
            LOG.info("Closing SQL");
            try {
                pool.closeConnection(con);
            } catch (Exception ex) {
                Logger.getLogger(DaoImplement.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        //Close RS
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException ex) {
                LOG.log(Level.SEVERE, "Error Closing RS in SignIn Process", ex);
            }
        }

        return user;
    }

    /**
     * Method for the SignUp process
     *
     * @param user contains the register data
     * @throws exceptions.UserAlreadyExistException if the user already exist,
     * error message
     * @return a user object
     */
    @Override
    public User signUp(User user) throws UserAlreadyExistException {
        Boolean exist = userExist(user.getLogin());

        try {
            con = pool.getConnection();
        } catch (Exception ex) {
            Logger.getLogger(DaoImplement.class.getName()).log(Level.SEVERE, "Getting connection from pool failed", ex);
        }
        try {
            stmt = con.prepareStatement(SignUp);
            stmt.setString(1, user.getLogin());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getFullName());
            stmt.setString(4, user.getPassword());

            if (!exist) {
                stmt.executeUpdate();
            } else {
                throw new UserAlreadyExistException();
            }

        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "SQL Error SingUp", ex);
        } finally {
            LOG.info("Closing SQL");
            try {
                pool.closeConnection(con);
            } catch (Exception ex) {
                Logger.getLogger(DaoImplement.class.getName()).log(Level.SEVERE, "Error Closing Connection in SignIn Process", ex);
            }
        }
        return user;
    }

    /**
     * Method that return a boolean if the username received exist or not
     *
     * @param login contains the username to verify if it exist in the DB
     * @return a boolean if it exist or not
     */
    public Boolean userExist(String login) {
        boolean exist = false;
        ResultSet rs = null;
        try {
            con = pool.getConnection();
        } catch (Exception ex) {
            Logger.getLogger(DaoImplement.class.getName()).log(Level.SEVERE, "Getting connection from pool failed in SignUp", ex);
        }
        try {
            //*****COMPROBACION DE USER YA EXISTE******  
            stmt = con.prepareStatement(UserExist);
            stmt.setString(1, login);
            rs = stmt.executeQuery();
            if (rs.next()) {
                exist = true;
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

        //Close RS
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException ex) {
                LOG.log(Level.SEVERE, "Error Closing RS in SignIn Process", ex);
            }
        }
        return exist;
    }
}
