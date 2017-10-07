package de.codemakers.bot.supreme.permission;

import de.codemakers.bot.supreme.sql.annotations.SQLField;
import de.codemakers.bot.supreme.sql.annotations.SQLTable;
import java.sql.JDBCType;

/**
 * UserPermission
 *
 * @author Panzer1119
 */
@SQLTable(name = "user_permissions", types = {JDBCType.BIGINT, JDBCType.BIGINT, JDBCType.BIGINT})
public class UserPermission {

    @SQLField(index = 1, column = "user_ID", type = JDBCType.BIGINT)
    public final long user_id;
    @SQLField(index = 2, column = "botRole_ID", type = JDBCType.BIGINT)
    public final long botRole_id;
    @SQLField(index = 3, column = "granter_ID", type = JDBCType.BIGINT)
    public final long granter_id;

    public UserPermission(long user_id, long botRole_id, long granter_id) {
        this.user_id = user_id;
        this.botRole_id = botRole_id;
        this.granter_id = granter_id;
    }

}
