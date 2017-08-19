package de.codemakers.bot.supreme.commands.impl.moderation;

import de.codemakers.bot.supreme.commands.arguments.ArgumentList;
import de.codemakers.bot.supreme.commands.invoking.Invoker;
import de.codemakers.bot.supreme.entities.MessageEvent;
import de.codemakers.bot.supreme.permission.PermissionRoleFilter;
import de.codemakers.bot.supreme.util.IntegerHolder;
import de.codemakers.bot.supreme.util.Standard;
import de.codemakers.bot.supreme.util.Timer;
import de.codemakers.bot.supreme.util.Util;
import java.awt.Color;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;

/**
 * RestartCommand
 *
 * @author Panzer1119
 */
public class RestartCommand extends AdministrativeCommand {

    @Override
    public final void initInvokers() {
        addInvokers(Invoker.createInvoker("restart", this), Invoker.createInvoker("reboot", this));
    }

    @Override
    public final boolean called(Invoker invoker, ArgumentList arguments, MessageEvent event) {
        return arguments == null || arguments.isSize(0, 2);
    }

    @Override
    public final void action(Invoker invoker, ArgumentList arguments, MessageEvent event) {
        if (arguments != null && arguments.size() >= 1) {
            try {
                final double delayStopInSeconds = Double.parseDouble(arguments.consumeFirst());
                double delayStartInSeconds_temp = -1;
                if (!arguments.isEmpty()) {
                    delayStartInSeconds_temp = Double.parseDouble(arguments.consumeFirst());
                }
                final double delayStartInSeconds = delayStartInSeconds_temp;
                final Message message = event.sendAndWaitMessage(getRestartingMessage(event, delayStopInSeconds, 0).build());
                final Timer timer_1 = Util.createTimer();
                final Timer timer_2 = Util.createTimer();
                final IntegerHolder i = new IntegerHolder();
                final Runnable run_1 = () -> {
                    message.editMessage(getStoppingMessage(event, delayStopInSeconds, i.value).build()).queue();
                    i.value++;
                    if (i.value >= delayStopInSeconds) {
                        timer_1.purge();
                    }
                };
                final Runnable run_2 = () -> {
                    timer_1.purge();
                    //run_1.cancel(); //FIXME?!??!?!
                    message.editMessage(Standard.getMessageEmbed(Color.YELLOW, "%s restarting me!", event.getAuthor().getAsMention()).build()).queue();
                    if (delayStartInSeconds == -1) {
                        restart();
                    } else {
                        stop();
                        final Timer timer_3 = Util.createTimer();
                        final Runnable run_3 = () -> {
                            start();
                        };
                        Util.sheduleTimerAndRemove(run_3, (long) (delayStartInSeconds * 1000.0 + 0.5), timer_3);
                    }
                };
                Util.sheduleTimerAtFixedRate(run_1, 0, 1000, timer_1);
                Util.sheduleTimerAndRemove(run_2, (long) (delayStopInSeconds * 1000.0 + 0.5), timer_2);
                return;
            } catch (Exception ex) {
            }
        }
        try {
            event.sendMessage(Standard.getMessageEmbed(Color.YELLOW, "%s restarted me!", event.getAuthor().getAsMention()).build());
            restart();
        } catch (Exception ex) {
        }
    }

    @Override
    public final void executed(boolean success, MessageEvent event) {
        System.out.println("[INFO] Command '" + getCommandID() + "' was executed!");
    }

    @Override
    public final EmbedBuilder getHelp(Invoker invoker, EmbedBuilder builder) {
        builder.addField(invoker + " [Delay 1] [Delay 2]", "Restarts the bot immediately or after the first given delay in seconds. The second delay is the time the bot should wait before starting again.", false);
        return builder;
    }

    @Override
    public final PermissionRoleFilter getPermissionRoleFilter() {
        return Standard.STANDARD_PERMISSIONROLEFILTER_OWNER_BOT_COMMANDER;
    }

    @Override
    public final String getCommandID() {
        return getClass().getName();
    }

}
