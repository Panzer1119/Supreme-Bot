package de.codemakers.bot.supreme.core;

import de.codemakers.bot.supreme.audio.recording.test.ARCommand;
import de.codemakers.bot.supreme.commands.impl.AudioRecorderCommand;
import de.codemakers.bot.supreme.commands.impl.HelpCommand;
import de.codemakers.bot.supreme.commands.impl.RolesCommand;
import de.codemakers.bot.supreme.commands.impl.fun.MusicCommand;
import de.codemakers.bot.supreme.commands.impl.PingCommand;
import de.codemakers.bot.supreme.commands.impl.SolveCommand;
import de.codemakers.bot.supreme.commands.impl.UptimeCommand;
import de.codemakers.bot.supreme.commands.impl.UserCommand;
import de.codemakers.bot.supreme.commands.impl.fun.GameOfLifeCommand;
import de.codemakers.bot.supreme.commands.impl.fun.TicTacToeCommand;
import de.codemakers.bot.supreme.commands.impl.moderation.ChangeCommandPrefixCommand;
import de.codemakers.bot.supreme.commands.impl.moderation.ClearCommand;
import de.codemakers.bot.supreme.commands.impl.moderation.CommandCommand;
import de.codemakers.bot.supreme.commands.impl.moderation.GetCommandPrefixCommand;
import de.codemakers.bot.supreme.commands.impl.moderation.GetFileCommand;
import de.codemakers.bot.supreme.commands.impl.moderation.ReloadCommand;
import de.codemakers.bot.supreme.commands.impl.moderation.RestartCommand;
import de.codemakers.bot.supreme.commands.impl.fun.SayCommand;
import de.codemakers.bot.supreme.commands.impl.moderation.ChangeNicknameCommand;
import de.codemakers.bot.supreme.commands.impl.moderation.CopyCommand;
import de.codemakers.bot.supreme.commands.impl.moderation.MoveAllCommand;
import de.codemakers.bot.supreme.commands.impl.moderation.MoveCommand;
import de.codemakers.bot.supreme.commands.impl.moderation.SettingsCommand;
import de.codemakers.bot.supreme.commands.impl.moderation.StopCommand;
import de.codemakers.bot.supreme.commands.impl.moderation.TempBanCommand;
import de.codemakers.bot.supreme.commands.impl.moderation.UploadFileCommand;
import de.codemakers.bot.supreme.commands.impl.moderation.VoiceKickCommand;
import de.codemakers.bot.supreme.commands.impl.moderation.util.BackupCommand;
import de.codemakers.bot.supreme.commands.impl.moderation.util.GetLogCommand;
import de.codemakers.bot.supreme.commands.impl.moderation.util.GuildsCommand;
import de.codemakers.bot.supreme.commands.impl.moderation.util.SystemCommand;
import de.codemakers.bot.supreme.commands.impl.moderation.util.XMLEditorCommand;
import de.codemakers.bot.supreme.commands.impl.secret.ConsoleCommand;
import de.codemakers.bot.supreme.commands.impl.secret.PasteServerCommand;
import de.codemakers.bot.supreme.exceptions.ExitTrappedException;
import de.codemakers.bot.supreme.listeners.GuildLogger;
import de.codemakers.bot.supreme.listeners.GuildMemberLogger;
import de.codemakers.bot.supreme.listeners.ReadyListener;
import de.codemakers.bot.supreme.listeners.GuildVoiceLogger;
import de.codemakers.bot.supreme.listeners.MessageHandler;
import de.codemakers.bot.supreme.listeners.UserLogger;
import de.codemakers.bot.supreme.sql.MySQL;
import de.codemakers.bot.supreme.util.NetworkUtil;
import de.codemakers.bot.supreme.util.Standard;
import de.codemakers.bot.supreme.util.SystemOutputStream;
import java.security.Permission;
import java.time.Instant;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Game.GameType;

/**
 * Supreme-Bot
 *
 * @author Panzer1119
 */
public class SupremeBot {

    private static JDABuilder builder = null;
    private static JDA jda = null;
    private static boolean running = false;
    private static Game game = null;

    public static final void main(String[] args) {
        try {
            Standard.setStarted(Instant.now());
            System.setOut(new SystemOutputStream(System.out, false));
            System.setErr(new SystemOutputStream(System.err, true));
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                //Standard.STANDARD_SETTINGS.saveSettings(); //FIXME WTF This is deleting the settings file all the time?!
                Standard.saveAllGuildSettings();
            }));
            Standard.JDA_SUPPLIER = () -> jda;
            NetworkUtil.init();
            reload();
            MySQL.init();
            builder = new JDABuilder(AccountType.BOT);
            builder.setAutoReconnect(true);
            //builder.setAudioSendFactory(null);
            builder.setStatus(OnlineStatus.ONLINE);
            builder.setGame(game = Game.of(GameType.DEFAULT, "Supreme-Bot"));
            initListeners();
            initCommands();
            init();
            initPlugins();
            loadAllGuilds();
            startJDA();
        } catch (Exception ex) {
            System.err.println("Main Error: " + ex);
            ex.printStackTrace();
        }
    }

    private static final boolean initListeners() {
        try {
            builder.addEventListener(new GuildLogger());
            builder.addEventListener(new GuildMemberLogger());
            builder.addEventListener(new GuildVoiceLogger());
            builder.addEventListener(MessageHandler.MESSAGE_HANDLER = new MessageHandler());
            builder.addEventListener(new ReadyListener());
            builder.addEventListener(new UserLogger());
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    private static final boolean initCommands() {
        try {
            //Normal Commands
            new AudioRecorderCommand();
            new HelpCommand();
            new PingCommand();
            new RolesCommand();
            new SolveCommand();
            new UptimeCommand();
            new UserCommand();
            //Fun Commands
            new GameOfLifeCommand();
            new MusicCommand();
            new SayCommand();
            new TicTacToeCommand();
            //Moderation Commands
            new ChangeCommandPrefixCommand();
            new ChangeNicknameCommand();
            new ClearCommand();
            new CommandCommand();
            new CopyCommand();
            new GetCommandPrefixCommand();
            new GetFileCommand();
            new MoveAllCommand();
            new MoveCommand();
            new ReloadCommand();
            new RestartCommand();
            new SettingsCommand();
            new StopCommand();
            new TempBanCommand();
            new UploadFileCommand();
            new VoiceKickCommand();
            //Util Commands
            new BackupCommand();
            new GetLogCommand();
            new GuildsCommand();
            new SystemCommand();
            new XMLEditorCommand();
            //Secret Commands
            new ConsoleCommand();
            new PasteServerCommand();
            //Test Commands
            new ARCommand(); //FIXME REMOVE THIS!!!
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    private static final boolean init() {
        try {
            SystemOutputStream.SEND_TO_CONSOLE_CONSUMER = (send_to_console) -> {
                setStatus(GameType.STREAMING, send_to_console ? "Redirecting to Console" : null);
            };
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    private static final boolean initPlugins() {
        try {
            Standard.loadPlugins();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static final boolean setStatus(GameType type, String status) {
        jda.getPresence().setGame(status == null ? game : Game.of(type != null ? type : GameType.DEFAULT, status));
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
            jda = builder.buildAsync();
            return true;
        } catch (Exception ex) {
            if (!(ex instanceof InterruptedException)) {
                ex.printStackTrace();
            } else {
                System.err.println(ex);
            }
            return false;
        }
    }

    public static final boolean stopJDA(boolean now) {
        if (!running) {
            return true;
        }
        try {
            final Thread thread = new Thread(() -> {
                if (now) {
                    jda.shutdownNow();
                } else {
                    jda.shutdown();
                }
            });
            thread.start();
            thread.join();
            running = false;
            reload();
            return true;
        } catch (Exception ex) {
            if (!(ex instanceof InterruptedException)) {
                ex.printStackTrace();
            } else {
                System.err.println(ex);
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

    public static final boolean initAdvancedGuilds() {
        try {
            Standard.initAdvancedGuilds();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static final boolean loadAllGuilds() {
        if (Standard.getJDA() == null) {
            return false;
        }
        try {
            Standard.loadAllGuilds();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static final boolean stopCompletely(int status) {
        Standard.runShutdownHooks();
        stopJDA(true);
        exit(status);
        return true;
    }

    private static final boolean exit(int status) {
        superSecurityManager.exit(status);
        return true;
    }

    private static final SuperSecurityManager superSecurityManager = new SuperSecurityManager();

    private static final class SuperSecurityManager extends SecurityManager {

        private static final String SETSECURITYMANAGER = "setSecurityManager";

        private boolean enabled = true;

        public SuperSecurityManager() {
            System.setSecurityManager(this);
        }

        @Override
        public final void checkExit(int status) {
            if (enabled) {
                throw new ExitTrappedException("!!!WARNING SOMEONE WANTED TO EXIT THE SYSTEM!!!");
            }
        }

        @Override
        public final void checkPermission(Permission permission) {
            if (permission == null || permission.getName() == null) {
                return;
            }
            if (permission.getName().equals(SETSECURITYMANAGER) || permission.getName().equalsIgnoreCase(SETSECURITYMANAGER) || permission.getName().startsWith(SETSECURITYMANAGER)) {
                throw new SecurityException("!!!WARNING SOMEONE WANTED TO CHANGE THE SECURITYMANAGER!!!");
            }
        }

        public final SuperSecurityManager exit(int status) {
            enabled = false;
            System.exit(status);
            enabled = true;
            return this;
        }

        @Override
        public final String toString() {
            return "SecurityManager by Panzer1119";
        }

    }

}
