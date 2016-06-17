package com.esotericsoftware.kryonet;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.serializers.DefaultArraySerializers;
import com.esotericsoftware.kryo.serializers.DefaultSerializers;
import com.esotericsoftware.kryonet.serializers.KryoSerialization;

/**
 * Created by Evan on 6/14/16.
 */
public class KryoTest extends AbstractSerializerTest<KryoSerialization> {

    public KryoTest() {
        super(new KryoSerialization());
    }





    private <T> void registerArray(Kryo kryo, Class<T> tag){
        kryo.register(tag, new DefaultArraySerializers.ObjectArraySerializer(kryo, tag));
    }


    protected void register(){
        final Kryo kryo = serializer.getKryo();
        

        kryo.register(Integer.class);
        kryo.register(Byte.class);
        kryo.register(Short.class);
        kryo.register(Float.class);
        kryo.register(Double.class);
        kryo.register(Boolean.class);


        registerArray(kryo, Integer[].class);
        registerArray(kryo, Byte[].class);
        registerArray(kryo, Short[].class);
        registerArray(kryo, Float[].class);
        registerArray(kryo, Double[].class);
        registerArray(kryo, Boolean[].class);

        kryo.register(boolean[].class, new DefaultArraySerializers.BooleanArraySerializer());
        kryo.register(int[].class, new DefaultArraySerializers.IntArraySerializer());
        kryo.register(byte[].class, new DefaultArraySerializers.ByteArraySerializer());
        kryo.register(float[].class, new DefaultArraySerializers.FloatArraySerializer());
        kryo.register(double[].class, new DefaultArraySerializers.DoubleArraySerializer());
        kryo.register(short[].class, new DefaultArraySerializers.ShortArraySerializer());



        kryo.register(String.class, new DefaultSerializers.StringSerializer());
        kryo.register(String[].class, new DefaultArraySerializers.StringArraySerializer());

        kryo.register(Data.class);
    }

}
