This project is based on Nathan Sweet's [KryoNet](https://github.com/EsotericSoftware/kryonet)  

This project is not backwards-compatible with kryonet, but the following guide expects you to be familiar with it. 


In comparison to KryoNet, this project:  
- Uses a more efficient sendToAll(msg) where msg is serialized only once.
- Uses Jackson for json serialization rather than jsonbeans.
- Adds support for 'queries' -- Messages that expect a reply that wil be handled sychnrounously or asynchronously
- Requires all message types to implement MessageToServer or MessageToClient
- Supports registering callbacks for message types. O(1) dispatch without instanceof chains.
- Uses ConcurrentUnit for unit tests which catch many failures that are ignored in KryoNet
- Removes support for RMI


Examples:

- [Creating a Server and Client](#creating-a-server-and-client)
- [Defining a message Type](#defining-a-message-type)
- [Using Queries](#queries)






## Creating a Server and Client
Getting started is very similar to KryoNet, expect the client has a type parameter and is usually creating with
a static factory method.
```java
	Server server = new Server(); // Just like kryonet
	server.start();
	server.bind(tcpPort, udpPort);
	
	Client<ServerConnection> client = Client.createKryoClient();
	client.start();
	client.connect(timeOut, "localhost", tcpPort, udpPort);
```




## Defining a message type;
- Messages should implement either MessageToServer, MessageToClient, or both.
- All messages have an isReliable method that indicates whether they should be send over TCP or UDP by default. The intent is that you first design your game using TCP for everything, and then optimize later by selecting messages types that can be send over UDP by just overriding this method.

```java
	/** This message indicates that the client has requested their player to move once in a particular direction.
	* This message is send over TCP by default when using server.send(MessageToSever).
	* If UDP is desired in specific instances, you can still use sever.sendUDP(MessageToServer)*/
    public class MovementMessage implements MessageToSever {
    	
    	public Direction dir;
    	
    	MovementMessage() {
    		// Used by Kryo deserializer
    	}
    	
    	public MovementMessage(Direction d){
    		dir = d;
    	}
    }
```


To define a message type that defaults to UDP:

```java
	/** This message updates the client's knowledge of the player's position. Since this message is sent
	* frequently and the info is quickly outdated, we will send this over UDP by default.
	* the method player.getConnection().sendTCP(MessageToClient) will override this behavior though.
	*/
    public class PositionUpdateMessage implements MessageToClient {
    	
    	public int x, y;
    	
    	PositionUpdateMessage() {
    		// Used by Kryo deserializer
    	}
    	
    	public PositionUpdateMessage(int xx , int yy){
    		x = xx;
    		y = yy;
    	}
    	
    	@Override
    	public boolean isReliable(){
    		return false; // False messages of this type should be sent over UDP
    	}
    }
```






## Queries
Let's say that you're developing a turn-based strategy game.
In your game client, you probably have code that involves logging into your game server, since every request will require a response, it may be appropriate to create a LoginQuery class that extends QueryToServer<T>.

Doing so produces code that is really easy to reason about. Additionally, there is no need for a dependency between your packet listener and your login logic
```java
    public void attemptLogin(String username, String password) {
	    LoginStatus response = server.sendAndWait(new LoginQuery(username, password)); // This call blocks until server responds.
	    if(response == LoginStatus.SUCCESS) {
	    	loadGame();
	    } else {
	    	showDialog("Username/Password combination is incorrect.")
	    }
    }
```


Queries can also be handled asynchronously with callbacks. Let's say our player is in the middle of a battle and the server needs to indicate to the client that it's time to select an option. 
```java
    player.sendAsync(new RequestSelection(), new Consumer<Selection>(){
    	@Override
    	public void accept(Selection reply){
    	   // Handle selection. This code will run in another thread. 
    	}
    }); 
    
```



