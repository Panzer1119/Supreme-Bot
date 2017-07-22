package de.codemakers.bot.supreme.commands.impl;

import de.codemakers.bot.supreme.commands.Command;
import de.codemakers.bot.supreme.commands.CommandHandler;
import de.codemakers.bot.supreme.commands.arguments.ArgumentConsumeType;
import de.codemakers.bot.supreme.commands.arguments.ArgumentList;
import de.codemakers.bot.supreme.permission.PermissionRoleFilter;
import de.codemakers.bot.supreme.util.Standard;
import de.codemakers.bot.supreme.util.Util;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;

/**
 * HelpCommand
 * 
 * @author Panzer1119
 */
public class HelpCommand implements Command {

    @Override
    public String[] getInvokes() {
        return new String[] {"help", "h"};
    }

    @Override
    public boolean called(String invoke, ArgumentList arguments, de.codemakers.bot.supreme.entities.MessageEvent event) {
        return true;
    }

    @Override
    public void action(String invoke, ArgumentList arguments, de.codemakers.bot.supreme.entities.MessageEvent event) { //TODO Das mit Seiten und so machen, wie bei der Musik Playlist....
        if (arguments != null && arguments.isSize(1, -1)) {
            final boolean sendPrivate = arguments.isConsumed(Standard.ARGUMENT_PRIVATE, ArgumentConsumeType.CONSUME_ALL_IGNORE_CASE);
            while (arguments.hasArguments()) {
                final String command_invoke = arguments.consumeFirst();
                final Command command = CommandHandler.getCommandByInvoke(command_invoke);
                if (command != null) {
                    CommandHandler.sendHelpMessage(event, command, sendPrivate);
                } else {
                    final Message message = event.sendAndWaitMessageFormat(":warning: Sorry %s, the command \"%s\" wasn't found!", event.getAuthor().getAsMention(), command_invoke);
                    final long delay = Standard.getAutoDeleteCommandNotFoundMessageDelayByGuild(event.getGuild());
                    if (delay != -1) {
                        Util.deleteMessage(message, delay);
                    }
                }
            }
        } else {
            CommandHandler.sendHelpMessage(event, this, false);
        }
    }

    @Override
    public void executed(boolean success, de.codemakers.bot.supreme.entities.MessageEvent event) {
        System.out.println("[INFO] Command '" + getInvokes()[0] + "' was executed!");
    }

    @Override
    public EmbedBuilder getHelp(EmbedBuilder builder) {
        for (String invoke : getInvokes()) {
            builder.addField(String.format("%s [%s] [Command 1] [Command 2] [Command 3]...", invoke, Standard.ARGUMENT_PRIVATE.getCompleteArgument(0)), String.format("Returns the help for the given commands. And with the flag \"%s\" the help will be send in the private chat.", Standard.ARGUMENT_PRIVATE.getCompleteArgument(0)), false);
        }
        return builder;
    }

    @Override
    public PermissionRoleFilter getPermissionRoleFilter() {
        return null;
    }
    
}
