package com.esotericsoftware.kryonet.serializers;

import com.esotericsoftware.jsonbeans.JsonException;
import com.esotericsoftware.kryo.io.ByteBufferInputStream;
import com.esotericsoftware.kryo.io.ByteBufferOutputStream;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
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
                .enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.WRAPPER_OBJECT)
                .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.PUBLIC_ONLY)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false)
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }


    @Override
    public void write(ByteBuffer buffer, Object object) {
        byteBufferOutputStream.setByteBuffer(buffer);
        try {
            mapper.writeValue(writer,object);
            writer.flush();
        } catch (Exception ex) {
            throw new JsonException("Error writing object: " + object, ex);
        }
    }

    @Override
    public Object read(ByteBuffer buffer) {
        byteBufferInputStream.setByteBuffer(buffer);
        try {
            return mapper.readValue(byteBufferInputStream, Object.class);
        } catch (IOException e) {
            throw new JsonException("Error unmarshalling json object", e);
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

}
