package de.codemakers.bot.supreme.commands.impl;

import de.codemakers.bot.supreme.commands.Command;
import de.codemakers.bot.supreme.commands.CommandCategory;
import de.codemakers.bot.supreme.commands.CommandParser;
import de.codemakers.bot.supreme.commands.CommandType;
import de.codemakers.bot.supreme.commands.arguments.ArgumentList;
import de.codemakers.bot.supreme.commands.invoking.Invoker;
import de.codemakers.bot.supreme.entities.AdvancedEmote;
import de.codemakers.bot.supreme.entities.MessageEvent;
import de.codemakers.bot.supreme.listeners.ReactionListener;
import de.codemakers.bot.supreme.permission.PermissionFilter;
import de.codemakers.bot.supreme.permission.ReactionPermissionFilter;
import de.codemakers.bot.supreme.util.Standard;
import de.codemakers.bot.supreme.util.TimeUnit;
import de.codemakers.math.complex.ComplexDouble;
import de.codemakers.math.expression.ComplexDoubleExpression;
import de.codemakers.math.expression.ComplexDoubleExpressionBuilder;
import de.codemakers.math.expression.ValidationResult;
import java.util.Map;
import java.util.stream.Collectors;
import net.dv8tion.jda.core.EmbedBuilder;

/**
 * SolveCommand
 *
 * @author Panzer1119
 */
public class SolveCommand extends Command {

    static {
        //ReactionListener.registerListener(AdvancedEmote.parse("1234"), (reaction, emote, guild, user) -> reaction.getChannel().getMessageById(reaction.getMessageIdLong()).queue((message) -> reaction.getChannel().sendMessage(message).queue()), false); //TESTING ONLY
    }

    @Override
    public void initInvokers() {
        addInvokers(Invoker.createInvoker("solve", this));
    }

    @Override
    public boolean called(Invoker invoker, ArgumentList arguments, MessageEvent event) {
        return arguments != null && !arguments.isEmpty();
    }

    @Override
    public void action(Invoker invoker, ArgumentList arguments, MessageEvent event) {
        final String expression_string = arguments.consumeFirst();
        try {
            final ComplexDoubleExpressionBuilder builder = new ComplexDoubleExpressionBuilder(expression_string);
            final Map<String, ComplexDouble> variables = arguments.getContentArguments().stream().filter((text) -> text.contains("=")).map((text) -> text.replaceAll("\\s", "")).collect(Collectors.toMap((text) -> text.split("=")[0], (text) -> ComplexDouble.ofString(text.split("=")[1])));
            builder.addVariables(variables.keySet());
            final ComplexDoubleExpression expression = builder.build();
            expression.setVariables(variables);
            final ValidationResult validationResult = expression.validate();
            if (!validationResult.isValid()) {
                event.getMessageChannel().sendMessage(Standard.getNoMessage(event.getAuthor(), "the expression is invalid:%s%s", Standard.NEW_LINE_DISCORD + Standard.NEW_LINE_DISCORD, validationResult.toString()).build()).queue((message) -> ReactionListener.deleteMessageWithReaction(message, "x", 5, TimeUnit.MINUTES, true, ReactionPermissionFilter.createUserFilter(event.getAuthor())));
                return;
            }
            final ComplexDouble result = expression.evalute();
            event.getMessageChannel().sendMessageFormat("%s%s = %s", variables.isEmpty() ? "" : String.format("%s%s", variables.entrySet().stream().map((variable) -> variable.getKey() + " = " + variable.getValue()).collect(Collectors.joining(", ")), Standard.NEW_LINE_DISCORD), expression_string, result).queue((message) -> ReactionListener.deleteMessageWithReaction(message, "x", 5, TimeUnit.MINUTES, true, ReactionPermissionFilter.createUserFilter(event.getAuthor())));
        } catch (Exception ex) {
            event.sendMessage(5 * Standard.STANDARD_MESSAGE_DELETING_DELAY, Standard.getNoMessage(event.getAuthor(), "there is an error in your expression: %s", ex.getMessage()).build());
        }
    }

    @Override
    public void executed(boolean success, MessageEvent event) {
        System.out.println("[INFO] Command '" + getCommandID() + "' was executed!");
    }

    @Override
    public EmbedBuilder getHelp(Invoker invoker, EmbedBuilder builder) {
        builder.addField(String.format("%s <Expression> [<variable1=value1> <variable2=value2>...]", invoker), "Solves an infix expression.", false);
        return builder;
    }

    @Override
    public PermissionFilter getPermissionFilter() {
        return Standard.STANDARD_PERMISSIONFILTER_BOTH_EVERYONE;
    }

    @Override
    public String getCommandID() {
        return getClass().getName();
    }

    @Override
    public CommandCategory getCommandCategory() {
        return Standard.COMMANDCATEGORY_NORMAL;
    }

}
