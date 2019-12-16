import PixelEngine.Server.*;
import PixelEngine.Network.*;
import PixelEngine.Util.*;

public class BasicServer extends Server
{
    public static void main(String[] args) {
        new BasicServer().start();
    }

    LobbyRoom lobby = new LobbyRoom(this);
    
    GameRoom gameRoom = new GameRoom(this);
    
    public BasicServer() {
        super();
        
        MessageTypes.add( HungerNet.values() );
    }
    
    public void setup() {
        TPS = 30;
        
        out("Setting up registry...");
        
        registry.get("HOSTNAME").setValue("Hunger Pixels");
        
        for( Property p : registry.getProperties() ) {
            out( p.getName() + ": " + p.getValue() );
        }
        
    }
    
    public void putIntoGame(User u) {
        out("Moving user to game...");
        gameRoom.addUser(u);
    }
    
    public void putIntoLobby(User u) {
        out("Moving user to lobby...");
        lobby.addUser(u);
    }

    public void addSocket(SocketHandler s) {
        out("Receiving socket...");
        s.sendString("Hello! Your connection has been received.");
        s.sendString("Putting you into lobby...");

        User newUser = new User(s);

        //lobby.addUser( newUser );
        putIntoLobby( newUser );

        s.start();
    }

    public void tick() {
        
        try{
            lobby.tick();
        }catch(Exception e) {
            e.printStackTrace();
            out("Exception in lobby tick");
        }
        
        try{
            gameRoom.tick();
        }catch(Exception e) {
            e.printStackTrace();
            out("Exception in game tick");
        }
        
    }
}