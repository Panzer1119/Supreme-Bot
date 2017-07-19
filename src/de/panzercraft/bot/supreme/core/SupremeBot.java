package de.panzercraft.bot.supreme.core;

import de.panzercraft.bot.supreme.commands.CommandHandler;
import de.panzercraft.bot.supreme.commands.ManagingCommands;
import de.panzercraft.bot.supreme.commands.MusicCommand;
import de.panzercraft.bot.supreme.commands.PingCommand;
import de.panzercraft.bot.supreme.listeners.CommandListener;
import de.panzercraft.bot.supreme.listeners.MemberListener;
import de.panzercraft.bot.supreme.listeners.ReadyListener;
import de.panzercraft.bot.supreme.listeners.VoiceListener;
import de.panzercraft.bot.supreme.permission.PermissionRole;
import de.panzercraft.bot.supreme.settings.Settings;
import de.panzercraft.bot.supreme.util.Standard;
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
    
    public static final void main(String[] args) {
        Settings.loadStandardSettings();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Settings.saveStandardSettings();
        }));
        Standard.init();
        builder = new JDABuilder(AccountType.BOT);
        builder.setToken(new String(Standard.getToken()));
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
        try {
            jda = builder.buildBlocking();
        } catch (Exception ex) {
            System.err.println(ex);
            ex.printStackTrace();
        }
    }
    
    private static final void initListeners() {
        builder.addEventListener(new ReadyListener());
        builder.addEventListener(new VoiceListener());
        builder.addEventListener(new MemberListener());
        builder.addEventListener(new CommandListener());
    }
    
    private static final void initCommands() {
        CommandHandler.registerCommand(new PingCommand());
        CommandHandler.registerCommand(new ManagingCommands.CommandPrefixChangeCommand());
        CommandHandler.registerCommand(new ManagingCommands.StopCommand());
        CommandHandler.registerCommand(new ManagingCommands.GetFileCommand());
        CommandHandler.registerCommand(new ManagingCommands.SayCommand());
        CommandHandler.registerCommand(new ManagingCommands.ClearCommand());
        CommandHandler.registerCommand(new MusicCommand());
    }
    
    private static final void initPermissions() {
        PermissionRole.loadPermissionRoles("/de/panzercraft/bot/supreme/permission/Permissions.txt");
    }
    
}
