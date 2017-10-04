package de.codemakers.bot.supreme.commands.impl.moderation.util;

import de.codemakers.bot.supreme.commands.Command;
import de.codemakers.bot.supreme.commands.CommandCategory;
import de.codemakers.bot.supreme.commands.arguments.ArgumentConsumeType;
import de.codemakers.bot.supreme.commands.arguments.ArgumentList;
import de.codemakers.bot.supreme.commands.invoking.Invoker;
import de.codemakers.bot.supreme.entities.MessageEvent;
import de.codemakers.bot.supreme.permission.PermissionRoleFilter;
import de.codemakers.bot.supreme.util.AdvancedFile;
import de.codemakers.bot.supreme.util.Standard;
import java.awt.Color;
import java.util.stream.Collectors;
import net.dv8tion.jda.core.EmbedBuilder;

/**
 * GetLogCommand
 *
 * @author Panzer1119
 */
public class GetLogCommand extends Command {

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
            final String list_logs = Standard.STANDARD_LOG_FOLDER.listAdvancedFiles((parent, name) -> (name != null && name.startsWith("log_") && name.endsWith(".txt"))).stream().map((advancedFile) -> (/*download ? Standard.embedLink(advancedFile.getName(), event.getTextChannel().sendFile(advancedFile.toByteArray(), advancedFile.getName(), null).complete().getAttachments().get(0).getUrl()) : */advancedFile.getName())).collect(Collectors.joining("\n"));
            event.sendMessage(4 * Standard.STANDARD_MESSAGE_DELETING_DELAY, Standard.getMessageEmbed(Color.YELLOW, null).addField("Logs:", list_logs, false).build());
        } else {
            String log = "";
            if (arguments.isEmpty()) {
                if (Standard.CURRENT_LOG_FILE == null) {
                    event.sendMessage(Standard.STANDARD_MESSAGE_DELETING_DELAY, Standard.getNoMessage(event.getAuthor(), "there is no current log file!").build());
                    return;
                }
                log = Standard.CURRENT_LOG_FILE.getName();
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
            event.sendFile(4 * Standard.STANDARD_MESSAGE_DELETING_DELAY, buffer, log, null);
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
    public PermissionRoleFilter getPermissionRoleFilter() {
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
