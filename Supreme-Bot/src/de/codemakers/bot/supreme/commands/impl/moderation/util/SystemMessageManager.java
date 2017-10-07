package de.codemakers.bot.supreme.commands.impl.moderation.util;

import de.codemakers.bot.supreme.entities.MessageEvent;
import de.codemakers.bot.supreme.entities.MessageManager;
import de.codemakers.bot.supreme.util.Emoji;
import de.codemakers.bot.supreme.util.NetworkUtil;
import de.codemakers.bot.supreme.util.Standard;
import java.awt.Color;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.entities.User;

/**
 * SystemMessageManager
 *
 * @author Panzer1119
 */
public class SystemMessageManager extends MessageManager {

    private final MessageEvent event;

    public SystemMessageManager(MessageEvent event) {
        super(event.sendAndWaitMessage(Standard.toBold("Live System Information")), false);
        this.event = event;
        message.addReaction(Emoji.NO).queue();
    }

    @Override
    public final void onReaction(MessageReaction messageReaction) {
        if (isReacted(messageReaction, Emoji.NO)) {
            deleteThis();
        }
    }

    @Override
    public final void onReaction(MessageReaction messageReaction, User user, boolean removed) {
    }

    @Override
    public final void update() {
        message.editMessage(Standard.getMessageEmbed(Color.YELLOW, Standard.toBold("Live System Information"))
                .addField("IP Address", NetworkUtil.getIPAddress(), false)
                .addField("CPU Cores available", "" + Runtime.getRuntime().availableProcessors(), false)
                .addField("Max Memory", getMemory(Runtime.getRuntime().maxMemory()), false)
                .addField("Total Memory", getMemory(Runtime.getRuntime().totalMemory()), false)
                .addField("Free Memory", getMemory(Runtime.getRuntime().freeMemory()), false).build()).queue();
    }

    static final String getMemory(long memory) {
        return (memory / 1_000_000) + " MB";
    }

    @Override
    public final void delete() {
        message_first.delete().queue();
    }

    public static final SystemMessageManager of(MessageEvent event) {
        return new SystemMessageManager(event);
    }

}
