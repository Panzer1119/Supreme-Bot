package de.codemakers.bot.supreme.commands.impl.moderation;

import de.codemakers.bot.supreme.commands.Command;
import de.codemakers.bot.supreme.core.SupremeBot;
import de.codemakers.bot.supreme.entities.MessageEvent;
import de.codemakers.bot.supreme.util.Standard;
import java.awt.Color;
import net.dv8tion.jda.core.EmbedBuilder;

/**
 * AdministrativeCommand
 * 
 * @author Panzer1119
 */
abstract class AdministrativeCommand extends Command {
    
    protected final boolean restart() {
        boolean good = false;
        if (stop()) {
            good = true;
        }
        if (!start()) {
            good = false;
        }
        return good;
    }

    protected final boolean stop() {
        return SupremeBot.stopJDA(false);
    }

    protected final boolean start() {
        return SupremeBot.startJDA();
    }

    protected final EmbedBuilder getRestartingMessage(MessageEvent event, double delayInSeconds, int value) {
        long rest = ((long) (delayInSeconds + 0.5)) - value;
        return Standard.getMessageEmbed(Color.YELLOW, "%s is restarting me in %d second%s!", event.getAuthor().getAsMention(), rest, (rest == 1 ? "" : "s"));
    }

    protected final EmbedBuilder getStoppingMessage(MessageEvent event, double delayInSeconds, int value) {
        long rest = ((long) (delayInSeconds + 0.5)) - value;
        return Standard.getMessageEmbed(Color.YELLOW, "%s is stopping me in %d second%s!", event.getAuthor().getAsMention(), rest, (rest == 1 ? "" : "s"));
    }
    
}
