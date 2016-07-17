package com.esotericsoftware.kryonet.utils;

import com.esotericsoftware.kryonet.network.messages.BidirectionalMessage;

/**
 * Created by Evan on 6/25/16.
 */
public class ObjectMessage implements BidirectionalMessage {
    Object data;

    public ObjectMessage(){}

    public ObjectMessage(Object msg){
        data = msg;
    }
}
