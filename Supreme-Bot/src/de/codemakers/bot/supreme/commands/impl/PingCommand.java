package de.codemakers.bot.supreme.commands.impl;

import de.codemakers.bot.supreme.commands.Command;
import de.codemakers.bot.supreme.commands.CommandCategory;
import de.codemakers.bot.supreme.commands.arguments.ArgumentList;
import de.codemakers.bot.supreme.commands.invoking.Invoker;
import de.codemakers.bot.supreme.entities.MessageEvent;
import de.codemakers.bot.supreme.permission.PermissionRoleFilter;
import de.codemakers.bot.supreme.util.Standard;
import net.dv8tion.jda.core.EmbedBuilder;

/**
 * PingCommand
 *
 * @author Panzer1119
 */
public class PingCommand extends Command {

    @Override
    public final void initInvokers() {
        addInvokers(Invoker.createInvoker("ping", this));
    }

    @Override
    public final boolean called(Invoker invoker, ArgumentList arguments, MessageEvent event) {
        return true;
    }

    @Override
    public final void action(Invoker invoker, ArgumentList arguments, MessageEvent event) {
        event.sendMessageFormat("Pong! (%d ms)", event.getJDA().getPing());
    }

    @Override
    public final void executed(boolean success, MessageEvent event) {
        System.out.println("[INFO] Command '" + getCommandID() + "' was executed!");
    }

    @Override
    public final EmbedBuilder getHelp(Invoker invoker, EmbedBuilder builder) {
        builder.addField(invoker + "", "Returns a \"Pong!\" with the ping in milliseconds.", false);
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
