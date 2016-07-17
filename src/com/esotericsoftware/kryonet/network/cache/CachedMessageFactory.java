package com.esotericsoftware.kryonet.network.cache;

import com.esotericsoftware.kryonet.network.messages.Message;
import com.esotericsoftware.kryonet.serializers.Serialization;

import java.nio.ByteBuffer;

/**
 * Created by Evan on 7/14/16.
 */
public class CachedMessageFactory {

    private final ByteBuffer buffer;

    private final Serialization serializer;


    public CachedMessageFactory(Serialization serializer, int maxBufferSize){
        this.serializer = serializer;
        buffer = ByteBuffer.allocate(maxBufferSize);
    }

    public synchronized <T extends Message> CachedMessage<T> create(T msg){
        buffer.clear();
        serializer.write(buffer, msg);
        ByteBuffer cache = ByteBuffer.allocateDirect(buffer.position()+1);


        buffer.flip();
        cache.put(buffer);
        cache.flip();
        return new CachedMessage<>(cache, msg.isReliable());
    }

}
