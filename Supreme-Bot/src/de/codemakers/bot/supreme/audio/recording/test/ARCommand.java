/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.codemakers.bot.supreme.audio.recording.test;

import de.codemakers.bot.supreme.commands.Command;
import de.codemakers.bot.supreme.commands.CommandCategory;
import de.codemakers.bot.supreme.commands.arguments.ArgumentList;
import de.codemakers.bot.supreme.commands.invoking.Invoker;
import de.codemakers.bot.supreme.entities.MessageEvent;
import de.codemakers.bot.supreme.permission.PermissionFilter;
import de.codemakers.bot.supreme.util.Standard;
import de.codemakers.logger.Logger;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.VoiceChannel;

/**
 * ARCommand
 *
 * @author Panzer1119
 */
public class ARCommand extends Command {

    private boolean temp = false;
    private AudioRecorder audioRecorder = new AudioRecorder();

    @Override
    public void initInvokers() {
        addInvokers(Invoker.createInvoker("ar", this));
    }

    @Override
    public boolean called(Invoker invoker, ArgumentList arguments, MessageEvent event) {
        return true;
    }

    @Override
    public void action(Invoker invoker, ArgumentList arguments, MessageEvent event) {
        temp = !temp;
        if (temp) {
            final VoiceChannel voiceChannel = event.getMember().getVoiceState().getChannel();
            event.getGuild().getAudioManager().openAudioConnection(voiceChannel);
            event.getGuild().getAudioManager().setReceivingHandler(audioRecorder);
            Logger.log("Activated AR");
        } else {
            audioRecorder.recording = false;
            event.getGuild().getAudioManager().setReceivingHandler(null);
            audioRecorder.close();
            event.getGuild().getAudioManager().closeAudioConnection();
            Logger.log("Deactivated AR");
        }
    }

    @Override
    public void executed(boolean success, MessageEvent event) {
    }

    @Override
    public EmbedBuilder getHelp(Invoker invoker, EmbedBuilder builder) {
        return builder;
    }

    @Override
    public PermissionFilter getPermissionFilter() {
        return Standard.STANDARD_PERMISSIONFILTER_BOT_SUPER_OWNER;
    }

    @Override
    public String getCommandID() {
        return getClass().getName();
    }

    @Override
    public CommandCategory getCommandCategory() {
        return Standard.COMMANDCATEGORY_TEST;
    }

}
