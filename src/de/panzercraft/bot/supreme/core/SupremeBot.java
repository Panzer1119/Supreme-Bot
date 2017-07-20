package de.panzercraft.bot.supreme.core;

import de.panzercraft.bot.supreme.commands.CommandHandler;
import de.panzercraft.bot.supreme.commands.impl.HelpCommand;
import de.panzercraft.bot.supreme.commands.impl.ManagingCommands;
import de.panzercraft.bot.supreme.commands.impl.MusicCommand;
import de.panzercraft.bot.supreme.commands.impl.PingCommand;
import de.panzercraft.bot.supreme.listeners.CommandListener;
import de.panzercraft.bot.supreme.listeners.MemberListener;
import de.panzercraft.bot.supreme.listeners.ReadyListener;
import de.panzercraft.bot.supreme.listeners.VoiceListener;
import de.panzercraft.bot.supreme.util.Standard;
import de.panzercraft.bot.supreme.util.SystemOutputStream;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;

/**
 * Supreme-Bot
 * 
 * @author Panzer1119
 */
public class SupremeBot {
    
    public static JDABuilder builder = null;
    public static JDA jda = null;
    private static boolean running = false;
    
    public static final void main(String[] args) {
        try {
            System.setOut(new SystemOutputStream(System.out));
            System.setErr(new SystemOutputStream(System.err));
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                Standard.STANDARD_SETTINGS.saveSettings();
                Standard.saveAllGuildSettings();
            }));
            reload();
            builder = new JDABuilder(AccountType.BOT);
            builder.setAutoReconnect(true);
            builder.setStatus(OnlineStatus.ONLINE);
            builder.setGame(new Game() {
                @Override
                public String getName() {
                    return "V" + Standard.VERSION;
                }

                @Override
                public String getUrl() {
                    return null;
                }

                @Override
                public GameType getType() {
                    return null;
                }
            });
            initListeners();
            initCommands();
            startJDA();
        } catch (Exception ex) {
            System.err.println("Main Error: " + ex);
            ex.printStackTrace();
        }
    }
    
    private static final boolean initListeners() {
        try {
            builder.addEventListener(new ReadyListener());
            builder.addEventListener(new VoiceListener());
            builder.addEventListener(new MemberListener());
            builder.addEventListener(new CommandListener());
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
    
    private static final boolean initCommands() {
        try {
            
            CommandHandler.registerCommand(new PingCommand());
            CommandHandler.registerCommand(new ManagingCommands.CommandPrefixChangeCommand());
            CommandHandler.registerCommand(new ManagingCommands.StopCommand());
            CommandHandler.registerCommand(new ManagingCommands.RestartCommand());
            CommandHandler.registerCommand(new ManagingCommands.GetFileCommand());
            CommandHandler.registerCommand(new ManagingCommands.SayCommand());
            CommandHandler.registerCommand(new ManagingCommands.ClearCommand());
            CommandHandler.registerCommand(new ManagingCommands.ReloadCommand());
            CommandHandler.registerCommand(new ManagingCommands.SettingsCommand());
            CommandHandler.registerCommand(new MusicCommand());
            CommandHandler.registerCommand(new HelpCommand());
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
    
    public static final boolean isRunning() {
        return running;
    }
    
    public static final boolean startJDA() {
        if (running) {
            return true;
        }
        running = true;
        try {
            builder.setToken(new String(Standard.getToken()));
            jda = builder.buildBlocking();
            return true;
        } catch (Exception ex) {
            System.err.println(ex);
            if (!(ex instanceof InterruptedException)) {
                ex.printStackTrace();
            }
            return false;
        }
    }
    
    public static final boolean stopJDA(boolean now) {
        if (!running) {
            return true;
        }
        try {
            if (now) {
                jda.shutdownNow();
            } else {
                jda.shutdown();
            }
            running = false;
            reload();
            return true;
        } catch (Exception ex) {
            System.err.println(ex);
            if (!(ex instanceof InterruptedException)) {
                ex.printStackTrace();
            }
            return false;
        }
    }
    
    public static final boolean restartJDA(boolean now) {
        stopJDA(now);
        return startJDA();
    }
    
    public static final boolean reload() {
        try {
            reloadSettings();
            reloadPermissions();
            loadAllGuilds();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
    
    public static final boolean reloadSettings() {
        try {
            Standard.reloadSettings();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
    
    public static final boolean reloadPermissions() {
        try {
            Standard.reloadPermissions();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
    
    public static final boolean loadAllGuilds() {
        try {
            Standard.loadAllGuilds();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
    
    public static final boolean reloadGuilds() {
        try {
            Standard.reloadAllGuilds();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
    
    public static final boolean reloadGuildSettings() {
        try {
            Standard.reloadAllGuildSettings();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
    
    public static final boolean reloadGuildSettings(String guild_id) {
        try {
            Standard.getGuildSettings(guild_id).loadSettings();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
    
}
