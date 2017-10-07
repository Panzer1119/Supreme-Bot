package de.codemakers.bot.supreme.permission;

import de.codemakers.bot.supreme.sql.MySQL;
import de.codemakers.bot.supreme.sql.SQLUtil;
import de.codemakers.bot.supreme.util.Standard;
import net.dv8tion.jda.core.entities.Member;

/**
 * BotRole
 *
 * @author Panzer1119
 */
public class BotRole {

    public static final void init() {
        //System.err.println(SQLUtil.serializeObjects(GuildBotRoleData.class, MySQL.STANDARD_DATABASE, false, new GuildBotRoleData(336876056265097237L, 336877240581095426L, 4, 243444280495046657L)));
        System.err.println("TEST");
        //GuildBotRole.BOT.test(null, null);
        final Member panzer1119 = Standard.getJDA().getGuildById(336876056265097237L).getMemberById(243444280495046657L);
        System.err.println(panzer1119);
        System.err.println(GuildBotRole.ADMIN);
        System.err.println(GuildBotRole.ADMIN.test(panzer1119));
    }

}
