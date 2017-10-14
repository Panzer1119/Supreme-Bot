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
import de.codemakers.bot.supreme.settings.Config;
import de.codemakers.bot.supreme.settings.ConfigType;
import de.codemakers.bot.supreme.sql.ConfigData;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;

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
        final boolean remove = arguments.isConsumed(Standard.ARGUMENT_REMOVE, ArgumentConsumeType.FIRST_IGNORE_CASE);
        final boolean list = arguments.isConsumed(Standard.ARGUMENT_LIST, ArgumentConsumeType.FIRST_IGNORE_CASE);
        if (set) {
            return arguments.isSize(3, 5);
        } else if (get) {
            return arguments.isSize(2, 5);
        } else if (remove) {
            return arguments.isSize(2, 4);
        } else if (list) {
            return arguments.isSize(1, 3);
        } else {
            return false;
        }
    }

    @Override
    public void action(Invoker invoker, ArgumentList arguments, MessageEvent event) {
        final boolean set = arguments.isConsumed(Standard.ARGUMENT_SET, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
        final boolean get = arguments.isConsumed(Standard.ARGUMENT_GET, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
        final boolean remove = arguments.isConsumed(Standard.ARGUMENT_REMOVE, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
        final boolean list = arguments.isConsumed(Standard.ARGUMENT_LIST, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
        long guild_id = 0;
        long user_id = 0;
        if (set && arguments.isSize(3, -1)) {
            User user = arguments.consumeUserFirst();
            if (user != null) {
                user_id = user.getIdLong();
            }
            String first = arguments.getFirst();
            guild_id = Standard.resolveGuildId(event.getGuild(), first);
            if (guild_id >= 0) {
                arguments.consumeFirst();
            }
            if (user == null) {
                user = arguments.consumeUserFirst();
            }
            if (user != null) {
                user_id = user.getIdLong();
            } else {
                user_id = Standard.resolveUserId(event.getAuthor(), (guild_id >= 0 && arguments.isSize(3, -1)) ? arguments.getFirst() : first);
                if (user_id >= 0) {
                    arguments.consumeFirst();
                }
            }
        } else if ((remove || get) && arguments.isSize(2, -1)) {
            User user = arguments.consumeUserFirst();
            if (user != null) {
                user_id = user.getIdLong();
            }
            String first = arguments.getFirst();
            guild_id = Standard.resolveGuildId(event.getGuild(), first);
            if (guild_id >= 0) {
                arguments.consumeFirst();
            }
            if (user == null) {
                user = arguments.consumeUserFirst();
            }
            if (user != null) {
                user_id = user.getIdLong();
            } else {
                user_id = Standard.resolveUserId(event.getAuthor(), (guild_id >= 0 && arguments.isSize(2, -1)) ? arguments.getFirst() : first);
                if (user_id >= 0) {
                    arguments.consumeFirst();
                }
            }
        } else if (list && arguments.isSize(1, -1)) {
            User user = arguments.consumeUserFirst();
            if (user != null) {
                user_id = user.getIdLong();
            }
            String first = arguments.getFirst();
            guild_id = Standard.resolveGuildId(event.getGuild(), first);
            if (guild_id >= 0) {
                arguments.consumeFirst();
            }
            if (user == null) {
                user = arguments.consumeUserFirst();
            }
            if (user != null) {
                user_id = user.getIdLong();
            } else {
                user_id = Standard.resolveUserId(event.getAuthor(), (guild_id >= 0 && arguments.isSize(1, -1)) ? arguments.getFirst() : first);
                if (user_id >= 0) {
                    arguments.consumeFirst();
                }
            }
        }
        guild_id = Math.max(0, guild_id);
        user_id = Math.max(0, user_id);
        final ConfigType configType = ConfigType.of(guild_id, user_id);
        final Guild guild = Standard.getGuildById(guild_id);
        final User user = Standard.getUserById(user_id);
        String key = "";
        String value = null;
        String value_temp = null;
        boolean sendPrivate = false;
        if (set) {
            key = arguments.consumeFirst();
            if (!isPermissionGranted(configType, guild_id, user_id, key, event)) {
                PermissionHandler.sendNoPermissionMessage(event);
                return;
            }
            sendPrivate = isPrivateNeeded(configType, event);
            value = arguments.consumeFirst();
            value_temp = Config.CONFIG.getValue(guild_id, user_id, key);
            Config.CONFIG.setValue(guild_id, user_id, key, value);
            final MessageEmbed message = Standard.getMessageEmbed(Color.YELLOW, getText(guild_id, user_id, guild, user, configType, "setted", event)).addField("\"" + key + "\" old:", "" + value_temp, false).addField("\"" + key + "\" new:", "" + value, false).build();
            if (sendPrivate) {
                Util.sendPrivateMessage(event.getAuthor(), message);
            } else {
                event.sendMessage(message);
            }
        } else if (get) {
            key = arguments.consumeFirst();
            if (!isPermissionGranted(configType, guild_id, user_id, key, event)) {
                PermissionHandler.sendNoPermissionMessage(event);
                return;
            }
            sendPrivate = isPrivateNeeded(configType, event);
            if (arguments.isSize(1)) {
                value_temp = arguments.consumeFirst();
            }
            final String value_temp_ = value_temp;
            value = Config.CONFIG.getValue(guild_id, user_id, key, () -> value_temp_);
            final MessageEmbed message = Standard.getMessageEmbed(Color.YELLOW, getText(guild_id, user_id, guild, user, configType, "getted", event)).addField(key, value, false).build();
            if (sendPrivate) {
                Util.sendPrivateMessage(event.getAuthor(), message);
            } else {
                event.sendMessage(message);
            }
        } else if (remove) {
            key = arguments.consumeFirst();
            if (!isPermissionGranted(configType, guild_id, user_id, key, event)) {
                PermissionHandler.sendNoPermissionMessage(event);
                return;
            }
            sendPrivate = isPrivateNeeded(configType, event);
            value = Config.CONFIG.getValue(guild_id, user_id, key);
            final ConfigData configData = Config.CONFIG.getConfigData(guild_id, user_id, key);
            if (configData != null) {
                configData.delete();
            }
            final MessageEmbed message = Standard.getMessageEmbed(Color.YELLOW, getText(guild_id, user_id, guild, user, configType, "removed" + (configData == null ? " not" : ""), event)).addField(key, value, false).build();
            if (sendPrivate) {
                Util.sendPrivateMessage(event.getAuthor(), message);
            } else {
                event.sendMessage(message);
            }
        } else if (list) {
            if (!isPermissionGranted(configType, guild_id, user_id, null, event)) {
                PermissionHandler.sendNoPermissionMessage(event);
                return;
            }
            sendPrivate = isPrivateNeeded(configType, event);
            final MessageEmbed message = Config.CONFIG.toEmbedBuilder(guild_id, user_id).setDescription(getText(guild_id, user_id, guild, user, configType, "listed", event)).build();
            if (sendPrivate) {
                Util.sendPrivateMessage(event.getAuthor(), message);
            } else {
                event.sendMessage(message);
            }
        }
    }

    @Override
    public void executed(boolean success, MessageEvent event) {
        System.out.println("[INFO] Command '" + getCommandID() + "' was executed!");
    }

    @Override
    public EmbedBuilder getHelp(Invoker invoker, EmbedBuilder builder) {
        builder.addField(String.format("%s [Guild ID or 0] [User ID or 0] %s <Key> <Value>", invoker, Standard.ARGUMENT_SET.getCompleteArgument(0, -1)), "Sets the value for the key. Use \"this\" as the guild id for referencing the current guild, or \"me\" to reference your personal settings. Or set an id to 0 to not set the setting for it.", false);
        builder.addField(String.format("%s [Guild ID or 0] [User ID or 0] %s <Key> [Default Value]", invoker, Standard.ARGUMENT_GET.getCompleteArgument(0, -1)), "Gets the value for the key. You can set a default value that gets returned, if the normal value is null. Use \"this\" as the guild id for referencing the current guild, or \"me\" to reference your personal settings. Or set an id to 0 to not get the setting for it.", false);
        builder.addField(String.format("%s [Guild ID or 0] [User ID or 0] %s <Key>", invoker, Standard.ARGUMENT_REMOVE.getCompleteArgument(0, -1)), "Removes the key and value. Use \"this\" as the guild id for referencing the current guild, or \"me\" to reference your personal settings. Or set an id to 0 to not remove the setting for it.", false);
        builder.addField(String.format("%s [Guild ID or 0] [User ID or 0] %s", invoker, Standard.ARGUMENT_LIST.getCompleteArgument(0, -1)), "Lists all keys and values. Use \"this\" as the guild id for referencing the current guild, or \"me\" to reference your personal settings. Or set an id to 0 to not list the setting for it.", false);
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

    private static final boolean isPermissionGranted(ConfigType configType, long guild_id, long user_id, String key, MessageEvent event) {
        if (!Util.contains(Standard.ULTRA_FORBIDDEN, key) || configType != ConfigType.BOT_CONFIG) {
            switch (configType) {
                case BOT_CONFIG:
                    break;
                case GUILD_CONFIG:
                    if (guild_id == event.getGuild().getIdLong() && PermissionHandler.isPermissionGranted(Standard.STANDARD_PERMISSIONFILTER_GUILD_ADMIN_BOT_COMMANDER, event.getMember())) {
                        return true;
                    }
                case GUILD_USER_CONFIG:
                case USER_CONFIG:
                    if (user_id == event.getAuthor().getIdLong()) {
                        return true;
                    }
            }
        }
        return Standard.isSuperOwner(event.getAuthor());
    }

    private static final boolean isPrivateNeeded(ConfigType configType, MessageEvent event) {
        return event.isPrivate() || configType != ConfigType.GUILD_CONFIG || !PermissionHandler.isPermissionGranted(Standard.STANDARD_PERMISSIONFILTER_VIP, event.getTextChannel());
    }

    private static final String getText(long guild_id, long user_id, Guild guild, User user, ConfigType configType, String extra, MessageEvent event) {
        switch (configType) {
            case BOT_CONFIG:
                return String.format("%s %s for the bot", event.getAuthor().getAsMention(), extra);
            case GUILD_CONFIG:
                return String.format("%s %s for the guild \"%s\" (ID: %d)", event.getAuthor().getAsMention(), extra, guild == null ? "" : guild.getName(), guild_id);
            case GUILD_USER_CONFIG:
                return String.format("%s %s for the user \"%s\" (ID: %d) on the guild \"%s\" (ID: %d)", event.getAuthor().getAsMention(), extra, user == null ? "" : Standard.getCompleteName(user), user_id, guild == null ? "" : guild.getName(), guild_id);
            case USER_CONFIG:
                return String.format("%s %s for the user \"%s\" (ID: %d)", event.getAuthor().getAsMention(), extra, user == null ? "" : Standard.getCompleteName(user), user_id);
            default:
                return null;
        }
    }

}
