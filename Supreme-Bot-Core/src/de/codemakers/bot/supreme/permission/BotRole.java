package de.codemakers.bot.supreme.permission;

import java.util.ArrayList;

/**
 * BotRole
 *
 * @author Panzer1119
 */
public class BotRole {

    private static final ArrayList<BotRole> BOT_ROLES = new ArrayList<>();

    private final String name;
    private final long id;
    private final ArrayList<String> userPermissions = new ArrayList<>(); //TODO Soll das hier in der ArrayList gespeichert sein, oder
    private final ArrayList<String> guildUserPermissions = new ArrayList<>(); //TODO reicht es, wenn die Klassen das intern haben?!?!?

    private BotRole(String name, long id) {
        this.name = name;
        this.id = id;
    }

    @Override
    public final boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (object == this) {
            return true;
        }
        if (object instanceof BotRole) {
            final BotRole botRole = (BotRole) object;
            return id == botRole.id;
        }
        return false;
    }

    public static final BotRole createBotRole(String name, long id) {
        final BotRole botRole = new BotRole(name, id);
        BOT_ROLES.add(botRole);
        return botRole;
    }

}
