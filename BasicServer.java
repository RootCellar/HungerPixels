import PixelEngine.Server.*;
import PixelEngine.Network.*;
import PixelEngine.Util.*;

public class BasicServer extends Server
{
    public static void main(String[] args) {
        new BasicServer().start();
    }

    LobbyRoom lobby = new LobbyRoom(this);
    
    GameRoom g = new GameRoom(this);
    
    public void setup() {
        TPS = 30;
        
        out("Setting up registry...");
        
        registry.get("HOSTNAME").setValue("FLRLAVRYLE");
        
        for( Property p : registry.getProperties() ) {
            out( p.getName() + ": " + p.getValue() );
        }
        
    }

    public void addSocket(SocketHandler s) {
        //out("Sending Bytes");
        s.sendString("Hello! Your connection has been received.");
        s.sendString("Putting you into lobby...");

        User newUser = new User(s);

        lobby.addUser( newUser );

        s.start();
        //s.close();
    }

    public void tick() {
        
        try{
            lobby.tick();
        }catch(Exception e) {
            e.printStackTrace();
            out("Exception in lobby tick");
        }
        
    }
}