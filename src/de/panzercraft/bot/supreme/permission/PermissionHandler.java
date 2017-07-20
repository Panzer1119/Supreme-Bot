package de.panzercraft.bot.supreme.permission;

import de.panzercraft.bot.supreme.entities.MessageEvent;
import de.panzercraft.bot.supreme.util.Standard;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.PermissionOverride;
import net.dv8tion.jda.core.entities.Role;

/**
 * PermissionHandler
 *
 * @author Panzer1119
 */
public class PermissionHandler {

    public static final boolean check(PermissionRoleFilter filter, MessageEvent event, boolean withMessage) {
        if (filter == null) {
            return true;
        }
        if (event == null) {
            return false;
        }
        if (!event.isPrivate()) {
            for (Role role : event.getGuild().getMember(event.getAuthor()).getRoles()) {
                final PermissionRole temp = PermissionRole.getPermissionRoleByGuildIdAndRoleId(event.getGuild().getId(), role.getId());
                if (temp != null && filter.isPermissionGranted(temp, event.getMember())) {
                    return true;
                }
            }
        } else {
            return Standard.isSuperOwner(event.getAuthor());
        }
        if (withMessage) {
            sendNoPermissionMessage(event);
        }
        return false;
    }
    
    public static final boolean check(PermissionRoleFilter filter, Member member) {
        if (filter == null) {
            return true;
        }
        if (member == null) {
            return false;
        }
        for (Role role : member.getRoles()) {
            final PermissionRole temp = PermissionRole.getPermissionRoleByGuildIdAndRoleId(member.getGuild().getId(), role.getId());
            if (temp != null && filter.isPermissionGranted(temp, member)) {
                return true;
            }
        }
        return false;
    }
    
    public static final boolean check(PermissionRoleFilter filter, Guild guild, Channel channel) {
        if (guild == null || channel == null) {
            return false;
        }
        if (filter == null) {
            return true;
        }
        if (channel.getRolePermissionOverrides().isEmpty()) {
            return false;
        }
        for (PermissionOverride po : channel.getRolePermissionOverrides()) {
            final PermissionRole permissionRole = PermissionRole.getPermissionRoleByGuildIdAndRoleId(guild.getId(), po.getRole().getId());
            if (("" + null).equals(permissionRole.getName())) {
                continue;
            }
            if (!filter.isPermissionGranted(permissionRole, null)) {
                return false;
            }
        }
        return true;
    }
    
    public static final boolean sendNoPermissionMessage(MessageEvent event) {
        return event.sendMessage(Standard.getNoPermissionMessage(event.getAuthor(), "command"));
    }
    
}
