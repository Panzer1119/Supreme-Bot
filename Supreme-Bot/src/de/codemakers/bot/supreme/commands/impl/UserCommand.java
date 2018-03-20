package de.codemakers.bot.supreme.commands.impl;

import de.codemakers.bot.supreme.commands.Command;
import de.codemakers.bot.supreme.commands.CommandCategory;
import de.codemakers.bot.supreme.commands.arguments.ArgumentConsumeType;
import de.codemakers.bot.supreme.commands.arguments.ArgumentList;
import de.codemakers.bot.supreme.commands.invoking.Invoker;
import de.codemakers.bot.supreme.entities.MessageEvent;
import de.codemakers.bot.supreme.permission.PermissionFilter;
import de.codemakers.bot.supreme.permission.PermissionHandler;
import de.codemakers.bot.supreme.util.Standard;
import java.awt.Color;
import java.time.Instant;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.User;

/**
 * UserCommand
 *
 * @author Panzer1119
 */
public class UserCommand extends Command {

    @Override
    public void initInvokers() {
        addInvokers(Invoker.createInvoker("user", this));
    }

    @Override
    public boolean called(Invoker invoker, ArgumentList arguments, MessageEvent event) {
        final boolean advanced = arguments.isConsumed(Standard.ARGUMENT_ADVANCED, ArgumentConsumeType.FIRST_IGNORE_CASE);
        if (advanced) {
            return arguments.isSize(1, 2);
        }
        return arguments.isSize(0, 1);
    }

    @Override
    public void action(Invoker invoker, ArgumentList arguments, MessageEvent event) {
        final boolean advanced = arguments.isConsumed(Standard.ARGUMENT_ADVANCED, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
        if (advanced) {
            if (!PermissionHandler.isPermissionGranted(Standard.STANDARD_PERMISSIONFILTER_GUILD_MODERATOR_BOT_COMMANDER_BOT_ADMIN, event.getMember())) {
                PermissionHandler.sendNoPermissionMessage(event);
                return;
            }
        }
        User user = null;
        if (!arguments.isEmpty()) {
            if (!PermissionHandler.isPermissionGranted(Standard.STANDARD_PERMISSIONFILTER_GUILD_MODERATOR_BOT_COMMANDER_BOT_ADMIN, event.getMember())) {
                PermissionHandler.sendNoPermissionMessage(event);
                return;
            }
            user = arguments.consumeUserFirst();
        } else {
            user = event.getAuthor();
        }
        if (user == null) {
            return;
        }
        final Instant creationTime = Standard.getUTCCreationTime(user.getIdLong());
        final EmbedBuilder builder = Standard.getMessageEmbed(Color.GREEN, "%s for %s", Standard.toBold("User Information"), user.getAsMention())
                .addField("Name", user.getName(), false)
                .addField("Discriminator", "" + user.getDiscriminator(), false)
                .addField("Created", creationTime.atZone(Standard.getZoneId()).format(Standard.STANDARD_DATE_TIME_FORMATTER), false)
                .addField("ID", "" + user.getId(), false);
        if (advanced) {
            builder.addField("Bot", "" + user.isBot(), false);
            builder.addField("Avatar ID", "" + user.getAvatarId(), false);
            builder.addField("Avatar URL", "" + user.getAvatarUrl(), false);
            builder.addField("Default Avatar ID", "" + user.getDefaultAvatarId(), false);
            builder.addField("Default Avatar URL", "" + user.getDefaultAvatarUrl(), false);
            builder.addField("Effective Avatar URL", "" + user.getEffectiveAvatarUrl(), false);
        }
        event.sendMessage(builder.build());
    }

    @Override
    public void executed(boolean success, MessageEvent event) {
        System.out.println("[INFO] Command '" + getCommandID() + "' was executed!");
    }

    @Override
    public EmbedBuilder getHelp(Invoker invoker, EmbedBuilder builder) {
        builder.addField(String.format("%s [User] [%s]", invoker, Standard.ARGUMENT_ADVANCED.getCompleteArgument(0, -1)), String.format("Shows information about you/another user. With %s you see more information.", Standard.ARGUMENT_ADVANCED.getCompleteArgument(0, -1)), false);
        return builder;
    }

    @Override
    public PermissionFilter getPermissionFilter() {
        return Standard.STANDARD_PERMISSIONFILTER_BOTH_EVERYONE;
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
