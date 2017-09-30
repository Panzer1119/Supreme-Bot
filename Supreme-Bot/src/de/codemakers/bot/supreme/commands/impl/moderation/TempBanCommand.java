package de.codemakers.bot.supreme.commands.impl.moderation;

import de.codemakers.bot.supreme.commands.Command;
import de.codemakers.bot.supreme.commands.CommandCategory;
import de.codemakers.bot.supreme.commands.arguments.ArgumentList;
import de.codemakers.bot.supreme.commands.invoking.Invoker;
import de.codemakers.bot.supreme.entities.MessageEvent;
import de.codemakers.bot.supreme.exceptions.ArgumentException;
import de.codemakers.bot.supreme.permission.PermissionHandler;
import de.codemakers.bot.supreme.permission.PermissionRoleFilter;
import de.codemakers.bot.supreme.sql.MySQL;
import de.codemakers.bot.supreme.sql.Result;
import de.codemakers.bot.supreme.util.Standard;
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
        return arguments.isSize(2, 4); //USER_ID BAN_TIME_IN_MS [REASON [BAN_TYPE]]
    }

    @Override
    public void action(Invoker invoker, ArgumentList arguments, MessageEvent event) {
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
            final String unban_date_string = arguments.consumeFirst();
            final String reason = arguments.consumeFirst();
            final Instant ban_date = Instant.now();
            final boolean ban_type = (arguments.isEmpty() ? true : Boolean.parseBoolean(arguments.consumeFirst()));
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
            final PreparedStatement preparedStatement = MySQL.STANDARD_DATABASE.prepareStatement("INSERT INTO %s (guild_ID, user_ID, unban_date, reason, banner_ID, ban_date, ban_type) VALUES (?, ?, ?, ?, ?, ?, ?)", MySQL.SQL_TABLE_TEMP_BANS);
            preparedStatement.setLong(1, event.getGuild().getIdLong());
            preparedStatement.setLong(2, Long.parseLong(user_id));
            preparedStatement.setTimestamp(3, new Timestamp(ban_date.toEpochMilli() + Long.parseLong(unban_date_string)));
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
