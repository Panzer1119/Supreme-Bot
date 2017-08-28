package de.codemakers.bot.supreme.commands;

import de.codemakers.bot.supreme.commands.invoking.Invoker;
import de.codemakers.bot.supreme.entities.MessageEvent;
import de.codemakers.bot.supreme.permission.PermissionHandler;
import de.codemakers.bot.supreme.permission.PermissionRoleFilter;
import de.codemakers.bot.supreme.util.Emoji;
import de.codemakers.bot.supreme.util.Standard;
import de.codemakers.bot.supreme.util.Util;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;

/**
 * CommandHandler
 *
 * @author Panzer1119
 */
public class CommandHandler {

    public static final String SEND_HELP_ALWAYS_PRIVATE = "send_help_always_private";

    public static final String COMMANDCATEGORY_HIERARCHY_SPACER = "   ";
    public static final String COMMANDCATEGORY_HIERARCHY_NEW_LINE = "\n";
    public static final String COMMANDCATEGORY_HIERARCHY_LINE_DOWN = " |";
    public static final String COMMANDCATEGORY_HIERARCHY_LINE_CROSS = "├──";
    public static final String COMMANDCATEGORY_HIERARCHY_LINE_END = "└──";

    public static final ArrayList<Command> COMMANDS = new ArrayList<>();

    public static final boolean handleCommand(CommandContainer commandContainer) {
        try {
            if (commandContainer == null) {
                return false;
            }
            if (commandContainer.invoker != null) {
                final Command command = ((Command) commandContainer.invoker.getInvokeable());
                if (Standard.isAutoDeletingCommandByGuild(commandContainer.event.getGuild())) {
                    commandContainer.event.getMessage().delete().queue();
                }
                if (command != null) {
                    if (!PermissionHandler.check(command.getPermissionRoleFilter(), commandContainer.event, true)) {
                        return false;
                    }
                    final boolean safe = command.called(commandContainer.invoker, commandContainer.arguments, commandContainer.event);
                    if (safe) {
                        command.action(commandContainer.invoker, commandContainer.arguments, commandContainer.event);
                    } else {
                        sendHelpMessage(commandContainer.invoker, commandContainer.event, command, false);
                    }
                    command.executed(safe, commandContainer.event);
                    return safe;
                }
            }
            commandContainer.event.sendMessageFormat(Standard.getAutoDeleteCommandNotFoundMessageDelayByGuild(commandContainer.event.getGuild()), "%s Sorry %s, the command \"%s\" wasn't found!", Emoji.WARNING, commandContainer.event.getAuthor().getAsMention(), commandContainer.invoker);
            return false;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static final boolean existsCommand(Invoker... invokers) {
        return getCommandByInvokers(invokers) != null;
    }

    public static final Command getCommandByInvokers(Invoker... invokers) {
        if (invokers == null || invokers.length == 0) {
            return null;
        }
        for (Command command : COMMANDS) {
            if (command.containsInvokers(invokers)) {
                return command;
            }
        }
        return null;
    }

    public static final boolean registerCommand(Command command) {
        if (!COMMANDS.contains(command)) {
            COMMANDS.add(command);
            return true;
        } else {
            return false;
        }
    }

    public static final boolean unregisterCommand(Command command) {
        if (COMMANDS.contains(command)) {
            COMMANDS.remove(command);
            return true;
        } else {
            return false;
        }
    }

    public static final boolean sendHelpMessage(Invoker invoker, MessageEvent event, Command command, boolean sendPrivate) {
        if (event == null || command == null) {
            return false;
        }
        final PermissionRoleFilter filter = command.getPermissionRoleFilter();
        if (filter != null && !PermissionHandler.check(filter, event, true)) {
            return false;
        }
        if (!sendPrivate && event.isPrivate()) {
            sendPrivate = true;
        }
        if (sendPrivate || Standard.getGuildSettings(event.getGuild()).getProperty(SEND_HELP_ALWAYS_PRIVATE, false)) {
            Util.sendPrivateMessage(event.getAuthor(), generateHelpMessage(invoker, event, command).build());
        } else {
            final Guild guild = event.getGuild();
            if (!PermissionHandler.check(filter, guild, event.getTextChannel())) {
                Util.sendPrivateMessage(event.getAuthor(), generateHelpMessage(invoker, event, command).build());
                return true;
            }
            event.sendMessage(generateHelpMessage(invoker, event, command).build());
        }
        return true;
    }

    public static final boolean sendHelpList(MessageEvent event, boolean sendPrivate) {
        if (event == null) {
            return false;
        }
        if (!sendPrivate && event.isPrivate()) {
            sendPrivate = true;
        }
        try {
            final List<Command> commands = CommandHandler.COMMANDS.stream().filter((command) -> {
                return PermissionHandler.check(command.getPermissionRoleFilter(), event, false); //FIXME Should a user only can see the Commands he is allowed to use?
            }).sorted(Command.COMPARATOR).collect(Collectors.toList());
            final StringBuilder sb = new StringBuilder();
            final String command_prefix = sendPrivate ? Standard.getStandardCommandPrefix() : Standard.getCommandPrefixByGuild(event.getGuild());
            sb.append(Standard.toBold("Help Overview | Command Prefix: " + command_prefix));
            sb.append("\n\n\n");
            sb.append(Standard.toUnderlineBold("Command Hierarchy:"));
            sb.append("\n\n");
            sb.append(generateCommandCategoriesHierarchy());
            sb.append("\n\n");
            sb.append(Standard.toUnderlineBold("Commands:"));
            sb.append("\n\n\n");
            final HashMap<CommandCategory, List<Command>> commands_categorized = new HashMap<>();
            commands.stream().forEach((command) -> {
                final CommandCategory commandCategory = Standard.getCommandCategory(command.getCommandCategory());
                List<Command> commands__ = commands_categorized.get(commandCategory);
                if (commands__ == null) {
                    commands__ = new ArrayList<>();
                    commands_categorized.put(commandCategory, commands__);
                }
                commands__.add(command);
            });
            final AtomicInteger length_max = new AtomicInteger(0);
            commands_categorized.keySet().stream().sorted(CommandCategory.COMPARATOR).forEach((commandCategory) -> {
                sb.append(commandCategory.getEmoji());
                sb.append(" - ");
                sb.append(Standard.toUnderlineBold(commandCategory.getName()));
                sb.append("\n");
                length_max.set(0);
                final List<Command> commands_ = commands_categorized.get(commandCategory);
                commands_.stream().forEach((command) -> {
                    length_max.set(Math.max(length_max.get(), command.getInvokers().get(0).toString().length()));
                });
                sb.append(Util.makeTable(commands_, (command) -> command.getInvokers().get(0).toString(), length_max.get() + 2, Util.getGoodSquareNumber(commands_.size())));
                sb.append("\n");
            });
            final String output = sb.toString();
            sb.delete(0, sb.length());
            if (sendPrivate || Standard.getGuildSettings(event.getGuild()).getProperty(SEND_HELP_ALWAYS_PRIVATE, false)) {
                Util.sendPrivateMessage(event.getAuthor(), output);
            } else {
                final Guild guild = event.getGuild();
                if (!PermissionHandler.check(commands, guild, event.getTextChannel())) {
                    Util.sendPrivateMessage(event.getAuthor(), output);
                    return true;
                }
                event.sendMessage(output);
            }
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static final EmbedBuilder generateHelpMessage(Invoker invoker, MessageEvent event, Command command) {
        final EmbedBuilder builder = command.getHelp(invoker, new EmbedBuilder().setTitle(String.format("Help for \"%s\"", invoker)));
        final ArrayList<Invoker> invokers = command.getInvokers();
        if (invokers.size() > 1) {
            builder.setDescription(String.format("Associated Command Invokers: %s", getInvokersAsString(invokers.stream().filter((invoker_) -> !invoker.equals(invoker_)).collect(Collectors.toList()))));
        }
        return builder;
    }

    public static final String generateCommandCategoriesHierarchy() {
        final StringBuilder sb = new StringBuilder();
        final List<CommandCategory> commandCategory_roots = CommandCategory.getRoots();
        commandCategory_roots.stream().sorted(CommandCategory.COMPARATOR).forEach((commandCategory) -> {
            generateCommandCategoriesHierarchy(sb, commandCategory, -1, false);
        });
        String output = sb.toString();
        final String[] split = output.split(COMMANDCATEGORY_HIERARCHY_NEW_LINE);
        for (int i = 0; i < split.length; i++) {
            if (!split[i].contains(COMMANDCATEGORY_HIERARCHY_LINE_END)) {
                continue;
            }
            int index = -1;
            while ((index = split[i].indexOf(COMMANDCATEGORY_HIERARCHY_LINE_END, index + 1)) != -1) {
                int z = 1;
                while (((i + z) < split.length) && (split[i + z].contains(COMMANDCATEGORY_HIERARCHY_LINE_DOWN))) {
                    split[i + z] = split[i + z].substring(0, index + COMMANDCATEGORY_HIERARCHY_LINE_DOWN.length() - 1) + " " + split[i + z].substring(index + COMMANDCATEGORY_HIERARCHY_LINE_DOWN.length());
                    z++;
                }
            }
        }
        output = "";
        for (String g : split) {
            output += g + COMMANDCATEGORY_HIERARCHY_NEW_LINE;
        }
        return output;
    }

    private static final boolean generateCommandCategoriesHierarchy(StringBuilder sb, CommandCategory commandCategory, int depth, boolean last) {
        if (depth > -1) {
            sb.append(COMMANDCATEGORY_HIERARCHY_SPACER);
        }
        for (int i = 0; i < depth; i++) {
            if (depth > 0) {
                sb.append(COMMANDCATEGORY_HIERARCHY_LINE_DOWN);
            }
            sb.append(COMMANDCATEGORY_HIERARCHY_SPACER);
            sb.append(COMMANDCATEGORY_HIERARCHY_SPACER);
            sb.append(COMMANDCATEGORY_HIERARCHY_SPACER);
        }
        final List<CommandCategory> children = commandCategory.getChildren(false);
        if (depth >= 0) {
            if (last) {
                sb.append(COMMANDCATEGORY_HIERARCHY_LINE_END);
            } else {
                sb.append(COMMANDCATEGORY_HIERARCHY_LINE_CROSS);
            }
        }
        sb.append(commandCategory.toListEntry());
        sb.append(COMMANDCATEGORY_HIERARCHY_NEW_LINE);
        final AtomicInteger index = new AtomicInteger(1);
        children.stream().sorted(CommandCategory.COMPARATOR).forEach((commandCategory_) -> {
            generateCommandCategoriesHierarchy(sb, commandCategory_, depth + 1, index.get() >= children.size());
            index.set(index.get() + 1);
        });
        return true;
    }

    public static final String getInvokersAsString(List<Invoker> invokers) {
        if (invokers == null || invokers.isEmpty()) {
            return "";
        }
        final StringBuilder sb = new StringBuilder();
        invokers.stream().forEach((invoker) -> {
            sb.append(", \"");
            sb.append(invoker);
            sb.append("\"");
        });
        sb.delete(0, 2);
        return sb.toString();
    }

}
