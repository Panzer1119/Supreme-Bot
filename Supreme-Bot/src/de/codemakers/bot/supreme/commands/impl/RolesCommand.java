package de.codemakers.bot.supreme.commands.impl;

import de.codemakers.bot.supreme.commands.Command;
import de.codemakers.bot.supreme.commands.CommandCategory;
import de.codemakers.bot.supreme.commands.arguments.ArgumentConsumeType;
import de.codemakers.bot.supreme.commands.arguments.ArgumentList;
import de.codemakers.bot.supreme.commands.invoking.Invoker;
import de.codemakers.bot.supreme.entities.MessageEvent;
import de.codemakers.bot.supreme.permission.PermissionRole;
import de.codemakers.bot.supreme.util.Emoji;
import de.codemakers.bot.supreme.util.Standard;
import java.awt.Color;
import java.util.List;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import de.codemakers.bot.supreme.permission.PermissionFilter;

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
        final Guild guild = event.getGuild();
        final Member member = event.getMember();
        final StringBuilder sb_1 = new StringBuilder();
        final StringBuilder sb_2 = new StringBuilder();
        int count = 0;
        final List<Role> roles = guild.getRoles();
        for (Role role : roles) {
            final boolean isMe = member.getRoles().contains(role);
            final StringBuilder temp = new StringBuilder();
            temp.append("[");
            temp.append(count);
            temp.append("] ");
            if (asMention) {
                temp.append(role.getAsMention());
            } else {
                temp.append(role.getName());
            }
            if (id) {
                temp.append(String.format(" (ID: %s)", role.getId()));
            }
            if (isMe) {
                sb_1.append(Standard.toBold(temp.toString()));
            } else {
                sb_1.append(temp.toString());
            }
            sb_1.append("\n");
            temp.delete(0, temp.length());
            final List<PermissionRole> permissionRoles_ = PermissionRole.getPermissionRolesByGuildAndRole(guild.getIdLong(), role.getIdLong());
            temp.append("[");
            temp.append(count);
            temp.append("] ");
            if (!permissionRoles_.isEmpty()) {
                for (PermissionRole permissionRole : permissionRoles_) {
                    temp.append(permissionRole.getPermissionRoleName());
                    if (id) {
                        temp.append(String.format(" (ID: %s)", permissionRole.getPermissionRoleID()));
                    }
                    temp.append(", ");
                }
                temp.delete(temp.length() - ", ".length(), temp.length());
                permissionRoles_.clear();
            } else {
                temp.append(Standard.toItalics("none"));
            }
            if (isMe) {
                sb_2.append(Standard.toBold(temp.toString()));
            } else {
                sb_2.append(temp.toString());
            }
            sb_2.append("\n");
            temp.delete(0, temp.length());
            count++;
        }
        final EmbedBuilder builder = Standard.getMessageEmbed(Color.YELLOW, "%s here are the Discord/Bot Roles\nYour Roles are bold%s", event.getAuthor().getAsMention(), (id ? String.format(" (Your ID: %s)", event.getAuthor().getId()) : ""));
        builder.addField("Discord Roles", sb_1.toString(), true);
        builder.addField("Bot Roles", sb_2.toString(), true);
        event.sendMessage(builder.build());
        sb_1.delete(0, sb_1.length());
        sb_2.delete(0, sb_2.length());
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
