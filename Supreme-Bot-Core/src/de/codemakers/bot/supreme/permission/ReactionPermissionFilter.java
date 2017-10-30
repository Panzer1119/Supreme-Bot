package de.codemakers.bot.supreme.permission;

import de.codemakers.bot.supreme.util.Util;
import java.util.Arrays;
import java.util.Objects;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;

/**
 * ReactionPermissionFilter
 *
 * @author Panzer1119
 */
public interface ReactionPermissionFilter {

    boolean isPermissionGranted(Guild guild, User user);

    public static ReactionPermissionFilter createUserFilter(User user) {
        return (guild_, user_) -> Objects.equals(user_, user);
    }

    public static ReactionPermissionFilter createUsersFilter(User... users) {
        return (guild_, user_) -> Util.contains(users, user_);
    }

    public static ReactionPermissionFilter createMemberFilter(Member member) {
        return (guild_, user_) -> (Objects.equals(guild_, member.getGuild()) && Objects.equals(user_, member.getUser()));
    }

    public static ReactionPermissionFilter createMembersFilter(Member... members) {
        return (guild_, user_) -> Arrays.asList(members).stream().anyMatch((member) -> (Objects.equals(guild_, member.getGuild()) && Objects.equals(user_, member.getUser())));
    }

    public static ReactionPermissionFilter createRoleFilter(Role role) {
        return (guild_, user_) -> (guild_ != null && guild_.isMember(user_) && guild_.getMember(user_).getRoles().contains(role));
    }

    public static ReactionPermissionFilter createRolesFilter(Role... roles) {
        return (guild_, user_) -> (guild_ != null && guild_.isMember(user_) && guild_.getMember(user_).getRoles().stream().anyMatch((role) -> Util.contains(roles, role)));
    }

}
