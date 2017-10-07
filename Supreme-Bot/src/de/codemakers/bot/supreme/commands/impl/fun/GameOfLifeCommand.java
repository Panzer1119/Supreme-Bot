package de.codemakers.bot.supreme.commands.impl.fun;

import de.codemakers.bot.supreme.commands.Command;
import de.codemakers.bot.supreme.commands.CommandCategory;
import de.codemakers.bot.supreme.commands.arguments.ArgumentConsumeType;
import de.codemakers.bot.supreme.commands.arguments.ArgumentList;
import de.codemakers.bot.supreme.commands.invoking.Invoker;
import de.codemakers.bot.supreme.entities.AdvancedMember;
import de.codemakers.bot.supreme.entities.MemberObject;
import de.codemakers.bot.supreme.entities.MessageEvent;
import de.codemakers.bot.supreme.game.GameOfLife;
import de.codemakers.bot.supreme.util.Emoji;
import de.codemakers.bot.supreme.util.Standard;
import java.util.ArrayList;
import net.dv8tion.jda.core.EmbedBuilder;
import de.codemakers.bot.supreme.permission.PermissionFilter;

/**
 * GameOfLifeCommand
 *
 * @author Panzer1119
 */
public class GameOfLifeCommand extends Command {

    @Override
    public void initInvokers() {
        addInvokers(Invoker.createInvoker("GameOfLife", this), Invoker.createInvoker("gol", this));
    }

    @Override
    public boolean called(Invoker invoker, ArgumentList arguments, MessageEvent event) {
        if (arguments == null) {
            return false;
        } //TODO Add command to toggle showing of coordinates
        final boolean create = arguments.isConsumed(Standard.ARGUMENT_CREATE, ArgumentConsumeType.FIRST_IGNORE_CASE);
        final boolean start = arguments.isConsumed(Standard.ARGUMENT_START, ArgumentConsumeType.FIRST_IGNORE_CASE);
        final boolean toggle = arguments.isConsumed(Standard.ARGUMENT_TOGGLE, ArgumentConsumeType.FIRST_IGNORE_CASE);
        final boolean toggle_all = arguments.isConsumed(Standard.ARGUMENT_TOGGLE_ALL, ArgumentConsumeType.FIRST_IGNORE_CASE);
        final boolean go = arguments.isConsumed(Standard.ARGUMENT_GO, ArgumentConsumeType.FIRST_IGNORE_CASE);
        final boolean stop = arguments.isConsumed(Standard.ARGUMENT_STOP, ArgumentConsumeType.FIRST_IGNORE_CASE);
        final boolean end = arguments.isConsumed(Standard.ARGUMENT_END, ArgumentConsumeType.FIRST_IGNORE_CASE);
        if (create) {
            return arguments.isSize(1, 5);
        } else if (start) {
            return arguments.isSize(1); //TODO Simulation Speed?
        } else if (toggle) {
            return arguments.isSize(2, 5);
        } else if (toggle_all) {
            return arguments.isSize(1, -1);
        } else if (go) {
            return arguments.isSize(1, 2);
        } else if (stop) {
            return arguments.isSize(1);
        } else if (end) {
            return arguments.isSize(1);
        } else {
            return false;
        }
    }

    @Override
    public void action(Invoker invoker, ArgumentList arguments, MessageEvent event) {
        final boolean create = arguments.isConsumed(Standard.ARGUMENT_CREATE, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
        final boolean start = arguments.isConsumed(Standard.ARGUMENT_START, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
        final boolean toggle = arguments.isConsumed(Standard.ARGUMENT_TOGGLE, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
        final boolean toggle_all = arguments.isConsumed(Standard.ARGUMENT_TOGGLE_ALL, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
        final boolean go = arguments.isConsumed(Standard.ARGUMENT_GO, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
        final boolean stop = arguments.isConsumed(Standard.ARGUMENT_STOP, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
        final boolean end = arguments.isConsumed(Standard.ARGUMENT_END, ArgumentConsumeType.CONSUME_FIRST_IGNORE_CASE);
        if (create) {
            final MemberObject memberObject = new MemberObject(AdvancedMember.ofMember(event.getMember()));
            final GameOfLife game = new GameOfLife();
            game.startGame(arguments, event);
            memberObject.putData(GameOfLife.class.getSimpleName(), game);
            memberObject.register();
        } else {
            final MemberObject memberObject = getMemberObject(event.getMember());
            final GameOfLife game = getObject(memberObject, GameOfLife.class);
            if (game == null || !(game instanceof GameOfLife)) {
                event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s Sorry %s, you have no open %s!", Emoji.WARNING, event.getAuthor().getAsMention(), GameOfLife.class.getSimpleName());
                return;
            }
            if (start) {
                game.setRunning(true);
            } else if (toggle) {
                game.sendInput(arguments, event);
            } else if (toggle_all) {
                final ArrayList<ArgumentList> lists = new ArrayList<>();
                ArgumentList list_temp = new ArgumentList(event.getGuild());
                while (arguments.hasArguments()) {
                    final String argument_raw = arguments.consumeFirst();
                    if (Standard.STANDARD_ARRAY_SEPARATOR.equals(argument_raw)) {
                        if (!list_temp.isEmpty()) {
                            lists.add(list_temp);
                        }
                        list_temp = new ArgumentList(event.getGuild());
                    } else {
                        list_temp.addArguments(argument_raw);
                    }
                }
                if (!list_temp.isEmpty()) {
                    lists.add(list_temp);
                }
                list_temp = null;
                if (!lists.isEmpty()) {
                    lists.stream().forEach((list) -> {
                        game.sendInput(list, event);
                    });
                }
            } else if (go) {
                int times = 1;
                if (arguments.isSize(1, -1)) {
                    try {
                        times = Integer.parseInt(arguments.consumeFirst());
                    } catch (NumberFormatException ex) {
                        event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s %s the number you enter isn't valid.", Emoji.WARNING, event.getAuthor().getAsMention());
                        return;
                    }
                }
                game.goGeneration(times);
            } else if (stop) {
                game.setRunning(false);
            } else if (end) {
                game.endGame(arguments, event);
                memberObject.delete();
                memberObject.unregister();
                event.sendMessageFormat(Standard.STANDARD_MESSAGE_DELETING_DELAY, "%s closed the %s", event.getAuthor().getAsMention(), GameOfLife.class.getSimpleName());
            }
        }
    }

    @Override
    public void executed(boolean success, MessageEvent event) {
        System.out.println("[INFO] Command '" + getCommandID() + "' was executed!");
    }

    @Override
    public EmbedBuilder getHelp(Invoker invoker, EmbedBuilder builder) {
        return builder;
    }

    @Override
    public PermissionFilter getPermissionFilter() {
        return null;
    }

    @Override
    public String getCommandID() {
        return getClass().getName();
    }

    @Override
    public CommandCategory getCommandCategory() {
        return Standard.COMMANDCATEGORY_FUN;
    }

}
