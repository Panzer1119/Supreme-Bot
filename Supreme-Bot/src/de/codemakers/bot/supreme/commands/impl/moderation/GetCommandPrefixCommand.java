package de.codemakers.bot.supreme.commands.impl.moderation;

import de.codemakers.bot.supreme.commands.Command;
import de.codemakers.bot.supreme.commands.CommandCategory;
import de.codemakers.bot.supreme.commands.arguments.ArgumentConsumeType;
import de.codemakers.bot.supreme.commands.arguments.ArgumentList;
import de.codemakers.bot.supreme.commands.invoking.Invoker;
import de.codemakers.bot.supreme.entities.MessageEvent;
import de.codemakers.bot.supreme.permission.PermissionRoleFilter;
import de.codemakers.bot.supreme.util.Standard;
import net.dv8tion.jda.core.EmbedBuilder;

/**
 * GetCommandPrefixCommand
 *
 * @author Panzer1119
 */
public class GetCommandPrefixCommand extends Command {

    @Override
    public final void initInvokers() {
        addInvokers(Invoker.createInvoker("getCommandPrefix", this), Invoker.createInvoker("getCmdPrefix", this));
    }

    @Override
    public final boolean called(Invoker invoker, ArgumentList arguments, MessageEvent event) {
        if (arguments == null) {
            return true;
        }

        if (arguments.isConsumed(Standard.ARGUMENT_GLOBAL, ArgumentConsumeType.FIRST_IGNORE_CASE)) {
            return arguments.isSize(1);
        } else {
            return arguments.isSize(0);
        }
    }

    @Override
    public final void action(Invoker invoker, ArgumentList arguments, MessageEvent event) {
        final boolean global = arguments.isConsumed(Standard.ARGUMENT_GLOBAL, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
        if (global) {
            event.sendMessageFormat("Global Command Prefix is \"%s\"", Standard.getStandardCommandPrefix());
        } else {
            event.sendMessageFormat("Command Prefix for this Guild is \"%s\"", Standard.getCommandPrefixByGuild(event.getGuild()));
        }
    }

    @Override
    public final void executed(boolean success, MessageEvent event) {
        System.out.println("[INFO] Command '" + getCommandID() + "' was executed!");
    }

    @Override
    public final EmbedBuilder getHelp(Invoker invoker, EmbedBuilder builder) {
        builder.addField(String.format("%s [%s]", invoker, Standard.ARGUMENT_GLOBAL.getCompleteArgument(0, -1)), String.format("Returns the command prefix for this guild or with the flag \"%s\" the global standard command prefix.", Standard.ARGUMENT_GLOBAL.getCompleteArgument(0, -1)), false);
        return builder;
    }

    @Override
    public final PermissionRoleFilter getPermissionRoleFilter() {
        return Standard.STANDARD_PERMISSIONROLEFILTER_OWNER_BOT_COMMANDER;
    }

    @Override
    public final String getCommandID() {
        return getClass().getName();
    }

    @Override
    public CommandCategory getCommandCategory() {
        return Standard.COMMANDCATEGORY_MODERATION;
    }

}
