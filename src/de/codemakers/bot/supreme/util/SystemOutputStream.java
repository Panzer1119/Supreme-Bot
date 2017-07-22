package de.codemakers.bot.supreme.util;

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
    public void print(char c) {
        print("" + c);
    }

    @Override
    public void print(long l) {
        print("" + l);
    }

    @Override
    public void print(double d) {
        print("" + d);
    }

    @Override
    public void print(float f) {
        print("" + f);
    }

    @Override
    public void print(boolean b) {
        print("" + b);
    }

    @Override
    public void print(int i) {
        print("" + i);
    }

    @Override
    public void print(char[] c) {
        print(new String(c));
    }

    @Override
    public void print(Object o) {
        if (o != null) {
            print(o.toString());
        } else {
            print("" + null);
        }
    }

    @Override
    public void print(String g) {
        print(g, Instant.now(), false);
    }

    @Override
    public void println(char c) {
        println("" + c);
    }

    @Override
    public void println(long l) {
        println("" + l);
    }

    @Override
    public void println(double d) {
        println("" + d);
    }

    @Override
    public void println(float f) {
        println("" + f);
    }

    @Override
    public void println(boolean b) {
        println("" + b);
    }

    @Override
    public void println(int i) {
        println("" + i);
    }

    @Override
    public void println(char[] c) {
        println(new String(c));
    }

    @Override
    public void println(Object o) {
        if (o != null) {
            println(o.toString());
        } else {
            println("" + null);
        }
    }

    @Override
    public void println(String g) {
        print(g, Instant.now(), true);
    }

    @Override
    public void println() {
        println("");
    }

    @Override
    public PrintStream format(Locale l, String format, Object... args) {
        print(String.format(l, format, args));
        return this;
    }

    @Override
    public PrintStream format(String format, Object... args) {
        print(String.format(format, args));
        return this;
    }

    @Override
    public PrintStream printf(Locale l, String format, Object... args) {
        print(String.format(l, format, args));
        return this;
    }

    @Override
    public PrintStream printf(String format, Object... args) {
        print(String.format(format, args));
        return this;
    }

    private void print(String g, Instant instant, boolean newLine) {
        final String msg = String.format("[%s]: %s", LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")), g);
        super.print(msg + (newLine ? "\n" : ""));
        if (error && false) {
            final Exception ex = new Exception();
            for (StackTraceElement e : ex.getStackTrace()) {
                super.print(e + "\n");
            }
        }
    }
    
}
