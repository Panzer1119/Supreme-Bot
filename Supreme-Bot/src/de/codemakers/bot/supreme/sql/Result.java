package de.codemakers.bot.supreme.sql;

import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Result
 *
 * @author Panzer1119
 */
public class Result {

    public final Statement statement;
    public final ResultSet resultSet;

    public Result(Statement statement, ResultSet resultSet) {
        this.statement = statement;
        this.resultSet = resultSet;
    }

}
