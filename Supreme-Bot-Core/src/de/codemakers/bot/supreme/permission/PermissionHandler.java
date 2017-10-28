package de.codemakers.bot.supreme.permission;

import de.codemakers.bot.supreme.commands.Command;
import de.codemakers.bot.supreme.entities.MessageEvent;
import de.codemakers.bot.supreme.util.Standard;
import java.util.List;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;

/**
 * PermissionHandler
 *
 * @author Panzer1119
 */
public class PermissionHandler {

    public static final boolean isPermissionGranted(PermissionFilter filter, MessageEvent event) {
        if (filter == null) {
            return true;
        }
        if (event == null) {
            return false;
        }
        return isPermissionGranted(filter, event.getGuild(), event.getAuthor());
    }

    public static final boolean isPermissionGranted(PermissionFilter filter, Member member) {
        return isPermissionGranted(filter, member.getGuild(), member.getUser());
    }

    public static final boolean isPermissionGranted(PermissionFilter filter, Guild guild, User user) {
        if (filter == null) {
            return true;
        }
        if (Standard.isSuperOwner(user)) {
            return true;
        }
        if (user == null) {
            return false;
        }
        if (!filter.isPermissionGranted(guild, user)) {
            return false;
        }
        if (filter.isGlobalPermissionGranted(GlobalBotRole.getGlobalBotRolesByUser(user))) {
            return true;
        }
        return guild != null && filter.isGuildPermissionGranted(GuildBotRole.getGuildBotRolesByGuildAndUser(guild, user));
    }

    public static final boolean isPermissionGranted(PermissionFilter filter, Channel channel) {
        return isPermissionGranted(filter, channel, false);
    }

    public static final boolean isPermissionGranted(PermissionFilter filter, Channel channel, boolean memberOnly) {
        if (filter == null) {
            return true;
        }
        if (channel == null || channel.getRolePermissionOverrides().isEmpty()) {
            return false;
        }
        if (channel.getMemberPermissionOverrides().stream().filter((po) -> !po.getDenied().contains(Permission.MESSAGE_READ) || !po.getDenied().contains(Permission.MESSAGE_HISTORY)).anyMatch((po) -> (!filter.isGuildPermissionGranted(GuildBotRole.getGuildBotRolesByMember(po.getMember())) && !filter.isGlobalPermissionGranted(GlobalBotRole.getGlobalBotRolesByUser(po.getMember().getUser()))))) {
            return false;
        }
        if (memberOnly) {
            return true;
        }
        return channel.getRolePermissionOverrides().stream().filter((po) -> !po.getDenied().contains(Permission.MESSAGE_READ) || !po.getDenied().contains(Permission.MESSAGE_HISTORY)).noneMatch((po) -> !filter.isGuildPermissionGranted(GuildBotRole.getGuildBotRolesByRole(po.getRole())));
    }

    public static final boolean isPermissionGranted(List<Command> commands, Channel channel) {
        if (channel == null) {
            return false;
        }
        if (commands == null || commands.isEmpty()) {
            return true;
        }
        return commands.stream().allMatch((command) -> isPermissionGranted(command.getPermissionFilter(), channel));
    }

    public static final boolean sendNoPermissionMessage(MessageEvent event) {
        return event.sendMessage(Standard.getNoPermissionMessage(event.getAuthor(), "command"));
    }

}
