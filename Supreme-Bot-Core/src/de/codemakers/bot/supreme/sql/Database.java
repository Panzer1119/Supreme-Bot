package de.codemakers.bot.supreme.sql;

import de.codemakers.bot.supreme.sql.annotations.SQLTable;
import de.codemakers.bot.supreme.util.Copyable;
import de.codemakers.bot.supreme.util.updater.Updater;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.concurrent.Future;

/**
 * Database
 *
 * @author Panzer1119
 */
public class Database implements Copyable {

    public static final boolean DEBUG = true;
    public static final boolean DEBUG_SQL = false;
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

    public final Future<?> connect(Runnable... runs) {
        return Updater.submit(() -> {
            try {
                final int id = (int) (Math.random() * 1_000_000);
                if (DEBUG) {
                    System.out.println(String.format("MySQL (%d): trying to connect to \"%s\" at database \"%s\" with username \"%s\"", id, hostname, database, username));
                }
                setConnection(MySQL.connect(hostname, database, username, password));
                if (DEBUG) {
                    System.out.println(String.format("MySQL (%d): connection established: %b", id, isConnected()));
                }
                if (runs != null && runs.length != 0) {
                    Arrays.asList(runs).forEach((run) -> run.run());
                }
            } catch (Exception ex) {
                System.err.println("Database: Connection error");
                ex.printStackTrace();
            }
        });
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
        String temp = null;
        try {
            temp = (args != null && args.length != 0) ? String.format(sql, args) : sql;
            if (DEBUG_SQL) {
                System.out.println(temp);
            }
            return connection.prepareStatement(temp);
        } catch (Exception ex) {
            System.err.println("Database: Statement preparing error");
            if (DEBUG) {
                System.err.println("SQL: " + temp);
            }
            ex.printStackTrace();
            return null;
        }
    }

    public final boolean execute(String sql, Object... args) {
        if (!isConnected()) {
            return false;
        }
        String temp = null;
        try {
            temp = (args != null && args.length != 0) ? String.format(sql, args) : sql;
            if (DEBUG_SQL) {
                System.out.println(temp);
            }
            final Statement statement = createStatement();
            statement.execute(temp);
            final boolean result = statement.getUpdateCount() >= 0;
            statement.close();
            return result;
        } catch (Exception ex) {
            System.err.println("Database: Executing error");
            if (DEBUG) {
                System.err.println("SQL: " + temp);
            }
            ex.printStackTrace();
            return false;
        }
    }

    public final Result executeQuery(String sql, Object... args) {
        if (!isConnected()) {
            return null;
        }
        String temp = null;
        try {
            temp = (args != null && args.length != 0) ? String.format(sql, args) : sql;
            if (DEBUG_SQL) {
                System.out.println(temp);
            }
            final Statement statement = createStatement();
            final ResultSet resultSet = statement.executeQuery(temp);
            return new Result(statement, resultSet);
        } catch (Exception ex) {
            System.err.println("Database: Executing query error");
            if (DEBUG) {
                System.err.println("SQL: " + temp);
            }
            ex.printStackTrace();
            return null;
        }
    }

    public final int executeUpdate(String sql, Object... args) {
        if (!isConnected()) {
            return -1;
        }
        String temp = null;
        try {
            temp = (args != null && args.length != 0) ? String.format(sql, args) : sql;
            if (DEBUG_SQL) {
                System.out.println(temp);
            }
            final Statement statement = createStatement();
            final int result = statement.executeUpdate(temp);
            statement.close();
            return result;
        } catch (Exception ex) {
            System.err.println("Database: Executing update error");
            if (DEBUG) {
                System.err.println("SQL: " + temp);
            }
            ex.printStackTrace();
            return -1;
        }
    }

    public final long executeLargeUpdate(String sql, Object... args) {
        if (!isConnected()) {
            return -1;
        }
        String temp = null;
        try {
            temp = (args != null && args.length != 0) ? String.format(sql, args) : sql;
            if (DEBUG_SQL) {
                System.out.println(temp);
            }
            final Statement statement = createStatement();
            final long result = statement.executeLargeUpdate(temp);
            statement.close();
            return result;
        } catch (Exception ex) {
            System.err.println("Database: Executing large update error");
            if (DEBUG) {
                System.err.println("SQL: " + temp);
            }
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

    public final boolean archive(Object object) {
        if (object == null || !isConnected()) {
            return false;
        }
        try {
            final Class<?> clazz = object.getClass();
            final SQLTable table = clazz.getAnnotation(SQLTable.class);
            final String table_archive = (table.extras().length >= 1 ? table.extras()[0] : ((!table.name().startsWith("archive_") ? "archive_" : "") + table.name()));
            SQLUtil.removeObjects(clazz, this, object);
            return SQLUtil.serializeObjects(clazz, this, true, table_archive, object);
        } catch (Exception ex) {
            System.err.println("Database: Archiving error");
            ex.printStackTrace();
            return false;
        }
    }

    public final boolean update(Object object) {
        if (object == null || !isConnected()) {
            return false;
        }
        try {
            final Class<?> clazz = object.getClass();
            SQLUtil.removeObjects(clazz, this, object);
            return SQLUtil.serializeObjects(clazz, this, true, object);
        } catch (Exception ex) {
            System.err.println("Database: Updating error");
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
