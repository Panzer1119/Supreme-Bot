package de.codemakers.bot.supreme.commands.impl.moderation;

import de.codemakers.bot.supreme.commands.Command;
import de.codemakers.bot.supreme.commands.CommandCategory;
import de.codemakers.bot.supreme.commands.arguments.ArgumentList;
import de.codemakers.bot.supreme.commands.invoking.Invoker;
import de.codemakers.bot.supreme.entities.MessageEvent;
import de.codemakers.bot.supreme.listeners.ModLogger;
import de.codemakers.bot.supreme.permission.PermissionFilter;
import de.codemakers.bot.supreme.settings.Config;
import de.codemakers.bot.supreme.util.Standard;
import java.time.Instant;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.managers.GuildController;

/**
 * VoiceKickCommand
 *
 * @author Panzer1119
 */
public class VoiceKickCommand extends Command {

    @Override
    public void initInvokers() {
        addInvokers(Invoker.createInvoker("voiceKick", this), Invoker.createInvoker("vkick", this), Invoker.createInvoker("vk", this));
    }

    @Override
    public boolean called(Invoker invoker, ArgumentList arguments, MessageEvent event) {
        if (arguments == null || arguments.isEmpty() || event.isPrivate()) {
            return false;
        }
        return arguments.isSize(1, 2);
    }

    @Override
    public void action(Invoker invoker, ArgumentList arguments, MessageEvent event) {
        final Member member = arguments.consumeMemberFirst();
        if (member == null) {
            event.sendMessage(Standard.STANDARD_MESSAGE_DELETING_DELAY * 2, Standard.getNoMessage(event.getAuthor(), "\"%s\" is not a valid member!", arguments.getFirst()).build());
            return;
        }
        if (!member.getVoiceState().inVoiceChannel()) {
            event.sendMessage(Standard.STANDARD_MESSAGE_DELETING_DELAY * 2, Standard.getNoMessage(event.getAuthor(), "%s cannot be kicked, because he is not in a VoiceChannel!", member.getAsMention()).build());
            return;
        }
        final String reason = arguments.consumeFirst();
        final String voiceChannel_ = member.getVoiceState().getChannel().getName();
        final GuildController controller = event.getGuild().getController();
        final VoiceChannel voiceChannel = (VoiceChannel) controller.createVoiceChannel("kick").complete();
        final Instant timestamp = Instant.now();
        controller.moveVoiceMember(member, voiceChannel).queue((t) -> voiceChannel.delete().queue(), (t) -> voiceChannel.delete().queue());
        ModLogger.log(timestamp, event.getGuild(), ModLogger.LOG_TEXT_MOD_VOICE_KICK, "[%1$s] [%2$s] %3$s voice kicked %4$s from #%5$s (Reason: %6$s)", Config.CONFIG.getUserNameForUser(event.getMember().getUser(), event.getGuild(), true), Config.CONFIG.getUserNameForUser(member.getUser(), event.getGuild(), true), voiceChannel_, ((reason == null || reason.isEmpty()) ? "not given" : "\"" + reason + "\""));
    }

    @Override
    public void executed(boolean success, MessageEvent event) {
        System.out.println("[INFO] Command '" + getCommandID() + "' was executed!");
    }

    @Override
    public EmbedBuilder getHelp(Invoker invoker, EmbedBuilder builder) {
        builder.addField(String.format("%s User [Reason]", invoker), "Kicks a user from his current VoiceChannel (closes connection), with an optionally reason.", false);
        return builder;
    }

    @Override
    public PermissionFilter getPermissionFilter() {
        return Standard.STANDARD_PERMISSIONFILTER_GUILD_MODERATOR_BOT_COMMANDER_BOT_ADMIN;
    }

    @Override
    public String getCommandID() {
        return getClass().getName();
    }

    @Override
    public CommandCategory getCommandCategory() {
        return Standard.COMMANDCATEGORY_MODERATION;
    }

}
