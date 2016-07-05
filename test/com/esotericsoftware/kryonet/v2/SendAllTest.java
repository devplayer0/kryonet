package com.esotericsoftware.kryonet.v2;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.ClientConnection;
import com.esotericsoftware.kryonet.ServerConnection;
import com.esotericsoftware.kryonet.adapters.ConnectionAdapter;
import com.esotericsoftware.kryonet.utils.StringMessage;

import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Evan on 6/28/16.
 */
public class SendAllTest extends KryoNetTestCase {

    public void testSendAllTCP() throws TimeoutException {
        Client<ServerConnection> client2 = Client.createKryoClient();

        reg(server.getKryo(), client.getKryo(), StringMessage.class);
        reg(client2.getKryo(), StringMessage.class);



        final StringMessage msg = new StringMessage("Hello, World!");

        AtomicInteger connected = new AtomicInteger(0);
        server.addListener(new ConnectionAdapter<ClientConnection>(){
            @Override
            public void onConnected(ClientConnection con){
                try {
                    if (connected.incrementAndGet() == 2) {
                        server.sendToAllTCP(msg);
                    }
                } catch (Exception e){
                    test.fail();
                }
            }
        });

        AtomicInteger received = new AtomicInteger(0);
        ConnectionAdapter<ServerConnection> listener = new ConnectionAdapter<ServerConnection>(){
            @Override
            public void received(ServerConnection serve, Object object){
                test.assertTrue(object instanceof StringMessage);
                test.assertEquals(((StringMessage)object).msg, msg.msg);
                if(received.incrementAndGet() == 2){
                    test.resume();
                }
            }
        };

        client.addListener(listener);
        client2.addListener(listener);

        start(server, client, client2);

        test.await(3000);
    }


    public void testSendAllUDP() throws TimeoutException {
        Client<ServerConnection> client2 = Client.createKryoClient();

        reg(server.getKryo(), client.getKryo(), StringMessage.class);
        reg(client2.getKryo(), StringMessage.class);



        final StringMessage msg = new StringMessage("Hello, World!");

        AtomicInteger connected = new AtomicInteger(0);
        server.addListener(new ConnectionAdapter<ClientConnection>(){
            @Override
            public void onConnected(ClientConnection con){
                try {
                    if (connected.incrementAndGet() == 2) {
                        server.sendToAllUDP(msg);
                    }
                } catch (Exception e){
                    test.fail();
                }
            }
        });

        AtomicInteger received = new AtomicInteger(0);
        ConnectionAdapter<ServerConnection> listener = new ConnectionAdapter<ServerConnection>(){
            @Override
            public void received(ServerConnection serve, Object object){
                test.assertTrue(object instanceof StringMessage);
                test.assertEquals(((StringMessage)object).msg, msg.msg);
                if(received.incrementAndGet() == 2){
                    test.resume();
                }
            }
        };

        client.addListener(listener);
        client2.addListener(listener);

        start(server, client, client2);

        test.await(3000);
    }







    public void testSendAllExceptTCP() throws TimeoutException {
        Client<ServerConnection> client2 = Client.createKryoClient();

        reg(server.getKryo(), client.getKryo(), StringMessage.class);
        reg(client2.getKryo(), StringMessage.class);



        final StringMessage msg = new StringMessage("Hello, World!");

        AtomicInteger connected = new AtomicInteger(0);
        server.addListener(new ConnectionAdapter<ClientConnection>(){
            @Override
            public void onConnected(ClientConnection con){
                try {
                    if (connected.incrementAndGet() == 2) {
                        server.sendToAllExceptTCP(con.getID(), msg);
                    }
                } catch (Exception e){
                    test.fail();
                }
            }
        });

        AtomicInteger received = new AtomicInteger(0);

        client2.addListener(new ConnectionAdapter<ServerConnection>(){
            @Override
            public void received(ServerConnection serve, Object object){
                test.fail("Client2 was not suppose to receive a message");
            }
        });


        client.addListener(new ConnectionAdapter<ServerConnection>(){
            @Override
            public void received(ServerConnection serve, Object object){
                test.assertTrue(object instanceof StringMessage);
                test.assertEquals(((StringMessage)object).msg, msg.msg);
                if(received.incrementAndGet() == 1){
                    test.resume();
                }
            }
        });



        start(server, client);
        sleep(200);
        start(client2);



        test.await(3000);
    }



    public void testSendAllExceptUDP() throws TimeoutException {
        Client<ServerConnection> client2 = Client.createKryoClient();

        reg(server.getKryo(), client.getKryo(), StringMessage.class);
        reg(client2.getKryo(), StringMessage.class);



        final StringMessage msg = new StringMessage("Hello, World!");

        AtomicInteger connected = new AtomicInteger(0);
        server.addListener(new ConnectionAdapter<ClientConnection>(){
            @Override
            public void onConnected(ClientConnection con){
                try {
                    if (connected.incrementAndGet() == 2) {
                        server.sendToAllExceptUDP(con.getID(), msg);
                    }
                } catch (Exception e){
                    test.fail();
                }
            }
        });

        AtomicInteger received = new AtomicInteger(0);

        client2.addListener(new ConnectionAdapter<ServerConnection>(){
            @Override
            public void received(ServerConnection serve, Object object){
                test.fail("Client2 was not suppose to receive a message");
            }
        });


        client.addListener(new ConnectionAdapter<ServerConnection>(){
            @Override
            public void received(ServerConnection serve, Object object){
                test.assertTrue(object instanceof StringMessage);
                test.assertEquals(((StringMessage)object).msg, msg.msg);
                if(received.incrementAndGet() == 1){
                    test.resume();
                }
            }
        });



        start(server, client);
        sleep(200);
        start(client2);



        test.await(3000);
    }
}
