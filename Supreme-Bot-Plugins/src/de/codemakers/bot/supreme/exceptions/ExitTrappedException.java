package de.codemakers.bot.supreme.exceptions;

/**
 * ExitTrappedException
 * 
 * @author Panzer1119
 */
public class ExitTrappedException extends RuntimeException {

    public ExitTrappedException() {
    }

    public ExitTrappedException(String message) {
        super(message);
    }

    public ExitTrappedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExitTrappedException(Throwable cause) {
        super(cause);
    }

    public ExitTrappedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
}
