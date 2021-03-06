package de.codemakers.bot.supreme.commands.impl.moderation;

import de.codemakers.bot.supreme.commands.Command;
import de.codemakers.bot.supreme.commands.CommandCategory;
import de.codemakers.bot.supreme.commands.arguments.ArgumentList;
import de.codemakers.bot.supreme.commands.invoking.Invoker;
import de.codemakers.bot.supreme.entities.MessageEvent;
import de.codemakers.bot.supreme.util.Standard;
import java.awt.Color;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageHistory;
import de.codemakers.bot.supreme.permission.PermissionFilter;

/**
 * ClearCommand
 *
 * @author Panzer1119
 */
public class ClearCommand extends Command {

    @Override
    public final void initInvokers() {
        addInvokers(Invoker.createInvoker("clear", this));
    }

    @Override
    public final boolean called(Invoker invoker, ArgumentList arguments, MessageEvent event) {
        return (arguments == null || arguments.isSize(0, 1)) && !event.isPrivate();
    }

    @Override
    public final void action(Invoker invoker, ArgumentList arguments, MessageEvent event) {
        int clearLines = -1;
        if (arguments != null && arguments.size() >= 1) {
            try {
                clearLines = Integer.parseInt(arguments.get(0));
            } catch (Exception ex) {
                clearLines = -1;
            }
        }
        if (clearLines == -1) {
            clearLines = Standard.STANDARD_NUMBER_OF_LINES_TO_GET_CLEARED;
        }
        clearLines %= 101;
        if (clearLines >= 1) {
            try {
                final MessageHistory history = new MessageHistory(event.getMessageChannel());
                if (clearLines > 1) {
                    event.getMessage().delete().queue();
                } else {
                    clearLines++;
                }
                history.retrievePast(clearLines).complete().stream().forEach((message) -> {
                    event.getMessageChannel().deleteMessageById(message.getId()).queue();
                });
                event.sendMessage(Standard.STANDARD_MESSAGE_DELETING_DELAY, Standard.getMessageEmbed(Color.GREEN, "Deleted %d messages!", clearLines).build());
            } catch (Exception ex) {
                System.err.println(ex);
                //ex.printStackTrace();
            }
        } else {
            event.sendMessageFormat(":warning: Sorry, %s you need to delete at least 1 messages!", event.getAuthor());
        }
    }

    @Override
    public final void executed(boolean success, MessageEvent event) {
        System.out.println("[INFO] Command '" + getCommandID() + "' was executed!");
    }

    @Override
    public final EmbedBuilder getHelp(Invoker invoker, EmbedBuilder builder) {
        builder.addField(invoker + " [Number of Lines]", String.format("Clears the last %d lines, or the last 1 to 100 lines, in the current (not private!) channel.", Standard.STANDARD_NUMBER_OF_LINES_TO_GET_CLEARED), false);
        return builder;
    }

    @Override
    public final PermissionFilter getPermissionFilter() {
        return Standard.STANDARD_PERMISSIONFILTER_GUILD_MODERATOR_BOT_ADMIN;
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
