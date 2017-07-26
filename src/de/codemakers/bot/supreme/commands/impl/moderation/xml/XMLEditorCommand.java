package de.codemakers.bot.supreme.commands.impl.moderation.xml;

import de.codemakers.bot.supreme.commands.Command;
import de.codemakers.bot.supreme.commands.arguments.ArgumentConsumeType;
import de.codemakers.bot.supreme.commands.arguments.ArgumentList;
import de.codemakers.bot.supreme.commands.invoking.Invoker;
import de.codemakers.bot.supreme.entities.MessageEvent;
import de.codemakers.bot.supreme.permission.PermissionRoleFilter;
import de.codemakers.bot.supreme.util.Standard;
import net.dv8tion.jda.core.EmbedBuilder;

/**
 * XMLEditorCommand
 * 
 * @author Panzer1119
 */
public class XMLEditorCommand extends Command { //Argument -start %s, -stop, -up %d, -down %s, -edit %s %s, -info %s

    @Override
    public void initInvokers() {
        addInvokers(Invoker.createInvoker("XMLEditor", this), Invoker.createInvoker("xml", this));
    }

    @Override
    public boolean called(Invoker invoker, ArgumentList arguments, MessageEvent event) {
        if (arguments == null) {
            return false;
        }
        final boolean start = arguments.isConsumed(Standard.ARGUMENT_XMLEDITOR_START, ArgumentConsumeType.FIRST_IGNORE_CASE);
        final boolean stop = arguments.isConsumed(Standard.ARGUMENT_XMLEDITOR_STOP, ArgumentConsumeType.FIRST_IGNORE_CASE);
        final boolean up = arguments.isConsumed(Standard.ARGUMENT_XMLEDITOR_UP, ArgumentConsumeType.FIRST_IGNORE_CASE);
        final boolean down = arguments.isConsumed(Standard.ARGUMENT_XMLEDITOR_DOWN, ArgumentConsumeType.FIRST_IGNORE_CASE);
        final boolean edit = arguments.isConsumed(Standard.ARGUMENT_XMLEDITOR_EDIT, ArgumentConsumeType.FIRST_IGNORE_CASE);
        final boolean info = arguments.isConsumed(Standard.ARGUMENT_XMLEDITOR_INFO, ArgumentConsumeType.FIRST_IGNORE_CASE);
        if (start) {
            return arguments.isSize(2);
        } else if (stop) {
            return arguments.isSize(1);
        } else if (up) {
            return arguments.isSize(1, 2);
        } else if (down) {
            return arguments.isSize(2);
        } else if (edit) {
            return arguments.isSize(3);
        } else if (info) {
            return arguments.isSize(2);
        } else {
            return false;
        }
    }

    @Override
    public void action(Invoker invoker, ArgumentList arguments, MessageEvent event) {
        final boolean start = arguments.isConsumed(Standard.ARGUMENT_XMLEDITOR_START, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
        final boolean stop = arguments.isConsumed(Standard.ARGUMENT_XMLEDITOR_STOP, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
        final boolean up = arguments.isConsumed(Standard.ARGUMENT_XMLEDITOR_UP, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
        final boolean down = arguments.isConsumed(Standard.ARGUMENT_XMLEDITOR_DOWN, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
        final boolean edit = arguments.isConsumed(Standard.ARGUMENT_XMLEDITOR_EDIT, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
        final boolean info = arguments.isConsumed(Standard.ARGUMENT_XMLEDITOR_INFO, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
        if (start) {
            
        } else if (stop) {
            
        } else if (up) {
            
        } else if (down) {
            
        } else if (edit) {
            
        } else if (info) {
            
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
    
}
