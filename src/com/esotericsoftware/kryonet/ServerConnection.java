package com.esotericsoftware.kryonet;

import com.esotericsoftware.kryonet.util.SameThreadListener;
import com.esotericsoftware.kryonet.messages.MessageToServer;
import com.esotericsoftware.kryonet.messages.QueryToServer;
import com.esotericsoftware.minlog.Log;

import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

/**
 * Created by Evan on 6/18/16.
 */
public class ServerConnection extends Connection<MessageToServer> {


    @SuppressWarnings("unchecked")
    <Q> void accept(QueryToServer<Q> response) {
        final Consumer<Q> callback = (Consumer<Q>) queries.get(response);
        if (callback != null)
            callback.accept(response.result);
        else {
            Log.warn("Received query response, but could not find matching request: " + response);
            new Exception().printStackTrace();
        }
    }



    /**Send a query message to this connection and block until a reply is received.
     *
     * @return The reply sent by this connection*/
    public <Q> Q sendAndWait(QueryToServer<Q> query) {
        final SameThreadListener<Q> callback = new SameThreadListener<>();
        sendAsync(query, callback);

        try {
            return callback.waitForResult();
        } catch (TimeoutException e) {
            queries.remove(query);
            throw new RuntimeException("Query was not replied to in time.");
        }
    }

    public <T> void sendAsync(QueryToServer<T> query, Consumer<T> callback) {
        queries.put(query, callback);
        sendObjectTCP(query);
    }


}
