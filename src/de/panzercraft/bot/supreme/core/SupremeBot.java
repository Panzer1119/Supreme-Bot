package de.panzercraft.bot.supreme.core;

import de.panzercraft.bot.supreme.commands.CommandHandler;
import de.panzercraft.bot.supreme.commands.impl.ManagingCommands;
import de.panzercraft.bot.supreme.commands.impl.MusicCommand;
import de.panzercraft.bot.supreme.commands.impl.PingCommand;
import de.panzercraft.bot.supreme.listeners.CommandListener;
import de.panzercraft.bot.supreme.listeners.MemberListener;
import de.panzercraft.bot.supreme.listeners.ReadyListener;
import de.panzercraft.bot.supreme.listeners.VoiceListener;
import de.panzercraft.bot.supreme.permission.PermissionRole;
import de.panzercraft.bot.supreme.settings.Settings;
import de.panzercraft.bot.supreme.util.Standard;
import java.io.File;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import org.apache.commons.io.FileUtils;

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
        Settings.loadStandardSettings();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Settings.saveStandardSettings();
        }));
        Standard.init();
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
        initPermissions();
        startJDA();
    }
    
    private static final boolean initListeners() {
        builder.addEventListener(new ReadyListener());
        builder.addEventListener(new VoiceListener());
        builder.addEventListener(new MemberListener());
        builder.addEventListener(new CommandListener());
        return true;
    }
    
    private static final boolean initCommands() {
        CommandHandler.registerCommand(new PingCommand());
        CommandHandler.registerCommand(new ManagingCommands.CommandPrefixChangeCommand());
        CommandHandler.registerCommand(new ManagingCommands.StopCommand());
        CommandHandler.registerCommand(new ManagingCommands.RestartCommand());
        CommandHandler.registerCommand(new ManagingCommands.GetFileCommand());
        CommandHandler.registerCommand(new ManagingCommands.SayCommand());
        CommandHandler.registerCommand(new ManagingCommands.ClearCommand());
        CommandHandler.registerCommand(new MusicCommand());
        return true;
    }
    
    private static final boolean initPermissions() {
        final File file = new File("permissions.txt");
        final String jar_path = "/de/panzercraft/bot/supreme/permission/permissions.txt";
        if (file.exists() && file.isFile()) {
            PermissionRole.loadPermissionRoles(file);
        } else if (!file.exists()) {
            try {
                FileUtils.copyInputStreamToFile(SupremeBot.class.getResourceAsStream(jar_path), file);
                PermissionRole.loadPermissionRoles(file);
            } catch (Exception ex) {
                System.err.println(ex);
                PermissionRole.loadPermissionRoles(jar_path);
            }
        } else if(!file.isFile()) {
            PermissionRole.loadPermissionRoles(jar_path);
        } else {
            return false;
        }
        return true;
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
        Standard.init();
        return startJDA();
    }
    
}
