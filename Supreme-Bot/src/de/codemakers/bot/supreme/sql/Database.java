package de.codemakers.bot.supreme.sql;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Arrays;

/**
 * Database
 *
 * @author Panzer1119
 */
public class Database {

    public static final String SPLITTER = ";";

    private String hostname;
    private String database;
    private String username;
    private byte[] password;
    private Connection connection = null;

    public Database(String hostname, String database, String username, byte[] password) {
        this.hostname = hostname;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    public final String getHostname() {
        return hostname;
    }

    public final Database setHostname(String hostname) {
        if (isConnected()) {
            return this;
        }
        this.hostname = hostname;
        return this;
    }

    public final String getDatabase() {
        return database;
    }

    public final Database setDatabase(String database) {
        if (isConnected()) {
            return this;
        }
        this.database = database;
        return this;
    }

    public final String getUsername() {
        return username;
    }

    public final Database setUsername(String username) {
        if (isConnected()) {
            return this;
        }
        this.username = username;
        return this;
    }

    public final byte[] getPassword() {
        return password;
    }

    public final Database setPassword(byte[] password) {
        if (isConnected()) {
            return this;
        }
        this.password = password;
        return this;
    }

    public final Connection getConnection() {
        return connection;
    }

    public final Database setConnection(Connection connection) {
        this.connection = connection;
        return this;
    }

    public final boolean isConnected() {
        return connection != null;
    }

    public final boolean connect() {
        try {
            setConnection(MySQL.connect(hostname, database, username, password));
            return isConnected();
        } catch (Exception ex) {
            System.err.println("Database: Connection error");
            ex.printStackTrace();
            return false;
        }
    }

    public final Statement createStatement() {
        if (!isConnected()) {
            return null;
        }
        try {
            return connection.createStatement();
        } catch (Exception ex) {
            System.err.println("Database: Statement creation error");
            ex.printStackTrace();
            return null;
        }
    }

    public final Statement prepareStatement(String sql) {
        if (!isConnected()) {
            return null;
        }
        try {
            return connection.prepareStatement(sql);
        } catch (Exception ex) {
            System.err.println("Database: Statement preparing error");
            ex.printStackTrace();
            return null;
        }
    }

    public final boolean close() {
        try {
            connection.close();
            if (connection.isClosed()) {
                setConnection(null);
            }
            return isConnected();
        } catch (Exception ex) {
            System.err.println("Database: Closing error");
            ex.printStackTrace();
            return false;
        }
    }

    public final boolean commit() {
        try {
            connection.commit();
            return true;
        } catch (Exception ex) {
            System.err.println("Database: Committing error");
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public String toString() {
        return String.format("%s%s%s%s%s%s%s", hostname, SPLITTER, database, SPLITTER, username, SPLITTER, Arrays.toString(password));
    }

    public static final Database of(String text) {
        if (text == null || text.isEmpty() || !text.contains(SPLITTER)) {
            return null;
        }
        try {
            final String[] split = text.split(SPLITTER);
            final String hostname = split[0];
            final String database = split[1];
            final String username = split[2];
            final String password_string = split[3].substring(0, split[3].length() - 1);
            final String[] split_2 = password_string.split(", ");
            byte[] password = new byte[split_2.length];
            for (int i = 0; i < password.length; i++) {
                password[i] = Byte.parseByte(split_2[i]);
            }
            return new Database(hostname, database, username, password);
        } catch (Exception ex) {
            System.err.println("Database: Creation error");
            ex.printStackTrace();
            return null;
        }
    }

}
