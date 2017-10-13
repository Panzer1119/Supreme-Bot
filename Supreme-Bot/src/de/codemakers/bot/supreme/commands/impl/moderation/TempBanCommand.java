package de.codemakers.bot.supreme.commands.impl.moderation;

import de.codemakers.bot.supreme.sql.entities.TempBan;
import de.codemakers.bot.supreme.commands.Command;
import de.codemakers.bot.supreme.commands.CommandCategory;
import de.codemakers.bot.supreme.commands.arguments.ArgumentConsumeType;
import de.codemakers.bot.supreme.commands.arguments.ArgumentList;
import de.codemakers.bot.supreme.commands.invoking.Invoker;
import de.codemakers.bot.supreme.entities.AdvancedGuild;
import de.codemakers.bot.supreme.entities.MessageEvent;
import de.codemakers.bot.supreme.permission.PermissionHandler;
import de.codemakers.bot.supreme.sql.MySQL;
import de.codemakers.bot.supreme.sql.SQLUtil;
import de.codemakers.bot.supreme.util.Standard;
import de.codemakers.bot.supreme.util.Util;
import de.codemakers.bot.supreme.util.updater.Updateable;
import de.codemakers.bot.supreme.util.updater.Updater;
import java.awt.Color;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.User;
import de.codemakers.bot.supreme.permission.PermissionFilter;
import de.codemakers.bot.supreme.settings.Config;
import net.dv8tion.jda.core.entities.Member;

/**
 * TempBanCommand
 *
 * @author Panzer1119
 */
public class TempBanCommand extends Command {

    public static final String LOG_NAME = "TempBans";
    public static final String LOG_CHANNEL_ID_TEMP_BANS = "log_channel_id_temp_bans";
    public static final String LOG_DATE_TIME_FORMAT = "log_date_time_format";
    public static final String LOG_TEXT_TEMP_BANS_KICKED = "log_text_temp_bans_kicked";
    public static final String LOG_TEXT_TEMP_BANS_BANNED = "log_text_temp_bans_banned";
    public static final String LOG_TEXT_TEMP_BANS_INTERNAL_KICKED = "log_text_temp_bans_internal_kicked";
    public static final String LOG_TEXT_TEMP_BANS_INTERNAL_BANNED = "log_text_temp_bans_internal_banned";
    public static final String STANDARD_LOG_TEXT_TEMP_BANS_KICKED = "[%1$s] [%2$s] %3$s was tempkicked by %4$s for %5$s (until %6$s)";
    public static final String STANDARD_LOG_TEXT_TEMP_BANS_BANNED = "[%1$s] [%2$s] %3$s was tempbanned by %4$s for %5$s (until %6$s)";
    public static final String STANDARD_LOG_TEXT_TEMP_BANS_INTERNAL_KICKED = "[%1$s] [%2$s] %3$s is still tempkicked by %4$s for %5$s (until %6$s)";
    public static final String STANDARD_LOG_TEXT_TEMP_BANS_INTERNAL_BANNED = "[%1$s] [%2$s] %3$s is still tempbanned by %4$s for %5$s (until %6$s)";
    private static final Updateable TEMP_BAN_COMMAND_UPDATER = new Updateable() {
        @Override
        public long update(long timestamp) {
            if (!updateAgain()) {
                return 20000;
            }
            return 10000;
        }

        @Override
        public void delete() {
        }
    };

    static {
        Updater.addUpdateable(TEMP_BAN_COMMAND_UPDATER);
    }

    @Override
    public final void initInvokers() {
        addInvokers(Invoker.createInvoker("tempban", this), Invoker.createInvoker("tban", this), Invoker.createInvoker("tempkick", this), Invoker.createInvoker("tkick", this));
    }

    @Override
    public final boolean called(Invoker invoker, ArgumentList arguments, MessageEvent event) {
        if (arguments == null || arguments.isEmpty()) {
            return false;
        }
        final boolean kick = arguments.isConsumed(Standard.ARGUMENT_KICK, ArgumentConsumeType.FIRST_IGNORE_CASE);
        final boolean ban = arguments.isConsumed(Standard.ARGUMENT_BAN, ArgumentConsumeType.FIRST_IGNORE_CASE);
        final boolean unban = arguments.isConsumed(Standard.ARGUMENT_UNBAN, ArgumentConsumeType.FIRST_IGNORE_CASE);
        if (kick && ban) {
            return false;
        } else if (kick || ban) {
            return arguments.isSize(3, 4); //USER_ID BAN_TIME(_IN_MINUTES) [REASON] [-kick/-ban]
        } else {
            return arguments.isSize(1, 3); //USER_ID [[BAN_TIME(_IN_MINUTES) [REASON]]/-unban]
        }
    }

    @Override
    public final void action(Invoker invoker, ArgumentList arguments, MessageEvent event) {
        final Instant ban_date = Instant.now();
        final boolean kick = arguments.isConsumed(Standard.ARGUMENT_KICK, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
        final boolean ban = arguments.isConsumed(Standard.ARGUMENT_BAN, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
        final boolean unban = arguments.isConsumed(Standard.ARGUMENT_UNBAN, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
        if (arguments.isSize(1) || (arguments.isSize(2) && unban)) {
            try {
                User user = arguments.consumeUserFirst();
                final String user_id = (user == null ? arguments.consumeFirst() : user.getId());
                if (user == null) {
                    user = Standard.getUserById(user_id);
                }
                if (Standard.getSelfUser().getId().equals(user_id)) {
                    event.sendMessage(Standard.STANDARD_MESSAGE_DELETING_DELAY, Standard.getNoMessage(event.getAuthor(), "i can't be banned!").build());
                    return;
                } else if (Standard.isSuperOwner(user_id)) {
                    event.sendMessage(Standard.STANDARD_MESSAGE_DELETING_DELAY, Standard.getNoMessage(event.getAuthor(), "my master can't be banned!").build());
                    return;
                } else if (event.getGuild().getOwner().getUser().getId().equals(user_id)) {
                    event.sendMessage(Standard.STANDARD_MESSAGE_DELETING_DELAY, Standard.getNoMessage(event.getAuthor(), "the owner of this server can't be banned!").build());
                    return;
                }
                final List<TempBan> tempBans = TempBan.getTempBans(Long.parseLong(user_id));
                final int temp_ban_count = (int) tempBans.stream().filter((tempBan) -> tempBan.isBan_type()).count();
                final int temp_kick_count = tempBans.size() - temp_ban_count;
                if (unban) {
                    if (temp_ban_count != 0 && !PermissionHandler.isPermissionGranted(Standard.STANDARD_PERMISSIONFILTER_ADMIN, event.getMember())) {
                        PermissionHandler.sendNoPermissionMessage(event);
                        return;
                    }
                    if (tempBans.isEmpty()) {
                        event.sendMessage(Standard.STANDARD_MESSAGE_DELETING_DELAY, Standard.getMessageEmbed(Color.RED, "%s has no temporary bans or kicks!", (user == null ? user_id : user.getAsMention())).build());
                        return;
                    }
                    TempBan.USING = true;
                    tempBans.stream().forEach((tempBan) -> {
                        if (!tempBan.unban() && tempBan.getGuild_id() == 0) {
                            tempBan.archive();
                        }
                    });
                    TempBan.USING = false;
                    event.sendMessage(2 * Standard.STANDARD_MESSAGE_DELETING_DELAY, Standard.getMessageEmbed(Color.YELLOW, "Removed all temporary bans and kicks from %s.", (user == null ? user_id : user.getAsMention())).build());
                } else {
                    event.sendMessage(2 * Standard.STANDARD_MESSAGE_DELETING_DELAY, Standard.getMessageEmbed(Color.YELLOW, "%s has %d temporary ban%s and %d temporary kick%s.", (user == null ? user_id : user.getAsMention()), temp_ban_count, (temp_ban_count != 1 ? "s" : ""), temp_kick_count, (temp_kick_count != 1 ? "s" : "")).build());
                }
            } catch (Exception ex) {
                TempBan.USING = false;
                ex.printStackTrace();
            }
        } else {
            try {
                final boolean ban_type = (invoker.getInvoker().contains("ban") ? (!kick || ban) : (invoker.getInvoker().contains("kick") ? !(kick || !ban) : true));
                User user = arguments.consumeUserFirst();
                final String user_id = (user == null ? arguments.consumeFirst() : user.getId());
                if (user == null) {
                    user = Standard.getUserById(user_id);
                }
                if (Standard.getSelfUser().getId().equals(user_id)) {
                    event.sendMessage(Standard.STANDARD_MESSAGE_DELETING_DELAY, Standard.getNoMessage(event.getAuthor(), "i can't be banned!").build());
                    return;
                } else if (Standard.isSuperOwner(user_id)) {
                    event.sendMessage(Standard.STANDARD_MESSAGE_DELETING_DELAY, Standard.getNoMessage(event.getAuthor(), "my master can't be banned!").build());
                    return;
                } else if (event.getGuild().getOwner().getUser().getId().equals(user_id)) {
                    event.sendMessage(Standard.STANDARD_MESSAGE_DELETING_DELAY, Standard.getNoMessage(event.getAuthor(), "the owner of this server can't be banned!").build());
                    return;
                }
                final Member member = event.getGuild().getMemberById(user_id);
                if (PermissionHandler.isPermissionGranted(Standard.STANDARD_PERMISSIONFILTER_GUILD_OWNER, member) || (PermissionHandler.isPermissionGranted(Standard.STANDARD_PERMISSIONFILTER_GUILD_ADMIN_BOT_COMMANDER, member) && !PermissionHandler.isPermissionGranted(Standard.STANDARD_PERMISSIONFILTER_GUILD_OWNER, event.getMember())) || (ban_type && !PermissionHandler.isPermissionGranted(Standard.STANDARD_PERMISSIONFILTER_ADMIN, event.getMember()))) { //FIXME Sollen Mods auch tempbannen duerfen?
                    PermissionHandler.sendNoPermissionMessage(event);
                    return;
                }
                final String ban_time_string = arguments.consumeFirst();
                Long ban_time_ms = Util.getTime(ban_time_string);
                if (ban_time_ms == null) {
                    ban_time_ms = Long.parseLong(ban_time_string) * 1_000 * 60;
                }
                final String reason = arguments.consumeFirst();
                if (ban_type) {
                    if (reason == null) {
                        event.getGuild().getController().ban(user_id, 0).queue();
                    } else {
                        event.getGuild().getController().ban(user_id, 0, reason).queue();
                    }
                } else if (reason == null) {
                    event.getGuild().getController().kick(user_id).queue();
                } else {
                    event.getGuild().getController().kick(user_id, reason).queue();
                }
                final AdvancedGuild advancedGuild = Standard.getAdvancedGuild(event.getGuild());
                final String log_date_time_format = advancedGuild.getSettings().getProperty(LOG_DATE_TIME_FORMAT, Standard.STANDARD_DATE_TIME_FORMAT);
                String date_time_formatted_unban_date = null;
                String ban_time_string_ = null;
                if (ban_time_ms < 0) {
                    date_time_formatted_unban_date = "forever";
                    ban_time_string_ = "forever";
                } else {
                    try {
                        date_time_formatted_unban_date = LocalDateTime.ofInstant(Instant.ofEpochMilli(ban_date.toEpochMilli() + ban_time_ms), Standard.getZoneId()).format(DateTimeFormatter.ofPattern(log_date_time_format));
                    } catch (Exception ex) {
                        date_time_formatted_unban_date = LocalDateTime.ofInstant(Instant.ofEpochMilli(ban_date.toEpochMilli() + ban_time_ms), Standard.getZoneId()).format(DateTimeFormatter.ofPattern(Standard.STANDARD_DATE_TIME_FORMAT));
                    }
                    try {
                        ban_time_string_ = Util.getTimeAsString(ban_time_ms, true, true);
                    } catch (Exception ex) {
                        ban_time_string_ = "error";
                    }
                }
                Standard.log(ban_date, event.getGuild(), LOG_NAME, LOG_CHANNEL_ID_TEMP_BANS, (ban_type ? LOG_TEXT_TEMP_BANS_BANNED : LOG_TEXT_TEMP_BANS_KICKED), (ban_type ? STANDARD_LOG_TEXT_TEMP_BANS_BANNED : STANDARD_LOG_TEXT_TEMP_BANS_KICKED), LOG_DATE_TIME_FORMAT, (user == null ? user_id : Config.CONFIG.getUserNameForUser(user, event.getGuild())), Config.CONFIG.getUserNameForUser(event.getAuthor(), event.getGuild()), ban_time_string_, date_time_formatted_unban_date);
                final PreparedStatement preparedStatement = MySQL.STANDARD_DATABASE.prepareStatement("INSERT INTO %s (guild_ID, user_ID, unban_date, reason, banner_ID, ban_date, ban_type) VALUES (?, ?, ?, ?, ?, ?, ?)", MySQL.SQL_TABLE_TEMP_BANS);
                preparedStatement.setLong(1, event.getGuild().getIdLong());
                preparedStatement.setLong(2, Long.parseLong(user_id));
                preparedStatement.setTimestamp(3, (ban_time_ms < 0 ? null : new Timestamp(ban_date.toEpochMilli() + ban_time_ms)));
                preparedStatement.setString(4, reason);
                preparedStatement.setLong(5, event.getAuthor().getIdLong());
                preparedStatement.setTimestamp(6, new Timestamp(ban_date.toEpochMilli()));
                preparedStatement.setBoolean(7, ban_type);
                preparedStatement.executeUpdate();
                preparedStatement.closeOnCompletion();
                preparedStatement.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public final void executed(boolean success, MessageEvent event) {
        System.out.println("[INFO] Command '" + getCommandID() + "' was executed!");
    }

    @Override
    public final EmbedBuilder getHelp(Invoker invoker, EmbedBuilder builder) {
        final boolean ban = invoker.getInvoker().contains("ban") && !invoker.getInvoker().contains("kick");
        builder.addField(String.format("%s <Username or @Mention> <%s time in minutes or as time string> [Reason] [%s]", invoker, (ban ? "Ban" : "Kick"), (ban ? Standard.ARGUMENT_KICK : Standard.ARGUMENT_BAN).getCompleteArgument(0, -1)), String.format("Temporary %ss a user for a specified time and optionally with an reason. With the flag \"%s\" you temporary %s a user.", (ban ? "ban" : "kick"), (ban ? Standard.ARGUMENT_KICK : Standard.ARGUMENT_BAN).getCompleteArgument(0, -1), (ban ? "kick" : "ban")), false);
        builder.addField(String.format("%s <Username or @Mention> [%s]", invoker, Standard.ARGUMENT_UNBAN.getCompleteArgument(0, -1)), String.format("Shows information about temporary bans and kicks of the user. With the flag \"%s\" you remove all temporary bans and kicks from the user.", Standard.ARGUMENT_BAN.getCompleteArgument(0, -1)), false);
        return builder;
    }

    @Override
    public final PermissionFilter getPermissionFilter() {
        return Standard.STANDARD_PERMISSIONFILTER_MODERATOR;
    }

    @Override
    public final String getCommandID() {
        return getClass().getName();
    }

    @Override
    public final CommandCategory getCommandCategory() {
        return Standard.COMMANDCATEGORY_MODERATION;
    }

    private static final boolean updateAgain() {
        try {
            final Instant instant_now = Instant.now();
            final ArrayList<TempBan> tempBans = SQLUtil.deserializeObjects(TempBan.class);
            if (tempBans == null) {
                return true;
            }
            TempBan.TEMP_BANS.clear();
            if (tempBans.isEmpty()) {
                return false;
            }
            TempBan.TEMP_BANS.addAll(tempBans);
            tempBans.stream().forEach((tempBan) -> {
                if (tempBan.isNeededToUnban(instant_now)) {
                    if (!tempBan.unban() && tempBan.getGuild_id() == 0) {
                        tempBan.archive();
                    }
                } else {
                    tempBan.ban();
                }
            });
            tempBans.clear();
            return true;
        } catch (Exception ex) {
            System.err.println("TempBanCommand: Updating again error");
            ex.printStackTrace();
            return true;
        }
    }

}
