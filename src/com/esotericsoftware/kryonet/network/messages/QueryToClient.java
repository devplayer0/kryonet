package com.esotericsoftware.kryonet.network.messages;

import com.esotericsoftware.kryonet.network.Query;
import com.esotericsoftware.kryonet.network.ServerConnection;

/**
 * Created by Evan on 6/17/16.
 */
public abstract class QueryToClient<T> extends Query<T, ServerConnection> {
}
