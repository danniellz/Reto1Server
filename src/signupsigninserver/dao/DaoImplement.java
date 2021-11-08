package signupsigninserver.dao;

import exceptions.DatabaseNotFoundException;
import exceptions.UserPasswordException;
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

    private Connection con;
    private PreparedStatement stmt;
    private CallableStatement cstmt;
    private final PoolConnection pool = PoolConnection.getInstace();

    private final String SignIn = "{CALL Login(?, ?)}";
    private final String SignUp = "INSERT INTO user (login, email, fullName, user.User_Status, user.User_Privilege, user.PASSWORD) VALUES (?,?,?,'enabled','user',?);";

    /**
     * Method for the SignIn process
     *
     * @param user contains the login info
     * @throws exceptions.DatabaseNotFoundException
     * @throws exceptions.UserPasswordException
     * @return the user object containing the data
     */
    @Override
    public User signIn(User user) throws DatabaseNotFoundException, UserPasswordException {
        ResultSet rs = null;

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
            cstmt.execute();
            rs = cstmt.getResultSet();

            //if rs is null throw a exception
            if (rs.next()) {
                LOG.info("Saving User data...");
                user.setId(rs.getInt(1));
                user.setLogin(rs.getString(2));
                user.setEmail(rs.getString(3));
                user.setFullName(rs.getString(4));
                user.setStatus(UserStatus.ENABLED);
                user.setPrivilege(UserPrivilege.USER);
                user.setPassword(rs.getString(7));

            } else {
                throw new UserPasswordException();
            }

        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, "SQL Error SingIn", ex);
            throw new DatabaseNotFoundException();
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
     */
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
            LOG.log(Level.SEVERE, "SQL Error SingUp", ex);
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
