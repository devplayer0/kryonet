package com.esotericsoftware.kryonet.utils;

import com.esotericsoftware.kryonet.network.messages.QueryToServer;

/**
 * Created by Evan on 6/27/16.
 */
public class YesNoServerQuery extends QueryToServer<Boolean> {
  @Override
  public boolean isReliable() {
    return true;
  }
}
