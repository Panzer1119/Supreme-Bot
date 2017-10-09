package de.codemakers.bot.supreme.util;

import static de.codemakers.bot.supreme.util.Standard.getZoneId;
import java.io.OutputStream;
import java.io.PrintStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * SystemOutputStream
 *
 * @author Panzer1119
 */
public class SystemOutputStream extends PrintStream {

    private final boolean error;

    public SystemOutputStream(OutputStream out, boolean error) {
        super(out);
        this.error = error;
    }

    @Override
    public final void print(char c) {
        print("" + c);
    }

    @Override
    public final void print(long l) {
        print("" + l);
    }

    @Override
    public final void print(double d) {
        print("" + d);
    }

    @Override
    public final void print(float f) {
        print("" + f);
    }

    @Override
    public final void print(boolean b) {
        print("" + b);
    }

    @Override
    public final void print(int i) {
        print("" + i);
    }

    @Override
    public final void print(char[] c) {
        print(new String(c));
    }

    @Override
    public final void print(Object o) {
        if (o != null) {
            print(o.toString());
        } else {
            print("" + null);
        }
    }

    @Override
    public final void print(String g) {
        print(g, Instant.now(), false);
    }

    @Override
    public final void println(char c) {
        println("" + c);
    }

    @Override
    public final void println(long l) {
        println("" + l);
    }

    @Override
    public final void println(double d) {
        println("" + d);
    }

    @Override
    public final void println(float f) {
        println("" + f);
    }

    @Override
    public final void println(boolean b) {
        println("" + b);
    }

    @Override
    public final void println(int i) {
        println("" + i);
    }

    @Override
    public final void println(char[] c) {
        println(new String(c));
    }

    @Override
    public final void println(Object o) {
        if (o != null) {
            println(o.toString());
        } else {
            println("" + null);
        }
    }

    @Override
    public final void println(String g) {
        print(g, Instant.now(), true);
    }

    @Override
    public final void println() {
        println("");
    }

    @Override
    public final PrintStream format(Locale l, String format, Object... args) {
        print(String.format(l, format, args));
        return this;
    }

    @Override
    public final PrintStream format(String format, Object... args) {
        print(String.format(format, args));
        return this;
    }

    @Override
    public final PrintStream printf(Locale l, String format, Object... args) {
        print(String.format(l, format, args));
        return this;
    }

    @Override
    public final PrintStream printf(String format, Object... args) {
        print(String.format(format, args));
        return this;
    }

    private final void print(String g, Instant instant, boolean newLine) {
        final String msg = String.format("[%s]: %s", LocalDateTime.ofInstant(instant, Standard.getZoneId()).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")), g);
        String temp = msg + (newLine ? Standard.NEW_LINE : "");
        super.print(temp);
        if (Standard.CURRENT_LOG_FILE != null) {
            Standard.addToFile(Standard.CURRENT_LOG_FILE, temp, true, false);
        }
        if (error && false) {
            final Exception ex = new Exception();
            for (StackTraceElement e : ex.getStackTrace()) {
                temp = e + Standard.NEW_LINE;
                super.print(temp);
                if (Standard.CURRENT_LOG_FILE != null) {
                    Standard.addToFile(Standard.CURRENT_LOG_FILE, temp, true, false);
                }
            }
        }
    }

}
