package com.esotericsoftware.kryonet.utils;

import com.esotericsoftware.kryonet.network.messages.BidirectionalMessage;

/**
 * Created by Evan on 6/18/16.
 */
public class StringMessage implements BidirectionalMessage {
    public String msg;

    public StringMessage(){
        this("");
    }


    public StringMessage(String s){
        msg = s;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StringMessage that = (StringMessage) o;

        return msg != null ? msg.equals(that.msg) : that.msg == null;

    }

    @Override
    public int hashCode() {
        return msg != null ? msg.hashCode() : 0;
    }
    @Override
    public boolean isReliable() {
      return true;
    }
}
