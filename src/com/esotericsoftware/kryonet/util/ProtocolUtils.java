package com.esotericsoftware.kryonet.util;


import org.eclipse.jdt.annotation.Nullable;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.SelectionKey;

import static com.esotericsoftware.minlog.Log.DEBUG;
import static com.esotericsoftware.minlog.Log.debug;

/**
 * Created by Evan on 6/18/16.
 */
public class ProtocolUtils {

    /**Closes the closable argument if its non-null and wakes up the selection key, if its non-null
     *
     * @return true if the closeable was not null, false otherwise.*/
    public static boolean close(@Nullable Closeable toBeClosed, @Nullable SelectionKey toBeWoken) {
        if (toBeClosed != null) {
            try {
                toBeClosed.close();
                if (toBeWoken != null)
                    toBeWoken.selector().wakeup();
            }catch (IOException ex) {
                    if (DEBUG) debug("kryonet", "Unable to close connection: " + toBeClosed, ex);
            }
            return true;
        }
        return false;
    }
}
