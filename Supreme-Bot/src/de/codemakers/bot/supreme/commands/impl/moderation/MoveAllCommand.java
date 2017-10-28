package de.codemakers.bot.supreme.commands.impl.moderation;

import de.codemakers.bot.supreme.commands.Command;
import de.codemakers.bot.supreme.commands.CommandCategory;
import de.codemakers.bot.supreme.commands.arguments.ArgumentList;
import de.codemakers.bot.supreme.commands.invoking.Invoker;
import de.codemakers.bot.supreme.entities.MessageEvent;
import de.codemakers.bot.supreme.permission.PermissionFilter;
import de.codemakers.bot.supreme.util.Standard;
import de.codemakers.bot.supreme.util.Util;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.VoiceChannel;

/**
 * MoveAllCommand
 *
 * @author Panzer1119
 */
public class MoveAllCommand extends Command {

    @Override
    public void initInvokers() {
        addInvokers(Invoker.createInvoker("moveAll", this), Invoker.createInvoker("mvAll", this));
    }

    @Override
    public boolean called(Invoker invoker, ArgumentList arguments, MessageEvent event) {
        if (arguments == null || arguments.isEmpty() || event.isPrivate()) {
            return false;
        }
        return arguments.isSize(1, 2);
    }

    @Override
    public void action(Invoker invoker, ArgumentList arguments, MessageEvent event) {
        final String voiceChannel_string = arguments.consumeFirst();
        final VoiceChannel voiceChannel = Util.resolveVoiceChannel(event.getGuild(), voiceChannel_string);
        if (voiceChannel == null) {
            event.sendMessage(Standard.STANDARD_MESSAGE_DELETING_DELAY, Standard.getNoMessage(event.getAuthor(), "the VoiceChannel \"%s\" wasn't found!", voiceChannel_string).build());
            return;
        }
        final String voiceChannel_string_2 = arguments.consumeFirst();
        if (voiceChannel_string_2 != null) {
            final VoiceChannel voiceChannel_2 = Util.resolveVoiceChannel(event.getGuild(), voiceChannel_string_2);
            if (voiceChannel_2 == null) {
                event.sendMessage(Standard.STANDARD_MESSAGE_DELETING_DELAY, Standard.getNoMessage(event.getAuthor(), "the VoiceChannel \"%s\" wasn't found!", voiceChannel_string_2).build());
                return;
            }
            boolean done = true;
            try {
                voiceChannel.getMembers().stream().forEach((member) -> {
                    try {
                        event.getGuild().getController().moveVoiceMember(member, voiceChannel_2).queue();
                    } catch (Exception ex) {
                    }
                });
            } catch (Exception ex) {
                done = false;
            }
            event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s%s moved everyone from \"%s\" to \"%s\"", event.getAuthor().getAsMention(), done ? "" : " not", voiceChannel_string, voiceChannel_string_2);
        } else {
            if (!event.getMember().getVoiceState().inVoiceChannel()) {
                event.sendMessage(Standard.STANDARD_MESSAGE_DELETING_DELAY, Standard.getNoMessage(event.getAuthor(), "you need to be in a VoiceChannel!").build());
                return;
            }
            VoiceChannel voiceChannel_2 = null;
            boolean done = true;
            try {
                voiceChannel_2 = event.getMember().getVoiceState().getChannel();
                voiceChannel_2.getMembers().stream().forEach((member) -> {
                    try {
                        event.getGuild().getController().moveVoiceMember(member, voiceChannel).queue();
                    } catch (Exception ex) {
                    }
                });
            } catch (Exception ex) {
                done = false;
            }
            event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s%s moved everyone from \"%s\" to \"%s\"", event.getAuthor().getAsMention(), done ? "" : " not", voiceChannel_2 == null ? voiceChannel_2 : voiceChannel_2.getName(), voiceChannel_string);
        }
    }

    @Override
    public void executed(boolean success, MessageEvent event) {
        System.out.println("[INFO] Command '" + getCommandID() + "' was executed!");
    }

    @Override
    public EmbedBuilder getHelp(Invoker invoker, EmbedBuilder builder) {
        builder.addField(String.format("%s <VoiceChannel>", invoker), "Moves everyone from your current VoiceChannel to the specified VoiceChannel. Use `VoiceChannel#Number` or its id when there are multiple VoiceChannels with the same name.", false);
        builder.addField(String.format("%s <VoiceChannel 1> <VoiceChannel 2>", invoker), "Moves everyone from VoiceChannel 1 to VoiceChannel 2. Use `VoiceChannel#Number` or its id when there are multiple VoiceChannels with the same name.", false);
        return builder;
    }

    @Override
    public PermissionFilter getPermissionFilter() {
        return Standard.STANDARD_PERMISSIONFILTER_GUILD_MODERATOR_BOT_COMMANDER_BOT_ADMIN;
    }

    @Override
    public String getCommandID() {
        return getClass().getName();
    }

    @Override
    public CommandCategory getCommandCategory() {
        return Standard.COMMANDCATEGORY_MODERATION;
    }

}
