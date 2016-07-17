package com.esotericsoftware.kryonet.adapters;

import com.esotericsoftware.kryonet.network.messages.Message;

/**
 * Created by Evan on 7/7/16.
 */
public interface ErrorHandler {

    void onError(Message msg, Exception e);
}
