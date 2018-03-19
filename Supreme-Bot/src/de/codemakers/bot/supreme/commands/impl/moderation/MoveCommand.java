package de.codemakers.bot.supreme.commands.impl.moderation;

import de.codemakers.bot.supreme.commands.Command;
import de.codemakers.bot.supreme.commands.CommandCategory;
import de.codemakers.bot.supreme.commands.arguments.ArgumentList;
import de.codemakers.bot.supreme.commands.invoking.Invoker;
import de.codemakers.bot.supreme.entities.MessageEvent;
import de.codemakers.bot.supreme.permission.PermissionFilter;
import de.codemakers.bot.supreme.util.Standard;
import de.codemakers.bot.supreme.util.Util;
import de.codemakers.logger.Logger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.VoiceChannel;

/**
 * MoveCommand
 *
 * @author Panzer1119
 */
public class MoveCommand extends Command {

    @Override
    public void initInvokers() {
        addInvokers(Invoker.createInvoker("move", this), Invoker.createInvoker("mv", this));
    }

    @Override
    public boolean called(Invoker invoker, ArgumentList arguments, MessageEvent event) {
        if (arguments == null || arguments.isEmpty() || event.isPrivate()) {
            return false;
        }
        return arguments.isSize(1, -1);
    }

    @Override
    public void action(Invoker invoker, ArgumentList arguments, MessageEvent event) {
        boolean maybeMe = arguments.isSize(1, 2);
        final String voiceChannel_string = arguments.consumeFirst();
        final VoiceChannel voiceChannel_1 = Util.resolveVoiceChannel(event.getGuild(), voiceChannel_string);
        if (voiceChannel_1 == null) {
            event.sendMessage(Standard.STANDARD_MESSAGE_DELETING_DELAY, Standard.getNoMessage(event.getAuthor(), "the VoiceChannel \"%s\" wasn't found!", voiceChannel_string).build());
            return;
        }
        String voiceChannel_string_2 = arguments.getFirst();
        temp_126555:
        if (voiceChannel_string_2 != null) {
            final VoiceChannel voiceChannel_2 = Util.resolveVoiceChannel(event.getGuild(), voiceChannel_string_2);
            if (voiceChannel_2 == null) {
                if (!event.getMember().getVoiceState().inVoiceChannel()) {
                    final List<Member> membersToMove = new ArrayList<>();
                    while (!arguments.isEmpty()) {
                        membersToMove.add(arguments.consumeMemberFirst());
                    }
                    boolean everyone = membersToMove.isEmpty();
                    boolean done = true;
                    try {
                        if (everyone) {
                            event.getGuild().getVoiceStates().forEach((voiceState) -> {
                                try {
                                    event.getGuild().getController().moveVoiceMember(voiceState.getMember(), voiceChannel_1).queue();
                                } catch (Exception ex) {
                                }
                            });
                        } else {
                            membersToMove.forEach(member -> {
                                try {
                                    event.getGuild().getController().moveVoiceMember(member, voiceChannel_1).queue();
                                } catch (Exception ex) {
                                }
                            });
                        }
                    } catch (Exception ex) {
                        Logger.logErr("Error while moving 1: " + ex, ex);
                        done = false;
                    }
                    if (done) {
                        if (everyone) {
                            event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s%s moved everyone to \"%s\"", event.getAuthor().getAsMention(), done ? "" : " not", voiceChannel_string);
                        } else {
                            final String temp = membersToMove.stream().map(Member::getAsMention).collect(Collectors.joining(", ", "[", "]"));
                            event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s%s moved %s to \"%s\"", event.getAuthor().getAsMention(), done ? "" : " not", temp, voiceChannel_string);
                        }
                    } else {
                        event.sendMessage(Standard.STANDARD_MESSAGE_DELETING_DELAY, Standard.getNoMessage(event.getAuthor(), "something went wrong, while moving through the VoiceChannels!").build());
                    }
                    return;
                }
                maybeMe = true;
                break temp_126555;
            }
            arguments.consumeFirst();
            final List<Member> membersToMove = new ArrayList<>();
            while (!arguments.isEmpty()) {
                membersToMove.add(arguments.consumeMemberFirst());
            }
            boolean everyone = membersToMove.isEmpty();
            boolean done = true;
            try {
                (everyone ? voiceChannel_1.getMembers().stream() : voiceChannel_1.getMembers().stream().filter(membersToMove::contains)).forEach((member) -> {
                    try {
                        event.getGuild().getController().moveVoiceMember(member, voiceChannel_2).queue();
                    } catch (Exception ex) {
                    }
                });
            } catch (Exception ex) {
                Logger.logErr("Error while moving 2: " + ex, ex);
                done = false;
            }
            if (done) {
                if (everyone) {
                    event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s%s moved everyone from \"%s\" to \"%s\"", event.getAuthor().getAsMention(), done ? "" : " not", voiceChannel_string, voiceChannel_string_2);
                } else {
                    final String temp = membersToMove.stream().map(Member::getAsMention).collect(Collectors.joining(", ", "[", "]"));
                    event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s%s moved %s from \"%s\" to \"%s\"", event.getAuthor().getAsMention(), done ? "" : " not", temp, voiceChannel_string, voiceChannel_string_2);
                }
            } else {
                event.sendMessage(Standard.STANDARD_MESSAGE_DELETING_DELAY, Standard.getNoMessage(event.getAuthor(), "something went wrong, while moving through the VoiceChannels!").build());
            }
            return;
        }
        if (maybeMe) {
            if (!arguments.isEmpty() && arguments.getMemberFirst() == null) {
                event.sendMessage(Standard.STANDARD_MESSAGE_DELETING_DELAY, Standard.getNoMessage(event.getAuthor(), "the VoiceChannel \"%s\" wasn't found!", voiceChannel_string_2).build());
                return;
            }
            if (arguments.isEmpty() || !event.getMember().getVoiceState().inVoiceChannel()) {
                arguments.consumeFirst();
                final List<Member> membersToMove = new ArrayList<>();
                while (!arguments.isEmpty()) {
                    membersToMove.add(arguments.consumeMemberFirst());
                }
                boolean everyone = membersToMove.isEmpty();
                boolean done = true;
                try {
                    if (everyone) {
                        event.getGuild().getVoiceStates().forEach((voiceState) -> {
                            try {
                                event.getGuild().getController().moveVoiceMember(voiceState.getMember(), voiceChannel_1).queue();
                            } catch (Exception ex) {
                            }
                        });
                    } else {
                        membersToMove.forEach(member -> {
                            try {
                                event.getGuild().getController().moveVoiceMember(member, voiceChannel_1).queue();
                            } catch (Exception ex) {
                            }
                        });
                    }
                } catch (Exception ex) {
                    Logger.logErr("Error while moving 3: " + ex, ex);
                    done = false;
                }
                if (done) {
                    if (everyone) {
                        event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s%s moved everyone to \"%s\"", event.getAuthor().getAsMention(), done ? "" : " not", voiceChannel_string);
                    } else {
                        final String temp = membersToMove.stream().map(Member::getAsMention).collect(Collectors.joining(", ", "[", "]"));
                        event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s%s moved %s to \"%s\"", event.getAuthor().getAsMention(), done ? "" : " not", temp, voiceChannel_string);
                    }
                } else {
                    event.sendMessage(Standard.STANDARD_MESSAGE_DELETING_DELAY, Standard.getNoMessage(event.getAuthor(), "something went wrong, while moving through the VoiceChannels!").build());
                }
                return;
            }
            final List<Member> membersToMove = new ArrayList<>();
            while (!arguments.isEmpty()) {
                membersToMove.add(arguments.consumeMemberFirst());
            }
            VoiceChannel voiceChannel_2 = null;
            boolean everyone = membersToMove.isEmpty();
            boolean done = true;
            try {
                voiceChannel_2 = event.getMember().getVoiceState().getChannel();
                voiceChannel_string_2 = voiceChannel_2.getName();
                (everyone ? voiceChannel_2.getMembers().stream() : voiceChannel_2.getMembers().stream().filter(membersToMove::contains)).forEach((member) -> {
                    try {
                        event.getGuild().getController().moveVoiceMember(member, voiceChannel_1).queue();
                    } catch (Exception ex) {
                    }
                });
            } catch (Exception ex) {
                Logger.logErr("Error while moving 4: " + ex, ex);
                done = false;
            }
            if (done) {
                if (everyone) {
                    event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s%s moved everyone from \"%s\" to \"%s\"", event.getAuthor().getAsMention(), done ? "" : " not", voiceChannel_string_2, voiceChannel_string);
                } else {
                    final String temp = membersToMove.stream().map(Member::getAsMention).collect(Collectors.joining(", ", "[", "]"));
                    event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s%s moved %s from \"%s\" to \"%s\"", event.getAuthor().getAsMention(), done ? "" : " not", temp, voiceChannel_string_2, voiceChannel_string);
                }
            } else {
                event.sendMessage(Standard.STANDARD_MESSAGE_DELETING_DELAY, Standard.getNoMessage(event.getAuthor(), "something went wrong, while moving through the VoiceChannels!").build());
            }
        } else {
            event.sendMessage(Standard.STANDARD_MESSAGE_DELETING_DELAY, Standard.getNoMessage(event.getAuthor(), "something went FATALLY wrong, while moving through the VoiceChannels!").build());
        }
    }

    @Override
    public void executed(boolean success, MessageEvent event) {
        System.out.println("[INFO] Command '" + getCommandID() + "' was executed!");
    }

    @Override
    public EmbedBuilder getHelp(Invoker invoker, EmbedBuilder builder) {
        builder.addField(String.format("%s <VoiceChannel> [User...]", invoker), "Moves everybody/users (from your current) VoiceChannel to the specified VoiceChannel. Use `VoiceChannel#Number` or its id when there are multiple VoiceChannels with the same name.", false);
        builder.addField(String.format("%s <VoiceChannel 1> <VoiceChannel 2> [User...]", invoker), "Moves everybody/users from VoiceChannel 1 to VoiceChannel 2. Use `VoiceChannel#Number` or its id when there are multiple VoiceChannels with the same name.", false);
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
