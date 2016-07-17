package com.esotericsoftware.kryonet.adapters;

import com.esotericsoftware.kryonet.network.ClientConnection;
import com.esotericsoftware.kryonet.network.messages.MessageToServer;
import com.esotericsoftware.kryonet.network.messages.QueryToServer;
import org.eclipse.jdt.annotation.Nullable;

import java.util.function.BiConsumer;

/**
 * This class provides a default server configuration for RegisteredListener.
 *
 * RegisteredListener takes 3 generic arguments, which gets pretty messy so this class
 * provides an easier way to use and subclass RegisteredListener for the server.
 *
 *
 *
 * Created by Evan on 6/30/16.
 */
public class RegisteredServerListener extends RegisteredListener<ClientConnection> {




    /** Register a handler for a message type. When of message of type clazz is received,
     * the callback is invoked with the message and the connection it came from passed
     * as arguments.
     *
     * A call to this method removes any previously existing handlers for the given class type.
     *
     * Example:
     * <code>
     *     // Add a handler that prints out the content of a StringMessage to stdout.
     *     endpoint.addHandler(StringMessage.class, (msg, conn) -> System.out.println(msg.data));
     *     // Add a handler that always responds 'YES' to a Yes/No prompt
     *     endpoint.addHandler(YesNoQuery.class, (query, conn) -> query.reply(YesNoQuery.YES));
     * </code>
     *
     * @return The previously registered callback for this message type, or null if none existed.
     */
    @SuppressWarnings("unchecked")
    public <K extends MessageToServer> @Nullable BiConsumer<? super K, ? super ClientConnection>
        addHandler(Class<K> clazz, BiConsumer<? super K, ? super ClientConnection> callback){
        return map.put(clazz, callback);
    }


    @SuppressWarnings("unchecked")
    public <Q extends QueryToServer<?>> @Nullable BiConsumer<? super Q, ? super ClientConnection>
        addQueryHandle(Class<Q> clazz, BiConsumer<? super Q, ? super ClientConnection> callback){
        return map.put(clazz, callback);
    }
}
