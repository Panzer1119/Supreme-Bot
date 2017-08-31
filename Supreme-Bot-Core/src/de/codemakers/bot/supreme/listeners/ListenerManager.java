package de.codemakers.bot.supreme.listeners;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * ListenerManager
 *
 * @author Panzer1119
 */
public class ListenerManager {

    private static final HashMap<Object, Listener> listeners = new HashMap<>();

    public static final boolean registerListener(Object id, Listener listener) {
        if (id == null || (!listeners.containsKey(id) && listener == null)) {
            return false;
        }
        final boolean unregister = listeners.containsKey(id) && listener == null;
        if (unregister) {
            listeners.remove(id);
            return true;
        } else {
            listeners.put(id, listener);
            return true;
        }
    }

    public static final Listener getListener(Object id) {
        if (listeners.containsKey(id)) {
            return listeners.get(id);
        } else {
            return (data) -> null;
        }
    }

    public static final Object[] fireListeners(Class<?> clazz, Object... data) {
        final ArrayList<Object> output = new ArrayList<>();
        listeners.values().stream().forEach((listener) -> {
            if ((clazz == null) || ((listener.getClass() == clazz) || clazz.isInstance(listener) || listener.getClass().isInstance(clazz))) {
                try {
                    output.add(listener.fired(data));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        return output.toArray();
    }

    public static final Object fireListener(Object id, Object... data) {
        return getListener(id).fired(data);
    }

}
