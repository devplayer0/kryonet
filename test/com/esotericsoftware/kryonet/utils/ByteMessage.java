package com.esotericsoftware.kryonet.utils;

import com.esotericsoftware.kryonet.messages.BidirectionalMessage;

/**
 * Created by Evan on 6/18/16.
 */
public class ByteMessage implements BidirectionalMessage {
    public byte[] bytes;

    public ByteMessage(){
        this(null);
    }

    public ByteMessage(byte[] bs) {
        bytes = bs;
    }
}
