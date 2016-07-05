package com.esotericsoftware.kryonet.serializers;

import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.io.ByteBufferInputStream;
import com.esotericsoftware.kryo.io.ByteBufferOutputStream;
import com.esotericsoftware.kryonet.util.KryoNetException;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;

/**
 * Created by Evan on 6/12/16.
 */
public class JacksonSerialization implements Serialization {
    private final ObjectMapper mapper;


    private final ByteBufferInputStream byteBufferInputStream = new ByteBufferInputStream();
    private final ByteBufferOutputStream byteBufferOutputStream = new ByteBufferOutputStream();
    private final OutputStreamWriter writer = new OutputStreamWriter(byteBufferOutputStream);

    private final Wrapper wrapper = new Wrapper();


    /**Constructs an serialization with an ObjectMapper that has the following properties:
     PropertyAccessor.FIELD  PUBLIC_ONLY
     FAIL_ON_UNKNOWN_PROPERTIES:    false
    */
     public JacksonSerialization(){
        this(getDefaultMapper());
    }


    public JacksonSerialization(ObjectMapper objectMapper){
        mapper = objectMapper;
    }


    public static ObjectMapper getDefaultMapper(){
        return new ObjectMapper()
                .enableDefaultTyping(ObjectMapper.DefaultTyping.JAVA_LANG_OBJECT, JsonTypeInfo.As.PROPERTY)
                .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.PUBLIC_ONLY)
                .setVisibility(PropertyAccessor.IS_GETTER, JsonAutoDetect.Visibility.NONE)
                .setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true)
                .configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false)
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    }


    @Override
    public void write(ByteBuffer buffer, Object object) {
        byteBufferOutputStream.setByteBuffer(buffer);
        try {
            synchronized (Wrapper.class) {
                wrapper.message = object;
                mapper.writeValue(System.out, wrapper);
                mapper.writeValue(writer, wrapper);
            }
            writer.flush();
        } catch (Exception ex) {
            throw new KryoException("Error writing object: " + object, ex);
        }
    }

    @Override
    public Object read(ByteBuffer buffer) {
        byteBufferInputStream.setByteBuffer(buffer);
        try {
            synchronized (Wrapper.class) {
                return mapper.readValue(byteBufferInputStream, Wrapper.class).message;
            }
        } catch (IOException e) {
            throw new KryoNetException("Error unmarshalling json object", e);
        }
    }

    @Override
    public int getLengthLength() {
        return 4;
    }


    @Override
    public void writeLength (ByteBuffer buffer, int length) {
        buffer.putInt(length);
    }

    @Override
    public int readLength (ByteBuffer buffer) {
        return buffer.getInt();
    }


    public static final class Wrapper {
        public Object message;
    }

}
