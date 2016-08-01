package com.esotericsoftware.kryonet.utils;

import com.esotericsoftware.kryonet.network.messages.QueryToClient;

/**
 * Created by Evan on 6/27/16.
 */
public class YesNoQuery extends QueryToClient<Boolean> {
  @Override
  public boolean isReliable() {
    return true;
  }
}
