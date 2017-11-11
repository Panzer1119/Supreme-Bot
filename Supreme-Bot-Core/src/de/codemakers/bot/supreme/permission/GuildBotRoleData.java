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
    @SQLField(index = 2, column = "ID", nullBehavior = NullBehavior.NOT_NULL, primaryKey = true, type = JDBCType.BIGINT)
    public final long id;
    @SQLField(index = 3, column = "botRole_ID", nullBehavior = NullBehavior.NOT_NULL, primaryKey = true, type = JDBCType.BIGINT)
    public final long botRole_id;
    @SQLField(index = 4, column = "granter_ID", nullBehavior = NullBehavior.NULL, defaultValue = "NULL", type = JDBCType.BIGINT)
    public final long granter_id;
    @SQLField(index = 5, column = "isRole", nullBehavior = NullBehavior.NOT_NULL, defaultValue = "1", type = JDBCType.TINYINT)
    public final boolean isRole;

    public GuildBotRoleData(long guild_id, long id, long botRole_id, long granter_id, boolean isRole) {
        this.guild_id = guild_id;
        this.id = id;
        this.botRole_id = botRole_id;
        this.granter_id = granter_id;
        this.isRole = isRole;
    }

    protected static final Queue<GuildBotRoleData> reloadData() {
        final ArrayList<GuildBotRoleData> data = SQLUtil.deserializeObjects(GuildBotRoleData.class, -1);
        if (data != null) {
            BOT_ROLE_DATA.clear();
            BOT_ROLE_DATA.addAll(data);
        }
        return BOT_ROLE_DATA;
    }

    public static final boolean isGranted(long guild_id, long id, long botRole_id, boolean isRole) {
        return BOT_ROLE_DATA.stream().anyMatch((guildBotRoleData) -> (guildBotRoleData.guild_id == guild_id && guildBotRoleData.id == id && guildBotRoleData.botRole_id == botRole_id && guildBotRoleData.isRole == isRole));
    }

}
