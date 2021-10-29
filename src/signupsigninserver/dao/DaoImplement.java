/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package signupsigninserver.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import user.User;
import user.UserPrivilege;
import user.UserStatus;

/**
 *
 * @author Aritz Arrieta
 */
public class DaoImplement implements Dao {

    private Connection con;
    private PreparedStatement stmt;
    private ResourceBundle configFile;
    private String driverDB;
    private String userDB;
    private String passDB;
    private String urlDB;

    private final String SignIn = "SELECT user.*  from user WHERE user.login= ? AND user.PASSWORD= ? ";
    private final String SignUp = "INSERT INTO user (login, email, fullName, user.User_Status, user.User_Privilege, user.PASSWORD) VALUES (?,?,?,'enabled','user',?);";

/*    public DaoImplement() {

        this.configFile = ResourceBundle.getBundle("control.config");
        this.driverDB = configFile.getString("driver");
        this.urlDB = configFile.getString("con");
        this.userDB = configFile.getString("DBUSER");
        this.passDB = configFile.getString("DBPASS");

    }*/

    private void connection() {
        try {
            con = DriverManager.getConnection(this.urlDB, this.userDB, this.passDB);
        } catch (SQLException e) {
            System.out.println("Error al intentar abrir la base de datos");
        }
    }

    private void close() throws SQLException {
        if (stmt != null) {
            stmt.close();
        } else {
            con.close();
        }

    }

    @Override
    public User signIn(User u) {
        ResultSet rs = null;
        this.connection();
        try {
            stmt = con.prepareStatement(SignIn);
            stmt.setString(0, u.getLogin());
            stmt.setString(1, u.getPassword());
            rs = stmt.executeQuery();

            while (rs.next()) {
                u.setId(rs.getInt(0));
                u.setLogin(rs.getString(1));
                u.setEmail(rs.getString(2));
                u.setFullName(rs.getString(3));
                u.setStatus(UserStatus.ENABLED);
                u.setPrivilege(UserPrivilege.USER);
                u.setPassword(rs.getString(6));
            }
        } catch (SQLException ex) {
            Logger.getLogger(DaoImplement.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException ex) {
                Logger.getLogger(DaoImplement.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try {
            this.close();
        } catch (SQLException ex) {
            Logger.getLogger(DaoImplement.class.getName()).log(Level.SEVERE, null, ex);
        }
        return u;
    }

    @Override
    public void signUp(User u) {
        this.connection();
        try {
            stmt = con.prepareStatement(SignUp);
            stmt.setString(0, u.getLogin());
            stmt.setString(1, u.getEmail());
            stmt.setString(2, u.getFullName());

            stmt.setString(5, u.getPassword());
        } catch (SQLException ex) {
            Logger.getLogger(DaoImplement.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
