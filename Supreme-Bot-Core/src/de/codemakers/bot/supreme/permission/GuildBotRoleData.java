package de.codemakers.bot.supreme.permission;

import de.codemakers.bot.supreme.sql.NullBehavior;
import de.codemakers.bot.supreme.sql.SQLUtil;
import de.codemakers.bot.supreme.sql.annotations.SQLField;
import de.codemakers.bot.supreme.sql.annotations.SQLTable;
import java.sql.JDBCType;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * GuildBotRoleData
 *
 * @author Panzer1119
 */
@SQLTable(name = "guild_bot_role_data", types = {JDBCType.BIGINT, JDBCType.BIGINT, JDBCType.BIGINT})
public class GuildBotRoleData {

    protected static final Queue<GuildBotRoleData> BOT_ROLE_DATA = new ConcurrentLinkedQueue<>();

    @SQLField(index = 1, column = "guild_ID", nullBehavior = NullBehavior.NOT_NULL, primaryKey = true, type = JDBCType.BIGINT)
    public final long guild_id;
    @SQLField(index = 2, column = "role_ID", nullBehavior = NullBehavior.NOT_NULL, primaryKey = true, type = JDBCType.BIGINT)
    public final long role_id;
    @SQLField(index = 3, column = "botRole_ID", nullBehavior = NullBehavior.NOT_NULL, primaryKey = true, type = JDBCType.BIGINT)
    public final long botRole_id;
    @SQLField(index = 4, column = "granter_ID", nullBehavior = NullBehavior.NULL, defaultValue = "NULL", type = JDBCType.BIGINT)
    public final long granter_id;

    public GuildBotRoleData(long guild_id, long role_id, long botRole_id, long granter_id) {
        this.guild_id = guild_id;
        this.role_id = role_id;
        this.botRole_id = botRole_id;
        this.granter_id = granter_id;
    }

    protected static final Queue<GuildBotRoleData> reloadData() {
        final ArrayList<GuildBotRoleData> data = SQLUtil.deserializeObjects(GuildBotRoleData.class);
        System.err.println("LOADED DATA: " + data);
        if (data != null) {
            BOT_ROLE_DATA.clear();
            BOT_ROLE_DATA.addAll(data);
        }
        return BOT_ROLE_DATA;
    }

    public static final boolean isRole(long guild_id, long role_id, long botRole_id) {
        return BOT_ROLE_DATA.stream().anyMatch((guildBotRoleData) -> (guildBotRoleData.guild_id == guild_id && guildBotRoleData.role_id == role_id && guildBotRoleData.botRole_id == botRole_id));
    }

}
