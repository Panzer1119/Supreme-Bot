package de.codemakers.bot.supreme.sql;

import de.codemakers.bot.supreme.util.Copyable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;

/**
 * Database
 *
 * @author Panzer1119
 */
public class Database implements Copyable {

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

    public final Database setPassword(String password) {
        if (isConnected()) {
            return this;
        }
        if (password != null) {
            this.password = password.getBytes();
        } else {
            this.password = null;
        }
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

    public final PreparedStatement prepareStatement(String sql, Object... args) {
        if (!isConnected()) {
            return null;
        }
        try {
            String temp = (args != null && args.length != 0) ? String.format(sql, args) : sql;
            System.out.println(temp);
            return connection.prepareStatement(temp);
        } catch (Exception ex) {
            System.err.println("Database: Statement preparing error");
            ex.printStackTrace();
            return null;
        }
    }

    public final boolean execute(String sql, Object... args) {
        if (!isConnected()) {
            return false;
        }
        try {
            String temp = (args != null && args.length != 0) ? String.format(sql, args) : sql;
            System.out.println(temp);
            final Statement statement = createStatement();
            final boolean result = statement.execute(temp);
            statement.close();
            return result;
        } catch (Exception ex) {
            System.err.println("Database: Executing error");
            ex.printStackTrace();
            return false;
        }
    }

    public final ResultSet executeQuery(String sql, Object... args) {
        if (!isConnected()) {
            return null;
        }
        try {
            String temp = (args != null && args.length != 0) ? String.format(sql, args) : sql;
            System.out.println(temp);
            final Statement statement = createStatement();
            final ResultSet resultSet = statement.executeQuery(temp);
            statement.close();
            return resultSet;
        } catch (Exception ex) {
            System.err.println("Database: Executing query error");
            ex.printStackTrace();
            return null;
        }
    }

    public final int executeUpdate(String sql, Object... args) {
        if (!isConnected()) {
            return -1;
        }
        try {
            String temp = (args != null && args.length != 0) ? String.format(sql, args) : sql;
            System.out.println(temp);
            final Statement statement = createStatement();
            final int result = statement.executeUpdate(temp);
            statement.close();
            return result;
        } catch (Exception ex) {
            System.err.println("Database: Executing update error");
            ex.printStackTrace();
            return -1;
        }
    }

    public final long executeLargeUpdate(String sql, Object... args) {
        if (!isConnected()) {
            return -1;
        }
        try {
            String temp = (args != null && args.length != 0) ? String.format(sql, args) : sql;
            System.out.println(temp);
            final Statement statement = createStatement();
            final long result = statement.executeLargeUpdate(temp);
            statement.close();
            return result;
        } catch (Exception ex) {
            System.err.println("Database: Executing large update error");
            ex.printStackTrace();
            return -1;
        }
    }

    public final boolean close() {
        if (!isConnected()) {
            return true;
        }
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

    public final boolean archive(String table, String id) {
        if (!isConnected()) {
            return false;
        }
        try {
            final String table_archive = (!table.startsWith("archive_") ? "archive_" : "") + table;
            final int result_1 = executeUpdate("SELECT * INTO %s FROM %s WHERE ID == %s;", table_archive, table, id);
            final int result_2 = executeUpdate("DELETE FROM %s WHERE ID == %s;", table, id);
            return true;
        } catch (Exception ex) {
            System.err.println("Database: Archiving error");
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public final Database copy() {
        return new Database(hostname, database, username, password);
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
