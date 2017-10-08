package de.codemakers.bot.supreme.exceptions;

import de.codemakers.bot.supreme.commands.Command;
import de.codemakers.bot.supreme.util.Standard;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Channel;

/**
 * ArgumentException
 *
 * @author Panzer1119
 */
public class ArgumentException extends BotException {

    private Command command = null;
    private String argument = null;

    //event.sendMessage(new ArgumentException().setCommand(this).setArgument("1").getMessage(event.getTextChannel()).build());
    public final Command getCommand() {
        return command;
    }

    public final ArgumentException setCommand(Command command) {
        this.command = command;
        return this;
    }

    public final String getArgument() {
        return argument;
    }

    public final ArgumentException setArgument(String argument) {
        this.argument = argument;
        return this;
    }

    @Override
    public final String getMessage() {
        if (argument == null) {
            return super.getMessage();
        }
        return String.format("Wrong argument in \"%s:%s\"!", command.getInvokers().get(0).getInvoker(), argument);
    }

    @Override
    public final EmbedBuilder getMessage(Channel channel) {
        return super.getMessage(channel).appendDescription(Standard.NEW_LINE_DISCORD + "Please check the help command for more information on how to use this command.");
    }

}
