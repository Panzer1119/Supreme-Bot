package de.codemakers.bot.supreme.commands.impl.moderation;

import de.codemakers.bot.supreme.commands.Command;
import de.codemakers.bot.supreme.commands.CommandCategory;
import de.codemakers.bot.supreme.commands.arguments.ArgumentConsumeType;
import de.codemakers.bot.supreme.commands.arguments.ArgumentList;
import de.codemakers.bot.supreme.commands.invoking.Invoker;
import de.codemakers.bot.supreme.entities.MessageEvent;
import de.codemakers.bot.supreme.util.Emoji;
import de.codemakers.bot.supreme.util.Standard;
import java.awt.Color;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.requests.RestAction;
import de.codemakers.bot.supreme.permission.PermissionFilter;

/**
 * Copy Command
 *
 * @author Panzer1119
 */
public class CopyCommand extends Command {

    @Override
    public void initInvokers() {
        addInvokers(Invoker.createInvoker("copy", this), Invoker.createInvoker("c", this));
    }

    @Override
    public boolean called(Invoker invoker, ArgumentList arguments, MessageEvent event) {
        if (arguments == null || arguments.isEmpty()) {
            return false;
        }
        //Example: !copy message "MESSAGE_ID" "CHANNEL_TAG" [-server "GUILD_ID"]
        //       Invoker arg1    arg2         arg3           arg4    arg5
        final boolean message = arguments.isConsumed(Standard.ARGUMENT_MESSAGE, ArgumentConsumeType.FIRST_IGNORE_CASE);
        final boolean server = arguments.isConsumed(Standard.ARGUMENT_SERVER, ArgumentConsumeType.FIRST_IGNORE_CASE);
        if (server) {
            if (message) {
                return arguments.isSize(5);
            } else {
                return false;
            }
        } else if (message) {
            return arguments.isSize(3);
        } else {
            return false;
        }
    }

    @Override
    public void action(Invoker invoker, ArgumentList arguments, MessageEvent event) {
        final JDA jda = Standard.getJDA();
        if (jda == null) {
            event.sendMessage(Standard.STANDARD_MESSAGE_DELETING_DELAY, Standard.getMessageEmbed(Color.RED, "%s Sorry %s, the Bot has no connection!", Emoji.WARNING, event.getAuthor().getAsMention()).build());
            return;
        }
        final boolean message = arguments.isConsumed(Standard.ARGUMENT_MESSAGE, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
        String guild_id = null;
        final int server = arguments.consume(Standard.ARGUMENT_SERVER, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE, true);
        if (server != -1) {
            guild_id = arguments.consume(server);
        }
        if (message) {
            final String message_id = arguments.consumeFirst();
            final String channel_id = arguments.consumeFirst();
            Guild guild = null;
            RestAction<Message> msg = null;
            if (guild_id != null) {
                event.sendMessageFormat("message_id: %s, channel_id: %s, guild_id: %s", message_id, channel_id, guild_id);
                guild = jda.getGuildById(guild_id);
            } else {
                event.sendMessageFormat("message_id: %s, channel_id: %s", message_id, channel_id);
                guild = jda.getGuildById(event.getGuild().getId());
            }
            System.out.println("FOUND GUILD: " + guild);
            msg = event.getMessageChannel().getMessageById(message_id);
            if (msg != null) {
                if (guild != null) {
                    final TextChannel channel = guild.getTextChannelById(channel_id);
                    if (channel != null) {
                        channel.sendMessage(msg.complete()).queue();
                    } else {
                        event.sendMessage(Standard.STANDARD_MESSAGE_DELETING_DELAY, Standard.getMessageEmbed(Color.RED, "%s Sorry %s, you'r channel \"%s\" wasn't found!", Emoji.WARNING, event.getAuthor().getAsMention(), channel_id).build());
                    }
                } else {
                    event.sendMessage(Standard.STANDARD_MESSAGE_DELETING_DELAY, Standard.getMessageEmbed(Color.RED, "%s Sorry %s, you'r server \"%s\" wasn't found!", Emoji.WARNING, event.getAuthor().getAsMention(), (guild_id != null ? guild_id : event.getGuild().getId())).build());
                }
            } else {
                event.sendMessage(Standard.STANDARD_MESSAGE_DELETING_DELAY, Standard.getMessageEmbed(Color.RED, "%s Sorry %s, you'r message \"%s\" wasn't found!", Emoji.WARNING, event.getAuthor().getAsMention(), message_id).build());
            }
        } else {
            event.sendMessage(Standard.STANDARD_MESSAGE_DELETING_DELAY, Standard.getMessageEmbed(Color.RED, "%s Sorry %s, this operation isn't supported yet!", Emoji.WARNING, event.getAuthor().getAsMention()).build());
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
        return Standard.STANDARD_PERMISSIONFILTER_GUILD_MODERATOR_BOT_COMMANDER_BOT_ADMIN;
    }

    @Override
    public String getCommandID() {
        return getClass().getName();
    }

    @Override
    public CommandCategory getCommandCategory() {
        return Standard.COMMANDCATEGORY_MODERATION;
    }

}
