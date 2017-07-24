package de.codemakers.bot.supreme.commands.impl.moderation;

import de.codemakers.bot.supreme.commands.Command;
import de.codemakers.bot.supreme.commands.arguments.ArgumentConsumeType;
import de.codemakers.bot.supreme.commands.arguments.ArgumentList;
import de.codemakers.bot.supreme.commands.invoking.Invoker;
import de.codemakers.bot.supreme.entities.MessageEvent;
import de.codemakers.bot.supreme.permission.PermissionRole;
import de.codemakers.bot.supreme.permission.PermissionRoleFilter;
import de.codemakers.bot.supreme.util.Emoji;
import de.codemakers.bot.supreme.util.Standard;
import net.dv8tion.jda.core.EmbedBuilder;

/**
 * CommandCommand
 *
 * @author Panzer1119
 */
public class CommandCommand extends Command {

    @Override
    public void initInvokers() {
        addInvokers(Invoker.createInvoker("command", this), Invoker.createInvoker("c", this));
    }

    @Override
    public boolean called(Invoker invoker, ArgumentList arguments, MessageEvent event) {
        if (arguments == null || !arguments.isSize(2, 3)) {
            return false;
        }
        if (arguments.isConsumed(Standard.ARGUMENT_OVERRIDE, ArgumentConsumeType.FIRST_IGNORE_CASE)) {
            return arguments.isSize(3);
        } else {
            return arguments.isSize(2);
        }
    }

    @Override
    public void action(Invoker invoker, ArgumentList arguments, MessageEvent event) { //FIXME Verhindern, dass man Zyklen basteln kann und ein Invoker ein zweites aufruft und das wieder das erste und so weiter...
        final boolean override = arguments.isConsumed(Standard.ARGUMENT_OVERRIDE, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
        final String invoker_existing_string = arguments.consumeFirst();
        final String invoker_new_string = arguments.consumeFirst();
        final Invoker invoker_existing = Invoker.getInvokerByInvokerString(invoker_existing_string);
        Invoker invoker_new = Invoker.getInvokerByInvokerString(invoker_new_string);
        if (invoker_existing == null) {
            event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s Sorry %s, the command \"%s\" wasn't found!", Emoji.WARNING, event.getAuthor().getAsMention(), invoker_existing_string);
        } else if ((invoker_new != null) && !override) {
            event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s Sorry %s, the command \"%s\" already exists! Use \"%s\" to override the command!", Emoji.WARNING, event.getAuthor().getAsMention(), invoker_new_string, Standard.ARGUMENT_OVERRIDE.getCompleteArgument(0));
        } else if ((invoker_new != null) && override) {
            if (invoker_new.getDirectInvokeable() instanceof Command) {
                event.sendMessageFormat("%s overrode \"%s\" to \"%s\"", event.getAuthor().getAsMention(), invoker_new, invoker_existing);
            } else {
                event.sendMessageFormat("%s overrode \"%s\" from \"%s\" to \"%s\"", event.getAuthor().getAsMention(), invoker_new, invoker_new.getDirectInvokeable(), invoker_existing);
            }
            invoker_new.setInvokeable(invoker_existing);
        } else {
            invoker_new = Invoker.createInvoker(invoker_new_string, invoker_existing);
            event.sendMessageFormat("%s associated \"%s\" to \"%s\"", event.getAuthor().getAsMention(), invoker_new, invoker_existing);
        }
    }

    @Override
    public void executed(boolean success, MessageEvent event) {
        System.out.println("[INFO] Command '" + getCommandID() + "' was executed!");
    }

    @Override
    public EmbedBuilder getHelp(Invoker invoker, EmbedBuilder builder) {
        builder.addField(String.format("%s <Existing Invoker> <New Association> [%s]", invoker, Standard.ARGUMENT_OVERRIDE.getCompleteArgument(0)), "Associates an Invoker to an existing Invoker.", false);
        return builder;
    }

    @Override
    public PermissionRoleFilter getPermissionRoleFilter() {
        final PermissionRole owner = PermissionRole.getPermissionRoleByName("Owner");
        final PermissionRole bot_commander = PermissionRole.getPermissionRoleByName("Bot_Commander");
        return (role, member) -> {
            if (role.isThisHigherOrEqual(owner) || role.isThisEqual(bot_commander)) {
                return true;
            }
            return Standard.isSuperOwner(member);
        };
    }

    @Override
    public String getCommandID() {
        return getClass().getName();
    }

}