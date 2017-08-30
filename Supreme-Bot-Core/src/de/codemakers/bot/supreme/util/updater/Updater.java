package de.codemakers.bot.supreme.util.updater;

import de.codemakers.bot.supreme.util.Standard;
import de.codemakers.bot.supreme.util.Timer;
import de.codemakers.bot.supreme.util.TimerTask;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Updater
 *
 * @author Panzer1119
 */
public class Updater {

    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(5);
    private static final HashMap<Updateable, UpdateTime> UPDATEABLES = new HashMap<>();
    private static final Timer TIMER = new Timer();
    private static final TimerTask TASK = new TimerTask() {
        @Override
        public void run() {
            try {
                updateAll();
            } catch (Exception ex) {
                System.err.println(ex);
            }
        }
    };

    static {
        TIMER.scheduleAtFixedRate(TASK, 0, 100);
    }

    protected static final boolean updateAll() {
        final long timestamp = Standard.getCurrentTime();
        final List<Updateable> toRemove = UPDATEABLES.keySet().stream().filter((updateable) -> {
            try {
                return UPDATEABLES.get(updateable).isRemove();
            } catch (Exception ex) {
                return true;
            }
        }).collect(Collectors.toList());
        toRemove.stream().forEach((updateable) -> {
            UPDATEABLES.remove(updateable);
        });
        toRemove.clear();
        UPDATEABLES.keySet().stream().filter((updateable) -> {
            if (!updateable.wantsUpdate(timestamp)) {
                return false;
            }
            try {
                final UpdateTime updateTime = UPDATEABLES.get(updateable);
                if (updateTime.isUpdating()) {
                    return false;
                }
                return updateTime.needsUpdate(timestamp);
            } catch (Exception ex) {
                System.err.println(ex);
                return false;
            }
        }).forEach((updateable) -> {
            try {
                final UpdateTime updateTime = UPDATEABLES.get(updateable);
                updateTime.setIsUpdating(true);
                EXECUTOR.submit(() -> {
                    try {
                        final long deltaTime = updateable.update(timestamp);
                        updateTime.update(timestamp, deltaTime);
                        if (deltaTime < 0) {
                            updateTime.setRemove(true);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    updateTime.setIsUpdating(false);
                });
            } catch (Exception ex) {
                System.err.println(ex);
            }
        });
        return true;
    }

    public static final boolean addUpdateable(Updateable updateable) {
        if (updateable == null) {
            return false;
        }
        if (!UPDATEABLES.containsKey(updateable)) {
            UPDATEABLES.put(updateable, new UpdateTime());
            return true;
        }
        return false;
    }

    public static final boolean removeUpdateable(Updateable updateable) {
        if (updateable == null) {
            return true;
        }
        if (UPDATEABLES.containsKey(updateable)) {
            UPDATEABLES.remove(updateable);
            return true;
        }
        return false;
    }

    public static final HashMap<Updateable, UpdateTime> getUpdateablesWithTimestamps() {
        return UPDATEABLES;
    }

    public static final Set<Updateable> getUpdateables() {
        return UPDATEABLES.keySet();
    }

    public static final boolean kill(long timeout, TimeUnit unit) {
        try {
            TIMER.cancel();
            EXECUTOR.submit(() -> {
                try {
                    UPDATEABLES.keySet().stream().forEach((updateable) -> {
                        updateable.delete();
                    });
                } catch (Exception ex) {
                    System.err.println(ex);
                }
            });
            EXECUTOR.shutdown();
            EXECUTOR.awaitTermination(timeout, unit);
            EXECUTOR.shutdownNow();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

}
