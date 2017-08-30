package de.codemakers.bot.supreme.commands.impl.moderation;

import de.codemakers.bot.supreme.commands.CommandCategory;
import de.codemakers.bot.supreme.commands.arguments.ArgumentList;
import de.codemakers.bot.supreme.commands.invoking.Invoker;
import static de.codemakers.bot.supreme.core.SupremeBot.stopCompletely;
import de.codemakers.bot.supreme.entities.MessageEvent;
import de.codemakers.bot.supreme.permission.PermissionRoleFilter;
import de.codemakers.bot.supreme.util.Standard;
import de.codemakers.bot.supreme.util.Timer;
import de.codemakers.bot.supreme.util.Util;
import java.awt.Color;
import java.util.concurrent.atomic.AtomicInteger;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;

/**
 * StopCommand
 *
 * @author Panzer1119
 */
public class StopCommand extends AdministrativeCommand {

    @Override
    public final void initInvokers() {
        addInvokers(Invoker.createInvoker("stop", this), Invoker.createInvoker("shutdown", this));
    }

    @Override
    public final boolean called(Invoker invoker, ArgumentList arguments, MessageEvent event) {
        return arguments == null || arguments.isSize(0, 1);
    }

    @Override
    public final void action(Invoker invoker, ArgumentList arguments, MessageEvent event) {
        if (arguments != null && arguments.size() >= 1) {
            try {
                final double delayStopInSeconds = Double.parseDouble(arguments.consumeFirst());
                final Message message = event.sendAndWaitMessage(getRestartingMessage(event, delayStopInSeconds, 0).build());
                final Timer timer_1 = Util.createTimer();
                final Timer timer_2 = Util.createTimer();
                final AtomicInteger i = new AtomicInteger(0);
                final Runnable run_1 = () -> {
                    message.editMessage(getStoppingMessage(event, delayStopInSeconds, i.get()).build()).queue();
                    i.set(i.get() + 1);
                    if (i.get() >= delayStopInSeconds) {
                        timer_1.purge();
                    }
                };
                final Runnable run_2 = () -> {
                    timer_1.purge();
                    message.editMessage(Standard.getMessageEmbed(Color.YELLOW, "%s stopped me!", event.getAuthor().getAsMention()).build()).complete();
                    final Timer timer_3 = Util.createTimer();
                    final Runnable run_3 = () -> {
                        stopCompletely(0);
                    };
                    Util.sheduleTimerAndRemove(run_3, 1000, timer_3);
                };
                Util.sheduleTimerAtFixedRate(run_1, 0, 1000, timer_1);
                Util.sheduleTimerAndRemove(run_2, (long) (delayStopInSeconds * 1000.0 + 0.5), timer_2);
                return;
            } catch (Exception ex) {
            }
        }
        try {
            event.sendMessage(Standard.getMessageEmbed(Color.YELLOW, "%s stopped me!", event.getAuthor().getAsMention()).build());
            stopCompletely(0);
        } catch (Exception ex) {
            stopCompletely(-1);
        }
    }

    @Override
    public final void executed(boolean success, MessageEvent event) {
        System.out.println("[INFO] Command '" + getCommandID() + "' was executed!");
    }

    @Override
    public final EmbedBuilder getHelp(Invoker invoker, EmbedBuilder builder) {
        builder.addField(invoker + " [Delay]", "Stops the bot immediately or after the given delay in seconds.", false);
        return builder;
    }

    @Override
    public final PermissionRoleFilter getPermissionRoleFilter() {
        return Standard.STANDARD_PERMISSIONROLEFILTER_SUPER_OWNER;
    }

    @Override
    public final String getCommandID() {
        return getClass().getName();
    }

    @Override
    public CommandCategory getCommandCategory() {
        return Standard.COMMANDCATEGORY_MODERATION;
    }

}
