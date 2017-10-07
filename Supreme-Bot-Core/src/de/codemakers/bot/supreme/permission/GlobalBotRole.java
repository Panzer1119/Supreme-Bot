package de.codemakers.bot.supreme.permission;

import de.codemakers.bot.supreme.util.Standard;
import de.codemakers.bot.supreme.util.Util;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.dv8tion.jda.core.entities.User;

/**
 * GlobalBotRole
 *
 * @author Panzer1119
 */
public enum GlobalBotRole {
    OWNER("Owner", 3, (user) -> Standard.isSuperOwner(user)),
    ADMIN("Admin", 2, (user) -> GlobalBotRoleData.isGranted(user.getIdLong(), 2)),
    VIP("VIP", 1, (user) -> GlobalBotRoleData.isGranted(user.getIdLong(), 1)),
    USER("User", 0, (user) -> GlobalBotRoleData.isGranted(user.getIdLong(), 0)),
    NOBODY("Nobody", -1, (user) -> GlobalBotRoleData.BOT_ROLE_DATA.stream().noneMatch((globalBotRoleData) -> globalBotRoleData.user_id == user.getIdLong()));

    public static final GlobalBotRole STANDARD = NOBODY;

    static {
        OWNER.addInherits(ADMIN, VIP, USER);
        ADMIN.addInherits(VIP, USER);
        VIP.addInherits(USER);
    }

    private final String name;
    private final long id;
    private final Predicate<User> detect;
    private final ArrayList<GlobalBotRole> inherits = new ArrayList<GlobalBotRole>() {
        @Override
        public boolean addAll(Collection<? extends GlobalBotRole> c) {
            if (c == null) {
                return false;
            }
            return super.addAll(c.stream().filter((globalBotRole) -> !contains(globalBotRole)).collect(Collectors.toList()));
        }
    };

    private GlobalBotRole(String name, long id, Predicate<User> detect) {
        this.name = name;
        this.id = id;
        this.detect = detect;
    }

    public final String getName() {
        return name;
    }

    public final long getId() {
        return id;
    }

    private final GlobalBotRole addInherits(GlobalBotRole... inherits) {
        this.inherits.addAll(Arrays.asList(inherits));
        return this;
    }

    public final boolean hasGlobalBotRoles(GlobalBotRole... globalBotRoles) {
        if (globalBotRoles == null || globalBotRoles.length == 0) {
            return false;
        }
        if (Util.contains(globalBotRoles, this)) {
            return true;
        }
        return inherits.stream().anyMatch((globalBotRole) -> Util.contains(globalBotRoles, globalBotRole));
    }

    public final boolean hasGlobalBotRoles(List<GlobalBotRole> globalBotRoles) {
        if (globalBotRoles == null || globalBotRoles.isEmpty()) {
            return false;
        }
        if (globalBotRoles.contains(this)) {
            return true;
        }
        return inherits.stream().anyMatch((globalBotRole) -> globalBotRoles.contains(globalBotRole));
    }

    public final GlobalBotRole[] getInheritsAsArray() {
        return inherits.toArray(new GlobalBotRole[inherits.size()]);
    }

    public final List<GlobalBotRole> getInherits() {
        return new ArrayList<>(inherits);
    }

    public final boolean test(User user) {
        if (user == null) {
            return false;
        }
        GlobalBotRoleData.reloadData();
        return detect.test(user);
    }

    public static final List<GlobalBotRole> getGlobalBotRolesByUser(User user) {
        if (user == null) {
            return new ArrayList<>();
        }
        GlobalBotRoleData.reloadData();
        return Arrays.asList(values()).stream().filter((globalBotRole) -> globalBotRole.detect.test(user)).map((globalBotRole) -> {
            final List<GlobalBotRole> inherits = new ArrayList<>();
            inherits.add(globalBotRole);
            inherits.addAll(globalBotRole.getInherits());
            return inherits;
        }).flatMap(List::stream).distinct().collect(Collectors.toList());
    }

    public static final boolean isUserAllowed(User user, GlobalBotRole globalBotRole) {
        if (user == null || globalBotRole == null) {
            return false;
        }
        return getGlobalBotRolesByUser(user).contains(globalBotRole);
    }

}
