package de.codemakers.bot.supreme.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Registration
 *
 * @author Panzer1119
 */
public abstract class Registration {

    private static final ArrayList<Registration> OBJECTS = new ArrayList<>();
    protected static boolean isOBJECTSPublic = true;

    public final boolean register() {
        if (!OBJECTS.contains(this)) {
            OBJECTS.add(this);
            return true;
        }
        return false;
    }

    public final boolean unregister() {
        if (!OBJECTS.contains(this)) {
            OBJECTS.remove(this);
            return true;
        }
        return false;
    }

    public static final ArrayList<Registration> getObjects() {
        if (!isOBJECTSPublic) {
            return new ArrayList<>();
        }
        return OBJECTS;
    }
    
    public static final <T> ArrayList<T> getObjects(Class<? extends T> clazz) {
        if (!isOBJECTSPublic) {
            return new ArrayList<>();
        }
        return new ArrayList<>((Collection<? extends T>) Arrays.asList(OBJECTS.toArray()));
    }

}
