package com.esotericsoftware.kryonet.v2;

import com.esotericsoftware.kryonet.network.ClientConnection;
import com.esotericsoftware.kryonet.adapters.Listener;
import com.esotericsoftware.kryonet.network.ServerConnection;
import com.esotericsoftware.kryonet.adapters.ConnectionAdapter;
import com.esotericsoftware.kryonet.utils.DataMessage;
import com.esotericsoftware.minlog.Log;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Evan on 6/27/16.
 */
public class MultithreadTest extends KryoNetTestCase {


    public void testThreads() throws TimeoutException {
        final AtomicInteger csent = new AtomicInteger(0);
        final AtomicInteger cgot = new AtomicInteger(0);

        Listener<ServerConnection> clientSide = new ConnectionAdapter.ThreadedListener<>(new ConnectionAdapter<ServerConnection>() {
            @Override
            public void onConnected(ServerConnection con) {
                try {
                    for (int i = 0; i < 200; ++i) {
                        if(ThreadLocalRandom.current().nextBoolean()) {
                            con.send(new PingTest.Ping2());
                        } else {
                            con.send(DataMessage.random());
                        }
                        csent.incrementAndGet();
                    }
                } catch (Exception e){
                    test.fail(e);
                }
            }


            @Override
            public void received(ServerConnection con, Object o) {
                try {
                    if (cgot.incrementAndGet() < 250) {
                        final int rand = ThreadLocalRandom.current().nextInt(10);
                        for (int i = 0; i < rand; ++i) {
                            con.send(new PingTest.Ping2());
                            csent.incrementAndGet();
                        }
                    } else {
                        Thread.sleep(2000);
                        client.close();
                    }
                } catch (Exception e){
                    test.fail(e);
                }
            }
        }, Executors.newFixedThreadPool(100));


        final AtomicInteger ssent = new AtomicInteger(0);
        final AtomicInteger sgot = new AtomicInteger(0);
        Listener<ClientConnection> serverSide = new ConnectionAdapter.ThreadedListener<>(new ConnectionAdapter<ClientConnection>() {
            @Override
            public void onConnected(final ClientConnection con) {
                try {
                    for (int i = 0; i < 250; i++) {
                      new Thread(new Runnable() {
                        @Override
                        public void run() {
                          con.send(new PingTest.Ping2());
                          ssent.incrementAndGet();
                        }
                      }, "Thread"+ i).start();
                    }
                } catch (Exception e){
                    test.fail(e);
                }
            }


            @Override
            public void received(ClientConnection con, Object o) {
                try {
                    sgot.incrementAndGet();
                 } catch (Exception e){
                    test.fail(e);
                }
            }

            @Override
            public void onDisconnected(ClientConnection con) {
                test.resume();
            }
        }, Executors.newFixedThreadPool(100));


        server.addListener(serverSide);
        client.addListener(clientSide);

        super.reg(server.getKryo(), client.getKryo(), PingTest.Ping2.class);
        DataMessage.reg(server.getKryo(), client.getKryo());
        super.start(server, client);

        test.await(5_000);
        assertEquals(sgot.intValue(), csent.intValue());
        assertEquals(ssent.intValue(), cgot.intValue());
        Log.info("Server sent " + ssent.get() + " Messages");
        Log.info("AbstractClient sent " + csent.get() + " Messages");
    }
}
