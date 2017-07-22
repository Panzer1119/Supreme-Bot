package de.codemakers.bot.supreme.permission;

import net.dv8tion.jda.core.entities.Member;

/**
 * PermissionRoleFilter
 * 
 * @author Panzer1119
 */
public interface PermissionRoleFilter {
    
    public boolean isPermissionGranted(PermissionRole role, Member member);
    
}
