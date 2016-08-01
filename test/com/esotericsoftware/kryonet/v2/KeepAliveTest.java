package com.esotericsoftware.kryonet.v2;

import com.esotericsoftware.kryonet.network.ClientConnection;
import com.esotericsoftware.kryonet.network.impl.Client;
import com.esotericsoftware.kryonet.network.impl.Server;
import com.esotericsoftware.kryonet.network.messages.FrameworkMessage;
import com.esotericsoftware.minlog.Log;

import java.io.IOException;

/**
 * Created by Evan on 6/30/16.
 */
public class KeepAliveTest extends KryoNetTestCase {


    public void testKeepAliveKryo() throws InterruptedException {
        start(server, client);

        Log.TRACE();
        for (ClientConnection c : server.getConnections()) {
          c.send(FrameworkMessage.keepAlive);
        }
        Thread.sleep(5000);
        client.sendTCP(FrameworkMessage.keepAlive);
        Thread.sleep(1000);
        client.updateReturnTripTime();
    }



    public void testKeepAliveJackson() throws InterruptedException, IOException {
        Server server = new Server();
        Client client = new Client();
        server.start();
        client.start();

        server.bind(tcpPort+1, udpPort+1);
        client.connect(2000, host, tcpPort+1, udpPort +1);

        server.getUpdateThread().setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
          @Override
          public void uncaughtException(Thread t, Throwable e) {
      			test.fail(e);
          }
    		});
        client.getUpdateThread().setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
          @Override
          public void uncaughtException(Thread t, Throwable e) {
      			test.fail(e);
          }
    		});


        Log.TRACE();
        for (ClientConnection c : server.getConnections()) {
          c.send(FrameworkMessage.keepAlive);
        }
        sleep(1000);
        client.sendTCP(FrameworkMessage.keepAlive);
        sleep(1000);
        for (ClientConnection c : server.getConnections()) {
          c.send(FrameworkMessage.keepAlive);
        }
        sleep(1000);
        client.updateReturnTripTime();



        sleep(2000);
        server.close();
        client.close();
    }

}
