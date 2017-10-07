package de.codemakers.bot.supreme.commands.impl.moderation;

import de.codemakers.bot.supreme.commands.Command;
import de.codemakers.bot.supreme.commands.CommandCategory;
import de.codemakers.bot.supreme.commands.arguments.ArgumentConsumeType;
import de.codemakers.bot.supreme.commands.arguments.ArgumentList;
import de.codemakers.bot.supreme.commands.invoking.Invoker;
import de.codemakers.bot.supreme.entities.MessageEvent;
import de.codemakers.bot.supreme.permission.PermissionHandler;
import de.codemakers.bot.supreme.util.Standard;
import de.codemakers.bot.supreme.util.Util;
import java.awt.Color;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import de.codemakers.bot.supreme.permission.PermissionFilter;

/**
 * SettingsCommand
 *
 * @author Panzer1119
 */
public class SettingsCommand extends Command { //TODO Info command hinzufuegen (der vielleicht noch je nach permission level mehr informationen anzeigt (ueber einen channel/member(/user)))

    @Override
    public void initInvokers() {
        addInvokers(Invoker.createInvoker("settings", this), Invoker.createInvoker("s", this));
    }

    @Override
    public boolean called(Invoker invoker, ArgumentList arguments, MessageEvent event) {
        if (arguments == null || arguments.isEmpty()) {
            return false;
        }
        final boolean set = arguments.isConsumed(Standard.ARGUMENT_SET, ArgumentConsumeType.FIRST_IGNORE_CASE);
        final boolean get = arguments.isConsumed(Standard.ARGUMENT_GET, ArgumentConsumeType.FIRST_IGNORE_CASE);
        final boolean get_default = arguments.consume(Standard.ARGUMENT_DEFAULT, ArgumentConsumeType.FIRST_IGNORE_CASE, 3);
        final boolean remove = arguments.isConsumed(Standard.ARGUMENT_REMOVE, ArgumentConsumeType.FIRST_IGNORE_CASE);
        final boolean list = arguments.isConsumed(Standard.ARGUMENT_LIST, ArgumentConsumeType.FIRST_IGNORE_CASE);
        if (set) {
            return arguments.isSize(3, 4);
        } else if (get && !get_default) {
            return arguments.isSize(2, 3);
        } else if (get && get_default) {
            return arguments.isSize(4, 5);
        } else if (remove) {
            return arguments.isSize(2, 3);
        } else if (list) {
            return arguments.isSize(1, 2);
        } else {
            return false;
        }
    }

    @Override
    public void action(Invoker invoker, ArgumentList arguments, MessageEvent event) {
        final boolean set = arguments.isConsumed(Standard.ARGUMENT_SET, ArgumentConsumeType.CONSUME_ALL_IGNORE_CASE);
        final boolean get = arguments.isConsumed(Standard.ARGUMENT_GET, ArgumentConsumeType.CONSUME_ALL_IGNORE_CASE);
        final boolean remove = arguments.isConsumed(Standard.ARGUMENT_REMOVE, ArgumentConsumeType.CONSUME_ALL_IGNORE_CASE);
        final boolean list = arguments.isConsumed(Standard.ARGUMENT_LIST, ArgumentConsumeType.CONSUME_ALL_IGNORE_CASE);
        String guild_id = null;
        String key = "";
        String value = null;
        String value_temp = null;
        boolean sendPrivate = false;
        if (set) {
            if (arguments.isSize(3)) {
                guild_id = Standard.resolveGuildId(event.getGuild(), arguments.consumeFirst());
            }
            key = arguments.consumeFirst();
            if (Util.contains(Standard.ULTRA_FORBIDDEN, key) && !Standard.isSuperOwner(event.getAuthor())) {
                PermissionHandler.sendNoPermissionMessage(event);
                return;
            } else if (Standard.isSuperOwner(event.getAuthor())) {
                sendPrivate = true;
            }
            value = arguments.consumeFirst();
            if (guild_id == null) {
                value_temp = Standard.STANDARD_SETTINGS.getProperty(key, null);
                Standard.STANDARD_SETTINGS.setProperty(key, value);
                Standard.reloadSettings();
                final MessageEmbed message = Standard.getMessageEmbed(Color.YELLOW, event.getAuthor().getAsMention() + " set").addField(key + " old:", "" + value_temp, false).addField(key + " new:", "" + value, false).build();
                if (!sendPrivate) {
                    event.sendMessage(message);
                } else {
                    Util.sendPrivateMessage(event.getAuthor(), message);
                }
            } else {
                value_temp = Standard.getGuildSettings(guild_id).getProperty(key, null);
                Standard.getGuildSettings(guild_id).setProperty(key, value);
                final MessageEmbed message = Standard.getMessageEmbed(Color.YELLOW, "%s %s (ID: %s) set", event.getAuthor().getAsMention(), Standard.getGuildById(guild_id).getName(), guild_id).addField(key + " old:", "" + value_temp, false).addField(key + " new:", "" + value, false).build();
                if (!sendPrivate) {
                    event.sendMessage(message);
                } else {
                    Util.sendPrivateMessage(event.getAuthor(), message);
                }
            }
        } else if (get) {
            final boolean get_default = arguments.consume(Standard.ARGUMENT_DEFAULT, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE, 2);
            if (arguments.isSize(2) || (get_default && arguments.isSize(3))) {
                guild_id = Standard.resolveGuildId(event.getGuild(), arguments.consumeFirst());
            }
            key = arguments.consumeFirst();
            if (get_default && arguments.isSize(1)) {
                value_temp = arguments.consumeFirst();
            }
            if (Util.contains(Standard.ULTRA_FORBIDDEN, key) && !Standard.isSuperOwner(event.getAuthor())) {
                PermissionHandler.sendNoPermissionMessage(event);
                return;
            } else if (Standard.isSuperOwner(event.getAuthor())) {
                sendPrivate = true;
            }
            if (guild_id == null) {
                value = Standard.STANDARD_SETTINGS.getProperty(key, value_temp);
                final MessageEmbed message = Standard.getMessageEmbed(Color.YELLOW, event.getAuthor().getAsMention() + " get").addField("" + key, "" + value, false).build();
                if (!sendPrivate) {
                    event.sendMessage(message);
                } else {
                    Util.sendPrivateMessage(event.getAuthor(), message);
                }
            } else {
                value = Standard.getGuildSettings(guild_id).getProperty(key, value_temp);
                final MessageEmbed message = Standard.getMessageEmbed(Color.YELLOW, "%s %s (ID: %s) get", event.getAuthor().getAsMention(), Standard.getGuildById(guild_id).getName(), guild_id).addField("" + key, "" + value, false).build();
                if (!sendPrivate) {
                    event.sendMessage(message);
                } else {
                    Util.sendPrivateMessage(event.getAuthor(), message);
                }
            }
        } else if (remove) {
            if (arguments.isSize(2)) {
                guild_id = Standard.resolveGuildId(event.getGuild(), arguments.consumeFirst());
            }
            key = arguments.consumeFirst();
            if (Util.contains(Standard.ULTRA_FORBIDDEN, key)) {
                PermissionHandler.sendNoPermissionMessage(event);
                return;
            }
            if (guild_id == null) {
                value = Standard.STANDARD_SETTINGS.getProperty(key, null);
                Standard.STANDARD_SETTINGS.removeProperty(key);
                event.sendMessage(Standard.getMessageEmbed(Color.YELLOW, event.getAuthor().getAsMention() + " removed").addField("" + key, "" + value, false).build());
            } else {
                value = Standard.getGuildSettings(guild_id).getProperty(key, null);
                Standard.getGuildSettings(guild_id).removeProperty(key);
                event.sendMessage(Standard.getMessageEmbed(Color.YELLOW, "%s %s (ID: %s) removed", event.getAuthor().getAsMention(), Standard.getGuildById(guild_id).getName(), guild_id).addField("" + key, "" + value, false).build());
            }
        } else if (list) {
            if (arguments.isSize(1)) {
                guild_id = Standard.resolveGuildId(event.getGuild(), arguments.consumeFirst());
            }
            if (guild_id == null) {
                event.sendMessage(Standard.STANDARD_SETTINGS.toEmbed(new EmbedBuilder().setDescription(event.getAuthor().getAsMention() + " list")).build());
            } else {
                event.sendMessage(Standard.getGuildSettings(guild_id).toEmbed(new EmbedBuilder().setDescription(event.getAuthor().getAsMention() + " list")).build());
            }
        }
    }

    @Override
    public void executed(boolean success, MessageEvent event) {
        System.out.println("[INFO] Command '" + getCommandID() + "' was executed!");
    }

    @Override
    public EmbedBuilder getHelp(Invoker invoker, EmbedBuilder builder) {
        builder.addField(String.format("%s %s [Guild ID] <Key> <Value>", invoker, Standard.ARGUMENT_SET.getCompleteArgument(0, -1)), "Sets the value for the key. If a valid guild id is given, then the guild settings will be edited.", false);
        builder.addField(String.format("%s %s [Guild ID] <Key> [%s Default Value]", invoker, Standard.ARGUMENT_GET.getCompleteArgument(0, -1), Standard.ARGUMENT_DEFAULT.getCompleteArgument(0, -1)), "Gets the value for the key. If a valid guild id is given, then the guild settings will be edited.", false);
        builder.addField(String.format("%s %s [Guild ID] <Key>", invoker, Standard.ARGUMENT_REMOVE.getCompleteArgument(0, -1)), "Removes the key and value. If a valid guild id is given, then the guild settings will be edited.", false);
        builder.addField(String.format("%s %s [Guild ID]", invoker, Standard.ARGUMENT_LIST.getCompleteArgument(0, -1)), "Lists all keys and values. If a valid guild id is given, then the guild settings will be edited.", false);
        return builder;
    }

    @Override
    public PermissionFilter getPermissionFilter() {
        return Standard.STANDARD_PERMISSIONFILTER_BOT_COMMANDER;
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
