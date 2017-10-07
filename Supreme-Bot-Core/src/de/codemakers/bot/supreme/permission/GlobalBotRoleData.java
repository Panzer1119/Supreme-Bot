package de.codemakers.bot.supreme.permission;

import de.codemakers.bot.supreme.sql.NullBehavior;
import de.codemakers.bot.supreme.sql.annotations.SQLField;
import de.codemakers.bot.supreme.sql.annotations.SQLTable;
import java.sql.JDBCType;
import java.util.ArrayList;

/**
 * GlobalBotRoleData
 *
 * @author Panzer1119
 */
@SQLTable(name = "global_bot_role_data", types = {JDBCType.BIGINT, JDBCType.BIGINT, JDBCType.BIGINT})
public class GlobalBotRoleData {

    protected static final ArrayList<GlobalBotRoleData> BOT_ROLE_DATA = new ArrayList<>();

    @SQLField(index = 1, column = "user_ID", nullBehavior = NullBehavior.NOT_NULL, primaryKey = true, type = JDBCType.BIGINT)
    public final long user_id;
    @SQLField(index = 2, column = "botRole_ID", nullBehavior = NullBehavior.NOT_NULL, primaryKey = true, type = JDBCType.BIGINT)
    public final long botRole_id;
    @SQLField(index = 3, column = "granter_ID", nullBehavior = NullBehavior.NULL, defaultValue = "NULL", type = JDBCType.BIGINT)
    public final long granter_id;

    public GlobalBotRoleData(long user_id, long botRole_id, long granter_id) {
        this.user_id = user_id;
        this.botRole_id = botRole_id;
        this.granter_id = granter_id;
    }

}
