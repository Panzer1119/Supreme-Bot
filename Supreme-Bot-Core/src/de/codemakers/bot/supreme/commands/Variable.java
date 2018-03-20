package de.codemakers.bot.supreme.commands;

/**
 * Variable
 *
 * @author Panzer1119
 */
public class Variable extends Text {

    private final String name;

    public Variable(String name) {
        super(name);
        this.name = name;
    }

    public final String getName() {
        return name;
    }

}
