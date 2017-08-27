package de.codemakers.bot.supreme.commands.impl;

import de.codemakers.bot.supreme.commands.Command;
import de.codemakers.bot.supreme.commands.CommandCategory;
import de.codemakers.bot.supreme.commands.CommandHandler;
import de.codemakers.bot.supreme.commands.arguments.ArgumentConsumeType;
import de.codemakers.bot.supreme.commands.arguments.ArgumentList;
import de.codemakers.bot.supreme.commands.invoking.Invoker;
import de.codemakers.bot.supreme.permission.PermissionRoleFilter;
import de.codemakers.bot.supreme.util.Emoji;
import de.codemakers.bot.supreme.util.Standard;
import net.dv8tion.jda.core.EmbedBuilder;

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
    public final void action(Invoker invoker, ArgumentList arguments, de.codemakers.bot.supreme.entities.MessageEvent event) {
        if (arguments == null || arguments.isEmpty()) {
            CommandHandler.sendHelpList(event, false);
        } else if (arguments.isSize(1) && arguments.isConsumed(Standard.ARGUMENT_PRIVATE, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE)) {
            CommandHandler.sendHelpList(event, true);
        } else if (arguments.isSize(1, -1)) {
            final boolean sendPrivate = arguments.isConsumed(Standard.ARGUMENT_PRIVATE, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
            while (arguments.hasArguments()) {
                final String command_help_invoker_string = arguments.consumeFirst();
                final Invoker command_help_invoker = Invoker.getInvokerByInvokerString(command_help_invoker_string);
                final Command command = CommandHandler.getCommandByInvokers(command_help_invoker);
                if (command != null) {
                    CommandHandler.sendHelpMessage(command_help_invoker, event, command, sendPrivate);
                } else {
                    event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s Sorry %s, the command \"%s\" wasn't found!", Emoji.WARNING, event.getAuthor().getAsMention(), command_help_invoker_string);
                }
            }
        }
    }

    @Override
    public final void executed(boolean success, de.codemakers.bot.supreme.entities.MessageEvent event) {
        System.out.println("[INFO] Command '" + getCommandID() + "' was executed!");
    }

    @Override
    public final EmbedBuilder getHelp(Invoker invoker, EmbedBuilder builder) {
        builder.addField(String.format("%s [%s] [Command 1] [Command 2] [Command 3]...", invoker, Standard.ARGUMENT_PRIVATE.getCompleteArgument(0, -1)), String.format("Returns the help for the given commands. And with the flag \"%s\" the help will be send in the private chat.", Standard.ARGUMENT_PRIVATE.getCompleteArgument(0, -1)), false);
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

    @Override
    public CommandCategory getCommandCategory() {
        return Standard.COMMANDCATEGORY_NORMAL;
    }

}
