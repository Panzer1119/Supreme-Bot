package de.codemakers.bot.supreme.commands.impl.moderation.util;

import de.codemakers.bot.supreme.entities.MessageEvent;
import de.codemakers.bot.supreme.entities.MessageManager;
import de.codemakers.bot.supreme.util.Emoji;
import de.codemakers.bot.supreme.util.NetworkUtil;
import de.codemakers.bot.supreme.util.Standard;
import java.awt.Color;
import java.time.LocalDateTime;
import java.util.Date;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.entities.User;

/**
 * SystemMessageManager
 *
 * @author Panzer1119
 */
public class SystemMessageManager extends MessageManager {

    private final MessageEvent event;
    private final boolean ip;

    public SystemMessageManager(MessageEvent event, boolean ip) {
        super(event.sendAndWaitMessage(Standard.toBold("Live System Information")), false);
        this.event = event;
        this.ip = ip;
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
        message.editMessage(generateMessage(true, ip)).queue();
    }

    static final String getMemory(long memory) {
        return (memory / 1_000_000) + " MB";
    }

    @Override
    public final void delete() {
        message_first.delete().queue();
    }

    public static final SystemMessageManager of(MessageEvent event, boolean ip) {
        return new SystemMessageManager(event, ip);
    }

    public static final MessageEmbed generateMessage(boolean live, boolean ip) {
        final EmbedBuilder builder = Standard.getMessageEmbed(Color.YELLOW, Standard.toBold((live ? "Live " : "") + "System Information"))
                .addField(String.format("%s Version", Standard.STANDARD_NAME), Standard.VERSION, false)
                .addField("Java Version", System.getProperty("java.version"), false);
        if (ip) {
            builder.addField("IP Address", NetworkUtil.getIPAddress(), false);
        }
        builder.addField("CPU Cores available", "" + Runtime.getRuntime().availableProcessors(), false)
                .addField("Max Memory", getMemory(Runtime.getRuntime().maxMemory()), false)
                .addField("Total Memory", getMemory(Runtime.getRuntime().totalMemory()), false)
                .addField("Free Memory", getMemory(Runtime.getRuntime().freeMemory()), false)
                .addField("Local Server Time", Standard.STANDARD_DATE_TIME_FORMATTER.format(LocalDateTime.now()), false)
                .addField("Converted Time", Standard.STANDARD_DATE_TIME_FORMATTER.format(LocalDateTime.now(Standard.getZoneId())), false);
        return builder.build();
    }

}
