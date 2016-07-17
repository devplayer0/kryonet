package com.esotericsoftware.kryonet.network.messages;

import com.esotericsoftware.kryonet.network.ClientConnection;
import com.esotericsoftware.kryonet.network.Query;

/**
 * Created by Evan on 6/17/16.
 */
public abstract class QueryToServer<T> extends Query<T, ClientConnection> {
}
