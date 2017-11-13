package de.codemakers.bot.supreme.util;

/**
 * Returner
 *
 * @author Panzer1119
 */
public class Returner<T> {

    private T value;

    public Returner(T value) {
        this.value = value;
    }

    public final T getValue() {
        return value;
    }

    public final Returner<T> setValue(T value) {
        this.value = value;
        return this;
    }

    public final T or(T other) {
        return (value != null) ? value : other;
    }

    public static final Returner<Integer> of(Integer value) {
        return new Returner<>(value);
    }

    public static final Returner<Float> of(Float value) {
        return new Returner<>(value);
    }

    public static final Returner<Boolean> of(Boolean value) {
        return new Returner<>(value);
    }

    public static final Returner<Object> of(Object value) {
        return new Returner<>(value);
    }

    public static final Returner<String> of(String value) {
        return new Returner<>(value);
    }

    public static final Returner<Short> of(Short value) {
        return new Returner<>(value);
    }

    public static final Returner<Long> of(Long value) {
        return new Returner<>(value);
    }

    public static final Returner<Character> of(Character value) {
        return new Returner<>(value);
    }

    public static final Returner<Byte> of(Byte value) {
        return new Returner<>(value);
    }

    public static final Returner<Double> of(Double value) {
        return new Returner<>(value);
    }

}
