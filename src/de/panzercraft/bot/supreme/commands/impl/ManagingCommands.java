package de.panzercraft.bot.supreme.commands.impl;

import de.panzercraft.bot.supreme.commands.Command;
import de.panzercraft.bot.supreme.commands.arguments.ArgumentConsumeType;
import de.panzercraft.bot.supreme.commands.arguments.ArgumentList;
import de.panzercraft.bot.supreme.core.SupremeBot;
import de.panzercraft.bot.supreme.permission.PermissionRole;
import de.panzercraft.bot.supreme.permission.PermissionRoleFilter;
import de.panzercraft.bot.supreme.util.IntegerHolder;
import de.panzercraft.bot.supreme.util.Standard;
import java.awt.Color;
import java.io.File;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageHistory;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 *
 * @author Panzer1119
 */
public class ManagingCommands {

    public static class CommandPrefixChangeCommand implements Command {

        @Override
        public final String[] getInvokes() {
            return new String[]{"changeCommandPrefix", "getCommandPrefix"};
        }

        @Override
        public final boolean called(String invoke, ArgumentList arguments, MessageReceivedEvent event) {
            final boolean global = (arguments == null ? false : arguments.isConsumed(Standard.ARGUMENT_GLOBAL, ArgumentConsumeType.ALL_IGNORE_CASE));
            switch (invoke) {
                case "changeCommandPrefix":
                    if (arguments == null) {
                        return false;
                    }
                    return (global && (arguments.isSize(2))) || (!global && (arguments.isSize(1)));
                case "getCommandPrefix":
                    if (arguments == null) {
                        return true;
                    }
                    return (global && (arguments.isSize(1))) || (!global && (arguments.isSize(0)));
            }
            return false;
        }

        @Override
        public final void action(String invoke, ArgumentList arguments, MessageReceivedEvent event) {
            final boolean global = arguments.isConsumed(Standard.ARGUMENT_GLOBAL, ArgumentConsumeType.CONSUME_ALL_IGNORE_CASE);
            if (!global && arguments.isSize(2, -1)) {
                return;
            }
            switch (invoke) {
                case "changeCommandPrefix":
                    final String commandPrefix = arguments.consumeFirst();
                    if (commandPrefix == null) {
                        break;
                    }
                    if (global) {
                        if (Standard.setStandardCommandPrefix(commandPrefix)) {
                            event.getTextChannel().sendMessageFormat("Changed Gloval Command Prefix to \"%s\"", commandPrefix).queue();
                        } else {
                            event.getTextChannel().sendMessageFormat("Global Command Prefix wasn't changed, it's still \"%s\"", Standard.getStandardCommandPrefix()).queue();
                        }
                    } else if (Standard.setCommandPrefixForGuild(event.getGuild(), commandPrefix)) {
                        event.getTextChannel().sendMessageFormat("Changed Command Prefix to \"%s\"", commandPrefix).queue();
                    } else {
                        event.getTextChannel().sendMessageFormat("Command Prefix wasn't changed, it's still \"%s\"", Standard.getCommandPrefixByGuild(event.getGuild())).queue();
                    }
                    break;
                case "getCommandPrefix":
                    if (global) {
                        event.getTextChannel().sendMessageFormat("Global Command Prefix is \"%s\"", Standard.getStandardCommandPrefix()).queue();
                    } else {
                        event.getTextChannel().sendMessageFormat("Command Prefix for this Guild is \"%s\"", Standard.getCommandPrefixByGuild(event.getGuild())).queue();
                    }
                    break;
            }
        }

        @Override
        public final void executed(boolean success, MessageReceivedEvent event) {
            System.out.println("[INFO] Command '" + getInvokes()[0] + "' was executed!");
        }

        @Override
        public final String getHelp() {
            return String.format("%n`%s <New Command Prefix> [global]`%n`%s [global]`", getInvokes()[0], getInvokes()[1]);
        }

        @Override
        public final PermissionRoleFilter getPermissionRoleFilter() {
            final PermissionRole admin = PermissionRole.getPermissionRoleByName("Admin");
            return (role, member) -> role.isThisHigherOrEqual(admin);
        }

    }

    public static class StopCommand implements Command {

        @Override
        public final String[] getInvokes() {
            return new String[]{"stop", "shutdown"};
        }

        @Override
        public final boolean called(String invoke, ArgumentList arguments, MessageReceivedEvent event) {
            return arguments == null || arguments.isSize(0, 1);
        }

        @Override
        public final void action(String invoke, ArgumentList arguments, MessageReceivedEvent event) {
            if (arguments != null && arguments.size() >= 1) {
                try {
                    final double delayInSeconds = Double.parseDouble(arguments.get(0));
                    event.getTextChannel().sendMessage(Standard.getMessageEmbed(Color.YELLOW, "%s is stopping me in %d seconds!", event.getAuthor().getAsMention(), ((long) (delayInSeconds + 0.5))).build()).queue();
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            event.getTextChannel().sendMessage(Standard.getMessageEmbed(Color.YELLOW, "%s stopped me!", event.getAuthor().getAsMention()).build()).queue();
                            System.exit(0);
                        }
                    }, (long) (delayInSeconds * 1000.0 + 0.5));
                    return;
                } catch (Exception ex) {
                }
            }
            try {
                event.getTextChannel().sendMessage(Standard.getMessageEmbed(Color.YELLOW, "%s stopped me!", event.getAuthor().getAsMention()).build()).queue();
                System.exit(0);
            } catch (Exception ex) {
                System.exit(-1);
            }
        }

        @Override
        public final void executed(boolean success, MessageReceivedEvent event) {
            System.out.println("[INFO] Command '" + getInvokes()[0] + "' was executed!");
        }

        @Override
        public final String getHelp() {
            return String.format("%n`%s/%s [Delay Time in Seconds]`", getInvokes()[0], getInvokes()[1]);
        }

        @Override
        public final PermissionRoleFilter getPermissionRoleFilter() {
            final PermissionRole owner = PermissionRole.getPermissionRoleByName("Owner");
            final PermissionRole bot_commander = PermissionRole.getPermissionRoleByName("Bot_Commander");
            return (role, member) -> {
                if (role.isThisHigherOrEqual(owner) || role.isThisEqual(bot_commander)) {
                    return true;
                }
                return Standard.isSuperOwner(member);
            };
        }

    }

    public static class RestartCommand implements Command {

        @Override
        public final String[] getInvokes() {
            return new String[]{"restart", "reboot"};
        }

        @Override
        public final boolean called(String invoke, ArgumentList arguments, MessageReceivedEvent event) {
            return arguments == null || arguments.isSize(0, 1);
        }

        @Override
        public final void action(String invoke, ArgumentList arguments, MessageReceivedEvent event) {
            if (arguments != null && arguments.size() >= 1) {
                try {
                    final double delayInSeconds = Double.parseDouble(arguments.get(0));
                    final Message message = event.getTextChannel().sendMessage(getRestartingMessage(event, delayInSeconds, 0).build()).complete();
                    final Timer timer = new Timer();
                    final IntegerHolder i = new IntegerHolder();
                    final TimerTask timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            message.editMessage(getRestartingMessage(event, delayInSeconds, i.value).build()).queue();
                            i.value++;
                            if (i.value >= delayInSeconds) {
                                timer.purge();
                            }
                        }
                    };
                    timer.scheduleAtFixedRate(timerTask, 0, 1000);
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            timer.purge();
                            timerTask.cancel();
                            message.editMessage(Standard.getMessageEmbed(Color.YELLOW, "%s restarted me!", event.getAuthor().getAsMention()).build()).queue();
                            restart();
                        }
                    }, (long) (delayInSeconds * 1000.0 + 0.5));
                    return;
                } catch (Exception ex) {
                }
            }
            try {
                event.getTextChannel().sendMessage(Standard.getMessageEmbed(Color.YELLOW, "%s restarted me!", event.getAuthor().getAsMention()).build()).queue();
                restart();
            } catch (Exception ex) {
            }
        }

        @Override
        public final void executed(boolean success, MessageReceivedEvent event) {
            System.out.println("[INFO] Command '" + getInvokes()[0] + "' was executed!");
        }

        @Override
        public final String getHelp() {
            return String.format("%n`%s/%s [Delay Time in Seconds]`", getInvokes()[0], getInvokes()[1]);
        }

        @Override
        public final PermissionRoleFilter getPermissionRoleFilter() {
            final PermissionRole owner = PermissionRole.getPermissionRoleByName("Owner");
            final PermissionRole bot_commander = PermissionRole.getPermissionRoleByName("Bot_Commander");
            return (role, member) -> {
                if (role.isThisHigherOrEqual(owner) || role.isThisEqual(bot_commander)) {
                    return true;
                }
                return Standard.isSuperOwner(member);
            };
        }

        private final void restart() {
            SupremeBot.restartJDA(false);
        }

        private final EmbedBuilder getRestartingMessage(MessageReceivedEvent event, double delayInSeconds, int value) {
            long rest = ((long) (delayInSeconds + 0.5)) - value;
            return Standard.getMessageEmbed(Color.YELLOW, "%s is restarting me in %d second%s!", event.getAuthor().getAsMention(), rest, (rest == 1 ? "" : "s"));
        }

    }

    public static class GetFileCommand implements Command {

        @Override
        public final String[] getInvokes() {
            return new String[]{"getFile"};
        }

        @Override
        public final boolean called(String invoke, ArgumentList arguments, MessageReceivedEvent event) {
            return arguments != null && arguments.isSize(1, 2);
        }

        @Override
        public final void action(String invoke, ArgumentList arguments, MessageReceivedEvent event) {
            final File file = new File(arguments.get(0));
            if (file.exists() && file.isFile()) {
                final Message message = new MessageBuilder().appendFormat("%s here is your requested file:", event.getAuthor().getAsMention()).build();
                if (arguments.size() == 2) {
                    event.getTextChannel().sendFile(file, arguments.get(1), message).queue();
                } else {
                    event.getTextChannel().sendFile(file, message).queue();
                }
            } else {
                event.getTextChannel().sendMessageFormat(":warning: Sorry, %s the file \"%s\" wasn't found!", event.getAuthor(), arguments.get(0)).queue();
            }
        }

        @Override
        public final void executed(boolean success, MessageReceivedEvent event) {
            System.out.println("[INFO] Command '" + getInvokes()[0] + "' was executed!");
        }

        @Override
        public final String getHelp() {
            return String.format("%n`%s <File Path> [Visible File Name]`", getInvokes()[0]);
        }

        @Override
        public final PermissionRoleFilter getPermissionRoleFilter() {
            final PermissionRole admin = PermissionRole.getPermissionRoleByName("Admin");
            return (role, member) -> role.isThisHigherOrEqual(admin);
        }

    }

    public static class SayCommand implements Command {

        @Override
        public final String[] getInvokes() {
            return new String[]{"say"};
        }

        @Override
        public final boolean called(String invoke, ArgumentList arguments, MessageReceivedEvent event) {
            return true;
        }

        @Override
        public final void action(String invoke, ArgumentList arguments, MessageReceivedEvent event) {
            event.getTextChannel().sendMessage("Say!").queue();
        }

        @Override
        public final void executed(boolean success, MessageReceivedEvent event) {
            System.out.println("[INFO] Command '" + getInvokes()[0] + "' was executed!");
        }

        @Override
        public final String getHelp() {
            return null;
        }

        @Override
        public final PermissionRoleFilter getPermissionRoleFilter() {
            return null;
        }

    }

    public static class ClearCommand implements Command {

        @Override
        public final String[] getInvokes() {
            return new String[]{"clear"};
        }

        @Override
        public final boolean called(String invoke, ArgumentList arguments, MessageReceivedEvent event) {
            return arguments == null || arguments.isSize(0, 1);
        }

        @Override
        public final void action(String invoke, ArgumentList arguments, MessageReceivedEvent event) {
            int clearLines = -1;
            if (arguments != null && arguments.size() >= 1) {
                try {
                    clearLines = Integer.parseInt(arguments.get(0));
                } catch (Exception ex) {
                    clearLines = -1;
                }
            }
            if (clearLines == -1) {
                clearLines = Standard.STANDARD_NUMBER_OF_LINES_TO_GET_CLEARED;
            }
            clearLines %= 101;
            if (clearLines >= 1) {
                try {
                    final MessageHistory history = new MessageHistory(event.getTextChannel());
                    if (clearLines > 1) {
                        event.getMessage().delete().queue();
                    } else {
                        clearLines++;
                    }
                    final List<Message> messagesToDelete = history.retrievePast(clearLines).complete();
                    event.getTextChannel().deleteMessages(messagesToDelete).queue();
                    final Message message = event.getTextChannel().sendMessage(Standard.getMessageEmbed(Color.GREEN, "Deleted %d messages!", clearLines).build()).complete();
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            message.delete().queue();
                        }
                    }, 3000);
                } catch (Exception ex) {
                    System.err.println(ex);
                    ex.printStackTrace();
                }
            } else {
                event.getTextChannel().sendMessageFormat(":warning: Sorry, %s you need to delete at least 1 messages!", event.getAuthor()).queue();
            }
        }

        @Override
        public final void executed(boolean success, MessageReceivedEvent event) {
            System.out.println("[INFO] Command '" + getInvokes()[0] + "' was executed!");
        }

        @Override
        public final String getHelp() {
            return "[Number of Lines to get cleared (-1 = Standard (" + Standard.STANDARD_NUMBER_OF_LINES_TO_GET_CLEARED + "))]";
        }

        @Override
        public final PermissionRoleFilter getPermissionRoleFilter() {
            final PermissionRole admin = PermissionRole.getPermissionRoleByName("Admin");
            return (role, member) -> role.isThisHigherOrEqual(admin);
        }

    }

}
