package de.codemakers.bot.supreme.entities;

import java.util.Objects;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;

/**
 * AdvancedMemeber
 *
 * @author Panzer1119
 */
public class AdvancedMember {

    private Member member = null;
    private User user = null;

    public AdvancedMember() {
        this((Member) null);
    }

    public AdvancedMember(Member member) {
        setMember(member);
    }

    public AdvancedMember(User user) {
        setUser(user);
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
        return new AdvancedMember(member);
    }

    public static final AdvancedMember ofGuildAndUser(Guild guild, User user) {
        if (guild == null) {
            return null;
        }
        return new AdvancedMember(guild.getMember(user));
    }

}
