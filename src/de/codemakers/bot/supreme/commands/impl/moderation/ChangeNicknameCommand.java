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
 * ChangeNicknameCommand
 *
 * @author Panzer1119
 */
public class ChangeNicknameCommand extends Command {

    @Override
    public void initInvokers() {
        addInvokers(Invoker.createInvoker("changeNickname", this), Invoker.createInvoker("cName", this));
    }

    @Override
    public boolean called(Invoker invoker, ArgumentList arguments, MessageEvent event) {
        if (arguments == null || arguments.isEmpty()) {
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
    public void action(Invoker invoker, ArgumentList arguments, MessageEvent event) {
        final boolean global = arguments.isConsumed(Standard.ARGUMENT_GLOBAL, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
        final String nickname = arguments.consumeFirst();
        if (nickname == null) {
            event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s the new Nickname was invalid!", event.getAuthor().getAsMention());
            return;
        }
        if (global) {
            if (!Standard.isSuperOwner(event.getAuthor())) {
                PermissionHandler.sendNoPermissionMessage(event);
                return;
            }
            if (Standard.setNickname(nickname)) {
                event.sendMessageFormat("Changed Global Nickname to \"%s\"", nickname);
            } else {
                event.sendMessageFormat("Global Nickname wasn't changed, it's still \"%s\"", Standard.getNickname());
            }
        } else if (Standard.setNicknameForGuild(event.getGuild(), nickname)) {
            event.sendMessageFormat("Changed Nickname to \"%s\"", nickname);
        } else {
            event.sendMessageFormat("Nickname wasn't changed, it's still \"%s\"", Standard.getNicknameByGuild(event.getGuild()));
        }
    }

    @Override
    public void executed(boolean success, MessageEvent event) {
        System.out.println("[INFO] Command '" + getCommandID() + "' was executed!");
    }

    @Override
    public EmbedBuilder getHelp(Invoker invoker, EmbedBuilder builder) {
        builder.addField(String.format("%s <New Nickname> [%s]", invoker, Standard.ARGUMENT_GLOBAL.getCompleteArgument(0, -1)), String.format("Sets the Nickname for this guild or with the flag \"%s\" the global Nickname.", Standard.ARGUMENT_GLOBAL.getCompleteArgument(0, -1)), false);
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
