package de.codemakers.bot.supreme.sql;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * MySQL
 *
 * @author Panzer1119
 */
public class MySQL {

    static {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (Exception ex) {
            System.err.println("Exception while initializing the MySQL JDBC Driver");
            ex.printStackTrace();
        }
    }

    static final Connection connect(String hostname, String database, String username, byte[] password) {
        try {
            return DriverManager.getConnection(String.format("jdbc:mysql://%s/%s?user=%s&password=%s", hostname, database, username, new String(password)));
        } catch (Exception ex) {
            System.err.println("MySQL: Connection error");
            ex.printStackTrace();
            return null;
        }
    }

}
