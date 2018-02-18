package de.codemakers.bot.supreme.entities;

import de.codemakers.bot.supreme.settings.DefaultSettings;
import de.codemakers.bot.supreme.settings.SimpleSettings;
import de.codemakers.bot.supreme.util.Standard;
import de.codemakers.io.file.AdvancedFile;
import net.dv8tion.jda.core.entities.Guild;

/**
 * AdvancedGuild
 *
 * @author Panzer1119
 */
public class AdvancedGuild {

    private Guild guild = null;
    private long guild_id = 0;
    private AdvancedFile folder = null;
    private DefaultSettings settings = null;

    public AdvancedGuild(Guild guild, AdvancedFile folder) {
        this(guild);
        this.folder = folder;
    }

    public AdvancedGuild(Guild guild) {
        this.guild = guild;
    }

    public AdvancedGuild(long guild_id, AdvancedFile folder) {
        this(guild_id);
        this.folder = folder;
    }

    public AdvancedGuild(long guild_id) {
        this.guild_id = guild_id;
    }

    public AdvancedGuild(AdvancedFile folder) {
        this.folder = folder;
    }

    public AdvancedGuild() {
        this((AdvancedFile) null);
    }

    public final AdvancedFile getFile(String path) {
        if (getFolder() == null) {
            return null;
        }
        return new AdvancedFile(false, folder, path);
    }

    public final AdvancedFile getFolder() {
        if (folder == null) {
            if (guild != null) {
                folder = Standard.createGuildFolder(getGuild());
            } else {
                folder = Standard.createGuildFolder(getGuildIdLong());
            }
        }
        folder.createAdvancedFile();
        return folder;
    }

    public final Guild getGuild() {
        if (guild == null) {
            guild = Standard.getGuildById(guild_id);
        }
        return guild;
    }

    public final AdvancedGuild setGuild(Guild guild) {
        this.guild = guild;
        if (guild == null) {
            this.guild_id = 0;
        } else {
            this.guild_id = this.guild.getIdLong();
        }
        return this;
    }

    public final String getGuildId() {
        if (guild_id == 0 && guild != null) {
            guild_id = getGuild().getIdLong();
        }
        return "" + guild_id;
    }

    public final long getGuildIdLong() {
        if (guild_id == 0 && guild != null) {
            guild_id = getGuild().getIdLong();
        }
        return guild_id;
    }

    public final AdvancedFile getLogFile() {
        return getFile(Standard.STANDARD_LOG_FILE_NAME);
    }

    public final DefaultSettings getSettings() {
        if (settings == null) {
            getFolder();
            if (folder == null) {
                return (DefaultSettings) Standard.STANDARD_NULL_SETTINGS;
            }
            settings = new DefaultSettings(getFile(Standard.STANDARD_GUILD_SETTINGS_FILE_NAME)).setAutoAddProperties(true);
        }
        return settings;
    }

    public final SimpleSettings getPluginSettings() {
        return getSettings().toSimpleSettings();
    }

    public final AdvancedGuild sayHi() {
        if (getGuild() == null) {
            System.out.println(String.format("No guild, no startup message:  \"%s\"", guild));
            return this;
        }
        getSettings().loadSettings();
        String hi = getSettings().getProperty("startup_message", null);
        if (hi != null) {
            guild.getPublicChannel().sendMessage(hi).queue();
        } else {
            System.out.println(String.format("No startup message wanted: \"%s\"", guild));
        }
        return this;
    }

}
