package com.esotericsoftware.kryonet.util;

import com.esotericsoftware.kryonet.util.Consumer;
import com.esotericsoftware.minlog.Log;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

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
    public T waitForResult(Duration timeout) throws TimeoutException {
        try {
            synchronized (lock) {
                if(result == null)
                    lock.wait(timeout.toMillis());
            }
        } catch (InterruptedException e) {
            Log.error("Thread interrupted while waiting for response", e);
            throw new TimeoutException("Did not received response in time");
        }

        return result;
    }

}
