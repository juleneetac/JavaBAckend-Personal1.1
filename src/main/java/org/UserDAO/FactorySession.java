package org.UserDAO;

import org.SessionDAO.SessionImpl;
import org.SessionDAO.SessionDAO;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class FactorySession {
    public static SessionDAO openSession() {


        Connection conn = getConnection();

        SessionDAO session = new SessionImpl(conn);

        return session;
    }



    private static Connection getConnection() {
        Connection conn = null;
        try {

            Properties database = new Properties();            //to dont show the dbpassword. It is keep in an external file
            try (FileReader in = new FileReader("login.properties.txt")) {
                database.load(in);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String dbpass = database.getProperty("dbpassword");

            conn =DriverManager.getConnection("jdbc:mysql://localhost:3306/personal_proy" + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&user=root&password=" + dbpass);


        } catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        return conn;
    }
}
