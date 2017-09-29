package de.codemakers.bot.supreme.commands.impl.moderation;

import de.codemakers.bot.supreme.commands.Command;
import de.codemakers.bot.supreme.commands.CommandCategory;
import de.codemakers.bot.supreme.commands.arguments.ArgumentList;
import de.codemakers.bot.supreme.commands.invoking.Invoker;
import de.codemakers.bot.supreme.entities.MessageEvent;
import de.codemakers.bot.supreme.permission.PermissionRoleFilter;
import de.codemakers.bot.supreme.sql.MySQL;
import de.codemakers.bot.supreme.util.Standard;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.Instant;
import net.dv8tion.jda.core.EmbedBuilder;

/**
 * TempBanCommand
 *
 * @author Panzer1119
 */
public class TempBanCommand extends Command {

    @Override
    public void initInvokers() {
        addInvokers(Invoker.createInvoker("tempban", this), Invoker.createInvoker("tban", this));
    }

    @Override
    public boolean called(Invoker invoker, ArgumentList arguments, MessageEvent event) {
        if (arguments == null || arguments.isEmpty()) {
            return false;
        }
        return arguments.isSize(2, 3);
    }

    @Override
    public void action(Invoker invoker, ArgumentList arguments, MessageEvent event) {
        try {
            final String user_id = arguments.consumeFirst();
            final String unban_date_string = arguments.consumeFirst();
            final String reason = arguments.consumeFirst();
            final Instant ban_date = Instant.now();
            final boolean ban_type = true;
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
        return Standard.STANDARD_PERMISSIONROLEFILTER_SUPER_OWNER;
        //return Standard.STANDARD_PERMISSIONROLEFILTER_MODERATOR; //FIXME Only to prevent the NAS from turning on
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
