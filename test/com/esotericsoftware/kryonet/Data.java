package com.esotericsoftware.kryonet;

import java.util.Arrays;

/**
 * Created by Evan on 6/16/16.
 */
public class Data {
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

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(Booleans);
        result = prime * result + Arrays.hashCode(Bytes);
        result = prime * result + Arrays.hashCode(Floats);
        result = prime * result + Arrays.hashCode(Ints);
        result = prime * result + Arrays.hashCode(Shorts);
        result = prime * result + Arrays.hashCode(booleans);
        result = prime * result + Arrays.hashCode(bytes);
        result = prime * result + Arrays.hashCode(floats);
        result = prime * result + Arrays.hashCode(ints);
        result = prime * result + (isTCP ? 1231 : 1237);
        result = prime * result + Arrays.hashCode(shorts);
        result = prime * result + ((string == null) ? 0 : string.hashCode());
        result = prime * result + Arrays.hashCode(strings);
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Data other = (Data) obj;
        if (!Arrays.equals(Booleans, other.Booleans)) return false;
        if (!Arrays.equals(Bytes, other.Bytes)) return false;
        if (!Arrays.equals(Floats, other.Floats)) return false;
        if (!Arrays.equals(Ints, other.Ints)) return false;
        if (!Arrays.equals(Shorts, other.Shorts)) return false;
        if (!Arrays.equals(booleans, other.booleans)) return false;
        if (!Arrays.equals(bytes, other.bytes)) return false;
        if (!Arrays.equals(floats, other.floats)) return false;
        if (!Arrays.equals(ints, other.ints)) return false;
        if (isTCP != other.isTCP) return false;
        if (!Arrays.equals(shorts, other.shorts)) return false;
        if (string == null) {
            if (other.string != null) return false;
        } else if (!string.equals(other.string)) return false;
        if (!Arrays.equals(strings, other.strings)) return false;
        return true;
    }

    public String toString() {
        return "Data";
    }

}
