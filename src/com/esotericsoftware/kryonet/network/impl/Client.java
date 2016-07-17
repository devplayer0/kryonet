package com.esotericsoftware.kryonet.network.impl;

import com.esotericsoftware.kryonet.network.AbstractClient;
import com.esotericsoftware.kryonet.network.ServerConnection;
import com.esotericsoftware.kryonet.serializers.KryoSerialization;
import com.esotericsoftware.kryonet.serializers.Serialization;

/**
 * Created by Evan on 7/5/16.
 */
public class Client extends AbstractClient<ServerConnection> {


    /** Creates a AbstractClient with a write buffer size of 8192 and an object buffer size of 2048. */
    public Client() {
        this(DEFAULT_WRITE_BUFFER, DEFAULT_OBJ_BUFFER);
    }

    /** @param writeBufferSize One buffer of this size is allocated. Objects are serialized to the write buffer where the bytes are
     *           queued until they can be written to the TCP socket.
     *           <p>
     *           Normally the socket is writable and the bytes are written immediately. If the socket cannot be written to and
     *           enough serialized objects are queued to overflow the buffer, then the connection will be closed.
     *           <p>
     *           The write buffer should be sized at least as large as the largest object that will be sent, plus some head room to
     *           allow for some serialized objects to be queued in case the buffer is temporarily not writable. The amount of head
     *           room needed is dependent upon the size of objects being sent and how often they are sent.
     * @param objectBufferSize One (using only TCP) or three (using both TCP and UDP) buffers of this size are allocated. These
     *           buffers are used to hold the bytes for a single object graph until it can be sent over the network or
     *           deserialized.
     *           <p>
     *           The object buffers should be sized at least as large as the largest object that will be sent or received. */
    public Client(int writeBufferSize, int objectBufferSize) {
        this(writeBufferSize, objectBufferSize, new KryoSerialization());
    }

    public Client(int writeBufferSize, int objectBufferSize, Serialization serialization) {
        super(createDefaultConnection(), writeBufferSize, objectBufferSize, serialization);
    }


    private static ServerConnection createDefaultConnection(){
        ServerConnection con = new ServerConnection();
        con.setName("Server");
        return  con;
    }

}
