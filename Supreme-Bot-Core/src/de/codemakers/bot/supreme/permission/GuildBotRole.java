package de.codemakers.bot.supreme.permission;

import de.codemakers.bot.supreme.util.Util;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;

/**
 * GuildBotRole
 *
 * @author Panzer1119
 */
public enum GuildBotRole {
    BOT("Bot", 6, (guild, user) -> user.isBot()),
    OWNER("Owner", 5, (guild, user) -> (guild.isMember(user) && guild.getMember(user).isOwner())),
    ADMIN("Admin", 4, (guild, user) -> (guild.isMember(user) && guild.getMember(user).getRoles().stream().anyMatch((role) -> GuildBotRoleData.isRole(guild.getIdLong(), role.getIdLong(), 4)))),
    BOT_COMMANDER("Bot Commander", 3, (guild, user) -> (guild.isMember(user) && guild.getMember(user).getRoles().stream().anyMatch((role) -> GuildBotRoleData.isRole(guild.getIdLong(), role.getIdLong(), 3)))),
    MODERATOR("Moderator", 2, (guild, user) -> (guild.isMember(user) && guild.getMember(user).getRoles().stream().anyMatch((role) -> GuildBotRoleData.isRole(guild.getIdLong(), role.getIdLong(), 2)))),
    VIP("VIP", 1, (guild, user) -> (guild.isMember(user) && guild.getMember(user).getRoles().stream().anyMatch((role) -> GuildBotRoleData.isRole(guild.getIdLong(), role.getIdLong(), 1)))),
    USER("User", 0, (guild, user) -> (guild.isMember(user) && guild.getMember(user).getRoles().stream().anyMatch((role) -> GuildBotRoleData.isRole(guild.getIdLong(), role.getIdLong(), 0)))),
    EVERYONE("Everyone", -1, (guild, user) -> guild.isMember(user)),
    NOBODY("Nobody", -2, (guild, user) -> !guild.isMember(user)),
    FAKE("Fake", -3, (guild, user) -> user.isFake());

    public static final GuildBotRole STANDARD = NOBODY;

    static {
        System.err.println("t6b9t8b6t8b6t87");
        OWNER.addInherits(ADMIN, BOT_COMMANDER, MODERATOR, VIP, USER);
        ADMIN.addInherits(MODERATOR, VIP, USER);
        BOT_COMMANDER.addInherits(VIP, USER);
        MODERATOR.addInherits(VIP, USER);
        VIP.addInherits(USER);
        System.err.println("t6b9t8b6t8b6t87");
    }

    private final String name;
    private final int id;
    private final BiPredicate<Guild, User> detect;
    private final ArrayList<GuildBotRole> inherits = new ArrayList<GuildBotRole>() {
        @Override
        public boolean addAll(Collection<? extends GuildBotRole> c) {
            if (c == null) {
                return false;
            }
            return super.addAll(c.stream().filter((guildBotRole) -> !contains(guildBotRole)).collect(Collectors.toList()));
        }
    };

    private GuildBotRole(String name, int id, BiPredicate<Guild, User> detect) {
        this.name = name;
        this.id = id;
        this.detect = detect;
    }

    public final String getName() {
        return name;
    }

    public final int getId() {
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
        return detect.test(guild, user);
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
        return Arrays.asList(values()).stream().filter((guildBotRole) -> guildBotRole.detect.test(guild, user)).collect(Collectors.toList());
    }

    public static final boolean isUserFromGuildAllowed(Guild guild, User user, GuildBotRole guildBotRole) {
        if (guild == null || user == null || guildBotRole == null) {
            return false;
        }
        return guildBotRole.hasGuildBotRoles(getGuildBotRolesByGuildAndUser(guild, user));
    }

}
