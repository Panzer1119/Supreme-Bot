package de.codemakers.bot.supreme.listeners;

import de.codemakers.bot.supreme.util.Standard;
import java.time.Instant;
import net.dv8tion.jda.core.entities.Guild;

/**
 * ModLogger
 *
 * @author Panzer1119
 */
public class ModLogger {

    public static final String LOG_NAME = "Mod";
    public static final String LOG_CHANNEL_ID_MOD = "log_channel_id_mod";
    public static final String LOG_DATE_TIME_FORMAT = "log_date_time_format";
    public static final String LOG_MEMBER_ROLES_ASMENTION = "log_member_roles_asMention";
    public static final String LOG_TEXT_MOD_VOICE_KICK = "log_text_mod_voice_kick";

    public static final void log(Instant timestamp, Guild guild, String log_text, String standard_log_text, Object... args) {
        if (log_text.startsWith("log_text_mod_")) {
            log_text = "log_text_mod_" + log_text;
        }
        Standard.log(timestamp, guild, LOG_NAME, LOG_CHANNEL_ID_MOD, log_text, standard_log_text, LOG_DATE_TIME_FORMAT, args);
    }

}
