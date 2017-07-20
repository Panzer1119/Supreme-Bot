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
                default:
                    return false;
            }
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
                            event.getTextChannel().sendMessageFormat("Changed Global Command Prefix to \"%s\"", commandPrefix).queue();
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
        public final EmbedBuilder getHelp(EmbedBuilder builder) {
            builder.addField(String.format("%s <New Command Prefix> [%s]", getInvokes()[0], Standard.ARGUMENT_GLOBAL.getCompleteArgument(0)), String.format("Sets the command prefix for this guild or with the flag \"%s\" the global standard command prefix.", Standard.ARGUMENT_GLOBAL.getCompleteArgument(0)), false);
            builder.addField(String.format("%s [%s]", getInvokes()[1], Standard.ARGUMENT_GLOBAL.getCompleteArgument(0)), String.format("Returns the command prefix for this guild or with the flag \"%s\" the global standard command prefix.", Standard.ARGUMENT_GLOBAL.getCompleteArgument(0)), false);
            return builder;
        }

        @Override
        public final PermissionRoleFilter getPermissionRoleFilter() {
            final PermissionRole admin = PermissionRole.getPermissionRoleByName("Admin");
            return (role, member) -> role.isThisHigherOrEqual(admin);
        }

    }

    private static class AdministrativeCommands {

        protected final boolean restart() {
            boolean good = false;
            if (stop()) {
                good = true;
            }
            if (!start()) {
                good = false;
            }
            return good;
        }

        protected final boolean stop() {
            return SupremeBot.stopJDA(false);
        }

        protected final boolean start() {
            return SupremeBot.startJDA();
        }

        protected final boolean stopCompletely(int status) {
            stop();
            System.exit(status);
            return true;
        }

        protected final EmbedBuilder getRestartingMessage(MessageReceivedEvent event, double delayInSeconds, int value) {
            long rest = ((long) (delayInSeconds + 0.5)) - value;
            return Standard.getMessageEmbed(Color.YELLOW, "%s is restarting me in %d second%s!", event.getAuthor().getAsMention(), rest, (rest == 1 ? "" : "s"));
        }

        protected final EmbedBuilder getStoppingMessage(MessageReceivedEvent event, double delayInSeconds, int value) {
            long rest = ((long) (delayInSeconds + 0.5)) - value;
            return Standard.getMessageEmbed(Color.YELLOW, "%s is stopping me in %d second%s!", event.getAuthor().getAsMention(), rest, (rest == 1 ? "" : "s"));
        }

    }

    public static class StopCommand extends AdministrativeCommands implements Command {

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
                    final double delayStopInSeconds = Double.parseDouble(arguments.consumeFirst());
                    final Message message = event.getTextChannel().sendMessage(getRestartingMessage(event, delayStopInSeconds, 0).build()).complete();
                    final Timer timer = new Timer();
                    final IntegerHolder i = new IntegerHolder();
                    final TimerTask timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            message.editMessage(getStoppingMessage(event, delayStopInSeconds, i.value).build()).queue();
                            i.value++;
                            if (i.value >= delayStopInSeconds) {
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
                            message.editMessage(Standard.getMessageEmbed(Color.YELLOW, "%s stopped me!", event.getAuthor().getAsMention()).build()).queue(); //FIXME Das funzt immer noch nicht
                            new Timer().schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    stopCompletely(0);
                                }
                            }, 1000);
                        }
                    }, (long) (delayStopInSeconds * 1000.0 + 0.5));
                    return;
                } catch (Exception ex) {
                }
            }
            try {
                event.getTextChannel().sendMessage(Standard.getMessageEmbed(Color.YELLOW, "%s stopped me!", event.getAuthor().getAsMention()).build()).queue();
                stopCompletely(0);
            } catch (Exception ex) {
                stopCompletely(-1);
            }
        }

        @Override
        public final void executed(boolean success, MessageReceivedEvent event) {
            System.out.println("[INFO] Command '" + getInvokes()[0] + "' was executed!");
        }

        @Override
        public final EmbedBuilder getHelp(EmbedBuilder builder) {
            for (String invoke : getInvokes()) {
                builder.addField(invoke + " [Delay]", "Stops the bot immediately or after the given delay in seconds.", false);
            }
            return builder;
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

    public static class RestartCommand extends AdministrativeCommands implements Command {

        @Override
        public final String[] getInvokes() {
            return new String[]{"restart", "reboot"};
        }

        @Override
        public final boolean called(String invoke, ArgumentList arguments, MessageReceivedEvent event) {
            return arguments == null || arguments.isSize(0, 2);
        }

        @Override
        public final void action(String invoke, ArgumentList arguments, MessageReceivedEvent event) {
            if (arguments != null && arguments.size() >= 1) {
                try {
                    final double delayStopInSeconds = Double.parseDouble(arguments.consumeFirst());
                    double delayStartInSeconds_temp = -1;
                    if (!arguments.isEmpty()) {
                        delayStartInSeconds_temp = Double.parseDouble(arguments.consumeFirst());
                    }
                    final double delayStartInSeconds = delayStartInSeconds_temp;
                    final Message message = event.getTextChannel().sendMessage(getRestartingMessage(event, delayStopInSeconds, 0).build()).complete();
                    final Timer timer = new Timer();
                    final IntegerHolder i = new IntegerHolder();
                    final TimerTask timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            message.editMessage(getRestartingMessage(event, delayStopInSeconds, i.value).build()).queue();
                            i.value++;
                            if (i.value >= delayStopInSeconds) {
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
                            message.editMessage(Standard.getMessageEmbed(Color.YELLOW, "%s restarting me!", event.getAuthor().getAsMention()).build()).queue();
                            if (delayStartInSeconds == -1) {
                                restart();
                            } else {
                                stop();
                                new Timer().schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        start();
                                    }
                                }, (long) (delayStartInSeconds * 1000.0 + 0.5));
                            }
                        }
                    }, (long) (delayStopInSeconds * 1000.0 + 0.5));
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
        public final EmbedBuilder getHelp(EmbedBuilder builder) {
            for (String invoke : getInvokes()) {
                builder.addField(invoke + " [Delay 1] [Delay 2]", "Restarts the bot immediately or after the first given delay in seconds. The second delay is the time the bot should wait before starting again.", false);
            }
            return builder;
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

    public static class GetFileCommand implements Command { //TODO Einen UploadFileCommand machen, mit dem man files auf den Bot hochladen kann, um zum Beispiel die settings.txt oder permissions.txt zu ueberschreiben

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
        public final EmbedBuilder getHelp(EmbedBuilder builder) {
            builder.addField(getInvokes()[0] + " <File Path> [Visible File Name]", "Uploads a file from the bot to the current channel with optionally custom filename.", false);
            return builder;
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
            event.getTextChannel().sendMessage(event.getMessage().getContent()).queue();
        }

        @Override
        public final void executed(boolean success, MessageReceivedEvent event) {
            System.out.println("[INFO] Command '" + getInvokes()[0] + "' was executed!");
        }

        @Override
        public final EmbedBuilder getHelp(EmbedBuilder builder) {
            builder.addField(getInvokes()[0], "Currently no function.", false);
            return builder;
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
        public final EmbedBuilder getHelp(EmbedBuilder builder) {
            for (String invoke : getInvokes()) {
                builder.addField(invoke + " [Number of Lines]", String.format("Clears the last %d lines, or the last 1 to 100 lines, in the current channel.", Standard.STANDARD_NUMBER_OF_LINES_TO_GET_CLEARED), false);
            }
            return builder;
        }

        @Override
        public final PermissionRoleFilter getPermissionRoleFilter() {
            final PermissionRole admin = PermissionRole.getPermissionRoleByName("Admin");
            return (role, member) -> role.isThisHigherOrEqual(admin);
        }

    }

    public static class ReloadCommand implements Command {

        @Override
        public final String[] getInvokes() {
            return new String[]{"reload"};
        }

        @Override
        public final boolean called(String invoke, ArgumentList arguments, MessageReceivedEvent event) {
            return true;
        }

        @Override
        public final void action(String invoke, ArgumentList arguments, MessageReceivedEvent event) {
            if (arguments != null && arguments.size() == 2) {
                if (arguments.consumeFirst(Standard.ARGUMENT_GUILD_SETTINGS, ArgumentConsumeType.FIRST_IGNORE_CASE)) {
                    if (!arguments.consume(Standard.ARGUMENT_ALL, ArgumentConsumeType.FIRST_IGNORE_CASE, 1) && !arguments.consume(Standard.ARGUMENT_SETTINGS, ArgumentConsumeType.FIRST_IGNORE_CASE, 1) && !arguments.consume(Standard.ARGUMENT_PERMISSIONS, ArgumentConsumeType.FIRST_IGNORE_CASE, 1)) {
                        arguments.consume(Standard.ARGUMENT_GUILD_SETTINGS, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
                        final String guild_id = Standard.resolveGuildId(event.getGuild(), arguments.consumeFirst());
                        if (guild_id != null) {
                            SupremeBot.reloadGuildSettings(guild_id);
                            event.getTextChannel().sendMessage(Standard.getMessageEmbed(Color.YELLOW, "%s reloaded %s for %s (ID: %s)!", event.getAuthor().getAsMention(), Standard.ARGUMENT_GUILD_SETTINGS.getArgument(), Standard.getGuildById(guild_id).getName(), guild_id).build()).queue();
                            return;
                        }
                    }
                }
            }
            if (arguments != null && arguments.size() >= 1) {
                while (arguments.hasArguments()) {
                    if (arguments.consumeFirst(Standard.ARGUMENT_ALL, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE)) {
                        SupremeBot.reload();
                        event.getTextChannel().sendMessage(Standard.getMessageEmbed(Color.YELLOW, "%s reloaded %s!", event.getAuthor().getAsMention(), Standard.ARGUMENT_ALL.getArgument()).build()).queue();
                    } else if (arguments.consumeFirst(Standard.ARGUMENT_SETTINGS, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE)) {
                        SupremeBot.reloadSettings();
                        event.getTextChannel().sendMessage(Standard.getMessageEmbed(Color.YELLOW, "%s reloaded %s!", event.getAuthor().getAsMention(), Standard.ARGUMENT_SETTINGS.getArgument()).build()).queue();
                    } else if (arguments.consumeFirst(Standard.ARGUMENT_PERMISSIONS, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE)) {
                        SupremeBot.reloadPermissions();
                        event.getTextChannel().sendMessage(Standard.getMessageEmbed(Color.YELLOW, "%s reloaded %s!", event.getAuthor().getAsMention(), Standard.ARGUMENT_PERMISSIONS.getArgument()).build()).queue();
                    } else if (arguments.consumeFirst(Standard.ARGUMENT_GUILD_SETTINGS, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE)) {
                        SupremeBot.reloadGuildSettings();
                        event.getTextChannel().sendMessage(Standard.getMessageEmbed(Color.YELLOW, "%s reloaded %s!", event.getAuthor().getAsMention(), Standard.ARGUMENT_GUILD_SETTINGS.getArgument()).build()).queue();
                    }
                }
            } else {
                SupremeBot.reload();
                event.getTextChannel().sendMessage(Standard.getMessageEmbed(Color.YELLOW, "%s reloaded all!", event.getAuthor().getAsMention()).build()).queue();
            }
        }

        @Override
        public final void executed(boolean success, MessageReceivedEvent event) {
            System.out.println("[INFO] Command '" + getInvokes()[0] + "' was executed!");
        }

        @Override
        public final EmbedBuilder getHelp(EmbedBuilder builder) {
            for (String invoke : getInvokes()) {
                builder.addField(invoke + " [Tag 1] [Tag 2] [Tag 3]...", "Reloads everything or all given tags.", false);
            }
            return builder;
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

    public static class SettingsCommand implements Command {

        @Override
        public String[] getInvokes() {
            return new String[]{"settings", "s"};
        }

        @Override
        public boolean called(String invoke, ArgumentList arguments, MessageReceivedEvent event) {
            if (arguments == null || arguments.isEmpty()) {
                return false;
            }
            final boolean set = arguments.isConsumed(Standard.ARGUMENT_SETTINGS_SET, ArgumentConsumeType.FIRST_IGNORE_CASE);
            final boolean get = arguments.isConsumed(Standard.ARGUMENT_SETTINGS_GET, ArgumentConsumeType.FIRST_IGNORE_CASE);
            final boolean remove = arguments.isConsumed(Standard.ARGUMENT_SETTINGS_REMOVE, ArgumentConsumeType.FIRST_IGNORE_CASE);
            final boolean list = arguments.isConsumed(Standard.ARGUMENT_SETTINGS_LIST, ArgumentConsumeType.FIRST_IGNORE_CASE);
            if (set) {
                return arguments.isSize(3, 4);
            } else if (get) {
                return arguments.isSize(2, 3);
            } else if (remove) {
                return arguments.isSize(2, 3);
            } else if (list) {
                return arguments.isSize(1, 2);
            } else {
                return false;
            }
        }

        @Override
        public void action(String invoke, ArgumentList arguments, MessageReceivedEvent event) {
            final boolean set = arguments.isConsumed(Standard.ARGUMENT_SETTINGS_SET, ArgumentConsumeType.CONSUME_ALL_IGNORE_CASE);
            final boolean get = arguments.isConsumed(Standard.ARGUMENT_SETTINGS_GET, ArgumentConsumeType.CONSUME_ALL_IGNORE_CASE);
            final boolean remove = arguments.isConsumed(Standard.ARGUMENT_SETTINGS_REMOVE, ArgumentConsumeType.CONSUME_ALL_IGNORE_CASE);
            final boolean list = arguments.isConsumed(Standard.ARGUMENT_SETTINGS_LIST, ArgumentConsumeType.CONSUME_ALL_IGNORE_CASE);
            String guild_id = null;
            String key = "";
            String value = null;
            if (set) {
                if (arguments.isSize(3)) {
                    guild_id = Standard.resolveGuildId(event.getGuild(), arguments.consumeFirst());
                }
                key = arguments.consumeFirst();
                value = arguments.consumeFirst();
                String value_old = null;
                if (guild_id == null) {
                    value_old = Standard.STANDARD_SETTINGS.getProperty(key, null);
                    Standard.STANDARD_SETTINGS.setProperty(key, value);
                    Standard.reloadSettings();
                    event.getTextChannel().sendMessage(Standard.getMessageEmbed(Color.YELLOW, event.getAuthor().getAsMention() + " set").addField(key + " old:", "" + value_old, false).addField(key + " new:", "" + value, false).build()).queue();
                } else {
                    value_old = Standard.getGuildSettings(guild_id).getProperty(key, null);
                    Standard.getGuildSettings(guild_id).setProperty(key, value);
                    event.getTextChannel().sendMessage(Standard.getMessageEmbed(Color.YELLOW, "%s %s (ID: %s) set", event.getAuthor().getAsMention(), Standard.getGuildById(guild_id).getName(), guild_id).addField(key + " old:", "" + value_old, false).addField(key + " new:", "" + value, false).build()).queue();
                }
            } else if (get) {
                if (arguments.isSize(2)) {
                    guild_id = Standard.resolveGuildId(event.getGuild(), arguments.consumeFirst());
                }
                key = arguments.consumeFirst();
                if (guild_id == null) {
                    value = Standard.STANDARD_SETTINGS.getProperty(key, null);
                    event.getTextChannel().sendMessage(Standard.getMessageEmbed(Color.YELLOW, event.getAuthor().getAsMention() + " get").addField("" + key, "" + value, false).build()).queue();
                } else {
                    value = Standard.getGuildSettings(guild_id).getProperty(key, null);
                    event.getTextChannel().sendMessage(Standard.getMessageEmbed(Color.YELLOW, "%s %s (ID: %s) get", event.getAuthor().getAsMention(), Standard.getGuildById(guild_id).getName(), guild_id).addField("" + key, "" + value, false).build()).queue();
                }
            } else if (remove) {
                if (arguments.isSize(2)) {
                    guild_id = Standard.resolveGuildId(event.getGuild(), arguments.consumeFirst());
                }
                key = arguments.consumeFirst();
                if (guild_id == null) {
                    value = Standard.STANDARD_SETTINGS.getProperty(key, null);
                    Standard.STANDARD_SETTINGS.removeProperty(key);
                    event.getTextChannel().sendMessage(Standard.getMessageEmbed(Color.YELLOW, event.getAuthor().getAsMention() + " removed").addField("" + key, "" + value, false).build()).queue();
                } else {
                    value = Standard.getGuildSettings(guild_id).getProperty(key, null);
                    Standard.getGuildSettings(guild_id).removeProperty(key);
                    event.getTextChannel().sendMessage(Standard.getMessageEmbed(Color.YELLOW, "%s %s (ID: %s) removed", event.getAuthor().getAsMention(), Standard.getGuildById(guild_id).getName(), guild_id).addField("" + key, "" + value, false).build()).queue();
                }
            } else if (list) {
                if (arguments.isSize(1)) {
                    guild_id = Standard.resolveGuildId(event.getGuild(), arguments.consumeFirst());
                }
                if (guild_id == null) {
                    event.getTextChannel().sendMessage(Standard.STANDARD_SETTINGS.toEmbed(new EmbedBuilder().setDescription(event.getAuthor().getAsMention() + " list")).build()).queue();
                } else {
                    event.getTextChannel().sendMessage(Standard.getGuildSettings(guild_id).toEmbed(new EmbedBuilder().setDescription(event.getAuthor().getAsMention() + " list")).build()).queue();
                }
            }
        }

        @Override
        public void executed(boolean success, MessageReceivedEvent event) {
            System.out.println("[INFO] Command '" + getInvokes()[0] + "' was executed!");
        }

        @Override
        public EmbedBuilder getHelp(EmbedBuilder builder) {
            for (String invoke : getInvokes()) {
                builder.addField(String.format("%s %s [Guild ID] <Key> <Value>", invoke, Standard.ARGUMENT_SETTINGS_SET.getCompleteArgument(0)), "Sets the value for the key. If a valid guild id is given, then the guild settings will be edited.", false);
                builder.addField(String.format("%s %s [Guild ID] <Key>", invoke, Standard.ARGUMENT_SETTINGS_GET.getCompleteArgument(0)), "Gets the value for the key. If a valid guild id is given, then the guild settings will be edited.", false);
                builder.addField(String.format("%s %s [Guild ID]", invoke, Standard.ARGUMENT_SETTINGS_LIST.getCompleteArgument(0)), "Lists all keys and values. If a valid guild id is given, then the guild settings will be edited.", false);
            }
            return builder;
        }

        @Override
        public PermissionRoleFilter getPermissionRoleFilter() {
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

    //TODO Info command hinzufuegen (der vielleicht noch je nach permission level mehr informationen anzeigt (ueber einen channel/member(/user)))
}
