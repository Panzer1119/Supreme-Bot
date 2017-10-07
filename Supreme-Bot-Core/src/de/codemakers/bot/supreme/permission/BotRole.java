package de.codemakers.bot.supreme.permission;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * BotRole
 *
 * @author Panzer1119
 */
public class BotRole {

    public final List<BotRole> getInherits(List<BotRole> inherits, boolean recursiv) {
        if (!recursiv) {
            return inherits;
        }
        final ArrayList<BotRole> inherits_all = new ArrayList<BotRole>() {
            @Override
            public boolean addAll(Collection<? extends BotRole> c) {
                if (c == null || c.isEmpty()) {
                    return false;
                }
                return super.addAll(c.stream().filter((botRole) -> !contains(botRole)).collect(Collectors.toList()));
            }
        };
        inherits_all.addAll(inherits);
        inherits.stream().map((inherit) -> inherit.getInherits(inherits, recursiv)).forEach((inherits_) -> inherits_all.addAll(inherits_));
        return inherits_all;
    }

}
