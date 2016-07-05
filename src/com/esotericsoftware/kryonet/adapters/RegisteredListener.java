package com.esotericsoftware.kryonet.adapters;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.messages.Message;
import com.esotericsoftware.minlog.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Created by Evan on 6/25/16.
 */
public class RegisteredListener<C extends Connection> implements Listener<C> {

    /**This is the default handler for received messages. It logs the name of the class received */
    public static final BiConsumer<Message, Connection> DEFAULT_HANDLE = (m, b) -> Log.warn("Kryonet", "No handler for " + m.getClass() + " has been registered");

    /**This is a nop handler that does nothing when a message is received. No message is logged.*/
    public static final BiConsumer<Message, Connection> NO_OP = (m, b) -> {};


    protected final Map<Class<?>, BiConsumer> map = new HashMap<>();

    protected BiConsumer<? super Message, ? super C> defaultCallback = DEFAULT_HANDLE;

    public RegisteredListener(){

    }



    /**This call back is invoked when no other callback has been registered for a class.
     * This will apply to both Messages and Queries. */
    public void setDefaultHanler(BiConsumer<? super Message, ? super C> defaultHandler){
        defaultCallback = defaultHandler;
    }


    @SuppressWarnings("unchecked")
    protected void invoke(Object msg, C connection){
        map.getOrDefault(msg.getClass(), DEFAULT_HANDLE).accept(msg, connection);
    }



    @Override
    public void onConnected(C connection) {
        Log.debug("kryonet", "Connected with " + connection);
    }

    @Override
    public void onDisconnected(C connection) {
        Log.debug("kryonet", "Disconnected with " + connection);
    }


    @Override
    public void onIdle(C connection) {

    }

    @Override
    public void received(C connection, Object msg){
        invoke(msg, connection);
    }

}
