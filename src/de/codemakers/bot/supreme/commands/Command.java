package de.codemakers.bot.supreme.commands;

import de.codemakers.bot.supreme.commands.arguments.ArgumentList;
import de.codemakers.bot.supreme.entities.MessageEvent;
import de.codemakers.bot.supreme.permission.PermissionRoleFilter;
import net.dv8tion.jda.core.EmbedBuilder;

/**
 * Command
 *
 * @author Panzer1119
 */
public abstract class Command {

    public Command() {
        this(true);
    }

    public Command(boolean register) {
        if (register) {
            CommandHandler.registerCommand(this);
        }
    }

    public abstract String[] getInvokes();

    public abstract boolean called(String invoke, ArgumentList arguments, MessageEvent event);

    public abstract void action(String invoke, ArgumentList arguments, MessageEvent event);

    public abstract void executed(boolean success, MessageEvent event);

    public abstract EmbedBuilder getHelp(EmbedBuilder builder);

    /**
     * e.g. PermissionRole.getPermissionRoleByName("Admin");
     *
     * @param role PermissionRole
     * @return <tt>true</tt> or <tt>false</tt>
     */
    public abstract PermissionRoleFilter getPermissionRoleFilter();

}
