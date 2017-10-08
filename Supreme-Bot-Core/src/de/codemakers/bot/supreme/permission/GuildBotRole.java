package de.codemakers.bot.supreme.permission;

import de.codemakers.bot.supreme.util.Util;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;

/**
 * GuildBotRole
 *
 * @author Panzer1119
 */
public enum GuildBotRole {
    BOT("Bot", 6, (guild, user) -> user.isBot(), (role) -> role.isManaged()),
    OWNER("Owner", 5, (guild, user) -> (guild.isMember(user) && guild.getMember(user).isOwner()), (role) -> GuildBotRoleData.isGranted(role.getGuild().getIdLong(), role.getIdLong(), 5, true)),
    ADMIN("Admin", 4, (guild, user) -> (guild.isMember(user) && (guild.getMember(user).getRoles().stream().anyMatch((role) -> GuildBotRoleData.isGranted(guild.getIdLong(), role.getIdLong(), 4, true)) || GuildBotRoleData.isGranted(guild.getIdLong(), user.getIdLong(), 4, false))), (role) -> GuildBotRoleData.isGranted(role.getGuild().getIdLong(), role.getIdLong(), 4, true)),
    BOT_COMMANDER("Bot Commander", 3, (guild, user) -> (guild.isMember(user) && (guild.getMember(user).getRoles().stream().anyMatch((role) -> GuildBotRoleData.isGranted(guild.getIdLong(), role.getIdLong(), 3, true)) || GuildBotRoleData.isGranted(guild.getIdLong(), user.getIdLong(), 3, false))), (role) -> GuildBotRoleData.isGranted(role.getGuild().getIdLong(), role.getIdLong(), 3, true)),
    MODERATOR("Moderator", 2, (guild, user) -> (guild.isMember(user) && (guild.getMember(user).getRoles().stream().anyMatch((role) -> GuildBotRoleData.isGranted(guild.getIdLong(), role.getIdLong(), 2, true)) || GuildBotRoleData.isGranted(guild.getIdLong(), user.getIdLong(), 2, false))), (role) -> GuildBotRoleData.isGranted(role.getGuild().getIdLong(), role.getIdLong(), 2, true)),
    VIP("VIP", 1, (guild, user) -> (guild.isMember(user) && (guild.getMember(user).getRoles().stream().anyMatch((role) -> GuildBotRoleData.isGranted(guild.getIdLong(), role.getIdLong(), 1, true)) || GuildBotRoleData.isGranted(guild.getIdLong(), user.getIdLong(), 1, false))), (role) -> GuildBotRoleData.isGranted(role.getGuild().getIdLong(), role.getIdLong(), 1, true)),
    USER("User", 0, (guild, user) -> (guild.isMember(user) && (guild.getMember(user).getRoles().stream().anyMatch((role) -> GuildBotRoleData.isGranted(guild.getIdLong(), role.getIdLong(), 0, true)) || GuildBotRoleData.isGranted(guild.getIdLong(), user.getIdLong(), 0, false))), (role) -> GuildBotRoleData.isGranted(role.getGuild().getIdLong(), role.getIdLong(), 0, true)),
    EVERYONE("Everyone", -1, (guild, user) -> guild.isMember(user), (role) -> role.isPublicRole()),
    NOBODY("Nobody", -2, (guild, user) -> !guild.isMember(user), (role) -> false),
    FAKE("Fake", -3, (guild, user) -> user.isFake(), (role) -> false);

    static {
        OWNER.addInherits(ADMIN, BOT_COMMANDER, MODERATOR, VIP, USER);
        ADMIN.addInherits(MODERATOR, VIP, USER);
        BOT_COMMANDER.addInherits(VIP, USER);
        MODERATOR.addInherits(VIP, USER);
        VIP.addInherits(USER);
    }

    private final String name;
    private final long id;
    private final BiPredicate<Guild, User> detect_user;
    private final Predicate<Role> detect_role;
    private final ArrayList<GuildBotRole> inherits = new ArrayList<GuildBotRole>() {
        @Override
        public boolean addAll(Collection<? extends GuildBotRole> c) {
            if (c == null) {
                return false;
            }
            return super.addAll(c.stream().filter((guildBotRole) -> !contains(guildBotRole)).collect(Collectors.toList()));
        }
    };

    private GuildBotRole(String name, long id, BiPredicate<Guild, User> detect_user, Predicate<Role> detect_role) {
        this.name = name;
        this.id = id;
        this.detect_user = detect_user;
        this.detect_role = detect_role;
    }

    public final String getName() {
        return name;
    }

    public final long getId() {
        return id;
    }

    private final GuildBotRole addInherits(GuildBotRole... inherits) {
        this.inherits.addAll(Arrays.asList(inherits));
        return this;
    }

    public final boolean hasGuildBotRoles(GuildBotRole... guildBotRoles) {
        if (guildBotRoles == null || guildBotRoles.length == 0) {
            return false;
        }
        if (Util.contains(guildBotRoles, this)) {
            return true;
        }
        return inherits.stream().anyMatch((guildBotRole) -> Util.contains(guildBotRoles, guildBotRole));
    }

    public final boolean hasGuildBotRoles(List<GuildBotRole> guildBotRoles) {
        if (guildBotRoles == null || guildBotRoles.isEmpty()) {
            return false;
        }
        if (guildBotRoles.contains(this)) {
            return true;
        }
        return inherits.stream().anyMatch((guildBotRole) -> guildBotRoles.contains(guildBotRole));
    }

    public final GuildBotRole[] getInheritsAsArray() {
        return inherits.toArray(new GuildBotRole[inherits.size()]);
    }

    public final List<GuildBotRole> getInherits() {
        return new ArrayList<>(inherits);
    }

    public final boolean test(Member member) {
        if (member == null) {
            return false;
        }
        return test(member.getGuild(), member.getUser());
    }

    public final boolean test(Guild guild, User user) {
        if (guild == null || user == null) {
            return false;
        }
        GuildBotRoleData.reloadData();
        return detect_user.test(guild, user);
    }

    public final boolean test(Role role) {
        if (role == null) {
            return false;
        }
        GuildBotRoleData.reloadData();
        return detect_role.test(role);
    }

    public static final List<GuildBotRole> getGuildBotRolesByMember(Member member) {
        if (member == null) {
            return new ArrayList<>();
        }
        return getGuildBotRolesByGuildAndUser(member.getGuild(), member.getUser());
    }

    public static final List<GuildBotRole> getGuildBotRolesByGuildAndUser(Guild guild, User user) {
        if (guild == null || user == null) {
            return new ArrayList<>();
        }
        GuildBotRoleData.reloadData();
        return stream().filter((guildBotRole) -> guildBotRole.detect_user.test(guild, user)).map((guildBotRole) -> {
            final List<GuildBotRole> inherits = new ArrayList<>();
            inherits.add(guildBotRole);
            inherits.addAll(guildBotRole.getInherits());
            return inherits;
        }).flatMap(List::stream).distinct().collect(Collectors.toList());
    }

    public static final List<GuildBotRole> getGuildBotRolesByRole(Role role) {
        if (role == null) {
            return new ArrayList<>();
        }
        GuildBotRoleData.reloadData();
        return stream().filter((guildBotRole) -> guildBotRole.detect_role.test(role)).map((guildBotRole) -> {
            final List<GuildBotRole> inherits = new ArrayList<>();
            inherits.add(guildBotRole);
            inherits.addAll(guildBotRole.getInherits());
            return inherits;
        }).flatMap(List::stream).distinct().collect(Collectors.toList());
    }

    public static final boolean isMemberAllowed(Member member, GuildBotRole guildBotRole) {
        if (member == null) {
            return false;
        }
        return isUserFromGuildAllowed(member.getGuild(), member.getUser(), guildBotRole);
    }

    public static final boolean isUserFromGuildAllowed(Guild guild, User user, GuildBotRole guildBotRole) {
        if (guild == null || user == null || guildBotRole == null) {
            return false;
        }
        return getGuildBotRolesByGuildAndUser(guild, user).contains(guildBotRole);
    }

    public static final boolean isRoleAllowed(Role role, GuildBotRole guildBotRole) {
        if (role == null) {
            return false;
        }
        return getGuildBotRolesByRole(role).contains(guildBotRole);
    }

    public static final Stream<GuildBotRole> stream() {
        return Arrays.asList(values()).stream();
    }

}
