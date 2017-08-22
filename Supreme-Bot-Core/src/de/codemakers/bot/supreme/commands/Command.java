package de.codemakers.bot.supreme.commands;

import de.codemakers.bot.supreme.commands.arguments.ArgumentList;
import de.codemakers.bot.supreme.commands.invoking.Invokeable;
import de.codemakers.bot.supreme.commands.invoking.Invoker;
import de.codemakers.bot.supreme.entities.AdvancedMember;
import de.codemakers.bot.supreme.entities.MemberObject;
import de.codemakers.bot.supreme.entities.MessageEvent;
import de.codemakers.bot.supreme.permission.PermissionRoleFilter;
import de.codemakers.bot.supreme.util.Util;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.User;

/**
 * Command
 *
 * @author Panzer1119
 */
public abstract class Command implements Invokeable {

    private final ArrayList<Invoker> invokers = Invoker.createInvokerArrayList();

    public Command(Invoker... invokers) {
        addInvokers(invokers);
        initInvokers();
        CommandHandler.registerCommand(this);
    }

    public final ArrayList<Invoker> getInvokers() {
        return Invoker.getInvokersByCommand(this);
    }

    public final boolean addInvokers(Invoker... invokers) {
        return this.invokers.addAll(Arrays.asList(invokers));
    }

    public final boolean removeInvokers(Invoker... invokers) {
        return this.invokers.removeAll(Arrays.asList(invokers));
    }

    public final boolean containsInvokers(Invoker... invokers) {
        return getInvokers().stream().filter((invoker) -> Util.contains(invokers, invoker)).collect(Collectors.toList()).size() == invokers.length;
    }

    @Override
    public final Invokeable getInvokeable() {
        return this;
    }

    public abstract void initInvokers();

    public abstract boolean called(Invoker invoker, ArgumentList arguments, MessageEvent event);

    public abstract void action(Invoker invoker, ArgumentList arguments, MessageEvent event);

    public abstract void executed(boolean success, MessageEvent event);

    public abstract EmbedBuilder getHelp(Invoker invoker, EmbedBuilder builder);

    /**
     * e.g. PermissionRole.getPermissionRoleByName("Admin");
     *
     * @param role PermissionRole
     * @return <tt>true</tt> or <tt>false</tt>
     */
    public abstract PermissionRoleFilter getPermissionRoleFilter();

    public abstract String getCommandID();

    @Override
    public final boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (object instanceof Command) {
            final Command command = (Command) object;
            return command.getCommandID().equals(getCommandID());
        } else if (object instanceof String) {
            final String command_id = (String) object;
            return command_id.equals(getCommandID());
        }
        return false;
    }

    @Override
    public String toString() {
        return getCommandID();
    }

    protected final MemberObject getMemberObject(User user) {
        if (user == null) {
            return null;
        }
        return MemberObject.getMemberObjectByExactMembers(AdvancedMember.ofUser(user));
    }

    protected final <T> T getObject(MemberObject memberObject, Class<? extends T> type) {
        if (memberObject == null || type == null) {
            return null;
        }
        return (T) memberObject.getData(type.getSimpleName());
    }

}
