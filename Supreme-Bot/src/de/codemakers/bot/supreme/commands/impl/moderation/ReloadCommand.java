package de.codemakers.bot.supreme.commands.impl.moderation;

import de.codemakers.bot.supreme.commands.Command;
import de.codemakers.bot.supreme.commands.CommandCategory;
import de.codemakers.bot.supreme.commands.arguments.ArgumentConsumeType;
import de.codemakers.bot.supreme.commands.arguments.ArgumentList;
import de.codemakers.bot.supreme.commands.invoking.Invoker;
import de.codemakers.bot.supreme.core.SupremeBot;
import de.codemakers.bot.supreme.entities.MessageEvent;
import de.codemakers.bot.supreme.util.Standard;
import java.awt.Color;
import net.dv8tion.jda.core.EmbedBuilder;
import de.codemakers.bot.supreme.permission.PermissionFilter;

/**
 * ReloadCommand
 *
 * @author Panzer1119
 */
public class ReloadCommand extends Command {

    @Override
    public final void initInvokers() {
        addInvokers(Invoker.createInvoker("reload", this), Invoker.createInvoker("r", this));
    }

    @Override
    public final boolean called(Invoker invoker, ArgumentList arguments, MessageEvent event) {
        return true;
    }

    @Override
    public final void action(Invoker invoker, ArgumentList arguments, MessageEvent event) {
        if (arguments != null && arguments.size() == 2) {
            if (arguments.consumeFirst(Standard.ARGUMENT_GUILD_SETTINGS, ArgumentConsumeType.FIRST_IGNORE_CASE)) {
                if (!arguments.consume(Standard.ARGUMENT_ALL, ArgumentConsumeType.FIRST_IGNORE_CASE, 1) && !arguments.consume(Standard.ARGUMENT_SETTINGS, ArgumentConsumeType.FIRST_IGNORE_CASE, 1)) {
                    arguments.consume(Standard.ARGUMENT_GUILD_SETTINGS, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE, false);
                    final long guild_id = Standard.resolveGuildId(event.getGuild(), arguments.consumeFirst());
                    if (guild_id != 0) {
                        //SupremeBot.reloadGuildSettings(guild_id);
                        //event.sendMessage(Standard.STANDARD_MESSAGE_DELETING_DELAY, Standard.getMessageEmbed(Color.YELLOW, "%s reloaded %s for %s (ID: %s)!", event.getAuthor().getAsMention(), Standard.ARGUMENT_GUILD_SETTINGS.getArgument(), Standard.getGuildById(guild_id).getName(), guild_id).build());
                        event.sendMessage(Standard.getNoMessage(event.getAuthor(), "the guild settings reload function is no longer supported!").build());
                        return;
                    }
                }
            }
        }
        if (arguments != null && arguments.size() >= 1) {
            while (arguments.hasArguments()) {
                if (arguments.consumeFirst(Standard.ARGUMENT_ALL, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE)) {
                    SupremeBot.reload();
                    event.sendMessage(Standard.STANDARD_MESSAGE_DELETING_DELAY, Standard.getMessageEmbed(Color.YELLOW, "%s reloaded %s!", event.getAuthor().getAsMention(), Standard.ARGUMENT_ALL.getArgument()).build());
                } else if (arguments.consumeFirst(Standard.ARGUMENT_SETTINGS, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE)) {
                    SupremeBot.reloadSettings();
                    event.sendMessage(Standard.STANDARD_MESSAGE_DELETING_DELAY, Standard.getMessageEmbed(Color.YELLOW, "%s reloaded %s!", event.getAuthor().getAsMention(), Standard.ARGUMENT_SETTINGS.getArgument()).build());
                } else if (arguments.consumeFirst(Standard.ARGUMENT_GUILD_SETTINGS, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE)) {
                    //SupremeBot.reloadGuildSettings();
                    Standard.reloadAllGuildSettings();
                    event.sendMessage(Standard.STANDARD_MESSAGE_DELETING_DELAY, Standard.getMessageEmbed(Color.YELLOW, "%s reloaded %s!", event.getAuthor().getAsMention(), Standard.ARGUMENT_GUILD_SETTINGS.getArgument()).build());
                    //event.sendMessage(Standard.getNoMessage(event.getAuthor(), "the guild settings reload function is no longer supported!").build());
                }
            }
        } else {
            SupremeBot.reload();
            event.sendMessage(Standard.STANDARD_MESSAGE_DELETING_DELAY, Standard.getMessageEmbed(Color.YELLOW, "%s reloaded all!", event.getAuthor().getAsMention()).build());
        }
    }

    @Override
    public final void executed(boolean success, MessageEvent event) {
        System.out.println("[INFO] Command '" + getCommandID() + "' was executed!");
    }

    @Override
    public final EmbedBuilder getHelp(Invoker invoker, EmbedBuilder builder) {
        builder.addField(invoker + " [Tag 1] [Tag 2] [Tag 3]...", "Reloads everything or all given tags.", false);
        return builder;
    }

    @Override
    public final PermissionFilter getPermissionFilter() {
        return Standard.STANDARD_PERMISSIONFILTER_GUILD_BOT_COMMANDER_BOT_ADMIN;
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
