package de.codemakers.bot.supreme.commands.impl;

import de.codemakers.bot.supreme.commands.Command;
import de.codemakers.bot.supreme.commands.arguments.ArgumentConsumeType;
import de.codemakers.bot.supreme.commands.arguments.ArgumentList;
import de.codemakers.bot.supreme.commands.invoking.Invoker;
import de.codemakers.bot.supreme.entities.MessageEvent;
import de.codemakers.bot.supreme.permission.PermissionRoleFilter;
import de.codemakers.bot.supreme.util.Standard;
import net.dv8tion.jda.core.EmbedBuilder;

/**
 * RecorderCommand
 *
 * @author Panzer1119
 */
public class RecorderCommand extends Command {

    @Override
    public void initInvokers() {
        addInvokers(Invoker.createInvoker("recorder", this), Invoker.createInvoker("rc", this));
    }

    @Override
    public boolean called(Invoker invoker, ArgumentList arguments, MessageEvent event) {
        if (arguments == null || arguments.isEmpty()) {
            return false;
        }
        final boolean start = arguments.isConsumed(Standard.ARGUMENT_START, ArgumentConsumeType.FIRST_IGNORE_CASE);
        final boolean stop = arguments.isConsumed(Standard.ARGUMENT_STOP, ArgumentConsumeType.FIRST_IGNORE_CASE);
        if (start) {
            return arguments.isSize(1, 2); //[VoiceChannel ID] //TODO Vielleicht auch noch statt ID nur den Namen?
        } else if (stop) {
            return arguments.isSize(1);
        } else {
            return false;
        }
    }

    @Override
    public void action(Invoker invoker, ArgumentList arguments, MessageEvent event) {
        final boolean start = arguments.isConsumed(Standard.ARGUMENT_START, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
        final boolean stop = arguments.isConsumed(Standard.ARGUMENT_STOP, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
        if (start) {
            
        } else if (stop) {
            
        }
    }

    @Override
    public void executed(boolean success, MessageEvent event) {
        System.out.println("[INFO] Command '" + getCommandID() + "' was executed!");
    }

    @Override
    public EmbedBuilder getHelp(Invoker invoker, EmbedBuilder builder) {
        return builder;
    }

    @Override
    public PermissionRoleFilter getPermissionRoleFilter() {
        return Standard.STANDARD_PERMISSIONROLEFILTER_ADMIN_BOT_COMMANDER;
    }

    @Override
    public String getCommandID() {
        return getClass().getName();
    }

}
