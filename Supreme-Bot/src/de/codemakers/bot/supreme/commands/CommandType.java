package de.codemakers.bot.supreme.commands;

import de.codemakers.bot.supreme.entities.MessageEvent;
import de.codemakers.bot.supreme.util.Standard;

/**
 * CommandType
 *
 * @author Panzer1119
 */
public enum CommandType {
    NONE(false, false, false, false),
    ERROR(false, false, true, true),
    COMMAND(true, false, false, false),
    MENTIONED(true, false, true, false),
    NORMAL(true, false, false, true),
    PRIVATE_COMMAND(true, true, false, false),
    PRIVATE_MENTIONED(true, true, true, false),
    PRIVATE_NORMAL(true, true, false, true);

    private final boolean command;
    private final boolean private_;
    private final boolean mentioned;
    private final boolean normal;

    private CommandType(boolean command, boolean private_, boolean mentioned, boolean normal) {
        this.command = command;
        this.private_ = private_;
        this.mentioned = mentioned;
        this.normal = normal;
    }

    public final boolean isCommand() {
        return command;
    }

    public final boolean isPrivate() {
        return private_;
    }

    public final boolean isMentioned() {
        return mentioned;
    }

    public final boolean isNormal() {
        return normal;
    }

    public static final CommandType getCommandType(String content, String content_raw, MessageEvent event) {
        if (content == null || content_raw == null || event == null || event.getAuthor().getId().equals(Standard.getSelfUser().getId())) {
            return CommandType.NONE;
        }
        boolean command = false;
        boolean private_ = false;
        boolean mentioned = false;
        boolean normal = false;
        if (event.isPrivate()) {
            command = true;
            private_ = true;
        }
        if (content.startsWith(Standard.getCommandPrefixByGuild(event.getGuild()))) {
            command = true;
            normal = true;
        }
        if (content_raw.replaceFirst("!", "").startsWith(Standard.getSelfUser().getAsMention())) {
            command = true;
            mentioned = true;
        }
        if (!command) {
            return CommandType.NONE;
        } else if (mentioned && normal) {
            return CommandType.ERROR;
        }
        for (CommandType commandType : values()) {
            if (commandType.command && commandType.private_ == private_ && commandType.mentioned == mentioned && commandType.normal == normal) {
                return commandType;
            }
        }
        return CommandType.NONE;
    }
}
