package de.codemakers.bot.supreme.commands.impl.moderation;

import de.codemakers.bot.supreme.commands.Command;
import de.codemakers.bot.supreme.commands.CommandCategory;
import de.codemakers.bot.supreme.commands.arguments.ArgumentConsumeType;
import de.codemakers.bot.supreme.commands.arguments.ArgumentList;
import de.codemakers.bot.supreme.commands.invoking.Invoker;
import de.codemakers.bot.supreme.entities.MessageEvent;
import de.codemakers.bot.supreme.permission.PermissionHandler;
import de.codemakers.bot.supreme.permission.PermissionRoleFilter;
import de.codemakers.bot.supreme.sql.MySQL;
import de.codemakers.bot.supreme.sql.Result;
import de.codemakers.bot.supreme.util.Standard;
import de.codemakers.bot.supreme.util.Util;
import de.codemakers.bot.supreme.util.updater.Updateable;
import de.codemakers.bot.supreme.util.updater.Updater;
import java.awt.Color;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.User;

/**
 * TempBanCommand
 *
 * @author Panzer1119
 */
public class TempBanCommand extends Command {

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
    public void initInvokers() {
        addInvokers(Invoker.createInvoker("tempban", this), Invoker.createInvoker("tban", this));
    }

    @Override
    public boolean called(Invoker invoker, ArgumentList arguments, MessageEvent event) {
        if (arguments == null || arguments.isEmpty()) {
            return false;
        }
        final boolean kick = arguments.isConsumed(Standard.ARGUMENT_KICK, ArgumentConsumeType.FIRST_IGNORE_CASE);
        if (kick) {
            return arguments.isSize(3, 4); //USER_ID BAN_TIME(_IN_MINUTES) [REASON] -kick
        } else {
            return arguments.isSize(2, 3); //USER_ID BAN_TIME(_IN_MINUTES) [REASON]
        }
    }

    @Override
    public void action(Invoker invoker, ArgumentList arguments, MessageEvent event) {
        final Instant ban_date = Instant.now();
        final boolean kick = arguments.isConsumed(Standard.ARGUMENT_KICK, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
        try {
            final User user = arguments.consumeUserFirst();
            final String user_id = (user == null) ? arguments.consumeFirst() : user.getId();
            if (Standard.getUserById(user_id) == null) {
                event.sendMessage(new EmbedBuilder().setColor(Color.RED).setDescription(String.format("User \"%s\" doesn't exist or isn't on this Server!", user_id)).build());
                //event.sendMessage(new ArgumentException().setCommand(this).setArgument("1").getMessage(event.getTextChannel()).build());
                return;
            }
            if (Standard.getSelfUser().getId().equals(user_id) || Standard.isSuperOwner(user_id) || event.getGuild().getOwner().getUser().getId().equals(user_id) || PermissionHandler.check(Standard.STANDARD_PERMISSIONROLEFILTER_OWNER, user_id, event.getGuild()) || (PermissionHandler.check(Standard.STANDARD_PERMISSIONROLEFILTER_ADMIN_BOT_COMMANDER, user_id, event.getGuild()) && !PermissionHandler.check(Standard.STANDARD_PERMISSIONROLEFILTER_OWNER, event.getMember()))) {
                PermissionHandler.sendNoPermissionMessage(event);
                return;
            }
            final String ban_time_string = arguments.consumeFirst();
            Long ban_time_ms = Util.getTime(ban_time_string);
            if (ban_time_ms == null) {
                ban_time_ms = Long.parseLong(ban_time_string) * 1_000 * 60;
            }
            final String reason = arguments.consumeFirst();
            if (!kick) {
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
            final PreparedStatement preparedStatement = MySQL.STANDARD_DATABASE.prepareStatement("INSERT INTO %s (guild_ID, user_ID, unban_date, reason, banner_ID, ban_date, ban_type) VALUES (?, ?, ?, ?, ?, ?, ?)", MySQL.SQL_TABLE_TEMP_BANS);
            preparedStatement.setLong(1, event.getGuild().getIdLong());
            preparedStatement.setLong(2, Long.parseLong(user_id));
            preparedStatement.setTimestamp(3, new Timestamp(ban_date.toEpochMilli() + ban_time_ms));
            preparedStatement.setString(4, reason);
            preparedStatement.setLong(5, event.getAuthor().getIdLong());
            preparedStatement.setTimestamp(6, new Timestamp(ban_date.toEpochMilli()));
            preparedStatement.setBoolean(7, !kick);
            preparedStatement.executeUpdate();
            preparedStatement.closeOnCompletion();
            preparedStatement.close();
        } catch (Exception ex) {
            ex.printStackTrace();
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
    public PermissionRoleFilter getPermissionRoleFilter() {
        //return Standard.STANDARD_PERMISSIONROLEFILTER_SUPER_OWNER;
        return Standard.STANDARD_PERMISSIONROLEFILTER_MODERATOR; //FIXME Only to prevent the NAS from turning on
    }

    @Override
    public String getCommandID() {
        return getClass().getName();
    }

    @Override
    public CommandCategory getCommandCategory() {
        return Standard.COMMANDCATEGORY_MODERATION;
    }

    private static final boolean updateAgain() {
        try {
            final Instant instant_now = Instant.now();
            final Result result = MySQL.STANDARD_DATABASE.executeQuery("SELECT * FROM %s;", MySQL.SQL_TABLE_TEMP_BANS);
            if (result == null) {
                return true;
            }
            final ArrayList<TempBan> tempBans = TempBan.ofResultSet(result.resultSet);
            result.statement.close();
            if (tempBans == null || tempBans.isEmpty()) {
                return false;
            }
            tempBans.stream().filter((tempBan) -> tempBan.isNeededToUnban(instant_now)).forEach((tempBan) -> {
                if (!tempBan.unban() && tempBan.getGuild_id() == 0) {
                    tempBan.archive();
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
