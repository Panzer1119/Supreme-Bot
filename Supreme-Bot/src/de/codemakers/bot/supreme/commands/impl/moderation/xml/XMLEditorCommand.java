package de.codemakers.bot.supreme.commands.impl.moderation.xml;

import de.codemakers.bot.supreme.commands.Command;
import de.codemakers.bot.supreme.commands.CommandCategory;
import de.codemakers.bot.supreme.commands.arguments.ArgumentConsumeType;
import de.codemakers.bot.supreme.commands.arguments.ArgumentList;
import de.codemakers.bot.supreme.commands.invoking.Invoker;
import de.codemakers.bot.supreme.entities.AdvancedGuild;
import de.codemakers.bot.supreme.entities.AdvancedMember;
import de.codemakers.bot.supreme.entities.MemberObject;
import de.codemakers.bot.supreme.entities.MessageEvent;
import de.codemakers.bot.supreme.permission.PermissionRoleFilter;
import de.codemakers.bot.supreme.util.AdvancedFile;
import de.codemakers.bot.supreme.util.Emoji;
import de.codemakers.bot.supreme.util.Standard;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.dv8tion.jda.core.EmbedBuilder;
import org.jdom2.Element;

/**
 * XMLEditorCommand
 *
 * @author Panzer1119
 */
public class XMLEditorCommand extends Command { //Argument -start (%s) %s, -stop, -save (%s) (%s) [override],  -up %d, -down %s (%d), -edit %s %s [override], -info (%s)

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
        final boolean start = arguments.isConsumed(Standard.ARGUMENT_START, ArgumentConsumeType.FIRST_IGNORE_CASE);
        final boolean save = arguments.isConsumed(Standard.ARGUMENT_SAVE, ArgumentConsumeType.FIRST_IGNORE_CASE);
        final boolean stop = arguments.isConsumed(Standard.ARGUMENT_STOP, ArgumentConsumeType.FIRST_IGNORE_CASE);
        final boolean up = arguments.isConsumed(Standard.ARGUMENT_UP, ArgumentConsumeType.FIRST_IGNORE_CASE);
        final boolean down = arguments.isConsumed(Standard.ARGUMENT_DOWN, ArgumentConsumeType.FIRST_IGNORE_CASE);
        final boolean edit = arguments.isConsumed(Standard.ARGUMENT_EDIT, ArgumentConsumeType.FIRST_IGNORE_CASE);
        final boolean info = arguments.isConsumed(Standard.ARGUMENT_INFO, ArgumentConsumeType.FIRST_IGNORE_CASE);
        final boolean list = arguments.isConsumed(Standard.ARGUMENT_LIST, ArgumentConsumeType.FIRST_IGNORE_CASE);
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
            return arguments.isSize(2, 3);
        } else if (edit) {
            if (!override) {
                return arguments.isSize(3);
            } else {
                return arguments.isSize(4);
            }
        } else if (info) {
            return arguments.isSize(1);
        } else if (list) {
            return arguments.isSize(1);
        } else {
            return true;
        }
    }

    @Override
    public void action(Invoker invoker, ArgumentList arguments, MessageEvent event) {
        final boolean isThis = arguments.isConsumed(Standard.ARGUMENT_THIS, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
        final boolean override = arguments.isConsumed(Standard.ARGUMENT_OVERRIDE, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
        final boolean start = arguments.isConsumed(Standard.ARGUMENT_START, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
        final boolean save = arguments.isConsumed(Standard.ARGUMENT_SAVE, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
        final boolean stop = arguments.isConsumed(Standard.ARGUMENT_STOP, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
        final boolean up = arguments.isConsumed(Standard.ARGUMENT_UP, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
        final boolean down = arguments.isConsumed(Standard.ARGUMENT_DOWN, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
        final boolean edit = arguments.isConsumed(Standard.ARGUMENT_EDIT, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
        final boolean info = arguments.isConsumed(Standard.ARGUMENT_INFO, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
        final boolean list = arguments.isConsumed(Standard.ARGUMENT_LIST, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
        if (start) {
            String guild_id = null;
            if (isThis) {
                guild_id = event.getGuild().getId();
            } else if (arguments.isSize(2)) {
                guild_id = arguments.consumeFirst();
            }
            final String fileName = arguments.consumeFirst();
            AdvancedFile file = Standard.getFile(fileName);
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
            final MemberObject memberObject = new MemberObject(AdvancedMember.ofUser(event.getAuthor()));
            final XMLEditor xmleditor = new XMLEditor(file);
            memberObject.putData(XMLEditor.class.getSimpleName(), xmleditor);
            memberObject.register();
            event.sendMessageFormat("%s you opened successfully the file \"%s\" in the %s.", event.getAuthor().getAsMention(), fileName, XMLEditor.class.getSimpleName()); //TODO Message Auto Delete?
        } else {
            final MemberObject memberObject = getMemberObject(event.getAuthor());
            final XMLEditor xmleditor = getObject(memberObject, XMLEditor.class);
            if (xmleditor == null || !(xmleditor instanceof XMLEditor)) {
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
                    AdvancedFile file = Standard.getFile(fileName);
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
                        event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s Sorry %s, the file \"%s\" already exists! Use \"%s\" to override the file!", Emoji.WARNING, event.getAuthor().getAsMention(), fileName, Standard.ARGUMENT_OVERRIDE.getCompleteArgument(0, -1));
                        return;
                    }
                    if (xmleditor.save(file)) {
                        event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s successfully saved .xml file as \"%s\" in guild \"%s\".", event.getAuthor().getAsMention(), fileName, guild_id);
                    } else {
                        event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s Sorry %s, the file couldn't get saved as \"%s\" in guild \"%s\"!", Emoji.WARNING, event.getAuthor().getAsMention(), fileName, guild_id);
                    }
                } else if (arguments.isSize(1)) {
                    final String fileName = arguments.consumeFirst();
                    final AdvancedFile file = Standard.getFile(fileName);
                    if (file == null) {
                        return;
                    }
                    if (file.exists() && file.isDirectory()) {
                        event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s Sorry %s, you can't save a .xml file as a folder!", Emoji.WARNING, event.getAuthor().getAsMention());
                        return;
                    }
                    if (file.exists() && !override) {
                        event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s Sorry %s, the file \"%s\" already exists! Use \"%s\" to override the file!", Emoji.WARNING, event.getAuthor().getAsMention(), fileName, Standard.ARGUMENT_OVERRIDE.getCompleteArgument(0, -1));
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
                int times = 1;
                if (arguments.isSize(1)) {
                    try {
                        times = Integer.parseInt(arguments.consumeFirst());
                    } catch (Exception ex) {
                    }
                }
                int went = 0;
                for (int i = 0; i < times; i++) {
                    if (xmleditor.goUp() == null) {
                        break;
                    }
                    went++;
                }
                event.sendMessageFormat("%s you went %d time(s) up.", event.getAuthor().getAsMention(), went);
            } else if (down) {
                final String childName = arguments.consumeFirst();
                if (arguments.isSize(1)) {
                    int index = 0;
                    try {
                        index = Integer.parseInt(arguments.consumeFirst());
                    } catch (Exception ex) {
                    }
                    if (xmleditor.goDown(childName, index) != null) {
                        event.sendMessageFormat("%s you went to \"%s\" #%d.", event.getAuthor().getAsMention(), childName, index);
                    } else {
                        event.sendMessageFormat("%s Sorry %s, the child element \"%s\" #%d wasn't found!", Emoji.WARNING, event.getAuthor().getAsMention(), childName, index);
                    }
                } else {
                    final int children = xmleditor.getChildren(childName).size();
                    if (children > 1) {
                        event.sendMessageFormat("%s there were %d children found for \"%s\". Use \"%s %s %s #Index\" to go to the child you want.", event.getAuthor().getAsMention(), children, childName, invoker, Standard.ARGUMENT_DOWN.getCompleteArgument(0, -1), childName);
                    } else if (xmleditor.goDown(childName) != null) {
                        event.sendMessageFormat("%s you went to \"%s\".", event.getAuthor().getAsMention(), childName);
                    } else {
                        event.sendMessageFormat("%s Sorry %s, the child element \"%s\" wasn't found!", Emoji.WARNING, event.getAuthor().getAsMention(), childName);
                    }
                }
            } else if (edit) {
                if (override) {

                } else {

                }
            } else if (info) { //TODO Vielleicht noch einbauen, dass man ueber das parent Element wenn vorhanden anzeigt, wie viel mal das aktuelle Element da ist
                final Element element = xmleditor.getLast();
                if (element != null) {
                    final EmbedBuilder builder = new EmbedBuilder();
                    builder.addField(String.format("Current Element (Deepness %d)", (xmleditor.getPath().size() - 1)), element.getName(), false);
                    element.getAttributes().stream().forEach((attribute) -> {
                        builder.addField(attribute.getName(), attribute.getValue(), false);
                    });
                    event.sendMessage(builder.build());
                }
            } else if (list) {
                final Element element = xmleditor.getLast();
                if (element != null) {
                    final EmbedBuilder builder = new EmbedBuilder();
                    builder.addField(String.format("Current Element (Deepness %d)", (xmleditor.getPath().size() - 1)), element.getName(), false);
                    final List<Element> children = xmleditor.getAllChildren();
                    if (!children.isEmpty()) {
                        final ArrayList<ElementInfo> children_info = new ArrayList<>();
                        for (Element child : children) {
                            final int index = children_info.indexOf(child);
                            if (index == -1) {
                                children_info.add(new ElementInfo(child));
                            } else {
                                children_info.get(index).times++;
                            }
                        }
                        children.clear();
                        String children_string = "";
                        for (ElementInfo elementInfo : children_info) {
                            children_string += String.format("\n%s (%s time(s))", elementInfo.getName(), elementInfo.times);
                        }
                        children_string = children_string.substring("\n".length());
                        children_info.clear();
                        builder.addField("Children", children_string, false);
                    } else {
                        builder.addField("Children", "", false);
                    }
                    event.sendMessage(builder.build());
                }
            } else {
                final Element element = xmleditor.getLast();
                if (element != null) {
                    event.sendMessage(new EmbedBuilder().addField(String.format("Current Element (Deepness %d)", (xmleditor.getPath().size() - 1)), element.getName(), false).build());
                }
            }
        }
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

    private static final class ElementInfo {

        public final Element element;
        public int times = 1;

        public ElementInfo(Element element) {
            this.element = element;
        }

        public final String getName() {
            if (element == null) {
                return null;
            }
            return element.getName();
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 41 * hash + Objects.hashCode(this.element);
            return hash;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object == null) {
                return false;
            }
            if (object instanceof ElementInfo) {
                final ElementInfo elementInfo = (ElementInfo) object;
                return Objects.equals(element, elementInfo.element);
            }
            return false;
        }

    }

    @Override
    public CommandCategory getCommandCategory() {
        return Standard.COMMANDCATEGORY_MODERATION_XML;
    }

}
