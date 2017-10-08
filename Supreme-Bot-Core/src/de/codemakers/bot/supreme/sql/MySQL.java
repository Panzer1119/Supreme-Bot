package de.codemakers.bot.supreme.sql;

import de.codemakers.bot.supreme.sql.annotations.SQLTable;
import de.codemakers.bot.supreme.util.Standard;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;
import org.reflections.Reflections;

/**
 * MySQL
 *
 * @author Panzer1119
 */
public class MySQL {

    public static final Database STANDARD_DATABASE = new Database(null, null, null, null);
    public static final String SQL_TABLE_TEMP_BANS = "temp_bans";
    public static final String SQL_TABLE_TEMP_BANS_ARCHIVE = "archive_temp_bans";

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

    public static final void init() {
        try {
            STANDARD_DATABASE.close();
            STANDARD_DATABASE.setHostname(Standard.STANDARD_SETTINGS.asAutoAdd().getProperty("sql_hostname", null));
            STANDARD_DATABASE.setDatabase(Standard.STANDARD_SETTINGS.asAutoAdd().getProperty("sql_database", null));
            STANDARD_DATABASE.setUsername(Standard.STANDARD_SETTINGS.asAutoAdd().getProperty("sql_username", null));
            STANDARD_DATABASE.setPassword(Standard.STANDARD_SETTINGS.asAutoAdd().getProperty("sql_password", null));
            STANDARD_DATABASE.connect(() -> {
                try {
                    new Reflections(Standard.BASE_PACKAGE).getTypesAnnotatedWith(SQLTable.class).stream().filter((clazz) -> clazz.getAnnotation(SQLTable.class).createIfNotExists()).forEach((clazz) -> SQLUtil.createTableIfNotExists(clazz, STANDARD_DATABASE));
                } catch (Exception ex) {
                    System.err.println("MySQL: Init 2 error");
                    ex.printStackTrace();
                }
            });
        } catch (Exception ex) {
            System.err.println("MySQL: Init 1 error");
            ex.printStackTrace();
        }
    }

    public static final Object[] ResultSetToArray(ResultSet resultSet) {
        if (resultSet == null) {
            return null;
        }
        try {
            if (resultSet.isAfterLast()) {
                return null;
            }
            final ArrayList<Object> arrayList = new ArrayList<>();
            Object object = null;
            int i = 1;
            do {
                try {
                    object = resultSet.getObject(i);
                    i++;
                    arrayList.add(object);
                } catch (Exception ex) {
                    i = -1;
                }
            } while (i != -1);
            return arrayList.toArray(new String[arrayList.size()]);
        } catch (Exception ex) {
            System.err.println("MySQL: ResultSetToArray error");
            ex.printStackTrace();
            return null;
        }
    }

    public static final String ResultSetToString(ResultSet resultSet) {
        if (resultSet == null) {
            return null;
        }
        try {
            final Object[] array = ResultSetToArray(resultSet);
            if (array == null) {
                return null;
            }
            final StringBuilder output = new StringBuilder();
            for (Object object : array) {
                output.append(object);
                output.append(", ");
            }
            if (output.length() != 0) {
                output.delete(output.length() - ", ".length(), output.length());
            }
            return output.toString();
        } catch (Exception ex) {
            System.err.println("MySQL: ResultSetToString error");
            ex.printStackTrace();
            return null;
        }
    }

}
