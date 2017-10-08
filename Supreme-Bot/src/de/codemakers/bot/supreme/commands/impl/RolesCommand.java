package de.codemakers.bot.supreme.commands.impl;

import de.codemakers.bot.supreme.commands.Command;
import de.codemakers.bot.supreme.commands.CommandCategory;
import de.codemakers.bot.supreme.commands.arguments.ArgumentConsumeType;
import de.codemakers.bot.supreme.commands.arguments.ArgumentList;
import de.codemakers.bot.supreme.commands.invoking.Invoker;
import de.codemakers.bot.supreme.entities.MessageEvent;
import de.codemakers.bot.supreme.permission.GlobalBotRole;
import de.codemakers.bot.supreme.permission.GuildBotRole;
import de.codemakers.bot.supreme.util.Emoji;
import de.codemakers.bot.supreme.util.Standard;
import java.awt.Color;
import java.util.List;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import de.codemakers.bot.supreme.permission.PermissionFilter;
import java.util.stream.Collectors;
import net.dv8tion.jda.core.entities.User;

/**
 * RolesCommand
 *
 * @author Panzer1119
 */
public class RolesCommand extends Command {

    @Override
    public void initInvokers() {
        addInvokers(Invoker.createInvoker("roles", this), Invoker.createInvoker("role", this), Invoker.createInvoker("r", this));
    }

    @Override
    public boolean called(Invoker invoker, ArgumentList arguments, MessageEvent event) {
        if (arguments == null) {
            return true;
        }
        final boolean id = arguments.isConsumed(Standard.ARGUMENT_ID, ArgumentConsumeType.FIRST_IGNORE_CASE);
        final boolean asMention = arguments.isConsumed(Standard.ARGUMENT_ASMENTION, ArgumentConsumeType.FIRST_IGNORE_CASE);
        if (id && asMention) {
            return arguments.isSize(2, 3);
        } else if (id || asMention) {
            return arguments.isSize(1, 2);
        }
        return true;
    }

    @Override
    public void action(Invoker invoker, ArgumentList arguments, MessageEvent event) {
        final boolean id = (arguments != null) && arguments.isConsumed(Standard.ARGUMENT_ID, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
        final boolean asMention = (arguments != null) && arguments.isConsumed(Standard.ARGUMENT_ASMENTION, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
        final String command_string = (arguments != null ? arguments.consumeFirst() : null);
        if (command_string != null) {
            event.sendMessage(Standard.STANDARD_MESSAGE_DELETING_DELAY, String.format("%s Sorry %s, this feature isn't supported yet!", Emoji.WARNING, event.getAuthor().getAsMention()));
            return;
        }
        final Guild guild = (event.isPrivate() ? null : event.getGuild());
        final User user = event.getAuthor();
        final Member member = (event.isPrivate() ? null : event.getMember());
        final List<Role> roles_guild = (event.isPrivate() ? null : guild.getRoles());
        final List<Role> roles_member = (event.isPrivate() ? null : member.getRoles());
        final String s_1 = event.isPrivate() ? "" : roles_guild.stream().map((role) -> {
            String temp = asMention ? role.getAsMention() : role.getName();
            temp += id ? String.format(" (ID: %s)", role.getId()) : "";
            return roles_member.contains(role) ? Standard.toBold(temp) : temp;
        }).collect(Collectors.joining(Standard.NEW_LINE_DISCORD));
        final String s_2 = event.isPrivate() ? "" : GuildBotRole.stream().map((guildBotRole) -> {
            String temp = guildBotRole.getName();
            temp += id ? String.format(" (ID: %d)", guildBotRole.getId()) : "";
            return GuildBotRole.getGuildBotRolesByMember(member).contains(guildBotRole) ? Standard.toBold(temp) : temp;
        }).collect(Collectors.joining(Standard.NEW_LINE_DISCORD));
        final String s_3 = GlobalBotRole.stream().map((globalBotRole) -> {
            String temp = globalBotRole.getName();
            temp += id ? String.format(" (ID: %d)", globalBotRole.getId()) : "";
            return GlobalBotRole.getGlobalBotRolesByUser(user).contains(globalBotRole) ? Standard.toBold(temp) : temp;
        }).collect(Collectors.joining(Standard.NEW_LINE_DISCORD));
        final EmbedBuilder builder = Standard.getMessageEmbed(Color.YELLOW, "%s your Roles are bold%s", event.getAuthor().getAsMention(), (id ? String.format(" (Your ID: %s)", event.getAuthor().getId()) : ""));
        if (!event.isPrivate()) {
            builder.addField("Guild Roles", s_1, true);
            builder.addField("Guild Bot Roles", s_2, true);
        }
        builder.addField("Global Bot Roles", s_3, true);
        event.sendMessage(builder.build());
    }

    @Override
    public void executed(boolean success, MessageEvent event) {
        System.out.println("[INFO] Command '" + getCommandID() + "' was executed!");
    }

    @Override
    public EmbedBuilder getHelp(Invoker invoker, EmbedBuilder builder) {
        builder.addField(String.format("%s [%s] [%s]", invoker, Standard.ARGUMENT_ID.getCompleteArgument(0, -1), Standard.ARGUMENT_ASMENTION.getCompleteArgument(0, -1)), String.format("Shows all (your) Roles. With the flag \"%s\" the IDs will also be printed and with \"%s\" the Discord Roles will be printed asMention.", Standard.ARGUMENT_ID.getCompleteArgument(0, -1), Standard.ARGUMENT_ASMENTION.getCompleteArgument(0, -1)), false);
        builder.addField(String.format("%s <Command> [%s] [%s]", invoker, Standard.ARGUMENT_ID.getCompleteArgument(0, -1), Standard.ARGUMENT_ASMENTION.getCompleteArgument(0, -1)), String.format("Shows all Roles that are able to use a command. With the flag \"%s\" the IDs will also be printed and with \"%s\" the Discord Roles will be printed asMention.", Standard.ARGUMENT_ID.getCompleteArgument(0, -1), Standard.ARGUMENT_ASMENTION.getCompleteArgument(0, -1)), false);
        return builder;
    }

    @Override
    public PermissionFilter getPermissionFilter() {
        return null;
    }

    @Override
    public String getCommandID() {
        return getClass().getName();
    }

    @Override
    public CommandCategory getCommandCategory() {
        return Standard.COMMANDCATEGORY_NORMAL;
    }

}
