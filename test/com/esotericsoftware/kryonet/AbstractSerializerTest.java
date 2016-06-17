/* Copyright (c) 2008, Nathan Sweet
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 * 
 * - Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided with the distribution.
 * - Neither the name of Esoteric Software nor the names of its contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */

package com.esotericsoftware.kryonet;

import com.esotericsoftware.kryonet.adapters.ConnectionAdapter;
import com.esotericsoftware.kryonet.serializers.Serialization;
import com.esotericsoftware.minlog.Log;

import java.io.IOException;

public abstract class AbstractSerializerTest<T extends Serialization> extends KryoNetTestCase {
	String fail;


	protected final T serializer;
	protected AbstractSerializerTest(T serial){
		this.serializer = serial;
		register();
		Log.WARN();
	}


	protected abstract void register();




	public void testSerializer() throws IOException {
		fail = null;

		final Data dataTCP = new Data();
		populateData(dataTCP, true);
		final Data dataUDP = new Data();
		populateData(dataUDP, false);

		final Server server = new Server(16384, 8192, serializer);

		Log.set(Log.LEVEL_DEBUG);
		startEndPoint(server);
		server.bind(tcpPort, udpPort);
		server.addListener(new ConnectionAdapter<Connection>() {
			public void connected (Connection connection) {
				try {
					connection.sendTCP(dataTCP);
					connection.sendUDP(dataUDP); // Note UDP ping pong stops if a UDP packet is lost.
				} catch (Exception e){
					e.printStackTrace();
					setFail("Unable to send data");
				}
			}

			public void received (Connection connection, Object object) {
				try {
					if (object instanceof Data) {
						Data data = (Data) object;
						if (data.isTCP) {
							if (!data.equals(dataTCP)) {
								setFail("TCP data is not equal on server.");
							}
							connection.sendTCP(data);
						} else {
							if (!data.equals(dataUDP)) {
								setFail("UDP data is not equal on server.");
							}
							connection.sendUDP(data);
						}
					}
				} catch (Exception e){
					setFail("Server failed to respond to message");
					e.printStackTrace();
				}
			}
		});

		// ----

		final Client<Connection> client = Client.createClient(16384, 8192, serializer);
		startEndPoint(client);
		client.addListener(new ConnectionAdapter<Connection>() {
			public void received (Connection connection, Object object) {
				try {
					if (object instanceof Data) {
						Data data = (Data) object;
						if (data.isTCP) {
							if (!data.equals(dataTCP)) {
								setFail("TCP data is not equal on client.");
							}
							connection.sendTCP(data);
						} else {
							if (!data.equals(dataUDP)) {
								setFail("UDP data is not equal on client.");
							}
							connection.sendUDP(data);
						}
					}
				} catch (Exception e){
					e.printStackTrace();
					setFail("Exception in client handler");
				}
			}
		});

		client.connect(5000, host, tcpPort, udpPort);

		waitForThreads(7500);

		if (fail != null) fail(fail);
	}

	private void setFail(String msg){
		if(fail != null){
			Log.error(msg);
		}
		fail = msg;
	}



	private void populateData (Data data, boolean isTCP) {
		data.isTCP = isTCP;

		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < 3000; i++)
			buffer.append('a');
		data.string = buffer.toString();

		data.strings = new String[] {"abcdefghijklmnopqrstuvwxyz0123456789", "", null, "!@#$", "�����"};
		data.ints = new int[] {-1234567, 1234567, -1, 0, 1, Integer.MAX_VALUE, Integer.MIN_VALUE};
		data.shorts = new short[] {-12345, 12345, -1, 0, 1, Short.MAX_VALUE, Short.MIN_VALUE};
		data.floats = new float[] {0, 1, -1, 123456, -123456, 0.1f, 0.2f, -0.3f, (float)Math.PI, Float.MAX_VALUE, Float.MIN_VALUE};
		data.bytes = new byte[] {-123, 123, -1, 0, 1, Byte.MAX_VALUE, Byte.MIN_VALUE};
		data.booleans = new boolean[] {true, false};
		data.Ints = new Integer[] {-1234567, 1234567, -1, 0, 1, Integer.MAX_VALUE, Integer.MIN_VALUE};
		data.Shorts = new Short[] {-12345, 12345, -1, 0, 1, Short.MAX_VALUE, Short.MIN_VALUE};
		data.Floats = new Float[] {0f, 1f, -1f, 123456f, -123456f, 0.1f, 0.2f, -0.3f, (float)Math.PI, Float.MAX_VALUE,
			Float.MIN_VALUE};
		data.Bytes = new Byte[] {-123, 123, -1, 0, 1, Byte.MAX_VALUE, Byte.MIN_VALUE};
		data.Booleans = new Boolean[] {true, false};
	}


}
