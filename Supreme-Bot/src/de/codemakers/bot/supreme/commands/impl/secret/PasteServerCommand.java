package de.codemakers.bot.supreme.commands.impl.secret;

import de.codemakers.bot.supreme.commands.Command;
import de.codemakers.bot.supreme.commands.CommandCategory;
import de.codemakers.bot.supreme.commands.arguments.ArgumentList;
import de.codemakers.bot.supreme.commands.invoking.Invoker;
import de.codemakers.bot.supreme.entities.MessageEvent;
import de.codemakers.bot.supreme.util.Emoji;
import de.codemakers.bot.supreme.util.Standard;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.PermissionOverride;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.managers.ChannelManagerUpdatable;
import net.dv8tion.jda.core.managers.GuildController;
import net.dv8tion.jda.core.managers.RoleManagerUpdatable;
import net.dv8tion.jda.core.requests.restaction.ChannelAction;
import net.dv8tion.jda.core.requests.restaction.RoleAction;
import de.codemakers.bot.supreme.permission.PermissionFilter;

/**
 * PasteServerCommand
 *
 * @author Panzer1119
 */
public class PasteServerCommand extends Command {

    @Override
    public void initInvokers() {
        addInvokers(Invoker.createInvoker("pasteServer", this));
    }

    @Override
    public boolean called(Invoker invoker, ArgumentList arguments, MessageEvent event) {
        if (arguments == null || arguments.isEmpty()) {
            return false;
        }
        return arguments.isSize(1);
    }

    @Override
    public void action(Invoker invoker, ArgumentList arguments, MessageEvent event) {
        final String guild_id = arguments.consumeFirst();
        final Guild guild_toCopy = Standard.getGuildById(guild_id);
        if (guild_toCopy == null) {
            event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s Sorry %s, the guild \"%s\" wasn't found!", Emoji.WARNING, event.getAuthor().getAsMention(), guild_id);
            return;
        }
        final Guild guild_this = event.getGuild();
        try {
            final GuildController controller_toCopy = guild_toCopy.getController();
            final GuildController controller_this = guild_this.getController();
            guild_toCopy.getRoles().stream().forEach((role) -> {
                if (role.getName().equalsIgnoreCase("Supreme-Bot")) {
                    return;
                }
                try {
                    Role r = guild_this.getRolesByName(role.getName(), true).stream().findFirst().orElse(null);
                    if (r == null) {
                        RoleAction action = controller_this.createRole();
                        action = action.setColor(role.getColor());
                        action = action.setHoisted(role.isHoisted());
                        action = action.setMentionable(role.isMentionable());
                        action = action.setName(role.getName());
                        action = action.setPermissions(role.getPermissions());
                        r = action.complete();
                    } else {
                        RoleManagerUpdatable updater = r.getManagerUpdatable();
                        updater = updater.getColorField().setValue(role.getColor());
                        updater = updater.getHoistedField().setValue(role.isHoisted());
                        updater = updater.getMentionableField().setValue(role.isMentionable());
                        updater = updater.getPermissionField().setPermissions(role.getPermissions());
                        updater.update().complete();
                    }
                    System.out.println("Role: " + role.getId() + " -> " + r.getId() + " " + r.getName());
                } catch (Exception ex) {
                    System.err.println("Role Error: " + ex);
                }
            });
            guild_toCopy.getTextChannels().stream().forEach((textChannel) -> {
                try {
                    TextChannel tc = guild_this.getTextChannelsByName(textChannel.getName(), false).stream().findFirst().orElse(null);
                    if (tc == null) {
                        ChannelAction action = controller_this.createTextChannel(textChannel.getName()).setNSFW(textChannel.isNSFW()).setTopic(textChannel.getTopic());
                        for (PermissionOverride override : textChannel.getPermissionOverrides()) {
                            if (override.isRoleOverride()) {
                                action = action.addPermissionOverride(guild_this.getRolesByName(override.getRole().getName(), false).stream().findFirst().orElse(null), override.getAllowed(), override.getDenied());
                            } else if (override.isMemberOverride()) {
                                action = action.addPermissionOverride(override.getMember(), override.getAllowed(), override.getDenied());
                            }
                        }
                        tc = (TextChannel) action.complete();
                    } else {
                        ChannelManagerUpdatable updater = tc.getManagerUpdatable();
                        updater = updater.getNSFWField().setValue(textChannel.isNSFW());
                        updater = updater.getTopicField().setValue(textChannel.getTopic());
                        updater.update().queue();
                    }
                    System.out.println("TextChannel: " + textChannel.getId() + " -> " + tc.getId() + " " + tc.getName());
                } catch (Exception ex) {
                    System.err.println("TextChannel Error: " + ex);
                }
            });
            guild_toCopy.getVoiceChannels().stream().forEach((VoiceChannel voiceChannel) -> {
                try {
                    VoiceChannel vc = guild_this.getVoiceChannelsByName(voiceChannel.getName(), false).stream().findFirst().orElse(null);
                    if (vc == null) {
                        ChannelAction action = controller_this.createVoiceChannel(voiceChannel.getName()).setBitrate(voiceChannel.getBitrate()).setUserlimit(voiceChannel.getUserLimit());
                        for (PermissionOverride override : voiceChannel.getPermissionOverrides()) {
                            if (override.isRoleOverride()) {
                                action = action.addPermissionOverride(guild_this.getRolesByName(override.getRole().getName(), false).stream().findFirst().orElse(null), override.getAllowed(), override.getDenied());
                            } else if (override.isMemberOverride()) {
                                action = action.addPermissionOverride(override.getMember(), override.getAllowed(), override.getDenied());
                            }
                        }
                        vc = (VoiceChannel) action.complete();
                    } else {
                        ChannelManagerUpdatable updater = voiceChannel.getManagerUpdatable();
                        updater = updater.getBitrateField().setValue(voiceChannel.getBitrate());
                        updater = updater.getUserLimitField().setValue(voiceChannel.getUserLimit());
                        updater.update().queue();
                    }
                    System.out.println("VoiceChannel: " + voiceChannel.getId() + " -> " + vc.getId() + " " + vc.getName());
                } catch (Exception ex) {
                    System.err.println("VoiceChannel Error: " + ex);
                }
            });
            guild_toCopy.getAfkChannel();
            //System.setProperty("http.agent", "Chrome");
            guild_toCopy.getEmotes().stream().forEach((emote) -> {
                System.out.println(emote.getImageUrl() + " " + emote.getName());
                /*
                try {
                    controller_this.createEmote(emote.getName(), Icon.from(new URL(emote.getImageUrl()).openStream()), emote.getRoles().toArray(new Role[0])).queue();
                } catch (Exception ex) {
                    System.err.println("Emote Error: " + ex);
                    ex.printStackTrace();
                }
                 */
            });
            event.sendMessageFormat("Copied all Roles, Text- and VoiceChannels from \"%s\" (ID: %s) to this Guild!", guild_toCopy.getName(), guild_id);
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
        builder.addField(String.format("%s [Guild ID]", invoker), "Copies a complete Discord server to this one.", false);
        return builder;
    }

    @Override
    public PermissionFilter getPermissionFilter() {
        return Standard.STANDARD_PERMISSIONFILTER_BOT_SUPER_OWNER;
    }

    @Override
    public String getCommandID() {
        return getClass().getName();
    }

    @Override
    public CommandCategory getCommandCategory() {
        return Standard.COMMANDCATEGORY_SECRET;
    }

}
