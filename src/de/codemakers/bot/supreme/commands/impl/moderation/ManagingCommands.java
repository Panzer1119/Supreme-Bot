package de.codemakers.bot.supreme.commands.impl.moderation;

import de.codemakers.bot.supreme.commands.Command;
import de.codemakers.bot.supreme.commands.arguments.ArgumentConsumeType;
import de.codemakers.bot.supreme.commands.arguments.ArgumentList;
import de.codemakers.bot.supreme.commands.arguments.Invoker;
import de.codemakers.bot.supreme.core.SupremeBot;
import static de.codemakers.bot.supreme.core.SupremeBot.stopCompletely;
import de.codemakers.bot.supreme.entities.MessageEvent;
import de.codemakers.bot.supreme.permission.PermissionHandler;
import de.codemakers.bot.supreme.permission.PermissionRole;
import de.codemakers.bot.supreme.permission.PermissionRoleFilter;
import de.codemakers.bot.supreme.util.IntegerHolder;
import de.codemakers.bot.supreme.util.Standard;
import de.codemakers.bot.supreme.util.Timer;
import de.codemakers.bot.supreme.util.Util;
import java.awt.Color;
import java.io.File;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageHistory;

/**
 *
 * @author Panzer1119
 */
public class ManagingCommands {

    public static class CommandPrefixChangeCommand extends Command {

        @Override
        public final void initInvokers() {
            addInvokers(Invoker.createInvoker("changeCommandPrefix", this), Invoker.createInvoker("getCommandPrefix", this));
        }

        @Override
        public final boolean called(Invoker invoker, ArgumentList arguments, MessageEvent event) {
            final boolean global = (arguments == null ? false : arguments.isConsumed(Standard.ARGUMENT_GLOBAL, ArgumentConsumeType.ALL_IGNORE_CASE));
            switch (invoker.getInvoker()) {
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
        public final void action(Invoker invoker, ArgumentList arguments, MessageEvent event) {
            final boolean global = arguments.isConsumed(Standard.ARGUMENT_GLOBAL, ArgumentConsumeType.CONSUME_ALL_IGNORE_CASE);
            if (!global && arguments.isSize(2, -1)) {
                return;
            }
            switch (invoker.getInvoker()) {
                case "changeCommandPrefix":
                    final String commandPrefix = arguments.consumeFirst();
                    if (commandPrefix == null) {
                        break;
                    }
                    if (global) {
                        if (Standard.setStandardCommandPrefix(commandPrefix)) {
                            event.sendMessageFormat("Changed Global Command Prefix to \"%s\"", commandPrefix);
                        } else {
                            event.sendMessageFormat("Global Command Prefix wasn't changed, it's still \"%s\"", Standard.getStandardCommandPrefix());
                        }
                    } else if (Standard.setCommandPrefixForGuild(event.getGuild(), commandPrefix)) {
                        event.sendMessageFormat("Changed Command Prefix to \"%s\"", commandPrefix);
                    } else {
                        event.sendMessageFormat("Command Prefix wasn't changed, it's still \"%s\"", Standard.getCommandPrefixByGuild(event.getGuild()));
                    }
                    break;
                case "getCommandPrefix":
                    if (global) {
                        event.sendMessageFormat("Global Command Prefix is \"%s\"", Standard.getStandardCommandPrefix());
                    } else {
                        event.sendMessageFormat("Command Prefix for this Guild is \"%s\"", Standard.getCommandPrefixByGuild(event.getGuild()));
                    }
                    break;
            }
        }

        @Override
        public final void executed(boolean success, MessageEvent event) {
            System.out.println("[INFO] Command '" + getCommandID() + "' was executed!");
        }

        @Override
        public final EmbedBuilder getHelp(Invoker invoker, EmbedBuilder builder) { //FIXME Ja was ist wenn ein Command 2 UNTERSCHIEDLICHE Funktionen hat??? Das darf eingetlich dann nicht sein!
            if (invoker == getInvokers().get(0)) {
                builder.addField(String.format("%s <New Command Prefix> [%s]", getInvokers().get(0), Standard.ARGUMENT_GLOBAL.getCompleteArgument(0)), String.format("Sets the command prefix for this guild or with the flag \"%s\" the global standard command prefix.", Standard.ARGUMENT_GLOBAL.getCompleteArgument(0)), false);
            } else if (invoker == getInvokers().get(1)) {
                builder.addField(String.format("%s [%s]", getInvokers().get(1), Standard.ARGUMENT_GLOBAL.getCompleteArgument(0)), String.format("Returns the command prefix for this guild or with the flag \"%s\" the global standard command prefix.", Standard.ARGUMENT_GLOBAL.getCompleteArgument(0)), false);
            }
            return builder;
        }

        @Override
        public final PermissionRoleFilter getPermissionRoleFilter() {
            final PermissionRole admin = PermissionRole.getPermissionRoleByName("Admin");
            return (role, member) -> role.isThisHigherOrEqual(admin);
        }

        @Override
        public final String getCommandID() {
            return getClass().getName();
        }

    }

    private static abstract class AdministrativeCommands extends Command {

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

        protected final EmbedBuilder getRestartingMessage(MessageEvent event, double delayInSeconds, int value) {
            long rest = ((long) (delayInSeconds + 0.5)) - value;
            return Standard.getMessageEmbed(Color.YELLOW, "%s is restarting me in %d second%s!", event.getAuthor().getAsMention(), rest, (rest == 1 ? "" : "s"));
        }

        protected final EmbedBuilder getStoppingMessage(MessageEvent event, double delayInSeconds, int value) {
            long rest = ((long) (delayInSeconds + 0.5)) - value;
            return Standard.getMessageEmbed(Color.YELLOW, "%s is stopping me in %d second%s!", event.getAuthor().getAsMention(), rest, (rest == 1 ? "" : "s"));
        }

    }

    public static class StopCommand extends AdministrativeCommands {

        @Override
        public final void initInvokers() {
            addInvokers(Invoker.createInvoker("stop", this), Invoker.createInvoker("shutdown", this));
        }

        @Override
        public final boolean called(Invoker invoker, ArgumentList arguments, MessageEvent event) {
            return arguments == null || arguments.isSize(0, 1);
        }

        @Override
        public final void action(Invoker invoker, ArgumentList arguments, MessageEvent event) {
            if (arguments != null && arguments.size() >= 1) {
                try {
                    final double delayStopInSeconds = Double.parseDouble(arguments.consumeFirst());
                    final Message message = event.sendAndWaitMessage(getRestartingMessage(event, delayStopInSeconds, 0).build());
                    final Timer timer_1 = Util.createTimer();
                    final Timer timer_2 = Util.createTimer();
                    final IntegerHolder i = new IntegerHolder();
                    final Runnable run_1 = () -> {
                        message.editMessage(getStoppingMessage(event, delayStopInSeconds, i.value).build()).queue();
                        i.value++;
                        if (i.value >= delayStopInSeconds) {
                            timer_1.purge();
                        }
                    };
                    final Runnable run_2 = () -> {
                        timer_1.purge();
                        //run_1.cancel(); //FIXME?!??!?!
                        message.editMessage(Standard.getMessageEmbed(Color.YELLOW, "%s stopped me!", event.getAuthor().getAsMention()).build()).queue(); //FIXME Das funzt immer noch nicht
                        final Timer timer_3 = Util.createTimer();
                        final Runnable run_3 = () -> {
                            stopCompletely(0);
                        };
                        Util.sheduleTimerAndRemove(run_3, 1000, timer_3);
                    };
                    Util.sheduleTimerAtFixedRateAndRemove(run_1, 0, 1000, timer_1);
                    Util.sheduleTimerAndRemove(run_2, (long) (delayStopInSeconds * 1000.0 + 0.5), timer_2);
                    return;
                } catch (Exception ex) {
                }
            }
            try {
                event.sendMessage(Standard.getMessageEmbed(Color.YELLOW, "%s stopped me!", event.getAuthor().getAsMention()).build());
                stopCompletely(0);
            } catch (Exception ex) {
                stopCompletely(-1);
            }
        }

        @Override
        public final void executed(boolean success, MessageEvent event) {
            System.out.println("[INFO] Command '" + getCommandID() + "' was executed!");
        }

        @Override
        public final EmbedBuilder getHelp(Invoker invoker, EmbedBuilder builder) {
            builder.addField(invoker + " [Delay]", "Stops the bot immediately or after the given delay in seconds.", false);
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

        @Override
        public final String getCommandID() {
            return getClass().getName();
        }

    }

    public static class RestartCommand extends AdministrativeCommands {

        @Override
        public final void initInvokers() {
            addInvokers(Invoker.createInvoker("restart", this), Invoker.createInvoker("reboot", this));
        }

        @Override
        public final boolean called(Invoker invoker, ArgumentList arguments, MessageEvent event) {
            return arguments == null || arguments.isSize(0, 2);
        }

        @Override
        public final void action(Invoker invoker, ArgumentList arguments, MessageEvent event) {
            if (arguments != null && arguments.size() >= 1) {
                try {
                    final double delayStopInSeconds = Double.parseDouble(arguments.consumeFirst());
                    double delayStartInSeconds_temp = -1;
                    if (!arguments.isEmpty()) {
                        delayStartInSeconds_temp = Double.parseDouble(arguments.consumeFirst());
                    }
                    final double delayStartInSeconds = delayStartInSeconds_temp;
                    final Message message = event.sendAndWaitMessage(getRestartingMessage(event, delayStopInSeconds, 0).build());
                    final Timer timer_1 = Util.createTimer();
                    final Timer timer_2 = Util.createTimer();
                    final IntegerHolder i = new IntegerHolder();
                    final Runnable run_1 = () -> {
                        message.editMessage(getStoppingMessage(event, delayStopInSeconds, i.value).build()).queue();
                        i.value++;
                        if (i.value >= delayStopInSeconds) {
                            timer_1.purge();
                        }
                    };
                    final Runnable run_2 = () -> {
                        timer_1.purge();
                        //run_1.cancel(); //FIXME?!??!?!
                        message.editMessage(Standard.getMessageEmbed(Color.YELLOW, "%s restarting me!", event.getAuthor().getAsMention()).build()).queue();
                        if (delayStartInSeconds == -1) {
                            restart();
                        } else {
                            stop();
                            final Timer timer_3 = Util.createTimer();
                            final Runnable run_3 = () -> {
                                start();
                            };
                            Util.sheduleTimerAndRemove(run_3, (long) (delayStartInSeconds * 1000.0 + 0.5), timer_3);
                        }
                    };
                    Util.sheduleTimerAtFixedRateAndRemove(run_1, 0, 1000, timer_1);
                    Util.sheduleTimerAndRemove(run_2, (long) (delayStopInSeconds * 1000.0 + 0.5), timer_2);
                    return;
                } catch (Exception ex) {
                }
            }
            try {
                event.sendMessage(Standard.getMessageEmbed(Color.YELLOW, "%s restarted me!", event.getAuthor().getAsMention()).build());
                restart();
            } catch (Exception ex) {
            }
        }

        @Override
        public final void executed(boolean success, MessageEvent event) {
            System.out.println("[INFO] Command '" + getCommandID() + "' was executed!");
        }

        @Override
        public final EmbedBuilder getHelp(Invoker invoker, EmbedBuilder builder) {
            builder.addField(invoker + " [Delay 1] [Delay 2]", "Restarts the bot immediately or after the first given delay in seconds. The second delay is the time the bot should wait before starting again.", false);
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

        @Override
        public final String getCommandID() {
            return getClass().getName();
        }

    }

    public static class GetFileCommand extends Command { //TODO Einen UploadFileCommand machen, mit dem man files auf den Bot hochladen kann, um zum Beispiel die settings.txt oder permissions.txt zu ueberschreiben
        
        @Override
        public final void initInvokers() {
            addInvokers(Invoker.createInvoker("getFile", this));
        }

        @Override
        public final boolean called(Invoker invoker, ArgumentList arguments, MessageEvent event) {
            return arguments != null && arguments.isSize(1, 2);
        }

        @Override
        public final void action(Invoker invoker, ArgumentList arguments, MessageEvent event) {
            final File file = new File(arguments.get(0));
            if (file.exists() && file.isFile()) {
                final Message message = new MessageBuilder().appendFormat("%s here is your requested file:", event.getAuthor().getAsMention()).build();
                if (arguments.size() == 2) {
                    event.sendFile(file, arguments.get(1), message);
                } else {
                    event.sendFile(file, message);
                }
            } else {
                event.sendMessageFormat(":warning: Sorry, %s the file \"%s\" wasn't found!", event.getAuthor(), arguments.get(0));
            }
        }

        @Override
        public final void executed(boolean success, MessageEvent event) {
            System.out.println("[INFO] Command '" + getCommandID() + "' was executed!");
        }

        @Override
        public final EmbedBuilder getHelp(Invoker invoker, EmbedBuilder builder) {
            builder.addField(invoker + " <File Path> [Visible File Name]", "Uploads a file from the bot to the current channel with optionally custom filename.", false);
            return builder;
        }

        @Override
        public final PermissionRoleFilter getPermissionRoleFilter() {
            final PermissionRole admin = PermissionRole.getPermissionRoleByName("Admin");
            return (role, member) -> role.isThisHigherOrEqual(admin);
        }

        @Override
        public final String getCommandID() {
            return getClass().getName();
        }

    }

    public static class SayCommand extends Command {

        @Override
        public final void initInvokers() {
            addInvokers(Invoker.createInvoker("say", this));
        }

        @Override
        public final boolean called(Invoker invoker, ArgumentList arguments, MessageEvent event) {
            return true;
        }

        @Override
        public final void action(Invoker invoker, ArgumentList arguments, MessageEvent event) {
            event.sendMessage(arguments.toString());
        }

        @Override
        public final void executed(boolean success, MessageEvent event) {
            System.out.println("[INFO] Command '" + getCommandID() + "' was executed!");
        }

        @Override
        public final EmbedBuilder getHelp(Invoker invoker, EmbedBuilder builder) {
            builder.addField(invoker + "", "The bot says what you said.", false);
            return builder;
        }

        @Override
        public final PermissionRoleFilter getPermissionRoleFilter() {
            return null;
        }

        @Override
        public final String getCommandID() {
            return getClass().getName();
        }
        
    }

    public static class ClearCommand extends Command {

        @Override
        public final void initInvokers() {
            addInvokers(Invoker.createInvoker("clear", this));
        }

        @Override
        public final boolean called(Invoker invoker, ArgumentList arguments, MessageEvent event) {
            return (arguments == null || arguments.isSize(0, 1)) && !event.isPrivate();
        }

        @Override
        public final void action(Invoker invoker, ArgumentList arguments, MessageEvent event) {
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
                    final MessageHistory history = new MessageHistory(event.getMessageChannel());
                    if (clearLines > 1) {
                        event.getMessage().delete().queue();
                    } else {
                        clearLines++;
                    }
                    history.retrievePast(clearLines).complete().stream().forEach((message) -> {
                        event.getMessageChannel().deleteMessageById(message.getId()).queue();
                    });
                    final Message message = event.sendAndWaitMessage(Standard.getMessageEmbed(Color.GREEN, "Deleted %d messages!", clearLines).build());
                    Util.sheduleTimerAndRemove(() -> {
                        message.delete().queue();
                    }, 3000);
                } catch (Exception ex) {
                    System.err.println(ex);
                    ex.printStackTrace();
                }
            } else {
                event.sendMessageFormat(":warning: Sorry, %s you need to delete at least 1 messages!", event.getAuthor());
            }
        }

        @Override
        public final void executed(boolean success, MessageEvent event) {
            System.out.println("[INFO] Command '" + getCommandID() + "' was executed!");
        }

        @Override
        public final EmbedBuilder getHelp(Invoker invoker, EmbedBuilder builder) {
            builder.addField(invoker + " [Number of Lines]", String.format("Clears the last %d lines, or the last 1 to 100 lines, in the current (not private!) channel.", Standard.STANDARD_NUMBER_OF_LINES_TO_GET_CLEARED), false);
            return builder;
        }

        @Override
        public final PermissionRoleFilter getPermissionRoleFilter() {
            final PermissionRole admin = PermissionRole.getPermissionRoleByName("Admin");
            return (role, member) -> role.isThisHigherOrEqual(admin);
        }

        @Override
        public final String getCommandID() {
            return getClass().getName();
        }
        
    }

    public static class ReloadCommand extends Command {

        @Override
        public final void initInvokers() {
            addInvokers(Invoker.createInvoker("reload", this));
        }

        @Override
        public final boolean called(Invoker invoker, ArgumentList arguments, MessageEvent event) {
            return true;
        }

        @Override
        public final void action(Invoker invoker, ArgumentList arguments, MessageEvent event) {
            if (arguments != null && arguments.size() == 2) {
                if (arguments.consumeFirst(Standard.ARGUMENT_GUILD_SETTINGS, ArgumentConsumeType.FIRST_IGNORE_CASE)) {
                    if (!arguments.consume(Standard.ARGUMENT_ALL, ArgumentConsumeType.FIRST_IGNORE_CASE, 1) && !arguments.consume(Standard.ARGUMENT_SETTINGS, ArgumentConsumeType.FIRST_IGNORE_CASE, 1) && !arguments.consume(Standard.ARGUMENT_PERMISSIONS, ArgumentConsumeType.FIRST_IGNORE_CASE, 1)) {
                        arguments.consume(Standard.ARGUMENT_GUILD_SETTINGS, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
                        final String guild_id = Standard.resolveGuildId(event.getGuild(), arguments.consumeFirst());
                        if (guild_id != null) {
                            SupremeBot.reloadGuildSettings(guild_id);
                            event.sendMessage(Standard.getMessageEmbed(Color.YELLOW, "%s reloaded %s for %s (ID: %s)!", event.getAuthor().getAsMention(), Standard.ARGUMENT_GUILD_SETTINGS.getArgument(), Standard.getGuildById(guild_id).getName(), guild_id).build());
                            return;
                        }
                    }
                }
            }
            if (arguments != null && arguments.size() >= 1) {
                while (arguments.hasArguments()) {
                    if (arguments.consumeFirst(Standard.ARGUMENT_ALL, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE)) {
                        SupremeBot.reload();
                        event.sendMessage(Standard.getMessageEmbed(Color.YELLOW, "%s reloaded %s!", event.getAuthor().getAsMention(), Standard.ARGUMENT_ALL.getArgument()).build());
                    } else if (arguments.consumeFirst(Standard.ARGUMENT_SETTINGS, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE)) {
                        SupremeBot.reloadSettings();
                        event.sendMessage(Standard.getMessageEmbed(Color.YELLOW, "%s reloaded %s!", event.getAuthor().getAsMention(), Standard.ARGUMENT_SETTINGS.getArgument()).build());
                    } else if (arguments.consumeFirst(Standard.ARGUMENT_PERMISSIONS, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE)) {
                        SupremeBot.reloadPermissions();
                        event.sendMessage(Standard.getMessageEmbed(Color.YELLOW, "%s reloaded %s!", event.getAuthor().getAsMention(), Standard.ARGUMENT_PERMISSIONS.getArgument()).build());
                    } else if (arguments.consumeFirst(Standard.ARGUMENT_GUILD_SETTINGS, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE)) {
                        SupremeBot.reloadGuildSettings();
                        event.sendMessage(Standard.getMessageEmbed(Color.YELLOW, "%s reloaded %s!", event.getAuthor().getAsMention(), Standard.ARGUMENT_GUILD_SETTINGS.getArgument()).build());
                    }
                }
            } else {
                SupremeBot.reload();
                event.sendMessage(Standard.getMessageEmbed(Color.YELLOW, "%s reloaded all!", event.getAuthor().getAsMention()).build());
            }
        }

        @Override
        public final void executed(boolean success, MessageEvent event) {
            System.out.println("[INFO] Command '" + getCommandID() + "' was executed!");
        }

        @Override
        public final EmbedBuilder getHelp(Invoker invoker, EmbedBuilder builder) {
            builder.addField(invoker + " [Tag 1] [Tag 2] [Tag 3]...", "Reloads everything or all given tags.", false);
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

        @Override
        public final String getCommandID() {
            return getClass().getName();
        }

    }

    public static class SettingsCommand extends Command {

        @Override
        public void initInvokers() {
            addInvokers(Invoker.createInvoker("settings", this), Invoker.createInvoker("s", this));
        }

        @Override
        public boolean called(Invoker invoker, ArgumentList arguments, MessageEvent event) {
            if (arguments == null || arguments.isEmpty()) {
                return false;
            }
            final boolean set = arguments.isConsumed(Standard.ARGUMENT_SETTINGS_SET, ArgumentConsumeType.FIRST_IGNORE_CASE);
            final boolean get = arguments.isConsumed(Standard.ARGUMENT_SETTINGS_GET, ArgumentConsumeType.FIRST_IGNORE_CASE);
            final boolean get_default = arguments.consume(Standard.ARGUMENT_SETTINGS_DEFAULT, ArgumentConsumeType.FIRST_IGNORE_CASE, 3);
            final boolean remove = arguments.isConsumed(Standard.ARGUMENT_SETTINGS_REMOVE, ArgumentConsumeType.FIRST_IGNORE_CASE);
            final boolean list = arguments.isConsumed(Standard.ARGUMENT_SETTINGS_LIST, ArgumentConsumeType.FIRST_IGNORE_CASE);
            if (set) {
                return arguments.isSize(3, 4);
            } else if (get && !get_default) {
                return arguments.isSize(2, 3);
            } else if (get && get_default) {
                return arguments.isSize(4, 5);
            } else if (remove) {
                return arguments.isSize(2, 3);
            } else if (list) {
                return arguments.isSize(1, 2);
            } else {
                return false;
            }
        }

        @Override
        public void action(Invoker invoker, ArgumentList arguments, MessageEvent event) {
            final boolean set = arguments.isConsumed(Standard.ARGUMENT_SETTINGS_SET, ArgumentConsumeType.CONSUME_ALL_IGNORE_CASE);
            final boolean get = arguments.isConsumed(Standard.ARGUMENT_SETTINGS_GET, ArgumentConsumeType.CONSUME_ALL_IGNORE_CASE);
            final boolean remove = arguments.isConsumed(Standard.ARGUMENT_SETTINGS_REMOVE, ArgumentConsumeType.CONSUME_ALL_IGNORE_CASE);
            final boolean list = arguments.isConsumed(Standard.ARGUMENT_SETTINGS_LIST, ArgumentConsumeType.CONSUME_ALL_IGNORE_CASE);
            String guild_id = null;
            String key = "";
            String value = null;
            String value_temp = null;
            boolean sendPrivate = false;
            if (set) {
                if (arguments.isSize(3)) {
                    guild_id = Standard.resolveGuildId(event.getGuild(), arguments.consumeFirst());
                }
                key = arguments.consumeFirst();
                if (Util.contains(Standard.ULTRA_FORBIDDEN, key) && !Standard.isSuperOwner(event.getAuthor())) {
                    PermissionHandler.sendNoPermissionMessage(event);
                    return;
                } else if (Standard.isSuperOwner(event.getAuthor())) {
                    sendPrivate = true;
                }
                value = arguments.consumeFirst();
                if (guild_id == null) {
                    value_temp = Standard.STANDARD_SETTINGS.getProperty(key, null);
                    Standard.STANDARD_SETTINGS.setProperty(key, value);
                    Standard.reloadSettings();
                    final MessageEmbed message = Standard.getMessageEmbed(Color.YELLOW, event.getAuthor().getAsMention() + " set").addField(key + " old:", "" + value_temp, false).addField(key + " new:", "" + value, false).build();
                    if (!sendPrivate) {
                        event.sendMessage(message);
                    } else {
                        Util.sendPrivateMessage(event.getAuthor(), message);
                    }
                } else {
                    value_temp = Standard.getGuildSettings(guild_id).getProperty(key, null);
                    Standard.getGuildSettings(guild_id).setProperty(key, value);
                    final MessageEmbed message = Standard.getMessageEmbed(Color.YELLOW, "%s %s (ID: %s) set", event.getAuthor().getAsMention(), Standard.getGuildById(guild_id).getName(), guild_id).addField(key + " old:", "" + value_temp, false).addField(key + " new:", "" + value, false).build();
                    if (!sendPrivate) {
                        event.sendMessage(message);
                    } else {
                        Util.sendPrivateMessage(event.getAuthor(), message);
                    }
                }
            } else if (get) {
                final boolean get_default = arguments.consume(Standard.ARGUMENT_SETTINGS_DEFAULT, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE, 2);
                if (arguments.isSize(2) || (get_default && arguments.isSize(3))) {
                    guild_id = Standard.resolveGuildId(event.getGuild(), arguments.consumeFirst());
                }
                key = arguments.consumeFirst();
                if (get_default && arguments.isSize(1)) {
                    value_temp = arguments.consumeFirst();
                }
                if (Util.contains(Standard.ULTRA_FORBIDDEN, key) && !Standard.isSuperOwner(event.getAuthor())) {
                    PermissionHandler.sendNoPermissionMessage(event);
                    return;
                } else if (Standard.isSuperOwner(event.getAuthor())) {
                    sendPrivate = true;
                }
                if (guild_id == null) {
                    value = Standard.STANDARD_SETTINGS.getProperty(key, value_temp);
                    final MessageEmbed message = Standard.getMessageEmbed(Color.YELLOW, event.getAuthor().getAsMention() + " get").addField("" + key, "" + value, false).build();
                    if (!sendPrivate) {
                        event.sendMessage(message);
                    } else {
                        Util.sendPrivateMessage(event.getAuthor(), message);
                    }
                } else {
                    value = Standard.getGuildSettings(guild_id).getProperty(key, value_temp);
                    final MessageEmbed message = Standard.getMessageEmbed(Color.YELLOW, "%s %s (ID: %s) get", event.getAuthor().getAsMention(), Standard.getGuildById(guild_id).getName(), guild_id).addField("" + key, "" + value, false).build();
                    if (!sendPrivate) {
                        event.sendMessage(message);
                    } else {
                        Util.sendPrivateMessage(event.getAuthor(), message);
                    }
                }
            } else if (remove) {
                if (arguments.isSize(2)) {
                    guild_id = Standard.resolveGuildId(event.getGuild(), arguments.consumeFirst());
                }
                key = arguments.consumeFirst();
                if (Util.contains(Standard.ULTRA_FORBIDDEN, key)) {
                    PermissionHandler.sendNoPermissionMessage(event);
                    return;
                }
                if (guild_id == null) {
                    value = Standard.STANDARD_SETTINGS.getProperty(key, null);
                    Standard.STANDARD_SETTINGS.removeProperty(key);
                    event.sendMessage(Standard.getMessageEmbed(Color.YELLOW, event.getAuthor().getAsMention() + " removed").addField("" + key, "" + value, false).build());
                } else {
                    value = Standard.getGuildSettings(guild_id).getProperty(key, null);
                    Standard.getGuildSettings(guild_id).removeProperty(key);
                    event.sendMessage(Standard.getMessageEmbed(Color.YELLOW, "%s %s (ID: %s) removed", event.getAuthor().getAsMention(), Standard.getGuildById(guild_id).getName(), guild_id).addField("" + key, "" + value, false).build());
                }
            } else if (list) {
                if (arguments.isSize(1)) {
                    guild_id = Standard.resolveGuildId(event.getGuild(), arguments.consumeFirst());
                }
                if (guild_id == null) {
                    event.sendMessage(Standard.STANDARD_SETTINGS.toEmbed(new EmbedBuilder().setDescription(event.getAuthor().getAsMention() + " list")).build());
                } else {
                    event.sendMessage(Standard.getGuildSettings(guild_id).toEmbed(new EmbedBuilder().setDescription(event.getAuthor().getAsMention() + " list")).build());
                }
            }
        }

        @Override
        public void executed(boolean success, MessageEvent event) {
            System.out.println("[INFO] Command '" + getCommandID() + "' was executed!");
        }

        @Override
        public EmbedBuilder getHelp(Invoker invoker, EmbedBuilder builder) {
            builder.addField(String.format("%s %s [Guild ID] <Key> <Value>", invoker, Standard.ARGUMENT_SETTINGS_SET.getCompleteArgument(0)), "Sets the value for the key. If a valid guild id is given, then the guild settings will be edited.", false);
            builder.addField(String.format("%s %s [Guild ID] <Key> [%s Default Value]", invoker, Standard.ARGUMENT_SETTINGS_GET.getCompleteArgument(0), Standard.ARGUMENT_SETTINGS_DEFAULT.getCompleteArgument(0)), "Gets the value for the key. If a valid guild id is given, then the guild settings will be edited.", false);
            builder.addField(String.format("%s %s [Guild ID] <Key>", invoker, Standard.ARGUMENT_SETTINGS_REMOVE.getCompleteArgument(0)), "Removes the key and value. If a valid guild id is given, then the guild settings will be edited.", false);
            builder.addField(String.format("%s %s [Guild ID]", invoker, Standard.ARGUMENT_SETTINGS_LIST.getCompleteArgument(0)), "Lists all keys and values. If a valid guild id is given, then the guild settings will be edited.", false);
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

        @Override
        public String getCommandID() {
            return getClass().getName();
        }

    }

    //TODO Info command hinzufuegen (der vielleicht noch je nach permission level mehr informationen anzeigt (ueber einen channel/member(/user)))
}