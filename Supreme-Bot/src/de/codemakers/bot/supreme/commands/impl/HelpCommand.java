package de.codemakers.bot.supreme.commands.impl;

import de.codemakers.bot.supreme.commands.Command;
import de.codemakers.bot.supreme.commands.CommandCategory;
import de.codemakers.bot.supreme.commands.CommandHandler;
import de.codemakers.bot.supreme.commands.arguments.ArgumentConsumeType;
import de.codemakers.bot.supreme.commands.arguments.ArgumentList;
import de.codemakers.bot.supreme.commands.invoking.Invoker;
import de.codemakers.bot.supreme.entities.MessageEvent;
import de.codemakers.bot.supreme.util.Emoji;
import de.codemakers.bot.supreme.util.Standard;
import net.dv8tion.jda.core.EmbedBuilder;
import de.codemakers.bot.supreme.permission.PermissionFilter;

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
    public final boolean called(Invoker invoker, ArgumentList arguments, MessageEvent event) {
        if (arguments == null) {
            return false;
        }
        return true;
    }

    @Override
    public final void action(Invoker invoker, ArgumentList arguments, MessageEvent event) {
        final boolean here = arguments.isConsumed(Standard.ARGUMENT_HERE, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
        final boolean sendPrivate = arguments.isConsumed(Standard.ARGUMENT_PRIVATE, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
        if (arguments.isEmpty()) {
            CommandHandler.sendHelpList(event, false, false);
        } else if (arguments.isSize(1, -1)) {
            while (arguments.hasArguments()) {
                final String command_help_invoker_string = arguments.consumeFirst();
                final Invoker command_help_invoker = Invoker.getInvokerByInvokerString(command_help_invoker_string);
                final Command command = CommandHandler.getCommandByInvokers(command_help_invoker);
                if (command != null) {
                    CommandHandler.sendHelpMessage(command_help_invoker, event, command, here, sendPrivate);
                } else {
                    event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s Sorry %s, the command \"%s\" wasn't found!", Emoji.WARNING, event.getAuthor().getAsMention(), command_help_invoker_string);
                }
            }
        } else {
            CommandHandler.sendHelpList(event, here, sendPrivate);
        }
    }

    @Override
    public final void executed(boolean success, MessageEvent event) {
        System.out.println("[INFO] Command '" + getCommandID() + "' was executed!");
    }

    @Override
    public final EmbedBuilder getHelp(Invoker invoker, EmbedBuilder builder) {
        builder.addField(String.format("%s [%s|%s] [Command 1] [Command 2] [Command 3]...", invoker, Standard.ARGUMENT_PRIVATE.getCompleteArgument(0, -1), Standard.ARGUMENT_HERE.getCompleteArgument(0, -1)), String.format("Returns the help for the given commands. With the flag \"%s\" the help will be send in the private chat, or with the flag \"%s\" the bot tries to send the help in the current TextChannel.", Standard.ARGUMENT_PRIVATE.getCompleteArgument(0, -1), Standard.ARGUMENT_HERE.getCompleteArgument(0, -1)), false);
        return builder;
    }

    @Override
    public final PermissionFilter getPermissionFilter() {
        return null;
    }

    @Override
    public final String getCommandID() {
        return getClass().getName();
    }

    @Override
    public CommandCategory getCommandCategory() {
        return Standard.COMMANDCATEGORY_NORMAL;
    }

}
