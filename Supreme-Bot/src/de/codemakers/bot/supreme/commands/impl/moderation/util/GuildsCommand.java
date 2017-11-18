package de.codemakers.bot.supreme.commands.impl.moderation.util;

import de.codemakers.bot.supreme.commands.Command;
import de.codemakers.bot.supreme.commands.CommandCategory;
import de.codemakers.bot.supreme.commands.arguments.ArgumentConsumeType;
import de.codemakers.bot.supreme.commands.arguments.ArgumentList;
import de.codemakers.bot.supreme.commands.invoking.Invoker;
import de.codemakers.bot.supreme.entities.AdvancedEmote;
import de.codemakers.bot.supreme.entities.MessageEvent;
import de.codemakers.bot.supreme.listeners.ReactionListener;
import de.codemakers.bot.supreme.permission.PermissionFilter;
import de.codemakers.bot.supreme.permission.ReactionPermissionFilter;
import de.codemakers.bot.supreme.util.Emoji;
import de.codemakers.bot.supreme.util.Standard;
import de.codemakers.bot.supreme.util.TimeUnit;
import de.codemakers.bot.supreme.util.Timeout;
import java.util.stream.Collectors;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;

/**
 * GuildsCommand
 *
 * @author Panzer1119
 */
public class GuildsCommand extends Command {

    @Override
    public void initInvokers() {
        addInvokers(Invoker.createInvoker("guilds", this));
    }

    @Override
    public boolean called(Invoker invoker, ArgumentList arguments, MessageEvent event) {
        if (arguments == null || arguments.isEmpty()) {
            return false;
        }
        final boolean list = arguments.isConsumed(Standard.ARGUMENT_LIST, ArgumentConsumeType.FIRST_IGNORE_CASE);
        final boolean leave = arguments.isConsumed(Standard.ARGUMENT_LEAVE, ArgumentConsumeType.FIRST_IGNORE_CASE);
        if (list) {
            return arguments.isSize(1);
        } else if (leave) {
            return arguments.isSize(2);
        }
        return false;
    }

    @Override
    public void action(Invoker invoker, ArgumentList arguments, MessageEvent event) {
        final boolean list = arguments.isConsumed(Standard.ARGUMENT_LIST, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
        final boolean leave = arguments.isConsumed(Standard.ARGUMENT_LEAVE, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
        if (list) {
            if (Standard.getJDA() == null) {
                System.err.println("WTF JDA IS NULL AND " + getCommandID() + " WAS EXECUTED?!");
                return;
            }
            event.getMessageChannel().sendMessageFormat("I'm running on the following guilds:%s", Standard.getJDA().getGuilds().stream().map(Standard::getCompleteName).collect(Collectors.joining(Standard.NEW_LINE_DISCORD, Standard.NEW_LINE_DISCORD, ""))).queue((message) -> ReactionListener.deleteMessageWithReaction(message, "x", 2, TimeUnit.MINUTES, true, ReactionPermissionFilter.createUserFilter(event.getAuthor())));
        } else if (leave) {
            final String guild_id = arguments.consumeFirst();
            final Guild guild = Standard.getGuildById(guild_id);
            if (guild == null) {
                event.sendMessage(Standard.STANDARD_MESSAGE_DELETING_DELAY, Standard.getNoMessage(event.getAuthor(), "i'm not in the guild with the id \"%s\" ", guild_id).build());
                return;
            }
            event.getMessageChannel().sendMessageFormat("%s do you really want to leave the guild %s?", event.getAuthor().getAsMention(), Standard.getCompleteName(guild)).queue((message) -> {
                ReactionListener.registerListener(message, AdvancedEmote.parse(Emoji.CHECK_MARK), (reaction, emote, guild_, user) -> {
                    guild.leave().queue();
                }, null, ReactionPermissionFilter.createUserFilter(event.getAuthor()), true);
                ReactionListener.registerListener(message, AdvancedEmote.parse(Emoji.MARK_MULTIPLICATION_SIGN), (reaction, emote, guild_, user) -> {
                    ReactionListener.unregisterListener(message, true);
                    message.delete().queue();
                }, new Timeout(1, TimeUnit.MINUTES, () -> {
                    ReactionListener.unregisterListener(message, true);
                    message.delete().queue();
                }), ReactionPermissionFilter.createUserFilter(event.getAuthor()), true);
            });
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
    public PermissionFilter getPermissionFilter() {
        return Standard.STANDARD_PERMISSIONFILTER_BOT_SUPER_OWNER;
    }

    @Override
    public String getCommandID() {
        return getClass().getName();
    }

    @Override
    public CommandCategory getCommandCategory() {
        return Standard.COMMANDCATEGORY_MODERATION_UTIL;
    }

}
