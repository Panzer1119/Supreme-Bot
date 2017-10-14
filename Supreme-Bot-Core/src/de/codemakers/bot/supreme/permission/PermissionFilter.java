package de.codemakers.bot.supreme.permission;

import java.util.List;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;

/**
 * PermissionFilter
 *
 * @author Panzer1119
 */
public interface PermissionFilter {

    public String getName();

    public boolean isGuildPermissionGranted(GuildBotRole guildBotRole);

    default boolean isGuildPermissionGranted(List<GuildBotRole> guildBotRoles) {
        if (guildBotRoles == null) {
            System.err.println("PermissionFilter: isGuildPermissionGranted error");
            new Exception().printStackTrace();
            return false;
        }
        return guildBotRoles.stream().anyMatch((guildBotRole) -> isGuildPermissionGranted(guildBotRole));
    }

    public boolean isGlobalPermissionGranted(GlobalBotRole globalBotRole);

    default boolean isGlobalPermissionGranted(List<GlobalBotRole> globalBotRoles) {
        if (globalBotRoles == null) {
            System.err.println("PermissionFilter: isGlobalPermissionGranted error");
            new Exception().printStackTrace();
            return false;
        }
        return globalBotRoles.stream().anyMatch((globalBotRole) -> isGlobalPermissionGranted(globalBotRole));
    }

    default public boolean isPermissionGranted(Guild guild, User user) {
        return true;
    }

}
