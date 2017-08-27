package de.codemakers.bot.supreme.permission;

import de.codemakers.bot.supreme.entities.MessageEvent;
import de.codemakers.bot.supreme.util.AdvancedFile;
import de.codemakers.bot.supreme.util.Standard;
import de.codemakers.bot.supreme.util.XMLUtil;
import java.io.InputStream;
import java.util.List;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.PermissionOverride;
import net.dv8tion.jda.core.entities.Role;
import org.jdom2.Document;
import org.jdom2.Element;

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
                final List<PermissionRole> temp = PermissionRole.getPermissionRolesByGuildAndRole(event.getGuild().getId(), role.getId());
                for (PermissionRole role_ : temp) {
                    if (temp != null && filter.isPermissionGranted(role_, event.getMember())) {
                        return true;
                    }
                }
            }
        } else if (Standard.isSuperOwner(event.getAuthor())) {
            return true;
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
            final List<PermissionRole> temp = PermissionRole.getPermissionRolesByGuildAndRole(member.getGuild().getId(), role.getId());
            for (PermissionRole role_ : temp) {
                if (temp != null && filter.isPermissionGranted(role_, member)) {
                    return true;
                }
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
            final List<PermissionRole> permissionRoles = PermissionRole.getPermissionRolesByGuildAndRole(guild.getId(), po.getRole().getId());
            if (permissionRoles.isEmpty()) { //TODO Ist das richtig?!!
                return false;
            }
            for (PermissionRole permissionRole : permissionRoles) {
                if (Standard.STANDARD_PERMISSION_ROLE != null && Standard.STANDARD_PERMISSION_ROLE.equals(permissionRole)) {
                    continue;
                }
                if (permissionRole == null || !filter.isPermissionGranted(permissionRole, null)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static final boolean sendNoPermissionMessage(MessageEvent event) {
        return event.sendMessage(Standard.getNoPermissionMessage(event.getAuthor(), "command"));
    }

    public static final boolean loadPermissionRoles(AdvancedFile file) {
        if (file == null) {
            return false;
        }
        try {
            return loadPermissionRoles(file.createInputStream());
        } catch (Exception ex) {
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
            return loadPermissionRoles(PermissionHandler.class.getResourceAsStream(jar_path));
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static final boolean loadPermissionRoles(InputStream inputStream) {
        if (inputStream == null) {
            return false;
        }
        try {
            final Document document = XMLUtil.load(inputStream);
            final Element rootElement = document.getRootElement();
            final List<Element> permissionRolesRaws = rootElement.getChildren(Standard.XML_PERMISSIONROLES);
            if (permissionRolesRaws == null) {
                if (Standard.DEBUG_PERMISSION_HANDLER) {
                    System.err.println(String.format("No \"%s\" tag found!", Standard.XML_PERMISSIONROLES));
                }
            } else {
                PermissionRole.PERMISSIONROLES.clear();
                Standard.STANDARD_PERMISSION_ROLE = null;
                permissionRolesRaws.stream().forEach((permissionRolesRaw) -> {
                    final List<Element> permissionRolesRaw_ = permissionRolesRaw.getChildren(Standard.XML_PERMISSIONROLE);
                    if (permissionRolesRaw_ != null) {
                        permissionRolesRaw_.stream().forEach((permissionRoleRaw) -> {
                            try {
                                final String permissionRoleName = permissionRoleRaw.getAttributeValue(Standard.XML_NAME);
                                final String permissionRoleID = permissionRoleRaw.getAttributeValue(Standard.XML_PERMISSIONROLEID);
                                final boolean standard = permissionRoleRaw.getAttributeValue(Standard.XML_STANDARD) != null;
                                if (permissionRoleName != null && permissionRoleID != null) {
                                    final PermissionRole permissionRole = new PermissionRole(permissionRoleName, permissionRoleID);
                                    PermissionRole.PERMISSIONROLES.add(permissionRole);
                                    if (Standard.DEBUG_PERMISSION_HANDLER) {
                                        System.out.println(String.format("Added PermissionRole: \"%s\"", permissionRole));
                                    }
                                    if (standard) {
                                        if (Standard.STANDARD_PERMISSION_ROLE == null) {
                                            Standard.STANDARD_PERMISSION_ROLE = permissionRole;
                                            if (Standard.DEBUG_PERMISSION_HANDLER) {
                                                System.out.println(String.format("Setted \"%s\" as standard", permissionRole));
                                            }
                                        } else {
                                            System.err.println("You can't have 2 standard PermissionRoles!");
                                        }
                                    }
                                }
                            } catch (Exception ex) {
                                System.err.println("Error while adding PermissionRoles: " + ex);
                            }
                        });
                    }
                });
            }
            final List<Element> permissionsRaws = rootElement.getChildren(Standard.XML_PERMISSIONS);
            if (permissionsRaws == null) {
                System.err.println(String.format("No \"%s\" tag found!", Standard.XML_PERMISSIONS));
            } else {
                permissionsRaws.stream().forEach((permissionsRaw) -> {
                    final List<Element> permissionsRaw_ = permissionsRaw.getChildren(Standard.XML_PERMISSION);
                    if (permissionsRaw_ != null) {
                        permissionsRaw_.stream().forEach((permissionRaw) -> {
                            try {
                                final String permissionRoleID = permissionRaw.getAttributeValue(Standard.XML_PERMISSIONROLEID);
                                final String inherit = permissionRaw.getAttributeValue(Standard.XML_INHERIT);
                                final String inheritAll_string = permissionRaw.getAttributeValue(Standard.XML_INHERITALL);
                                final boolean inheritAll = (inheritAll_string == null ? false : Boolean.parseBoolean(inheritAll_string));
                                final PermissionRole permissionRole = PermissionRole.getPermissionRoleByPermissionRoleID(permissionRoleID);
                                final PermissionRole permissionRoleSuper = PermissionRole.getPermissionRoleByPermissionRoleID(inherit);
                                if (permissionRole != null && permissionRoleSuper != null) {
                                    permissionRole.inherit(permissionRoleSuper, inheritAll);
                                    if (Standard.DEBUG_PERMISSION_HANDLER) {
                                        System.out.println(String.format("Added \"%s\" to \"%s\"", permissionRoleSuper, permissionRole));
                                    }
                                } else {
                                    System.err.println(String.format("Can't add permissions for \"%s\" from \"%s\"", permissionRoleID, inherit));
                                }
                            } catch (Exception ex) {
                                System.err.println("Error while adding permissions: " + ex);
                            }
                        });
                    }
                });
            }
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static final boolean loadPermissionsForGuild(AdvancedFile file, String guild_id) {
        if (file == null) {
            return false;
        }
        try {
            return loadPermissionsForGuild(file.createInputStream(), guild_id);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static final boolean loadPermissionsForGuild(String jar_path, String guild_id) {
        if (jar_path == null) {
            return false;
        }
        try {
            if (!jar_path.startsWith("/")) {
                jar_path = "/" + jar_path;
            }
            return loadPermissionsForGuild(PermissionHandler.class.getResourceAsStream(jar_path), guild_id);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static final boolean loadPermissionsForGuild(InputStream inputStream, String guild_id) {
        if (inputStream == null || guild_id == null) {
            return false;
        }
        try {
            final Document document = XMLUtil.load(inputStream);
            final Element rootElement = document.getRootElement();
            final List<Element> roless = rootElement.getChildren(Standard.XML_ROLES);
            if (roless == null) {
                System.err.println(String.format("No \"%s\" tag found!", Standard.XML_ROLES));
            } else {
                roless.stream().forEach((roles) -> {
                    final List<Element> roles_ = roles.getChildren(Standard.XML_ROLE);
                    if (roles_ != null) {
                        roles_.stream().forEach((role) -> {
                            final String permissionRoleID = role.getAttributeValue(Standard.XML_PERMISSIONROLEID);
                            final String roleID = role.getAttributeValue(Standard.XML_ROLEID);
                            final PermissionRole permissionRole = PermissionRole.getPermissionRoleByPermissionRoleID(permissionRoleID);
                            if (permissionRole != null && roleID != null) {
                                permissionRole.addRoleForGuild(guild_id, roleID);
                                if (Standard.DEBUG_PERMISSION_HANDLER) {
                                    System.out.println(String.format("Added Role \"%s\" for Guild \"%s\" to \"%s\"", roleID, guild_id, permissionRole));
                                }
                            } else {
                                System.err.println(String.format("Can't add permissions for \"%s\" to \"%s\" (GUILD ID: %s)", permissionRoleID, roleID, guild_id));
                            }
                        });
                    }
                });
            }
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

}
