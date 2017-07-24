package de.codemakers.bot.supreme.commands.impl.moderation;

import de.codemakers.bot.supreme.commands.Command;
import de.codemakers.bot.supreme.commands.arguments.ArgumentList;
import de.codemakers.bot.supreme.commands.invoking.Invoker;
import de.codemakers.bot.supreme.entities.MessageEvent;
import de.codemakers.bot.supreme.permission.PermissionRoleFilter;
import net.dv8tion.jda.core.EmbedBuilder;

/**
 * SayCommand
 *
 * @author Panzer1119
 */
public class SayCommand extends Command {

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
