package de.codemakers.bot.supreme.commands.impl.moderation.util;

import de.codemakers.bot.supreme.commands.Command;
import de.codemakers.bot.supreme.commands.CommandCategory;
import de.codemakers.bot.supreme.commands.arguments.ArgumentConsumeType;
import de.codemakers.bot.supreme.commands.arguments.ArgumentList;
import de.codemakers.bot.supreme.commands.invoking.Invoker;
import de.codemakers.bot.supreme.entities.DeleteMessageManager;
import de.codemakers.bot.supreme.entities.MessageEvent;
import de.codemakers.bot.supreme.util.AdvancedFile;
import de.codemakers.bot.supreme.util.Standard;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.dv8tion.jda.core.EmbedBuilder;
import de.codemakers.bot.supreme.permission.PermissionFilter;
import de.codemakers.bot.supreme.settings.GlobalConfig;

/**
 * GetLogCommand
 *
 * @author Panzer1119
 */
public class GetLogCommand extends Command {

    public static final String LOG_PREFIX = "log_";
    public static final String LOG_SUFFIX = ".txt";

    @Override
    public void initInvokers() {
        addInvokers(Invoker.createInvoker("getLog", this), Invoker.createInvoker("gLog", this));
    }

    @Override
    public boolean called(Invoker invoker, ArgumentList arguments, MessageEvent event) {
        if (arguments == null) {
            return false;
        }
        if (arguments.isEmpty()) {
            return true;
        }
        final boolean list = arguments.isConsumed(Standard.ARGUMENT_LIST, ArgumentConsumeType.FIRST_IGNORE_CASE);
        //final boolean download = arguments.isConsumed(Standard.ARGUMENT_DOWNLOAD, ArgumentConsumeType.FIRST_IGNORE_CASE);
        if (list/* || download*/) {
            /*
            if (!list) {
                return false;
            }
            if (download) {
                return arguments.isSize(2);
            }*/
            return arguments.isSize(1); //-list
        }
        return arguments.isSize(1); //Log
    }

    @Override
    public void action(Invoker invoker, ArgumentList arguments, MessageEvent event) {
        final boolean list = arguments.isConsumed(Standard.ARGUMENT_LIST, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
        //final boolean download = arguments.isConsumed(Standard.ARGUMENT_DOWNLOAD, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
        if (list) { //-list
            //final String list_logs = Standard.STANDARD_LOG_FOLDER.listAdvancedFiles((parent, name) -> (name != null && name.startsWith(LOG_PREFIX) && name.endsWith(LOG_SUFFIX))).stream().map((advancedFile) -> advancedFile.toFile()).map((advancedFile) -> (/*download ? Standard.embedLink(advancedFile.getName(), event.getTextChannel().sendFile(advancedFile.toByteArray(), advancedFile.getName(), null).complete().getAttachments().get(0).getUrl()) : */advancedFile.getName())).collect(Collectors.joining(Standard.NEW_LINE_DISCORD));
            //final int log_file_show_count = Standard.STANDARD_SETTINGS.asAutoAdd().getProperty("log_file_show_count", 25);
            final int log_file_show_count = GlobalConfig.GLOBAL_CONFIG.getLogFileShowCount();
            List<AdvancedFile> files = Standard.STANDARD_LOG_FOLDER.listAdvancedFiles((parent, name) -> (name != null && name.startsWith(LOG_PREFIX) && name.endsWith(".txt")));
            files = files.stream().skip(files.size() - log_file_show_count).collect(Collectors.toList());
            final Map<String, Map<String, Map<String, List<AdvancedFile>>>> years = new HashMap<>();
            files.stream().forEach((advancedFile) -> {
                final String name = advancedFile.getName();
                final String year = name.substring(LOG_PREFIX.length(), LOG_PREFIX.length() + 4);
                final String month = name.substring(LOG_PREFIX.length() + 5, LOG_PREFIX.length() + 7);
                final String day = name.substring(LOG_PREFIX.length() + 8, LOG_PREFIX.length() + 10);
                years.computeIfAbsent(year, (key) -> new HashMap<>());
                years.get(year).computeIfAbsent(month, (key) -> new HashMap<>());
                years.get(year).get(month).computeIfAbsent(day, (key) -> new ArrayList<>());
                years.get(year).get(month).get(day).add(advancedFile);
            });
            final StringBuilder out = new StringBuilder();
            out.append(Standard.toUnderlineBold(String.format("Logs (last %d):", log_file_show_count))).append(Standard.NEW_LINE_DISCORD).append(Standard.NEW_LINE_DISCORD);
            years.keySet().stream().map((year) -> {
                out.append(Standard.toBold(Standard.toUnderline("YEAR:") + " " + year)).append(Standard.NEW_LINE_DISCORD);
                return year;
            }).map((year) -> years.get(year)).forEach((months) -> {
                months.keySet().stream().map((month) -> {
                    out.append(Standard.TAB).append(Standard.TAB).append(Standard.toBold(Standard.toUnderline("MONTH:") + " " + month)).append(Standard.NEW_LINE_DISCORD);
                    return month;
                }).map((month) -> months.get(month)).forEach((days) -> {
                    days.keySet().stream().map((day) -> {
                        out.append(Standard.TAB).append(Standard.TAB).append(Standard.TAB).append(Standard.TAB).append(Standard.toBold(Standard.toUnderline("DAY:") + " " + day)).append(Standard.NEW_LINE_DISCORD);
                        return day;
                    }).map((day) -> days.get(day)).forEach((logs) -> logs.stream().forEach((advancedFile) -> out.append(Standard.TAB).append(Standard.TAB).append(Standard.TAB).append(Standard.TAB).append(Standard.TAB).append(Standard.TAB).append(advancedFile.getName()).append(Standard.NEW_LINE_DISCORD)));
                });
            });
            DeleteMessageManager.monitor(event.sendAndWaitMessage(out.toString()));
        } else {
            String log = "";
            if (arguments.isEmpty()) {
                final AdvancedFile log_file = Standard.getCurrentLogFile();
                if (log_file == null) {
                    event.sendMessage(Standard.STANDARD_MESSAGE_DELETING_DELAY, Standard.getNoMessage(event.getAuthor(), "there is no current log file!").build());
                    return;
                }
                log = log_file.getName();
            } else { //Log
                log = arguments.consumeFirst();
            }
            final AdvancedFile log_file = Standard.getLogFile(log);
            if (log_file == null || !log_file.exists() || !log_file.isFile()) {
                event.sendMessage(Standard.STANDARD_MESSAGE_DELETING_DELAY, Standard.getNoMessage(event.getAuthor(), "the log file \"%s\" doesn't exists!", log).build());
                return;
            }
            final byte[] buffer = log_file.toByteArray();
            if (buffer == null) {
                event.sendMessage(Standard.STANDARD_MESSAGE_DELETING_DELAY, Standard.getNoMessage(event.getAuthor(), "the log file \"%s\" can't be read!", log).build());
                return;
            }
            DeleteMessageManager.monitor(event.sendAndWaitFile(buffer, log, null));
        }
    }

    @Override
    public void executed(boolean success, MessageEvent event) {
        System.out.println("[INFO] Command '" + getCommandID() + "' was executed!");
    }

    @Override
    public EmbedBuilder getHelp(Invoker invoker, EmbedBuilder builder) {
        builder.addField(String.format("%s", invoker), "Sends the current log.", false);
        builder.addField(String.format("%s <Log>", invoker), "Sends the specified log.", false);
        builder.addField(String.format("%s %s", invoker, Standard.ARGUMENT_LIST.getCompleteArgument(0, -1)), "Lists all logs.", false);
        //builder.addField(String.format("%s %s [%s]", invoker, Standard.ARGUMENT_LIST.getCompleteArgument(0, -1), Standard.ARGUMENT_DOWNLOAD.getCompleteArgument(0, -1)), String.format("Lists all logs. The flag \"%s\" uploads all logs to the server.", Standard.ARGUMENT_DOWNLOAD.getCompleteArgument(0, -1)), false);
        return builder;
    }

    @Override
    public PermissionFilter getPermissionFilter() {
        return Standard.STANDARD_PERMISSIONROLEFILTER_SUPER_OWNER;
    }

    @Override
    public String getCommandID() {
        return getClass().getName();
    }

    @Override
    public CommandCategory getCommandCategory() {
        return Standard.COMMANDCATEGORY_MODERATION_UTIL;
    }

}
