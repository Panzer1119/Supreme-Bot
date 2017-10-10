package de.codemakers.bot.supreme.commands.impl.moderation.util;

import de.codemakers.bot.supreme.commands.Command;
import de.codemakers.bot.supreme.commands.CommandCategory;
import de.codemakers.bot.supreme.commands.arguments.ArgumentConsumeType;
import de.codemakers.bot.supreme.commands.arguments.ArgumentList;
import de.codemakers.bot.supreme.commands.invoking.Invoker;
import de.codemakers.bot.supreme.entities.DeleteMessageManager;
import de.codemakers.bot.supreme.entities.MessageEvent;
import de.codemakers.bot.supreme.util.NetworkUtil;
import de.codemakers.bot.supreme.util.Standard;
import java.awt.Color;
import net.dv8tion.jda.core.EmbedBuilder;
import de.codemakers.bot.supreme.permission.PermissionFilter;

/**
 * SystemCommand
 *
 * @author Panzer1119
 */
public class SystemCommand extends Command {

    @Override
    public final void initInvokers() {
        addInvokers(Invoker.createInvoker("system", this), Invoker.createInvoker("sys", this));
    }

    @Override
    public final boolean called(Invoker invoker, ArgumentList arguments, MessageEvent event) {
        if (arguments == null) {
            return false;
        }
        final boolean live = arguments.isConsumed(Standard.ARGUMENT_LIVE, ArgumentConsumeType.FIRST_IGNORE_CASE);
        if (live) {
            return arguments.isSize(1);
        }
        return arguments.isEmpty();
    }

    @Override
    public final void action(Invoker invoker, ArgumentList arguments, MessageEvent event) {
        final boolean live = arguments.isConsumed(Standard.ARGUMENT_LIVE, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
        if (live) {
            SystemMessageManager.of(event);
        } else {
            DeleteMessageManager.monitor(event.sendAndWaitMessage(Standard.getMessageEmbed(Color.YELLOW, Standard.toBold("System Information"))
                    .addField(String.format("%s Version", Standard.STANDARD_NAME), Standard.VERSION, false)
                    .addField("Java Version", System.getProperty("java.version"), false)
                    .addField("IP Address", NetworkUtil.getIPAddress(), false)
                    .addField("CPU Cores available", "" + Runtime.getRuntime().availableProcessors(), false)
                    .addField("Max Memory", SystemMessageManager.getMemory(Runtime.getRuntime().maxMemory()), false)
                    .addField("Total Memory", SystemMessageManager.getMemory(Runtime.getRuntime().totalMemory()), false)
                    .addField("Free Memory", SystemMessageManager.getMemory(Runtime.getRuntime().freeMemory()), false).build()));
        }
    }

    @Override
    public final void executed(boolean success, MessageEvent event) {
        System.out.println("[INFO] Command '" + getCommandID() + "' was executed!");
    }

    @Override
    public final EmbedBuilder getHelp(Invoker invoker, EmbedBuilder builder) {
        builder.addField(String.format("%s [%s]", invoker, Standard.ARGUMENT_LIVE.getCompleteArgument(0, -1)), "Shows information about the (host) system. Optionally shows the info live.", false);
        return builder;
    }

    @Override
    public final PermissionFilter getPermissionFilter() {
        return Standard.STANDARD_PERMISSIONROLEFILTER_SUPER_OWNER;
    }

    @Override
    public final String getCommandID() {
        return getClass().getName();
    }

    @Override
    public final CommandCategory getCommandCategory() {
        return Standard.COMMANDCATEGORY_MODERATION_UTIL;
    }

}
