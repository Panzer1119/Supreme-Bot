package de.codemakers.bot.supreme.commands.impl.moderation;

import de.codemakers.bot.supreme.commands.Command;
import de.codemakers.bot.supreme.commands.arguments.ArgumentConsumeType;
import de.codemakers.bot.supreme.commands.arguments.ArgumentList;
import de.codemakers.bot.supreme.commands.invoking.Invoker;
import de.codemakers.bot.supreme.entities.MessageEvent;
import de.codemakers.bot.supreme.permission.PermissionHandler;
import de.codemakers.bot.supreme.permission.PermissionRoleFilter;
import de.codemakers.bot.supreme.util.Standard;
import net.dv8tion.jda.core.EmbedBuilder;

/**
 * ChangeCommandPrefixCommand
 *
 * @author Panzer1119
 */
public class ChangeCommandPrefixCommand extends Command {

    @Override
    public final void initInvokers() {
        addInvokers(Invoker.createInvoker("changeCommandPrefix", this), Invoker.createInvoker("changeCmdPrefix", this));
    }

    @Override
    public final boolean called(Invoker invoker, ArgumentList arguments, MessageEvent event) {
        if (arguments == null) {
            return false;
        }
        final boolean global = arguments.isConsumed(Standard.ARGUMENT_GLOBAL, ArgumentConsumeType.FIRST_IGNORE_CASE);
        if (global) {
            return arguments.isSize(2);
        } else {
            return arguments.isSize(1);
        }
    }

    @Override
    public final void action(Invoker invoker, ArgumentList arguments, MessageEvent event) {
        final boolean global = arguments.isConsumed(Standard.ARGUMENT_GLOBAL, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
        final String commandPrefix = arguments.consumeFirst();
        if (commandPrefix == null) {
            event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s the new command prefix was invalid!", event.getAuthor().getAsMention());
            return;
        }
        if (global) {
            if (!Standard.isSuperOwner(event.getAuthor())) {
                PermissionHandler.sendNoPermissionMessage(event);
                return;
            }
            if (Standard.setStandardCommandPrefix(commandPrefix)) {
                event.sendMessageFormat("Changed Global Command Prefix to \"%s\"", commandPrefix);
            } else {
                event.sendMessageFormat("Global Command Prefix wasn't changed, it's still \"%s\"", Standard.getStandardCommandPrefix());
            }
        } else if (Standard.setCommandPrefixForGuild(event.getGuild(), commandPrefix)) {
            event.sendMessageFormat("Changed Command Prefix to \"%s\"", commandPrefix);
        } else {
            event.sendMessageFormat("Command Prefix wasn't changed, it's still \"%s\"", Standard.getCommandPrefixByGuild(event.getGuild()));
        }
    }

    @Override
    public final void executed(boolean success, MessageEvent event) {
        System.out.println("[INFO] Command '" + getCommandID() + "' was executed!");
    }

    @Override
    public final EmbedBuilder getHelp(Invoker invoker, EmbedBuilder builder) {
        builder.addField(String.format("%s <New Command Prefix> [%s]", invoker, Standard.ARGUMENT_GLOBAL.getCompleteArgument(0, -1)), String.format("Sets the command prefix for this guild or with the flag \"%s\" the global standard command prefix.", Standard.ARGUMENT_GLOBAL.getCompleteArgument(0, -1)), false);
        return builder;
    }

    @Override
    public final PermissionRoleFilter getPermissionRoleFilter() {
        return Standard.STANDARD_PERMISSIONROLEFILTER_ADMIN_BOT_COMMANDER;
    }

    @Override
    public final String getCommandID() {
        return getClass().getName();
    }

}
