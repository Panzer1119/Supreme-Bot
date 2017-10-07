package de.codemakers.bot.supreme.commands.impl.moderation.util;

import de.codemakers.bot.supreme.commands.Command;
import de.codemakers.bot.supreme.commands.CommandCategory;
import de.codemakers.bot.supreme.commands.arguments.ArgumentList;
import de.codemakers.bot.supreme.commands.invoking.Invoker;
import de.codemakers.bot.supreme.entities.MessageEvent;
import de.codemakers.bot.supreme.util.Standard;
import de.codemakers.bot.supreme.util.XMLUtil;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import org.jdom2.Element;
import org.jdom2.output.Format;
import de.codemakers.bot.supreme.permission.PermissionFilter;

/**
 * BackupCommand
 *
 * @author Panzer1119
 */
public class BackupCommand extends Command {

    public static final String GUILD = "guild";
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String GUILD_AFK_CHANNEL_ID = "afk_channel_id";
    public static final String GUILD_AFK_CHANNEL_NAME = "afk_channel_name";
    public static final String GUILD_AFK_TIMEOUT = "afk_timeout";
    public static final String GUILD_VERIFICATION_LEVEL_KEY = "verification_level";
    public static final String GUILD_EXPLICIT_CONTENT_LEVEL_KEY = "explicit_content_level";
    public static final String GUILD_REGION_KEY = "region";
    public static final String GUILD_MFA_LEVEL_KEY = "mfa_level";
    public static final String ICON_ID = "icon_id";
    public static final String ICON_URL = "icon_url";
    public static final String SPLASH_ID = "splash_id";
    public static final String SPLASH_URL = "splash_url";
    public static final String ROLE = "role";
    public static final String ROLE_ID = "role_id";
    public static final String ROLE_NAME = "role_name";
    public static final String MEMBER = "member";
    public static final String USER_ID = "member_id";
    public static final String USER_NAME = "member_name";
    public static final String POSITION = "position";
    public static final String POSITION_RAW = "position_raw";
    public static final String COLOR = "color";
    public static final String HOISTED = "hoisted";
    public static final String MANAGED = "managed";
    public static final String MENTIONABLE = "mentionable";
    public static final String STANDARD = "standard";
    public static final String PERMISSIONS_RAW = "permissions_raw";
    public static final String PERMISSION = "permission";
    public static final String PERMISSION_OVERRIDE_ROLE = "permission_override_role";
    public static final String PERMISSION_OVERRIDE_MEMBER = "permission_override_member";
    public static final String PERMISSION_OVERRIDE_ALLOWED_RAW = "permission_override_allowed_raw";
    public static final String PERMISSION_OVERRIDE_INHERIT_RAW = "permission_override_inherit_raw";
    public static final String PERMISSION_OVERRIDE_DENIED_RAW = "permission_override_denied_raw";
    public static final String TEXT_CHANNEL = "text_channel";
    public static final String TOPIC = "topic";
    public static final String NSFW = "nsfw";
    public static final String VOICE_CHANNEL = "voice_channel";
    public static final String BITRATE = "bitrate";
    public static final String USER_LIMIT = "user_limit";
    public static final String EMOTE = "emote";
    public static final String IMAGE_URL = "image_url";

    @Override
    public void initInvokers() {
        addInvokers(Invoker.createInvoker("backup", this));
    }

    @Override
    public boolean called(Invoker invoker, ArgumentList arguments, MessageEvent event) {
        return true;
    }

    @Override
    public void action(Invoker invoker, ArgumentList arguments, MessageEvent event) {
        try {
            final Guild guild = event.getGuild();
            final Element rootElement = new Element(GUILD);
            rootElement.addContent(new Element(ID).setText(guild.getId()));
            rootElement.addContent(new Element(NAME).setText(guild.getName()));
            rootElement.addContent(new Element(GUILD_AFK_CHANNEL_ID).setText(((guild.getAfkChannel() == null) ? "" : guild.getAfkChannel().getId())));
            rootElement.addContent(new Element(GUILD_AFK_CHANNEL_NAME).setText(((guild.getAfkChannel() == null) ? "" : guild.getAfkChannel().getName())));
            rootElement.addContent(new Element(GUILD_AFK_TIMEOUT).setText("" + guild.getAfkTimeout().getSeconds()));
            rootElement.addContent(new Element(GUILD_VERIFICATION_LEVEL_KEY).setText("" + guild.getVerificationLevel().getKey()));
            rootElement.addContent(new Element(GUILD_EXPLICIT_CONTENT_LEVEL_KEY).setText("" + guild.getExplicitContentLevel().getKey()));
            rootElement.addContent(new Element(GUILD_REGION_KEY).setText("" + guild.getRegion().getKey()));
            rootElement.addContent(new Element(GUILD_MFA_LEVEL_KEY).setText("" + guild.getRequiredMFALevel().getKey()));
            rootElement.addContent(new Element(ICON_ID).setText(((guild.getIconId()) == null) ? "" : guild.getIconId()));
            rootElement.addContent(new Element(ICON_URL).setText(((guild.getIconUrl()) == null) ? "" : guild.getIconUrl()));
            rootElement.addContent(new Element(SPLASH_ID).setText(((guild.getSplashId()) == null) ? "" : guild.getSplashId()));
            rootElement.addContent(new Element(SPLASH_URL).setText(((guild.getSplashUrl()) == null) ? "" : guild.getSplashUrl()));
            try {
                guild.getRoles().stream().filter((role) -> !role.isManaged()).forEach((role) -> {
                    try {
                        final Element element = new Element(ROLE);
                        rootElement.addContent(element);
                        element.addContent(new Element(ID).setText(role.getId()));
                        element.addContent(new Element(NAME).setText(role.getName()));
                        element.addContent(new Element(POSITION).setText("" + role.getPosition()));
                        element.addContent(new Element(POSITION_RAW).setText("" + role.getPositionRaw()));
                        element.addContent(new Element(COLOR).setText(((role.getColor() == null) ? "" : ("" + role.getColor().getRGB()))));
                        element.addContent(new Element(PERMISSIONS_RAW).setText("" + role.getPermissionsRaw()));
                        element.addContent(new Element(HOISTED).setText("" + role.isHoisted()));
                        //element.addContent(new Element(MANAGED).setText("" + role.isManaged())); //TODO Shouldn't managed things also be saved?
                        element.addContent(new Element(MENTIONABLE).setText("" + role.isMentionable()));
                        if (role.isPublicRole()) {
                            element.addContent(new Element(STANDARD));
                        }
                    } catch (Exception ex) {
                        System.err.println("BACKUP Role Intern Error: " + ex);
                    }
                });
            } catch (Exception ex) {
                System.err.println("BACKUP Role Error: " + ex);
            }
            try {
                guild.getTextChannels().stream().forEach((textChannel) -> {
                    try {
                        final Element element = new Element(TEXT_CHANNEL);
                        rootElement.addContent(element);
                        element.addContent(new Element(ID).setText(textChannel.getId()));
                        element.addContent(new Element(NAME).setText(textChannel.getName()));
                        element.addContent(new Element(POSITION).setText("" + textChannel.getPosition()));
                        element.addContent(new Element(POSITION_RAW).setText("" + textChannel.getPositionRaw()));
                        element.addContent(new Element(TOPIC).setText(((textChannel.getTopic()) == null) ? "" : textChannel.getTopic()));
                        element.addContent(new Element(NSFW).setText("" + textChannel.isNSFW()));
                        if (textChannel.getIdLong() == guild.getPublicChannel().getIdLong()) {
                            element.addContent(new Element(STANDARD));
                        }
                        textChannel.getPermissionOverrides().stream().forEach((permissionOverride) -> {
                            if (permissionOverride.isRoleOverride()) {
                                final Element element_permission_override = new Element(PERMISSION_OVERRIDE_ROLE);
                                element.addContent(element_permission_override);
                                element_permission_override.addContent(new Element(ROLE_ID).setText(permissionOverride.getRole().getId()));
                                element_permission_override.addContent(new Element(ROLE_NAME).setText(permissionOverride.getRole().getName()));
                                element_permission_override.addContent(new Element(PERMISSION_OVERRIDE_ALLOWED_RAW).setText("" + permissionOverride.getAllowedRaw()));
                                element_permission_override.addContent(new Element(PERMISSION_OVERRIDE_INHERIT_RAW).setText("" + permissionOverride.getInheritRaw()));
                                element_permission_override.addContent(new Element(PERMISSION_OVERRIDE_DENIED_RAW).setText("" + permissionOverride.getDeniedRaw()));
                            } else if (permissionOverride.isMemberOverride()) {
                                final Element element_permission_override = new Element(PERMISSION_OVERRIDE_MEMBER);
                                element.addContent(element_permission_override);
                                element_permission_override.addContent(new Element(USER_ID).setText(permissionOverride.getMember().getUser().getId()));
                                element_permission_override.addContent(new Element(USER_NAME).setText(permissionOverride.getMember().getUser().getName()));
                                element_permission_override.addContent(new Element(PERMISSION_OVERRIDE_ALLOWED_RAW).setText("" + permissionOverride.getAllowedRaw()));
                                element_permission_override.addContent(new Element(PERMISSION_OVERRIDE_INHERIT_RAW).setText("" + permissionOverride.getInheritRaw()));
                                element_permission_override.addContent(new Element(PERMISSION_OVERRIDE_DENIED_RAW).setText("" + permissionOverride.getDeniedRaw()));
                            }
                        });
                    } catch (Exception ex) {
                        System.err.println("BACKUP TextChannel Intern Error: " + ex);
                    }
                });
            } catch (Exception ex) {
                System.err.println("BACKUP TextChannel Error: " + ex);
            }
            try {
                guild.getVoiceChannels().stream().forEach((voiceChannel) -> {
                    try {
                        final Element element = new Element(VOICE_CHANNEL);
                        rootElement.addContent(element);
                        element.addContent(new Element(ID).setText(voiceChannel.getId()));
                        element.addContent(new Element(NAME).setText(voiceChannel.getName()));
                        element.addContent(new Element(POSITION).setText("" + voiceChannel.getPosition()));
                        element.addContent(new Element(POSITION_RAW).setText("" + voiceChannel.getPositionRaw()));
                        element.addContent(new Element(BITRATE).setText("" + voiceChannel.getBitrate()));
                        element.addContent(new Element(USER_LIMIT).setText("" + voiceChannel.getUserLimit()));
                        if (voiceChannel.getIdLong() == guild.getAfkChannel().getIdLong()) {
                            element.addContent(new Element(STANDARD));
                        }
                        voiceChannel.getPermissionOverrides().stream().forEach((permissionOverride) -> {
                            if (permissionOverride.isRoleOverride()) {
                                final Element element_permission_override = new Element(PERMISSION_OVERRIDE_ROLE);
                                element.addContent(element_permission_override);
                                element_permission_override.addContent(new Element(ROLE_ID).setText(permissionOverride.getRole().getId()));
                                element_permission_override.addContent(new Element(ROLE_NAME).setText(permissionOverride.getRole().getName()));
                                element_permission_override.addContent(new Element(PERMISSION_OVERRIDE_ALLOWED_RAW).setText("" + permissionOverride.getAllowedRaw()));
                                element_permission_override.addContent(new Element(PERMISSION_OVERRIDE_INHERIT_RAW).setText("" + permissionOverride.getInheritRaw()));
                                element_permission_override.addContent(new Element(PERMISSION_OVERRIDE_DENIED_RAW).setText("" + permissionOverride.getDeniedRaw()));
                            } else if (permissionOverride.isMemberOverride()) {
                                final Element element_permission_override = new Element(PERMISSION_OVERRIDE_MEMBER);
                                element.addContent(element_permission_override);
                                element_permission_override.addContent(new Element(USER_ID).setText(permissionOverride.getMember().getUser().getId()));
                                element_permission_override.addContent(new Element(USER_NAME).setText(permissionOverride.getMember().getUser().getName()));
                                element_permission_override.addContent(new Element(PERMISSION_OVERRIDE_ALLOWED_RAW).setText("" + permissionOverride.getAllowedRaw()));
                                element_permission_override.addContent(new Element(PERMISSION_OVERRIDE_INHERIT_RAW).setText("" + permissionOverride.getInheritRaw()));
                                element_permission_override.addContent(new Element(PERMISSION_OVERRIDE_DENIED_RAW).setText("" + permissionOverride.getDeniedRaw()));
                            }
                        });
                    } catch (Exception ex) {
                        System.err.println("BACKUP VoiceChannel Intern Error: " + ex);
                    }
                });
            } catch (Exception ex) {
                System.err.println("BACKUP VoiceChannel Error: " + ex);
            }
            try {
                guild.getEmotes().stream().filter((emote) -> !emote.isManaged()).forEach((emote) -> {
                    try {
                        final Element element = new Element(EMOTE);
                        rootElement.addContent(element);
                        element.addContent(new Element(ID).setText(emote.getId()));
                        element.addContent(new Element(NAME).setText(emote.getName()));
                        element.addContent(new Element(IMAGE_URL).setText(emote.getImageUrl()));
                        //element.addContent(new Element(MANAGED).setText("" + emote.isManaged())); //TODO Shouldn't managed things also be saved?
                    } catch (Exception ex) {
                        System.err.println("BACKUP Emote Intern Error: " + ex);
                    }
                });
            } catch (Exception ex) {
                System.err.println("BACKUP Emote Error: " + ex);
            }
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            XMLUtil.save(rootElement, Format.getPrettyFormat(), byteArrayOutputStream); //TODO Make the Format changable
            event.sendFile(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()), guild.getId() + ".xml", new MessageBuilder().appendFormat("Saved all Roles, Emotes, Text- and VoiceChannels from \"%s\" (ID: %s)", guild.getName(), guild.getId()).build());
            byteArrayOutputStream.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void executed(boolean success, MessageEvent event) {
        System.out.println("[INFO] Command '" + getCommandID() + "' was executed!");
    }

    @Override
    public EmbedBuilder getHelp(Invoker invoker, EmbedBuilder builder) {
        builder.addField("SOON", "This command is not fully implemented yet!", false);
        return builder;
    }

    @Override
    public PermissionFilter getPermissionFilter() {
        return Standard.STANDARD_PERMISSIONFILTER_GUILD_OWNER_BOT_COMMANDER;
    }

    @Override
    public String getCommandID() {
        return getClass().getName();
    }

    @Override
    public CommandCategory getCommandCategory() {
        return Standard.COMMANDCATEGORY_MODERATION_UTIL;
    }

}
