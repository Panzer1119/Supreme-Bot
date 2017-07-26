package de.codemakers.bot.supreme.entities;

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

}
