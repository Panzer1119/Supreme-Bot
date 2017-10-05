package de.codemakers.bot.supreme.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * ThreadUtil
 *
 * @author Panzer1119
 */
public class ThreadUtil {

    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(10);

    static {
        Standard.SHUTDOWNHOOKS.add(() -> {
            try {
                EXECUTOR.shutdown();
                EXECUTOR.awaitTermination(1, java.util.concurrent.TimeUnit.MINUTES);
                EXECUTOR.shutdownNow();
            } catch (Exception ex) {
                System.err.println("ThreadUtil: EXECUTOR shutdown error");
                ex.printStackTrace();
                EXECUTOR.shutdownNow();
            }
        });
    }

    public static final Future<?> execute(Runnable run) {
        if (run == null) {
            return null;
        }
        return EXECUTOR.submit(run);
    }

}
