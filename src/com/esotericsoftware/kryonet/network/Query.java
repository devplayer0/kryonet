package com.esotericsoftware.kryonet.network;

import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryonet.network.messages.Message;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class is used to sendRaw messages that require a response.
 *
 * When an instance is initialized the result field is initially null,
 * when sent to an endpoint, the handler of the Query message should
 * generate a response of type T and call reply.
 *
 * Created by Evan on 6/16/16.
 */
public abstract class Query<T, C extends Connection> implements Message {
    private static final AtomicInteger counter = new AtomicInteger(0);

    @JsonTypeInfo( use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT )
    public T result;
    public final int id;

    private transient C origin;


    protected Query(){
        id = counter.incrementAndGet();
    }


    public void reply(T response){
        if (result == null && response != null) { // reply once
            result = response;
            origin.sendObjectTCP(this);
        }
    }




    void setOrigin(C sender){
        if(origin != null)
            throw new KryoException("Origin is already set");
        origin = sender;
    }


    public C getSender(){
        return origin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Query<?, ?> query = (Query<?, ?>) o;

        return id == query.id;

    }

    @Override
    public int hashCode() {
        return id;
    }


    @Override
    public String toString(){
        return getClass().getSimpleName() + "(" + id + ")";
    }
}
