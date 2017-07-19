package de.panzercraft.bot.supreme.util;

/**
 * Util
 *
 * @author Panzer1119
 */
public class Util {
    
    public static <T> int indexOf(T[] array, T toTest) {
        if (array == null || array.length == 0 || toTest == null) {
            return -1;
        }
        for (int i = 0; i < array.length; i++) {
            final T t = array[i];
            if (t == null) {
                continue;
            }
            if (t.equals(toTest) || t == toTest) {
                return i;
            }
        }
        return -1;
    }

    public static <T> boolean contains(T[] array, T toTest) {
        if (array == null || array.length == 0 || toTest == null) {
            return false;
        }
        for (T t : array) {
            if (t == null) {
                continue;
            }
            if (t.equals(toTest) || t == toTest) {
                return true;
            }
        }
        return false;
    }

    public static <T> boolean contains(T[] array, T... toTest) {
        return contains(array, null, toTest);
    }

    public static <T> boolean contains(T[] array, Filter<T> filter, T... toTest) {
        if (array == null || array.length == 0 || toTest == null) {
            return false;
        }
        if (filter == null) {
            filter = Filter.createFilterEquals();
        }
        if (toTest.length == 0) {
            return true;
        }
        for (T t_1 : toTest) {
            boolean found = false;
            for (T t_2 : array) {
                if (filter.filter(t_1, t_2)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        return true;
    }

    public interface Filter<T> {

        public boolean filter(T arrayEntry, T toTestEntry);

        public static <T> Filter<T> createFilterEquals() {
            return (T arrayEntry, T toTestEntry) -> {
                if (arrayEntry == null || toTestEntry == null) {
                    return false;
                }
                return (arrayEntry == toTestEntry || arrayEntry.equals(toTestEntry) || toTestEntry.equals(arrayEntry));
            };
        }

        public static Filter<String> createStringFilterEqualsIgnoreCase() {
            return (String arrayEntry, String toTestEntry) -> {
                if (arrayEntry == null || toTestEntry == null) {
                    return false;
                }
                return (arrayEntry.equalsIgnoreCase(toTestEntry) || toTestEntry.equalsIgnoreCase(arrayEntry));
            };
        }

        public static <T> Filter<T> createFilterAlways() {
            return (T arrayEntry, T toTestEntry) -> {
                return true;
            };
        }

        public static <T> Filter<T> createFilterNever() {
            return (T arrayEntry, T toTestEntry) -> {
                return false;
            };
        }

    }

}
