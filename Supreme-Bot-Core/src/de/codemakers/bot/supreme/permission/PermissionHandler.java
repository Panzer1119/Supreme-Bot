package de.codemakers.bot.supreme.permission;

import de.codemakers.bot.supreme.commands.Command;
import de.codemakers.bot.supreme.entities.MessageEvent;
import de.codemakers.bot.supreme.util.AdvancedFile;
import de.codemakers.bot.supreme.util.Emoji;
import de.codemakers.bot.supreme.util.Standard;
import de.codemakers.bot.supreme.util.Util;
import de.codemakers.bot.supreme.util.XMLUtil;
import java.io.InputStream;
import java.util.List;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;

/**
 * PermissionHandler
 *
 * @author Panzer1119
 */
public class PermissionHandler {

    public static final boolean isPermissionGranted(PermissionFilter filter, MessageEvent event) {
        if (filter == null) {
            return true;
        }
        if (event == null) {
            return false;
        }
        return isPermissionGranted(filter, event.getGuild(), event.getAuthor());
    }

    public static final boolean isPermissionGranted(PermissionFilter filter, Member member) {
        return isPermissionGranted(filter, member.getGuild(), member.getUser());
    }

    public static final boolean isPermissionGranted(PermissionFilter filter, Guild guild, User user) {
        if (filter == null) {
            return true;
        }
        if (Standard.isSuperOwner(user)) {
            return true;
        }
        if (user == null) {
            return false;
        }
        if (!filter.isPermissionGranted(guild, user)) {
            return false;
        }
        if (filter.isGlobalPermissionGranted(GlobalBotRole.getGlobalBotRolesByUser(user))) {
            return true;
        }
        return guild != null && filter.isGuildPermissionGranted(GuildBotRole.getGuildBotRolesByGuildAndUser(guild, user));
    }

    public static final boolean isPermissionGranted(PermissionFilter filter, Channel channel) {
        if (filter == null) {
            return true;
        }
        if (channel == null || channel.getRolePermissionOverrides().isEmpty()) {
            return false;
        }
        return channel.getRolePermissionOverrides().stream().filter((po) -> !po.getDenied().contains(Permission.MESSAGE_READ)).noneMatch((po) -> !filter.isGuildPermissionGranted(GuildBotRole.getGuildBotRolesByRole(po.getRole())));
    }

    public static final boolean isPermissionGranted(List<Command> commands, Channel channel) {
        if (channel == null) {
            return false;
        }
        if (commands == null || commands.isEmpty()) {
            return true;
        }
        return commands.stream().allMatch((command) -> isPermissionGranted(command.getPermissionFilter(), channel));
    }

    public static final boolean sendNoPermissionMessage(MessageEvent event) {
        return event.sendMessage(Standard.getNoPermissionMessage(event.getAuthor(), "command"));
    }

    @Deprecated
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

    @Deprecated
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

    @Deprecated
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

    @Deprecated
    public static final boolean loadPermissionsForGuild(AdvancedFile file, long guild_id) {
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

    @Deprecated
    public static final boolean loadPermissionsForGuild(String jar_path, long guild_id) {
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

    @Deprecated
    public static final boolean loadPermissionsForGuild(InputStream inputStream, long guild_id) {
        if (inputStream == null) {
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
                            final Attribute roleid = role.getAttribute(Standard.XML_ROLEID);
                            final PermissionRole permissionRole = PermissionRole.getPermissionRoleByPermissionRoleID(permissionRoleID);
                            if (roleid != null) {
                                final String roleID = roleid.getValue();
                                if (permissionRole != null && roleID != null) {
                                    permissionRole.addRoleForGuild(guild_id, Long.parseLong(roleID));
                                    if (Standard.DEBUG_PERMISSION_HANDLER) {
                                        System.out.println(String.format("Added Role \"%s\" for Guild \"%d\" to \"%s\"", roleID, guild_id, permissionRole));
                                    }
                                } else {
                                    System.err.println(String.format("Can't add permissions for Role \"%s\" to \"%s\" (GUILD ID: %d)", roleID, permissionRoleID, guild_id));
                                }
                            } else {
                                final Attribute userid = role.getAttribute(Standard.XML_USERID);
                                final String userID = userid.getValue();
                                if (permissionRole != null && userID != null) {
                                    permissionRole.addUserForGuild(guild_id, Long.parseLong(userID));
                                    if (Standard.DEBUG_PERMISSION_HANDLER) {
                                        System.out.println(String.format("Added User \"%s\" for Guild \"%d\" to \"%s\"", userID, guild_id, permissionRole));
                                    }
                                } else {
                                    System.err.println(String.format("Can't add permissions for User \"%s\" to \"%s\" (GUILD ID: %d)", userID, permissionRoleID, guild_id));
                                }
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
