package com.esotericsoftware.kryonet.network;

import com.esotericsoftware.kryonet.network.messages.MessageToClient;
import com.esotericsoftware.kryonet.network.messages.QueryToClient;
import com.esotericsoftware.kryonet.util.SameThreadListener;
import com.esotericsoftware.kryonet.util.Consumer;
import com.esotericsoftware.minlog.Log;

import java.util.concurrent.TimeoutException;

/**
 * Created by Evan on 6/17/16.
 */
public class ClientConnection extends Connection<MessageToClient> {



    @SuppressWarnings("unchecked")
    <Q> void accept(QueryToClient<Q> response) {
        final Consumer<Q> callback = (Consumer<Q>) queries.get(response);
        if (callback != null)
            callback.accept(response.result);
        else {
            new Exception().printStackTrace();
            Log.warn("Received query response, but could not find matching request: " + response);
        }
    }




    /**Send a query message to this connection and block until a reply is received.
     * If no reply is received within the timeout window, Optional.empty() is returned.
     *
     * @return The reply sent by this connection*/
    public <Q> Q sendAndWait(QueryToClient<Q> query, long timeout) {
        final SameThreadListener<Q> callback = new SameThreadListener<>();
        sendAsync(query, callback);

        try {
            return callback.waitForResult(timeout);
        } catch (TimeoutException e) {
            queries.remove(query);
            return null;
        }
    }

    public <T> void sendAsync(QueryToClient<T> query, Consumer<T> callback) {
        queries.put(query, callback);
        sendObjectTCP(query);
    }




}
