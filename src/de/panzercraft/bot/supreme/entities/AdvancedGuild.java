package de.panzercraft.bot.supreme.entities;

import de.panzercraft.bot.supreme.settings.Settings;
import de.panzercraft.bot.supreme.util.Standard;
import java.io.File;
import net.dv8tion.jda.core.entities.Guild;

/**
 * AdvancedGuild
 *
 * @author Panzer1119
 */
public class AdvancedGuild {

    private Guild guild = null;
    private String guild_id = null;
    private File folder = null;
    private Settings settings = null;
    
    public AdvancedGuild(Guild guild) {
        this.guild = guild;
    }
    
    public AdvancedGuild(String guild_id) {
        this.guild_id = guild_id;
    }
    
    public AdvancedGuild() {
        
    }

    public final Guild getGuild() {
        if (guild == null) {
            guild = Standard.getGuildById(guild_id);
        }
        return guild;
    }
    
    public final String getGuildId() {
        if (guild_id == null && guild != null) {
            guild_id = getGuild().getId();
        }
        return guild_id;
    }

    public final File getFolder() {
        if (folder == null) {
            folder = Standard.createGuildFolder(getGuild());
        }
        return folder;
    }

    public final Settings getSettings() {
        if (settings == null) {
            settings = new Settings(new File(getFolder().getAbsolutePath() + File.separator + Standard.STANDARD_GUILD_SETTINGS_FILE_NAME));
        }
        return settings;
    }
    
}
