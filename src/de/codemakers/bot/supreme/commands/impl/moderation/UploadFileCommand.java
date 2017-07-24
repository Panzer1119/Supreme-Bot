package de.codemakers.bot.supreme.commands.impl.moderation;

import de.codemakers.bot.supreme.commands.Command;
import de.codemakers.bot.supreme.commands.arguments.ArgumentList;
import de.codemakers.bot.supreme.commands.invoking.Invoker;
import de.codemakers.bot.supreme.entities.MessageEvent;
import de.codemakers.bot.supreme.permission.PermissionRole;
import de.codemakers.bot.supreme.permission.PermissionRoleFilter;
import de.codemakers.bot.supreme.util.Emoji;
import de.codemakers.bot.supreme.util.Standard;
import java.io.File;
import java.util.List;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message.Attachment;

/**
 * UploadFileCommand
 *
 * @author Panzer1119
 */
public class UploadFileCommand extends Command {

    @Override
    public void initInvokers() {
        addInvokers(Invoker.createInvoker("uploadFile", this), Invoker.createInvoker("uFile", this));
    }

    @Override
    public boolean called(Invoker invoker, ArgumentList arguments, MessageEvent event) {
        if (event == null) {
            return false;
        } else {
            if (event.getMessage() == null) {
                return false;
            } else {
                if (event.getMessage().getAttachments() == null || event.getMessage().getAttachments().isEmpty()) {
                    return false;
                } else {
                    if (arguments != null) {
                        return arguments.isSize(-1, event.getMessage().getAttachments().size());
                    } else {
                        return true;
                    }
                }
            }
        }
    }

    @Override
    public void action(Invoker invoker, ArgumentList arguments, MessageEvent event) {
        final List<Attachment> attachments = event.getMessage().getAttachments();
        attachments.stream().filter((attachment) -> attachment != null).forEach((attachment) -> {
            try {
                final String filePath = arguments.consumeFirst();
                final File file = (filePath != null ? new File(filePath) : new File(Standard.STANDARD_UPLOAD_FOLDER.getAbsolutePath() + File.separator + attachment.getFileName()));
                attachment.download(file);
                event.sendMessageFormat("%s uploaded \"%s\"", event.getAuthor().getAsMention(), attachment.getFileName());
            } catch (Exception ex) {
                event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s %s the file \"%s\" was unable to upload (%s)!", Emoji.WARNING, event.getAuthor().getAsMention(), attachment.getFileName(), ex);
            }
        });
    }

    @Override
    public void executed(boolean success, MessageEvent event) {
        System.out.println("[INFO] Command '" + getCommandID() + "' was executed!");
    }

    @Override
    public EmbedBuilder getHelp(Invoker invoker, EmbedBuilder builder) {
        builder.addField(invoker + " [Filepath 1] [Filepath 2] [Filepath 3]...", "Uploads a file to the bot with optionally custom filename.", false);
        return builder;
    }

    @Override
    public PermissionRoleFilter getPermissionRoleFilter() {
        final PermissionRole owner = PermissionRole.getPermissionRoleByName("Admin");
        final PermissionRole bot_commander = PermissionRole.getPermissionRoleByName("Bot_Commander");
        return (role, member) -> {
            if (role.isThisHigherOrEqual(owner) || role.isThisEqual(bot_commander)) {
                return true;
            }
            return Standard.isSuperOwner(member);
        };
    }

    @Override
    public String getCommandID() {
        return getClass().getName();
    }

}
