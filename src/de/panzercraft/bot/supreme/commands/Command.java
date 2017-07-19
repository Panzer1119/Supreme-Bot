package de.panzercraft.bot.supreme.commands;

import de.panzercraft.bot.supreme.permission.PermissionRole;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * Command
 *
 * @author Panzer1119
 */
public interface Command {

    public String[] getInvokes();

    public boolean called(String invoke, String[] args, MessageReceivedEvent event);

    public void action(String invoke, String[] args, MessageReceivedEvent event);

    public void executed(boolean success, MessageReceivedEvent event);

    public String getHelp();

    /**
     * If null then @everyone can use this command
     * e.g. PermissionRole.getPermissionRoleByName("Admin");
     *
     * @return
     */
    public PermissionRole getMinimumPermissionRole();

}
