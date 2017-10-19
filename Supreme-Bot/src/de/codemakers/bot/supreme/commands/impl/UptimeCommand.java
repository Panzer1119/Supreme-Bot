package de.codemakers.bot.supreme.commands.impl;

import de.codemakers.bot.supreme.commands.Command;
import de.codemakers.bot.supreme.commands.CommandCategory;
import de.codemakers.bot.supreme.commands.arguments.ArgumentList;
import de.codemakers.bot.supreme.commands.invoking.Invoker;
import de.codemakers.bot.supreme.entities.MessageEvent;
import de.codemakers.bot.supreme.permission.PermissionFilter;
import de.codemakers.bot.supreme.util.Standard;
import de.codemakers.bot.supreme.util.Util;
import java.time.Instant;
import net.dv8tion.jda.core.EmbedBuilder;

/**
 * UptimeCommand
 *
 * @author Panzer1119
 */
public class UptimeCommand extends Command {

    @Override
    public void initInvokers() {
        addInvokers(Invoker.createInvoker("uptime", this), Invoker.createInvoker("since", this));
    }

    @Override
    public boolean called(Invoker invoker, ArgumentList arguments, MessageEvent event) {
        return true;
    }

    @Override
    public void action(Invoker invoker, ArgumentList arguments, MessageEvent event) {
        event.sendMessage(Util.getUptimeMessage(event, Instant.now()));
    }

    @Override
    public void executed(boolean success, MessageEvent event) {
        System.out.println("[INFO] Command '" + getCommandID() + "' was executed!");
    }

    @Override
    public EmbedBuilder getHelp(Invoker invoker, EmbedBuilder builder) {
        builder.addField(invoker + "", "Shows the uptime of the bot.", false);
        return builder;
    }

    @Override
    public PermissionFilter getPermissionFilter() {
        return Standard.STANDARD_PERMISSIONFILTER_VIP;
    }

    @Override
    public String getCommandID() {
        return getClass().getName();
    }

    @Override
    public CommandCategory getCommandCategory() {
        return Standard.COMMANDCATEGORY_NORMAL;
    }

}
