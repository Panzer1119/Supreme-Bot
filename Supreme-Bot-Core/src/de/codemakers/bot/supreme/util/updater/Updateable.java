package de.codemakers.bot.supreme.util.updater;

/**
 * Updateable
 *
 * @author Panzer1119
 */
public interface Updateable {

    /**
     * Updates the Object and asks for the milliseconds to wait for the next
     * Update
     *
     * @param timestamp Current time
     * @return Next Update Time (Delta)
     */
    public long update(long timestamp);

    public void delete();

    /**
     * Asks the Object if it wants an update
     *
     * @param timestamp Current time
     * @return <tt>true</tt> if the this Object wants an update
     */
    default boolean wantsUpdate(long timestamp) {
        return true;
    }

}
