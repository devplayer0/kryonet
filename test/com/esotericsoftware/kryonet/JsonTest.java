package com.esotericsoftware.kryonet;

import com.esotericsoftware.kryonet.serializers.JsonSerialization;

/**
 * Created by Evan on 6/14/16.
 */
public class JsonTest extends AbstractSerializerTest <JsonSerialization> {
    public JsonTest() {
        super(new JsonSerialization());
    }

    @Override
    protected void register() {
        System.out.println("Registering classes....");


        serializer.addClassTag("Float", Float.class);
        serializer.addClassTag("Integer", Integer.class);
        serializer.addClassTag("Double", Double.class);
        serializer.addClassTag("Boolean", Boolean.class);
        serializer.addClassTag("Long", Long.class);
        serializer.addClassTag("Byte", Byte.class);
        serializer.addClassTag("Short", Short.class);
        serializer.addClassTag("String", String.class);


        serializer.addClassTag("Floats", Float[].class);
        serializer.addClassTag("Integers", Integer[].class);
        serializer.addClassTag("Doubles", Double[].class);
        serializer.addClassTag("Booleans", Boolean[].class);
        serializer.addClassTag("Longs", Long[].class);
        serializer.addClassTag("Bytes", Byte[].class);
        serializer.addClassTag("Strings", String[].class);
        serializer.addClassTag("Shorts", Short[].class);





        serializer.addClassTag("floats", float[].class);
        serializer.addClassTag("integers", int[].class);
        serializer.addClassTag("doubles", double[].class);
        serializer.addClassTag("booleans", boolean[].class);
        serializer.addClassTag("longs", long[].class);
        serializer.addClassTag("bytes", byte[].class);
        serializer.addClassTag("shorts", short[].class);



        serializer.addClassTag("Data", Data.class);

        /*
	public String string;
		public String[] strings;
		public int[] ints;
		public short[] shorts;
		public float[] floats;
		public byte[] bytes;
		public boolean[] booleans;
		public Integer[] Ints;
		public Short[] Shorts;
		public Float[] Floats;
		public Byte[] Bytes;
		public Boolean[] Booleans;
		public boolean isTCP;
 */
    }
}
