package de.codemakers.bot.supreme.permission;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;

/**
 * PermissionRole
 *
 * @author Panzer1119
 */
public class PermissionRole {

    static final ArrayList<PermissionRole> PERMISSIONROLES = new ArrayList<>();

    private String permissionRoleName;
    private String permissionRoleID;
    private final HashMap<PermissionRole, Boolean> superPermissionRoles = new HashMap<>();
    private final HashMap<String, ArrayList<String>> guildRoles = new HashMap<>();

    public PermissionRole(String permissionRoleName, String permissionRoleID) {
        this.permissionRoleName = permissionRoleName;
        this.permissionRoleID = permissionRoleID;
    }

    public final String getPermissionRoleName() {
        return permissionRoleName;
    }

    public final PermissionRole setPermissionRoleName(String permissionRoleName) {
        this.permissionRoleName = permissionRoleName;
        return this;
    }

    public final String getPermissionRoleID() {
        return permissionRoleID;
    }

    public final PermissionRole setPermissionRoleID(String permissionRoleID) {
        this.permissionRoleID = permissionRoleID;
        return this;
    }

    public final ArrayList<PermissionRole> getSuperPermissionRoles() {
        final ArrayList<PermissionRole> permissionRoles = new ArrayList<PermissionRole>() {
            @Override
            public boolean addAll(int index, Collection<? extends PermissionRole> permissionRoles) {
                if (permissionRoles == null) {
                    return false;
                }
                return super.addAll(index, permissionRoles.stream().filter((permissionRole) -> !contains(permissionRole)).collect(Collectors.toList()));
            }

            @Override
            public boolean addAll(Collection<? extends PermissionRole> permissionRoles) {
                if (permissionRoles == null) {
                    return false;
                }
                return super.addAll(permissionRoles.stream().filter((permissionRole) -> !contains(permissionRole)).collect(Collectors.toList()));
            }

            @Override
            public void add(int index, PermissionRole permissionRole) {
                if (permissionRole == null || contains(permissionRole)) {
                    return;
                }
                super.add(index, permissionRole);
            }

            @Override
            public boolean add(PermissionRole permissionRole) {
                if (permissionRole == null || contains(permissionRole)) {
                    return false;
                }
                return super.add(permissionRole);
            }

        };
        superPermissionRoles.keySet().stream().forEach((superPermissionRole) -> {
            permissionRoles.add(superPermissionRole);
            if (superPermissionRoles.get(superPermissionRole)) {
                permissionRoles.addAll(superPermissionRole.getSuperPermissionRoles());
            }
        });
        return permissionRoles;
    }

    public final PermissionRole inherit(PermissionRole permissionRole, boolean inheritAll) {
        superPermissionRoles.put(permissionRole, inheritAll);
        return this;
    }

    public final HashMap<String, ArrayList<String>> getGuildRoles() {
        return guildRoles;
    }

    public final ArrayList<String> getRolesByGuild(String guild_id) {
        if (!guildRoles.containsKey(guild_id)) {
            return new ArrayList<>();
        }
        return guildRoles.get(guild_id);
    }

    public final PermissionRole addRoleForGuild(String guild_id, String role_id) {
        ArrayList<String> roles = guildRoles.get(guild_id);
        if (roles == null) {
            roles = new ArrayList<>();
            guildRoles.put(guild_id, roles);
        }
        if (!roles.contains(role_id)) {
            roles.add(role_id);
        }
        return this;
    }

    public final boolean isPermissionGranted(PermissionRole permissionRole) {
        if (permissionRole == null) {
            return false;
        }
        if (permissionRole.equals(this)) {
            return true;
        }
        return getSuperPermissionRoles().contains(permissionRole);
    }

    public final boolean isPermissionGranted(PermissionRole... permissionRoles) {
        if (permissionRoles == null || permissionRoles.length == 0) {
            return false;
        }
        for (PermissionRole permissionRole : permissionRoles) {
            if (isPermissionGranted(permissionRole)) {
                return true;
            }
        }
        return false;
    }

    public final boolean isPermissionGranted(ArrayList<PermissionRole> permissionRoles) {
        if (permissionRoles == null || permissionRoles.isEmpty()) {
            return false;
        }
        return permissionRoles.stream().anyMatch((permissionRole) -> isPermissionGranted(permissionRole));
    }

    @Override
    public final boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null) {
            return false;
        }
        if (object instanceof PermissionRole) {
            final PermissionRole permissionRole = (PermissionRole) object;
            return Objects.equals(getPermissionRoleID(), permissionRole.getPermissionRoleID()) || Objects.equals(permissionRole.getPermissionRoleID(), getPermissionRoleID());
        } else {
            return false;
        }
    }
    
    @Override
    public final String toString() {
        return String.format("%s (ID: %s)", permissionRoleName, permissionRoleID);
    }

    public static final ArrayList<PermissionRole> getPermissionRolesByGuildAndUser(Guild guild, User user) {
        if (guild == null || user == null) {
            return new ArrayList<>();
        }
        return getPermissionRolesByMember(guild.getMember(user));
    }

    public static final ArrayList<PermissionRole> getPermissionRolesByMember(Member member) {
        if (member == null || member.getGuild() == null) {
            return new ArrayList<>();
        }
        final String guild_id = member.getGuild().getId();
        final ArrayList<PermissionRole> permissionRoles = new ArrayList<>();
        member.getRoles().stream().forEach((role) -> {
            final PermissionRole permissionRole = getPermissionRoleByGuildAndRole(guild_id, role.getId());
            if (permissionRole != null) {
                permissionRoles.add(permissionRole);
            }
        });
        return permissionRoles;
    }

    public static final PermissionRole getPermissionRoleByGuildAndRole(String guild_id, String role_id) {
        if (guild_id == null || guild_id.isEmpty() || role_id == null || role_id.isEmpty() || PERMISSIONROLES.isEmpty()) {
            return null;
        }
        return PERMISSIONROLES.stream().filter((permissionRole) -> permissionRole.getRolesByGuild(guild_id).contains(role_id)).findFirst().orElse(null);
    }

    public static final PermissionRole getPermissionRoleByPermissionRoleID(String permissionRoleID) {
        if (permissionRoleID == null || PERMISSIONROLES.isEmpty()) {
            return null;
        }
        return PERMISSIONROLES.stream().filter((permissionRole) -> permissionRoleID.equals(permissionRole.getPermissionRoleID())).findFirst().orElse(null);
    }

    public static final List<PermissionRole> getPermissionRolesByName(String permissionRoleName) {
        if (permissionRoleName == null || PERMISSIONROLES.isEmpty()) {
            return new ArrayList<>();
        }
        return PERMISSIONROLES.stream().filter((permissionRole) -> permissionRoleName.equals(permissionRole.getPermissionRoleName())).collect(Collectors.toList());
    }

    public static final PermissionRole getPermissionRoleByName(String permissionRoleName) {
        if (permissionRoleName == null || PERMISSIONROLES.isEmpty()) {
            return null;
        }
        return getPermissionRolesByName(permissionRoleName).stream().findFirst().orElse(null);
    }

}
