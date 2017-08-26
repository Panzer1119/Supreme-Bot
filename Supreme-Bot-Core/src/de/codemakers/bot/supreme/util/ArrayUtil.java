package de.codemakers.bot.supreme.util;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * ArrayUtil for Arrays, Lists and Maps
 *
 * @author Panzer1119
 */
public class ArrayUtil {

    @SuppressWarnings("unchecked")
    public static <T> T[] copyOf(T[] original, int newLength) {
        return (T[]) copyOf(original, newLength, original.getClass());
    }

    public static <T, U> T[] copyOf(U[] original, int newLength, Class<? extends T[]> newType) {
        @SuppressWarnings("unchecked")
        T[] copy = ((Object) newType == (Object) Object[].class) ? (T[]) new Object[newLength]
                : (T[]) Array.newInstance(newType.getComponentType(), newLength);
        System.arraycopy(original, 0, copy, 0, newLength);
        return copy;
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

    @SuppressWarnings("unchecked")
    public static <T> boolean contains(T[] array, T... toTest) {
        return contains(array, null, toTest);
    }

    @SuppressWarnings("unchecked")
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

    @SuppressWarnings("unchecked")
    @Deprecated
    public static final <T> void sortArrayListAsArray(List<T> list, Comparator<? super T> c) {
        final T[] array = (T[]) list.toArray();
        list.clear();
        Arrays.sort(array, c);
        list.addAll(Arrays.asList(array));
    }

    @SuppressWarnings("unchecked")
    public static final <T> void parallelSortArrayListAsArray(List<T> list, Comparator<? super T> c) {
        final T[] array = (T[]) list.toArray();
        list.clear();
        Arrays.parallelSort(array, c);
        list.addAll(Arrays.asList(array));
    }

}
