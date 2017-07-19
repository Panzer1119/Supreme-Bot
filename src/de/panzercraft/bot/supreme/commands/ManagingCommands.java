package de.panzercraft.bot.supreme.commands;

import de.panzercraft.bot.supreme.permission.PermissionRole;
import de.panzercraft.bot.supreme.util.Standard;
import de.panzercraft.bot.supreme.util.Util;
import java.awt.Color;
import java.io.File;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
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
            return new String[] {"changeCommandPrefix", "getCommandPrefix"};
        }

        @Override
        public final boolean called(String invoke, String[] args, MessageReceivedEvent event) {
            switch (invoke) {
                case "changeCommandPrefix":
                    return args != null && (args.length == 1 || args.length == 2);
                case "getCommandPrefix":
                    return true;
            }
            return false;
        }

        @Override
        public final void action(String invoke, String[] args, MessageReceivedEvent event) {
            int index = Util.indexOf(args, "global");
            switch (invoke) {
                case "changeCommandPrefix":
                    String temp = args[0];
                    if (args.length == 2 && index != -1) {
                        if (index == 0) {
                            temp = args[1];
                        }
                        if (Standard.setStandardCommandPrefix(temp)) {
                            event.getTextChannel().sendMessageFormat("Changed Gloval Command Prefix to \"%s\"", temp).queue();
                        } else {
                            event.getTextChannel().sendMessageFormat("Global Command Prefix wasn't changed, it's still \"%s\"", Standard.getStandardCommandPrefix()).queue();
                        }
                    } else {
                        if (Standard.setCommandPrefixForGuild(event.getGuild(), temp)) {
                            event.getTextChannel().sendMessageFormat("Changed Command Prefix to \"%s\"", temp).queue();
                        } else {
                            event.getTextChannel().sendMessageFormat("Command Prefix wasn't changed, it's still \"%s\"", Standard.getCommandPrefixByGuild(event.getGuild())).queue();
                        }
                    }
                    break;
                case "getCommandPrefix":
                    if (index != -1) {
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
        public final PermissionRole getMinimumPermissionRole() {
            return PermissionRole.getPermissionRoleByName("Admin");
        }
        
    }
    
    public static class StopCommand implements Command {

        @Override
        public final String[] getInvokes() {
            return new String[] {"stop"};
        }

        @Override
        public final boolean called(String invoke, String[] args, MessageReceivedEvent event) {
            return true;
        }

        @Override
        public final void action(String invoke, String[] args, MessageReceivedEvent event) {
            if (args != null && args.length >= 1) {
                try {
                    final double delayInSeconds = Double.parseDouble(args[0]);
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
            return null;
        }

        @Override
        public final PermissionRole getMinimumPermissionRole() {
            return PermissionRole.getPermissionRoleByName("Owner");
        }
        
    }
    
    public static class GetFileCommand implements Command {

        @Override
        public final String[] getInvokes() {
            return new String[] {"getFile"};
        }

        @Override
        public final boolean called(String invoke, String[] args, MessageReceivedEvent event) {
            return args != null && (args.length == 1 || args.length == 2);
        }

        @Override
        public final void action(String invoke, String[] args, MessageReceivedEvent event) {
            final File file = new File(args[0]);
            if (file.exists() && file.isFile()) {
                final Message message = new MessageBuilder().appendFormat("%s here is your requested file:", event.getAuthor().getAsMention()).build();
                if (args.length == 2) {
                    event.getTextChannel().sendFile(file, args[1], message).queue();
                } else {
                    event.getTextChannel().sendFile(file, message).queue();
                }
            } else {
                event.getTextChannel().sendMessageFormat(":warning: Sorry, %s the file \"%s\" wasn't found!", event.getAuthor(), args[0]).queue();
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
        public final PermissionRole getMinimumPermissionRole() {
            return PermissionRole.getPermissionRoleByName("Admin");
        }
        
    }
    
    public static class SayCommand implements Command {

        @Override
        public final String[] getInvokes() {
            return new String[] {"say"};
        }

        @Override
        public final boolean called(String invoke, String[] args, MessageReceivedEvent event) {
            return true;
        }

        @Override
        public final void action(String invoke, String[] args, MessageReceivedEvent event) {
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
        public final PermissionRole getMinimumPermissionRole() {
            return null;
        }

    }
    
    public static class ClearCommand implements Command {

        @Override
        public final String[] getInvokes() {
            return new String[] {"clear"};
        }

        @Override
        public final boolean called(String invoke, String[] args, MessageReceivedEvent event) {
            return args == null || args.length == 0 || args.length == 1;
        }

        @Override
        public final void action(String invoke, String[] args, MessageReceivedEvent event) {
            int clearLines = -1;
            if (args != null && args.length >= 1) {
                try {
                    clearLines = Integer.parseInt(args[0]);
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
        public final PermissionRole getMinimumPermissionRole() {
            return PermissionRole.getPermissionRoleByName("Admin");
        }
        
    }
    
}
