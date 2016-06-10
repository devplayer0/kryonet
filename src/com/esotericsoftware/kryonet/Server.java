package com.esotericsoftware.kryonet;

/**
 * Created by Evan on 6/9/16.
 */
public class Server extends AbstractServer<Connection> {


    public Server() {
        super(Connection.class);
    }


    public Server(int writeBufferSize, int objectBufferSize) {
        super(Connection.class, writeBufferSize, objectBufferSize);
    }


    public Server(int writeBufferSize, int objectBufferSize, Serialization format) {
        super(Connection.class, writeBufferSize, objectBufferSize, format);
    }


    @Override
    protected Connection newConnection() {
        return new Connection();
    }
}
