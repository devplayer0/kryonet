package com.esotericsoftware.kryonet.util;

import com.esotericsoftware.minlog.Log;

import java.time.Duration;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

/**
 * Created by Evan on 6/16/16.
 */
public class SameThreadListener<T> implements Consumer<T> {
    private T result = null;

    private final Object lock = new Object();

    /**Note: Null is not a valid response, although no explicit checking
     * is performed. May result in non-deterministic behavior.*/
    @Override
    public void accept(T response) {
        result = response;

        synchronized (lock) {
            lock.notifyAll();
        }
    }

    /**Calls wait() until a (Non-null) result is received. */
    public T waitForResult() throws TimeoutException {
        try {
            synchronized (lock) {
                if(result == null)
                    lock.wait(Duration.ofMinutes(20).toMillis());
            }
        } catch (InterruptedException e) {
            Log.error("Thread interrupted while waiting for response", e);
            throw new TimeoutException("Did not received response in time");
        }

        return result;
    }

}
