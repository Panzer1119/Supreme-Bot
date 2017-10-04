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
    private final HashMap<Long, ArrayList<Long>> guildRoles = new HashMap<>();
    private final HashMap<Long, ArrayList<Long>> guildUsers = new HashMap<>();

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

    public final HashMap<Long, ArrayList<Long>> getGuildRoles() {
        return guildRoles;
    }

    public final ArrayList<Long> getRolesByGuild(long guild_id) {
        if (!guildRoles.containsKey(guild_id)) {
            return new ArrayList<>();
        }
        return guildRoles.get(guild_id);
    }

    public final PermissionRole addRoleForGuild(long guild_id, long role_id) {
        ArrayList<Long> roles = guildRoles.get(guild_id);
        if (roles == null) {
            roles = new ArrayList<>();
            guildRoles.put(guild_id, roles);
        }
        if (!roles.contains(role_id)) {
            roles.add(role_id);
        }
        return this;
    }

    public final HashMap<Long, ArrayList<Long>> getGuildUsers() {
        return guildUsers;
    }

    public final ArrayList<Long> getUsersByGuild(long guild_id) {
        if (!guildUsers.containsKey(guild_id)) {
            return new ArrayList<>();
        }
        return guildUsers.get(guild_id);
    }

    public final PermissionRole addUserForGuild(long guild_id, long user_id) {
        ArrayList<Long> users = guildUsers.get(guild_id);
        if (users == null) {
            users = new ArrayList<>();
            guildUsers.put(guild_id, users);
        }
        if (!users.contains(user_id)) {
            users.add(user_id);
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

    public static final List<PermissionRole> getPermissionRolesByGuildAndUser(Guild guild, User user) {
        if (guild == null || user == null) {
            return new ArrayList<>();
        }
        return getPermissionRolesByMember(guild.getMember(user));
    }

    public static final List<PermissionRole> getPermissionRolesByMember(Member member) {
        if (member == null || member.getGuild() == null) {
            return new ArrayList<>();
        }
        final long guild_id = member.getGuild().getIdLong();
        final long user_id = member.getUser().getIdLong();
        final ArrayList<PermissionRole> permissionRoles = new ArrayList<>();
        final List<PermissionRole> permissionRoles_ = getPermissionRolesByGuildAndUser(guild_id, user_id);
        if (permissionRoles_ != null && !permissionRoles_.isEmpty()) {
            permissionRoles.addAll(permissionRoles_);
        }
        member.getRoles().stream().forEach((role) -> {
            final List<PermissionRole> permissionRoles__ = getPermissionRolesByGuildAndRole(guild_id, role.getIdLong());
            if (permissionRoles__ != null && !permissionRoles__.isEmpty()) {
                permissionRoles.addAll(permissionRoles__.stream().filter((permissionRole) -> !permissionRoles.contains(permissionRole)).collect(Collectors.toList()));
            }
        });
        return permissionRoles;
    }

    public static final List<PermissionRole> getPermissionRolesByGuildAndRole(long guild_id, long role_id) {
        if (PERMISSIONROLES.isEmpty()) {
            return new ArrayList<>();
        }
        return PERMISSIONROLES.stream().filter((permissionRole) -> permissionRole.getRolesByGuild(guild_id).contains(role_id)).collect(Collectors.toList());
    }

    public static final List<PermissionRole> getPermissionRolesByGuildAndUser(long guild_id, long user_id) {
        if (PERMISSIONROLES.isEmpty()) {
            return new ArrayList<>();
        }
        return PERMISSIONROLES.stream().filter((permissionRole) -> permissionRole.getUsersByGuild(guild_id).contains(user_id)).collect(Collectors.toList());
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
