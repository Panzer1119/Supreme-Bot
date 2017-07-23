package de.codemakers.bot.supreme.commands.impl;

import de.codemakers.bot.supreme.commands.Command;
import de.codemakers.bot.supreme.commands.CommandHandler;
import de.codemakers.bot.supreme.commands.arguments.ArgumentConsumeType;
import de.codemakers.bot.supreme.commands.arguments.ArgumentList;
import de.codemakers.bot.supreme.commands.arguments.Invoker;
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
public class HelpCommand extends Command {
    
    @Override
    public final void initInvokers() {
        addInvokers(Invoker.createInvoker("help", this), Invoker.createInvoker("h", this));
    }

    @Override
    public final boolean called(Invoker invoker, ArgumentList arguments, de.codemakers.bot.supreme.entities.MessageEvent event) {
        return true;
    }

    @Override
    public final void action(Invoker invoker, ArgumentList arguments, de.codemakers.bot.supreme.entities.MessageEvent event) { //TODO Das mit Seiten und so machen, wie bei der Musik Playlist....
        if (arguments != null && arguments.isSize(1, -1)) {
            final boolean sendPrivate = arguments.isConsumed(Standard.ARGUMENT_PRIVATE, ArgumentConsumeType.CONSUME_ALL_IGNORE_CASE);
            while (arguments.hasArguments()) {
                final String command_help_invoker_string = arguments.consumeFirst();
                final Invoker command_help_invoker = Invoker.getInvokerByInvokerString(command_help_invoker_string);
                final Command command = CommandHandler.getCommandByInvokers(command_help_invoker);
                if (command != null) {
                    CommandHandler.sendHelpMessage(command_help_invoker, event, command, sendPrivate);
                } else {
                    final Message message = event.sendAndWaitMessageFormat(":warning: Sorry %s, the command \"%s\" wasn't found!", event.getAuthor().getAsMention(), command_help_invoker_string);
                    final long delay = Standard.getAutoDeleteCommandNotFoundMessageDelayByGuild(event.getGuild());
                    if (delay != -1) {
                        Util.deleteMessage(message, delay);
                    }
                }
            }
        } else {
            CommandHandler.sendHelpMessage(invoker, event, this, false);
        }
    }

    @Override
    public final void executed(boolean success, de.codemakers.bot.supreme.entities.MessageEvent event) {
        System.out.println("[INFO] Command '" + getCommandID() + "' was executed!");
    }

    @Override
    public final EmbedBuilder getHelp(Invoker invoker, EmbedBuilder builder) {
        builder.addField(String.format("%s [%s] [Command 1] [Command 2] [Command 3]...", invoker, Standard.ARGUMENT_PRIVATE.getCompleteArgument(0)), String.format("Returns the help for the given commands. And with the flag \"%s\" the help will be send in the private chat.", Standard.ARGUMENT_PRIVATE.getCompleteArgument(0)), false);
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
