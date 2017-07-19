package de.panzercraft.bot.supreme.permission;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;

/**
 * PermissionRole
 *
 * @author Panzer1119
 */
public class PermissionRole {

    public static final String SPLITTER_1 = ";";
    public static final String SPLITTER_2 = "=";
    public static final String COMMENT = "#";
    public static final ArrayList<PermissionRole> roles = new ArrayList<>();

    private final String name;
    private final HashMap<String, String> roleNames = new HashMap<String, String>() {
        @Override
        public final String toString() {
            String temp = "";
            for (String key : keySet()) {
                temp += SPLITTER_1 + key + SPLITTER_2 + get(key);
            }
            return temp;
        }
    };
    /**
     * The higher the level the more permission the role have
     */
    private final int level;

    public PermissionRole(String name, int level) {
        this.name = name;
        this.level = level;
    }

    public final String getName() {
        return name;
    }

    /**
     * KEY = guild.getId(); //VALUE = role.getId();
     */
    public final HashMap<String, String> getRoleNames() {
        return roleNames;
    }

    public final int getLevel() {
        return level;
    }

    public final boolean isThisHigher(PermissionRole role) {
        if (role == null) {
            return true;
        }
        return level > role.level;
    }

    public final boolean isThisHigherOrEqual(PermissionRole role) {
        if (role == null) {
            return true;
        }
        return level >= role.level;
    }

    public final boolean isThisEqual(PermissionRole role) {
        if (role == null) {
            return false;
        }
        return level == role.level;
    }

    public final boolean isThisLower(PermissionRole role) {
        if (role == null) {
            return false;
        }
        return level < role.level;
    }

    public final boolean isThisLowerOrEqual(PermissionRole role) {
        if (role == null) {
            return false;
        }
        return level <= role.level;
    }

    public final Role getRoleByGuild(Guild guild) {
        if (guild == null) {
            return null;
        }
        final String roleID = roleNames.get(guild.getId());
        if (roleID == null) {
            return null;
        }
        return guild.getRoleById(roleID);
    }

    public final String getRoleNameByGuild(Guild guild) {
        final Role role = getRoleByGuild(guild);
        if (role == null) {
            return name;
        } else {
            return role.getName();
        }
    }

    @Override
    public final String toString() {
        return String.format("%s%s%s%s", name, SPLITTER_1, level, roleNames.toString());
    }

    public static final PermissionRole getPermissionRoleByGuildAndRoleId(Guild guild, String role_id) {
        if (guild == null || role_id == null || role_id.isEmpty()) {
            return getPermissionRoleByName("" + null);
        }
        for (PermissionRole role : roles) {
            for (String key : role.roleNames.keySet()) {
                final String value = role.roleNames.get(key);
                if (key != null && value != null && key.equals(guild.getId()) && value.equals(role_id)) {
                    return role;
                }
            }
        }
        return null;
    }

    public static final PermissionRole getPermissionRoleByName(String name) {
        if (name == null) {
            return getPermissionRoleByName("" + null);
        }
        for (PermissionRole role : roles) {
            if (role.getName() != null && role.getName().equals(name)) {
                return role;
            }
        }
        return null;
    }

    public static final boolean loadPermissionRoles(File file) {
        if (file == null) {
            return false;
        }
        try {
            return loadPermissionRoles(new FileInputStream(file));
        } catch (Exception ex) {
            System.err.println(ex);
            ex.printStackTrace();
            return false;
        }
    }

    public static final boolean loadPermissionRoles(String jar_path) {
        if (jar_path == null) {
            return false;
        }
        try {
            if (!jar_path.startsWith("/")) {
                jar_path = "/" + jar_path;
            }
            return loadPermissionRoles(PermissionRole.class.getResourceAsStream(jar_path));
        } catch (Exception ex) {
            System.err.println(ex);
            ex.printStackTrace();
            return false;
        }
    }

    public static final boolean loadPermissionRoles(InputStream inputStream) {
        if (inputStream == null) {
            return false;
        }
        return loadPermissionRoles(new InputStreamReader(inputStream));
    }

    public static final boolean loadPermissionRoles(InputStreamReader reader) {
        if (reader == null) {
            return false;
        }
        return loadPermissionRoles(new BufferedReader(reader));
    }

    /**
     * Example:
     *
     * PERMISSION_ROLE_NAME;LEVEL;GUILD_ID_1=ROLE_ID_1;GUILD_ID_2=ROLE_ID_2
     *
     * @param reader
     * @return
     */
    public static final boolean loadPermissionRoles(BufferedReader reader) {
        if (reader == null) {
            return false;
        }
        System.out.println("Loading PermissionRoles");
        try {
            roles.clear();
            String line = null;
            while ((line = reader.readLine()) != null) {
                try {
                    if (line.isEmpty() || line.startsWith(COMMENT)) {
                        continue;
                    }
                    final String[] split = line.split(SPLITTER_1);
                    if (split.length >= 2) {
                        final String name = split[0];
                        final int level = Integer.parseInt(split[1]);
                        final PermissionRole role = new PermissionRole(name, level);
                        if (split.length > 2) {
                            for (int i = 2; i < split.length; i++) {
                                final String[] temp = split[i].split(SPLITTER_2);
                                if (temp.length >= 2) {
                                    role.roleNames.put(temp[0], temp[1]);
                                }
                            }
                        }
                        roles.add(role);
                        System.out.println("Added " + role);
                    }
                } catch (Exception ex) {
                }
            }
            reader.close();
            return true;
        } catch (Exception ex) {
            System.err.println(ex);
            ex.printStackTrace();
            return false;
        }
    }

}
