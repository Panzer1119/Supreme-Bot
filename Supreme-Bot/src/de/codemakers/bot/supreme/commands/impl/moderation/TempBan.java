package de.codemakers.bot.supreme.commands.impl.moderation;

import de.codemakers.bot.supreme.entities.AdvancedGuild;
import de.codemakers.bot.supreme.sql.MySQL;
import de.codemakers.bot.supreme.util.Standard;
import de.codemakers.bot.supreme.util.Util;
import java.sql.ResultSet;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;

/**
 * TempBan
 *
 * @author Panzer1119
 */
public class TempBan {

    public static final String STANDARD_REASON = "No reason available!";
    private static final ArrayList<TempBan> TEMP_BANS = new ArrayList<>();
    private static boolean getting = false;
    public static boolean USING = false;

    private int id;
    private long guild_id;
    private long user_id;
    private Instant unban_date;
    private String reason;
    private long banner_id;
    private Instant ban_date;
    private boolean ban_type;

    private TempBan() {
        this(0, 0, 0, null, true);
    }

    private TempBan(int id, long user_id, long banner_id, Instant ban_date, boolean ban_type) {
        this(id, 0, user_id, null, null, banner_id, ban_date, ban_type);
    }

    private TempBan(int id, long guild_id, long user_id, Instant unban_date, String reason, long banner_id, Instant ban_date, boolean ban_type) {
        this.id = id;
        this.guild_id = guild_id;
        this.user_id = user_id;
        this.unban_date = unban_date;
        this.reason = reason;
        this.banner_id = banner_id;
        this.ban_date = ban_date;
        this.ban_type = ban_type;
    }

    public final int getId() {
        return id;
    }

    public final TempBan setId(int id) {
        this.id = id;
        return this;
    }

    public final long getGuild_id() {
        return guild_id;
    }

    public final TempBan setGuild_id(long guild_id) {
        this.guild_id = guild_id;
        return this;
    }

    public final long getUser_id() {
        return user_id;
    }

    public final TempBan setUser_id(long user_id) {
        this.user_id = user_id;
        return this;
    }

    public final Instant getUnban_date() {
        return unban_date;
    }

    public final TempBan setUnban_date(Instant unban_date) {
        this.unban_date = unban_date;
        return this;
    }

    public final String getReason() {
        return reason;
    }

    public final TempBan setReason(String reason) {
        this.reason = reason;
        return this;
    }

    public final long getBanner_id() {
        return banner_id;
    }

    public final TempBan setBanner_id(long banner_id) {
        this.banner_id = banner_id;
        return this;
    }

    public final Instant getBan_date() {
        return ban_date;
    }

    public final TempBan setBan_date(Instant ban_date) {
        this.ban_date = ban_date;
        return this;
    }

    public final boolean isBan_type() {
        return ban_type;
    }

    public final TempBan setBan_type(boolean ban_type) {
        this.ban_type = ban_type;
        return this;
    }

    public final boolean isNeededToUnban(Instant instant_now) {
        return unban_date != null && unban_date.isBefore(instant_now);
    }

    public final boolean remove() {
        return TEMP_BANS.remove(this);
    }

    public final boolean archive() {
        remove();
        MySQL.STANDARD_DATABASE.archive(MySQL.SQL_TABLE_TEMP_BANS, "" + id);
        return true;
    }

    public final boolean ban() {
        try {
            final Guild guild = Standard.getGuildById(guild_id);
            if (guild == null) {
                return false;
            }
            final User user = Standard.getUserById(user_id);
            final User banner = Standard.getUserById(banner_id);
            final AdvancedGuild advancedGuild = Standard.getAdvancedGuild(guild);
            final String log_date_time_format = advancedGuild.getSettings().getProperty(TempBanCommand.LOG_DATE_TIME_FORMAT, Standard.STANDARD_DATE_TIME_FORMAT);
            String date_time_formatted_unban_date = null;
            String ban_time_string = null;
            if (unban_date == null) {
                date_time_formatted_unban_date = "forever";
                ban_time_string = "forever";
            } else {
                try {
                    date_time_formatted_unban_date = LocalDateTime.ofInstant(unban_date, ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern(log_date_time_format));
                } catch (Exception ex) {
                    date_time_formatted_unban_date = LocalDateTime.ofInstant(unban_date, ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern(Standard.STANDARD_DATE_TIME_FORMAT));
                }
                try {
                    ban_time_string = Util.getTimeAsString(Duration.between(Instant.now(), unban_date).toMillis(), true, true);
                } catch (Exception ex) {
                    ban_time_string = "error";
                }
            }
            if (ban_type) {
                if (guild.getBans().complete().stream().noneMatch((user_) -> user_.getIdLong() == user_id)) {
                    if (reason == null) {
                        guild.getController().ban("" + user_id, 0).queue();
                    } else {
                        guild.getController().ban("" + user_id, 0, reason).queue();
                    }
                    Standard.log(Instant.now(), guild, TempBanCommand.LOG_NAME, TempBanCommand.LOG_CHANNEL_ID_TEMP_BANS, TempBanCommand.LOG_TEXT_TEMP_BANS_INTERNAL_BANNED, TempBanCommand.STANDARD_LOG_TEXT_TEMP_BANS_INTERNAL_BANNED, TempBanCommand.LOG_DATE_TIME_FORMAT, user.getAsMention(), banner.getAsMention(), ban_time_string, date_time_formatted_unban_date);
                } else {
                    return false;
                }
            } else if (user != null && guild.isMember(user)) {
                if (reason == null) {
                    guild.getController().kick("" + user_id).queue();
                } else {
                    guild.getController().kick("" + user_id, reason).queue();
                }
                Standard.log(Instant.now(), guild, TempBanCommand.LOG_NAME, TempBanCommand.LOG_CHANNEL_ID_TEMP_BANS, TempBanCommand.LOG_TEXT_TEMP_BANS_INTERNAL_KICKED, TempBanCommand.STANDARD_LOG_TEXT_TEMP_BANS_INTERNAL_KICKED, TempBanCommand.LOG_DATE_TIME_FORMAT, user.getAsMention(), banner.getAsMention(), ban_time_string, date_time_formatted_unban_date);
            } else {
                return false;
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public final boolean unban() {
        if (!ban_type) {
            archive();
            return true;
        }
        try {
            final Guild guild = Standard.getGuildById(guild_id);
            if (guild != null) {
                guild.getController().unban("" + user_id).queue();
                archive();
                return true;
            }
            return false;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public final String toString() {
        return "TempBan{" + "id=" + id + ", guild_id=" + guild_id + ", user_id=" + user_id + ", unban_date=" + unban_date + ", reason=" + reason + ", banner_id=" + banner_id + ", ban_date=" + ban_date + ", ban_type=" + ban_type + '}';
    }

    @Override
    public final boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (object == this) {
            return true;
        }
        if (object instanceof TempBan) {
            final TempBan tempBan = (TempBan) object;
            return ((tempBan.getId() == id) && (tempBan.getGuild_id() == guild_id) && (tempBan.getUser_id() == user_id) && (tempBan.getUnban_date() == unban_date) && ((reason == null) ? tempBan.getReason() == null : reason.equals(tempBan.getReason())) && (tempBan.getBanner_id() == banner_id) && (tempBan.getBan_date() == ban_date) && (tempBan.isBan_type() == ban_type));
        }
        return false;
    }

    public static final ArrayList<TempBan> ofResultSet(ResultSet resultSet) {
        while (getting || USING) {
        }
        TEMP_BANS.clear();
        try {
            if (resultSet == null || resultSet.isClosed() || resultSet.isAfterLast() || (!resultSet.isBeforeFirst() && !resultSet.next())) {
                return new ArrayList<>(TEMP_BANS);
            }
            resultSet.first();
            do {
                try {
                    final TempBan tempBan = new TempBan(resultSet.getInt("ID"), resultSet.getLong("guild_ID"), resultSet.getLong("user_ID"), (resultSet.getTimestamp("unban_date") == null ? null : resultSet.getTimestamp("unban_date").toInstant()), resultSet.getString("reason"), resultSet.getLong("banner_ID"), resultSet.getTimestamp("ban_date").toInstant(), resultSet.getBoolean("ban_type"));
                    TEMP_BANS.add(tempBan);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } while (resultSet.next());
            resultSet.close();
            return new ArrayList<>(TEMP_BANS);
        } catch (Exception ex) {
            System.err.println("TempBan: ofResultSet error");
            ex.printStackTrace();
            return new ArrayList<>(TEMP_BANS);
        }
    }

    private static final TempBan of(int id, long user_id, long banner_id, Instant ban_date, boolean ban_type) {
        return of(id, 0, user_id, null, null, banner_id, ban_date, ban_type);
    }

    private static final TempBan of(int id, long guild_id, long user_id, Instant unban_date, String reason, long banner_id, Instant ban_date, boolean ban_type) {
        try {
            final TempBan tempBan = new TempBan(id, guild_id, user_id, unban_date, reason, banner_id, ban_date, ban_type);
            final int index = TEMP_BANS.indexOf(tempBan);
            if (index == -1) {
                TEMP_BANS.add(tempBan);
                return tempBan;
            }
            return TEMP_BANS.get(index);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static final List<TempBan> getTempBans(long user_id) {
        getting = true;
        final List<TempBan> tempBans = TEMP_BANS.stream().filter((tempBan) -> tempBan.getUser_id() == user_id).collect(Collectors.toList());
        getting = false;
        return tempBans;
    }

    public static final List<TempBan> getTempBans(User user) {
        if (user == null) {
            return null;
        }
        return getTempBans(user.getIdLong());
    }

    public static final List<TempBan> getTempBans(Member member) {
        if (member == null) {
            return null;
        }
        return getTempBans(member.getUser());
    }

    public static final boolean isAllowedToJoin(long user_id) {
        final List<TempBan> bans = getTempBans(user_id);
        return bans.isEmpty();
    }

    public static final boolean isAllowedToJoin(User user) {
        if (user == null) {
            return false;
        }
        return isAllowedToJoin(user.getIdLong());
    }

    public static final boolean isAllowedToJoin(Member member) {
        if (member == null) {
            return false;
        }
        return isAllowedToJoin(member.getUser());
    }

    public static final TempBan getFirstBan(long user_id) {
        return getTempBans(user_id).stream().findFirst().orElse(null);
    }

    public static final TempBan getFirstBan(User user) {
        if (user == null) {
            return null;
        }
        return getFirstBan(user.getIdLong());
    }

    public static final TempBan getFirstBan(Member member) {
        if (member == null) {
            return null;
        }
        return getFirstBan(member.getUser());
    }

    public static final String getReason(long user_id) {
        final TempBan tempBan = getFirstBan(user_id);
        return (tempBan != null && tempBan.getReason() != null && !tempBan.getReason().isEmpty()) ? tempBan.getReason() : STANDARD_REASON;
    }

    public static final String getReason(User user) {
        if (user == null) {
            return null;
        }
        return getReason(user.getIdLong());
    }

    public static final String getReason(Member member) {
        if (member == null) {
            return null;
        }
        return getReason(member.getUser());
    }

}
