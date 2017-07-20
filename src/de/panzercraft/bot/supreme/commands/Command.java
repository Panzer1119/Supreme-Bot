package de.panzercraft.bot.supreme.commands;

import de.panzercraft.bot.supreme.commands.arguments.ArgumentList;
import de.panzercraft.bot.supreme.entities.MessageEvent;
import de.panzercraft.bot.supreme.permission.PermissionRoleFilter;
import net.dv8tion.jda.core.EmbedBuilder;

/**
 * Command
 *
 * @author Panzer1119
 */
public interface Command {

    public String[] getInvokes();

    public boolean called(String invoke, ArgumentList arguments, MessageEvent event);

    public void action(String invoke, ArgumentList arguments, MessageEvent event);

    public void executed(boolean success, MessageEvent event);

    public EmbedBuilder getHelp(EmbedBuilder builder);
    
    /**
     * e.g. PermissionRole.getPermissionRoleByName("Admin");
     * @param role PermissionRole
     * @return <tt>true</tt> or <tt>false</tt>
     */
    public PermissionRoleFilter getPermissionRoleFilter();

}
