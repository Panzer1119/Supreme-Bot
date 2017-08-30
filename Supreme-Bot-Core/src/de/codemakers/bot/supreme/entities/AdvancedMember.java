package de.codemakers.bot.supreme.entities;

import de.codemakers.bot.supreme.util.Registration;
import java.util.Objects;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;

/**
 * AdvancedMemeber
 *
 * @author Panzer1119
 */
public class AdvancedMember extends Registration {

    private Member member = null;
    private User user = null;

    public AdvancedMember() {
        this((Member) null);
        register();
        System.out.println("Created AdvancedMember: " + this);
    }

    public AdvancedMember(Member member) {
        setMember(member);
        register();
        System.out.println("Created AdvancedMember: " + this);
    }

    public AdvancedMember(User user) {
        setUser(user);
        register();
        System.out.println("Created AdvancedMember: " + this);
    }

    public final Member getMember() {
        return member;
    }

    public final AdvancedMember setMember(Member member) {
        if (member == null) {
            this.user = null;
        } else {
            this.user = member.getUser();
        }
        this.member = member;
        return this;
    }

    public final User getUser() {
        return user;
    }

    public final AdvancedMember setUser(User user) {
        this.user = user;
        return this;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + Objects.hashCode(this.user);
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
        if (object instanceof AdvancedMember) {
            final AdvancedMember advancedMember = (AdvancedMember) object;
            return Objects.equals(getUser(), advancedMember.getUser());
        } else {
            return false;
        }
    }

    public static final AdvancedMember ofMember(Member member) {
        final AdvancedMember temp = getObjects(AdvancedMember.class).stream().filter((advancedMember) -> {
            return advancedMember.member == member;
        }).findFirst().orElse(null);
        if (temp != null) {
            return temp;
        }
        return new AdvancedMember(member);
    }

    public static final AdvancedMember ofGuildAndUser(Guild guild, User user) {
        if (guild == null) {
            return null;
        }
        final AdvancedMember temp = getObjects(AdvancedMember.class).stream().filter((advancedMember) -> {
            if (advancedMember.getMember() == null) {
                return false;
            }
            return (advancedMember.getMember().getGuild() == guild) && (advancedMember.user == user);
        }).findFirst().orElse(null);
        if (temp != null) {
            return temp;
        }
        return new AdvancedMember(guild.getMember(user));
    }

    public static final AdvancedMember ofUser(User user) {
        final AdvancedMember temp = getObjects(AdvancedMember.class).stream().filter((advancedMember) -> {
            return advancedMember.user == user;
        }).findFirst().orElse(null);
        if (temp != null) {
            return temp;
        }
        return new AdvancedMember(user);
    }

}
