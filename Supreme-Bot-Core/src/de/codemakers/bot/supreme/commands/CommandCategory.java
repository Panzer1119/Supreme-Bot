package de.codemakers.bot.supreme.commands;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * CommandCategory
 *
 * @author Panzer1119
 */
public class CommandCategory {

    public static final ArrayList<CommandCategory> COMMANDCATEGORIES = new ArrayList<>();
    public static final Comparator<CommandCategory> COMPARATOR = (cc1, cc2) -> {
        if (cc1 == null || cc1.getName() == null || cc1.getName().isEmpty()) {
            return 1;
        }
        if (cc2 == null || cc2.getName() == null || cc2.getName().isEmpty()) {
            return -1;
        }
        return cc1.getName().compareToIgnoreCase(cc2.getName());
    };

    private final CommandCategory ME = this;
    private CommandCategory parent = null;
    private String name = "";
    private String emoji = "";

    public CommandCategory(CommandCategory parent, String name, String emoji) {
        this.parent = parent;
        this.name = name;
        this.emoji = emoji;
        register();
    }

    public final CommandCategory getParent() {
        return parent;
    }

    public final CommandCategory setParent(CommandCategory parent) {
        this.parent = parent;
        return this;
    }

    public final String getName() {
        return name;
    }

    public final CommandCategory setName(String name) {
        this.name = name;
        return this;
    }

    public final String getEmoji() {
        return emoji;
    }

    public final CommandCategory setEmoji(String emoji) {
        this.emoji = emoji;
        return this;
    }

    public final String toListEntry() {
        return emoji + " - " + name;
    }

    public final boolean register() {
        if (!COMMANDCATEGORIES.contains(this)) {
            COMMANDCATEGORIES.add(this);
            return true;
        }
        return false;
    }

    public final boolean unregister() {
        if (!COMMANDCATEGORIES.contains(this)) {
            COMMANDCATEGORIES.remove(this);
            return true;
        }
        return false;
    }

    public final ArrayList<CommandCategory> getParents() {
        if (parent == null) {
            return new ArrayList<>();
        }
        final ArrayList<CommandCategory> parents = new ArrayList<>();
        CommandCategory parent_ = parent;
        parents.add(parent);
        while ((parent_ = parent_.parent) != null) {
            parents.add(parent_);
        }
        return parents;
    }

    public final ArrayList<CommandCategory> getChildren(boolean all) {
        final ArrayList<CommandCategory> children = new ArrayList<>();
        COMMANDCATEGORIES.stream().forEach((commandCategory) -> {
            if (commandCategory.parent == ME) {
                children.add(commandCategory);
                if (all) {
                    children.addAll(commandCategory.getChildren(all).stream().filter((commandCategory_) -> !children.contains(commandCategory_)).collect(Collectors.toList()));
                }
            }
        });
        return children;
    }

    public final List<Command> getCommands(boolean all) {
        return CommandHandler.COMMANDS.stream().filter((command) -> {
            if (command.getCommandCategory() == null) {
                return false;
            }
            if (command.getCommandCategory() == ME) {
                return true;
            }
            if (all) {
                return command.getCommandCategory().getParents().contains(ME);
            } else {
                return false;
            }
        }).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return name;
    }

    public static final List<CommandCategory> getRoots() {
        if (COMMANDCATEGORIES.isEmpty()) {
            return new ArrayList<>();
        }
        return COMMANDCATEGORIES.stream().filter((commandCategory) -> {
            return (commandCategory.parent == null);
        }).collect(Collectors.toList());
    }

}
