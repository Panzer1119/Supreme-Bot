package de.codemakers.bot.supreme.commands.impl.secret;

import de.codemakers.bot.supreme.commands.Command;
import de.codemakers.bot.supreme.commands.CommandCategory;
import de.codemakers.bot.supreme.commands.arguments.ArgumentConsumeType;
import de.codemakers.bot.supreme.commands.arguments.ArgumentList;
import de.codemakers.bot.supreme.commands.invoking.Invoker;
import de.codemakers.bot.supreme.entities.MessageEvent;
import de.codemakers.bot.supreme.permission.PermissionFilter;
import de.codemakers.bot.supreme.util.Returner;
import de.codemakers.bot.supreme.util.Standard;
import de.codemakers.bot.supreme.util.SystemOutputStream;
import java.awt.Color;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;

/**
 * ConsoleCommand
 *
 * @author Panzer1119
 */
public class ConsoleCommand extends Command {

    @Override
    public void initInvokers() {
        addInvokers(Invoker.createInvoker("console", this));
    }

    @Override
    public boolean called(Invoker invoker, ArgumentList arguments, MessageEvent event) {
        if (arguments == null || arguments.isEmpty()) {
            return false;
        }
        final boolean toggle = arguments.isConsumed(Standard.ARGUMENT_TOGGLE, ArgumentConsumeType.FIRST_IGNORE_CASE);
        final boolean set = arguments.isConsumed(Standard.ARGUMENT_SET, ArgumentConsumeType.FIRST_IGNORE_CASE);
        final boolean get = arguments.isConsumed(Standard.ARGUMENT_GET, ArgumentConsumeType.FIRST_IGNORE_CASE);
        if (toggle) {
            return arguments.isSize(1);
        } else if (set) {
            return arguments.isSize(2, 3);
        } else if (get) {
            return arguments.isSize(1);
        }
        return false;
    }

    @Override
    public void action(Invoker invoker, ArgumentList arguments, MessageEvent event) {
        final boolean toggle = arguments.isConsumed(Standard.ARGUMENT_TOGGLE, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
        final boolean set = arguments.isConsumed(Standard.ARGUMENT_SET, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
        final boolean get = arguments.isConsumed(Standard.ARGUMENT_GET, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
        if (toggle) {
            if (!SystemOutputStream.isRedirecting() && Standard.getConsoleTextChannel() == null) {
                event.sendMessage(Standard.STANDARD_MESSAGE_DELETING_DELAY, Standard.getNoMessage(event.getAuthor(), "you have to set a home Guild and TextChannel!").build());
                return;
            }
            SystemOutputStream.setRedirecting(!SystemOutputStream.isRedirecting());
            if (Standard.getConsoleTextChannel() != null) {
                Standard.getConsoleTextChannel().sendMessage(String.format("%s %s console redirecting", event.getAuthor().getAsMention(), SystemOutputStream.isRedirecting() ? "enabled" : "disabled")).queue();
            } else {
                event.sendMessage(Standard.STANDARD_MESSAGE_DELETING_DELAY, String.format("%s disabled console redirecting", event.getAuthor().getAsMention()));
            }
        } else if (set) {
            final boolean setting_both = arguments.isSize(2);
            final long guild_id = setting_both ? Standard.resolveGuildId(event.getGuild(), arguments.consumeRawFirst()) : -1;
            final long textChannel_id = Standard.resolveTextChannelId(arguments.consumeRawFirst());
            String extra = "";
            if (setting_both) {
                Standard.STANDARD_SETTINGS.setProperty("home_guild_id", guild_id);
                extra = String.format("home Guild to %s and the ", guild_id == -1 ? "nothing" : Returner.of(Standard.getCompleteName(Standard.getGuildById(guild_id))).or("" + guild_id));
            }
            Standard.STANDARD_SETTINGS.setProperty("console_textchannel_id", textChannel_id);
            event.sendMessage(Standard.STANDARD_MESSAGE_DELETING_DELAY, Standard.getMessageEmbed(Color.YELLOW, "%s setted the %sconsole TextChannel to %s", event.getAuthor().getAsMention(), extra, textChannel_id == -1 ? "nothing" : "<#" + textChannel_id + ">").build());
        } else if (get) {
            final long guild_id = Standard.STANDARD_SETTINGS.getProperty("home_guild_id", -1L);
            final long textChannel_id = Standard.STANDARD_SETTINGS.getProperty("console_textchannel_id", -1L);
            event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s the home Guild is %s and the console TextChannel is %s, the console is currently %sredirected", event.getAuthor().getAsMention(), guild_id == -1 ? "nothing" : Returner.of(Standard.getCompleteName(Standard.getGuildById(guild_id))).or("" + guild_id), textChannel_id == -1 ? "nothing" : "<#" + textChannel_id + ">", SystemOutputStream.isRedirecting() ? "" : "not ");
        }
    }

    @Override
    public void executed(boolean success, MessageEvent event) {
        System.out.println("[INFO] Command '" + getCommandID() + "' was executed!");
    }

    @Override
    public EmbedBuilder getHelp(Invoker invoker, EmbedBuilder builder) {
        builder.addField(String.format("%s %s", invoker, Standard.ARGUMENT_TOGGLE.getCompleteArgument(0, -1)), String.format("Toggles the redirecting of the java console to the Discord console."), false);
        builder.addField(String.format("%s %s [Guild ID] <TextChannel (ID)>", invoker, Standard.ARGUMENT_SET.getCompleteArgument(0, -1)), String.format("Sets the home Guild and TextChannel for the Discord console."), false);
        builder.addField(String.format("%s %s", invoker, Standard.ARGUMENT_GET.getCompleteArgument(0, -1)), String.format("Gets the home Guild and TextChannel for the Discord console."), false);
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
        return Standard.COMMANDCATEGORY_SECRET;
    }

}
