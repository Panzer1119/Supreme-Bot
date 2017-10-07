package de.codemakers.bot.supreme.permission;

import de.codemakers.bot.supreme.util.Util;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * GlobalBotRole
 *
 * @author Panzer1119
 */
public enum GlobalBotRole {
    OWNER("Owner", 3),
    ADMIN("Admin", 2),
    VIP("VIP", 1),
    USER("User", 0),
    NOBODY("Nobody", -1);

    public static final GlobalBotRole STANDARD = NOBODY;

    static {
        OWNER.addInherits(ADMIN, VIP, USER);
        ADMIN.addInherits(VIP, USER);
        VIP.addInherits(USER);
    }

    private final String name;
    private final int id;
    private final ArrayList<GlobalBotRole> inherits = new ArrayList<GlobalBotRole>() {
        @Override
        public boolean addAll(Collection<? extends GlobalBotRole> c) {
            if (c == null) {
                return false;
            }
            return super.addAll(c.stream().filter((globalBotRole) -> !contains(globalBotRole)).collect(Collectors.toList()));
        }
    };

    private GlobalBotRole(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public final String getName() {
        return name;
    }

    public final int getId() {
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

    public final GlobalBotRole[] getInherits() {
        return inherits.toArray(new GlobalBotRole[inherits.size()]);
    }

}
