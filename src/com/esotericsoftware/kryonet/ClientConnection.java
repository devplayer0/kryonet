package com.esotericsoftware.kryonet;

import com.esotericsoftware.kryonet.util.SameThreadListener;
import com.esotericsoftware.kryonet.messages.MessageToClient;
import com.esotericsoftware.kryonet.messages.QueryToClient;
import com.esotericsoftware.minlog.Log;

import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

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
     *
     * @return The reply sent by this connection*/
    public <Q> Q sendAndWait(QueryToClient<Q> query) {
        final SameThreadListener<Q> callback = new SameThreadListener<>();
        sendAsync(query, callback);

        try {
            return callback.waitForResult();
        } catch (TimeoutException e) {
            queries.remove(query);
            throw new RuntimeException("Query was not replied to in time.");
        }
    }

    public <T> void sendAsync(QueryToClient<T> query, Consumer<T> callback) {
        queries.put(query, callback);
        sendObjectTCP(query);
    }




}
