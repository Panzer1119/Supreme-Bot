package de.codemakers.bot.supreme.commands.impl;

import de.codemakers.bot.supreme.commands.Command;
import de.codemakers.bot.supreme.commands.arguments.ArgumentList;
import de.codemakers.bot.supreme.commands.invoking.Invoker;
import de.codemakers.bot.supreme.entities.MessageEvent;
import de.codemakers.bot.supreme.permission.PermissionRole;
import de.codemakers.bot.supreme.permission.PermissionRoleFilter;
import de.codemakers.bot.supreme.util.Standard;
import java.util.ArrayList;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;

/**
 * RolesCommand
 *
 * @author Panzer1119
 */
public class RolesCommand extends Command {

    @Override
    public void initInvokers() {
        addInvokers(Invoker.createInvoker("permissionroles", this), Invoker.createInvoker("permroles", this), Invoker.createInvoker("proles", this));
    }

    @Override
    public boolean called(Invoker invoker, ArgumentList arguments, MessageEvent event) {
        return arguments == null || arguments.isEmpty();
    }

    @Override
    public void action(Invoker invoker, ArgumentList arguments, MessageEvent event) {
        final Member member = event.getMember();
        final ArrayList<PermissionRole> permissionRoles = PermissionRole.getPermissionRolesByMember(member);
        final StringBuilder sb = new StringBuilder();
        permissionRoles.stream().forEach((permissionRole) -> {
            sb.append("\n`");
            sb.append(permissionRole);
            sb.append("`");
        });
        event.sendMessage(Standard.getMessageEmbed(null, "PermissionRoles from %s:%s", event.getAuthor().getAsMention(), sb.toString()).build());
    }

    @Override
    public void executed(boolean success, MessageEvent event) {
        System.out.println("[INFO] Command '" + getCommandID() + "' was executed!");
    }

    @Override
    public EmbedBuilder getHelp(Invoker invoker, EmbedBuilder builder) {
        return builder;
    }

    @Override
    public PermissionRoleFilter getPermissionRoleFilter() {
        return null;
    }

    @Override
    public String getCommandID() {
        return getClass().getName();
    }

}
