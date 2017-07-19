package de.panzercraft.bot.supreme.commands;

import de.panzercraft.bot.supreme.permission.PermissionRole;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * PingCommand
 * 
 * @author Panzer1119
 */
public class PingCommand implements Command {

    @Override
    public String[] getInvokes() {
        return new String[] {"ping"};
    }

    @Override
    public final boolean called(String invoke, String[] args, MessageReceivedEvent event) {
        return true;
    }

    @Override
    public final void action(String invoke, String[] args, MessageReceivedEvent event) {
        event.getTextChannel().sendMessage("Pong!").queue();
    }

    @Override
    public final void executed(boolean success, MessageReceivedEvent event) {
        System.out.println("[INFO] Command '" + getInvokes()[0] + "' wurde ausgeführt!");
    }

    @Override
    public final String getHelp() {
        return null;
    }

    @Override
    public PermissionRole getMinimumPermissionRole() {
        return null;
    }
    
}
