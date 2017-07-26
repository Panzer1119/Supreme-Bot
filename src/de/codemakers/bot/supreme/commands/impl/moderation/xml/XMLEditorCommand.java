package de.codemakers.bot.supreme.commands.impl.moderation.xml;

import de.codemakers.bot.supreme.commands.Command;
import de.codemakers.bot.supreme.commands.arguments.ArgumentConsumeType;
import de.codemakers.bot.supreme.commands.arguments.ArgumentList;
import de.codemakers.bot.supreme.commands.invoking.Invoker;
import de.codemakers.bot.supreme.entities.AdvancedGuild;
import de.codemakers.bot.supreme.entities.AdvancedMember;
import de.codemakers.bot.supreme.entities.MemberObject;
import de.codemakers.bot.supreme.entities.MessageEvent;
import de.codemakers.bot.supreme.permission.PermissionRoleFilter;
import de.codemakers.bot.supreme.util.Emoji;
import de.codemakers.bot.supreme.util.Standard;
import java.io.File;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.User;
import org.jdom2.Element;

/**
 * XMLEditorCommand
 *
 * @author Panzer1119
 */
public class XMLEditorCommand extends Command { //Argument -start (%s) %s, -stop, -save (%s) (%s) [override],  -up %d, -down %s, -edit %s %s [override], -info (%s)

    @Override
    public void initInvokers() {
        addInvokers(Invoker.createInvoker("XMLEditor", this), Invoker.createInvoker("xml", this));
    }

    @Override
    public boolean called(Invoker invoker, ArgumentList arguments, MessageEvent event) {
        if (arguments == null) {
            return false;
        }
        final boolean isThis = arguments.isConsumed(Standard.ARGUMENT_THIS, ArgumentConsumeType.FIRST_IGNORE_CASE);
        if (isThis && (event == null || event.isPrivate())) {
            return false;
        }
        final boolean override = arguments.isConsumed(Standard.ARGUMENT_OVERRIDE, ArgumentConsumeType.FIRST_IGNORE_CASE);
        final boolean start = arguments.isConsumed(Standard.ARGUMENT_XMLEDITOR_START, ArgumentConsumeType.FIRST_IGNORE_CASE);
        final boolean save = arguments.isConsumed(Standard.ARGUMENT_XMLEDITOR_SAVE, ArgumentConsumeType.FIRST_IGNORE_CASE);
        final boolean stop = arguments.isConsumed(Standard.ARGUMENT_XMLEDITOR_STOP, ArgumentConsumeType.FIRST_IGNORE_CASE);
        final boolean up = arguments.isConsumed(Standard.ARGUMENT_XMLEDITOR_UP, ArgumentConsumeType.FIRST_IGNORE_CASE);
        final boolean down = arguments.isConsumed(Standard.ARGUMENT_XMLEDITOR_DOWN, ArgumentConsumeType.FIRST_IGNORE_CASE);
        final boolean edit = arguments.isConsumed(Standard.ARGUMENT_XMLEDITOR_EDIT, ArgumentConsumeType.FIRST_IGNORE_CASE);
        final boolean info = arguments.isConsumed(Standard.ARGUMENT_XMLEDITOR_INFO, ArgumentConsumeType.FIRST_IGNORE_CASE);
        if (start) {
            if (!isThis) {
                if (!override) {
                    return arguments.isSize(2, 3);
                } else {
                    return arguments.isSize(3, 4);
                }
            } else if (!override) {
                return arguments.isSize(3);
            } else {
                return arguments.isSize(4);
            }
        } else if (save) {
            if (!isThis) {
                return arguments.isSize(1, 2);
            } else {
                return arguments.isSize(3);
            }
        } else if (stop) {
            return arguments.isSize(1);
        } else if (up) {
            return arguments.isSize(1, 2);
        } else if (down) {
            return arguments.isSize(2);
        } else if (edit) {
            if (!override) {
                return arguments.isSize(3);
            } else {
                return arguments.isSize(4);
            }
        } else if (info) {
            return arguments.isSize(2);
        } else {
            return true;
        }
    }

    @Override
    public void action(Invoker invoker, ArgumentList arguments, MessageEvent event) {
        final boolean isThis = arguments.isConsumed(Standard.ARGUMENT_THIS, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
        final boolean override = arguments.isConsumed(Standard.ARGUMENT_OVERRIDE, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
        final boolean start = arguments.isConsumed(Standard.ARGUMENT_XMLEDITOR_START, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
        final boolean save = arguments.isConsumed(Standard.ARGUMENT_XMLEDITOR_SAVE, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
        final boolean stop = arguments.isConsumed(Standard.ARGUMENT_XMLEDITOR_STOP, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
        final boolean up = arguments.isConsumed(Standard.ARGUMENT_XMLEDITOR_UP, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
        final boolean down = arguments.isConsumed(Standard.ARGUMENT_XMLEDITOR_DOWN, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
        final boolean edit = arguments.isConsumed(Standard.ARGUMENT_XMLEDITOR_EDIT, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
        final boolean info = arguments.isConsumed(Standard.ARGUMENT_XMLEDITOR_INFO, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
        if (start) {
            String guild_id = null;
            if (isThis) {
                guild_id = event.getGuild().getId();
            } else if (arguments.isSize(2)) {
                guild_id = arguments.consumeFirst();
            }
            final String fileName = arguments.consumeFirst();
            File file = Standard.getFile(fileName);
            if (file == null) {
                return;
            }
            if (guild_id != null) {
                final AdvancedGuild advancedGuild = Standard.getAdvancedGuild(guild_id);
                if (advancedGuild != null) {
                    file = advancedGuild.getFile(fileName);
                } else {
                    event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s Sorry %s, the guild id \"%s\" doesn't exists!", Emoji.WARNING, event.getAuthor().getAsMention(), guild_id);
                    return;
                }
            }
            if (!file.exists()) { //StandardSecurityManager oder so machen, damit man zB bestimmte files verbieten kann usw.
                event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s Sorry %s, the file you wanted to edit doesn't exists!", Emoji.WARNING, event.getAuthor().getAsMention());
                return;
            } else if (file.isDirectory()) {
                event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s Sorry %s, you can't open a folder as an .xml file!", Emoji.WARNING, event.getAuthor().getAsMention());
                return;
            }
            final MemberObject memberObject = new MemberObject(new AdvancedMember(event.getAuthor()));
            final XMLEditor xmleditor = new XMLEditor(file);
            memberObject.putData(XMLEditor.class.getSimpleName(), xmleditor);
            memberObject.register();
            event.sendMessageFormat("%s you opened successfully the file \"%s\" in the %s.", event.getAuthor().getAsMention(), fileName, XMLEditor.class.getSimpleName()); //TODO Message Auto Delete?
        } else {
            final MemberObject memberObject = getMemberObject(event.getAuthor());
            final XMLEditor xmleditor = getXMLEditor(memberObject);
            if (xmleditor == null) {
                event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s Sorry %s, you have no open %s!", Emoji.WARNING, event.getAuthor().getAsMention(), XMLEditor.class.getSimpleName());
                return;
            }
            if (save) {
                if (isThis || arguments.isSize(2)) {
                    String guild_id = null;
                    if (isThis) {
                        guild_id = event.getGuild().getId();
                    } else {
                        guild_id = arguments.consumeFirst();
                    }
                    final String fileName = arguments.consumeFirst();
                    File file = Standard.getFile(fileName);
                    if (guild_id != null) {
                        final AdvancedGuild advancedGuild = Standard.getAdvancedGuild(guild_id);
                        if (advancedGuild != null) {
                            file = advancedGuild.getFile(fileName);
                        } else {
                            event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s Sorry %s, the guild id \"%s\" doesn't exists!", Emoji.WARNING, event.getAuthor().getAsMention(), guild_id);
                            return;
                        }
                    }
                    if (file == null) {
                        return;
                    }
                    if (file.exists() && file.isDirectory()) {
                        event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s Sorry %s, you can't save a .xml file as a folder!", Emoji.WARNING, event.getAuthor().getAsMention());
                        return;
                    }
                    if (file.exists() && !override) {
                        event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s Sorry %s, the file \"%s\" already exists! Use \"%s\" to override the file!", Emoji.WARNING, event.getAuthor().getAsMention(), fileName, Standard.ARGUMENT_OVERRIDE.getCompleteArgument(0));
                        return;
                    }
                    if (xmleditor.save(file)) {
                        event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s successfully saved .xml file as \"%s\" in guild \"%s\".", event.getAuthor().getAsMention(), fileName, guild_id);
                    } else {
                        event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s Sorry %s, the file couldn't get saved as \"%s\" in guild \"%s\"!", Emoji.WARNING, event.getAuthor().getAsMention(), fileName, guild_id);
                    }
                } else if (arguments.isSize(1)) {
                    final String fileName = arguments.consumeFirst();
                    final File file = Standard.getFile(fileName);
                    if (file == null) {
                        return;
                    }
                    if (file.exists() && file.isDirectory()) {
                        event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s Sorry %s, you can't save a .xml file as a folder!", Emoji.WARNING, event.getAuthor().getAsMention());
                        return;
                    }
                    if (file.exists() && !override) {
                        event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s Sorry %s, the file \"%s\" already exists! Use \"%s\" to override the file!", Emoji.WARNING, event.getAuthor().getAsMention(), fileName, Standard.ARGUMENT_OVERRIDE.getCompleteArgument(0));
                        return;
                    }
                    if (xmleditor.save(file)) {
                        event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s successfully saved .xml file as \"%s\".", event.getAuthor().getAsMention(), fileName);
                    } else {
                        event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s Sorry %s, the file couldn't get saved as \"%s\"!", Emoji.WARNING, event.getAuthor().getAsMention(), fileName);
                    }
                } else if (xmleditor.save()) {
                    event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s successfully saved .xml file.", event.getAuthor().getAsMention());
                } else {
                    event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s Sorry %s, the file couldn't get saved!", Emoji.WARNING, event.getAuthor().getAsMention());
                }
            } else if (stop) {
                memberObject.delete();
                memberObject.unregister();
                event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s closed the file \"%s\".", event.getAuthor().getAsMention(), xmleditor.getFile());
                //FIXME Only show relative part of the file!!!
            } else if (up) {
                
            } else if (down) {
                
            } else if (edit) {
                if (override) {
                    
                } else {
                    
                }
            } else if (info) {
                
            } else {
                final Element element = xmleditor.getLast();
                event.sendMessage(new EmbedBuilder().addField("Current Element", (element != null ? element.getName() : ""), false).addField("Deepness", (xmleditor.getPath().size() - 1) + "", false).build());
            }
        }
    }

    private final MemberObject getMemberObject(User user) {
        if (user == null) {
            return null;
        }
        return MemberObject.getMemberObjectByExactMembers(new AdvancedMember(user));
    }

    private final XMLEditor getXMLEditor(MemberObject memberObject) {
        if (memberObject == null) {
            return null;
        }
        XMLEditor xmleditor = null;
        final Object object = memberObject.getData(XMLEditor.class.getSimpleName());
        if (object != null && (object instanceof XMLEditor)) {
            xmleditor = (XMLEditor) object;
        }
        return xmleditor;
    }

    @Override
    public void executed(boolean success, MessageEvent event) {
        System.out.println("[INFO] Command '" + getCommandID() + "' was executed!");
    }

    @Override
    public EmbedBuilder getHelp(Invoker invoker, EmbedBuilder builder) {
        builder.addField("No Help available yet!", "Im Sorry for you!", false);
        return builder;
    }

    @Override
    public PermissionRoleFilter getPermissionRoleFilter() {
        return Standard.STANDARD_PERMISSIONROLEFILTER_ADMIN_BOT_COMMANDER;
    }

    @Override
    public String getCommandID() {
        return getClass().getName();
    }

}
