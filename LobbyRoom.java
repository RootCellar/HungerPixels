import java.util.*;

//import PixelEngine.Game.*;
import PixelEngine.Network.*;
import PixelEngine.Server.*;
import PixelEngine.Util.*;

public class LobbyRoom extends Room
{
    short idPoint = 0;

    ArrayList<Player> players = new ArrayList<Player>();

    public LobbyRoom(Server s) {
        super(s);
        name = "Lobby";
    }

    public synchronized void addUser(User u) {
        out("Accepting a user...");
        users.add(u);
        u.setRoom(this);
        u.send("Welcome to " + name + "!");

        Player newP = new Player(u);

        for(Player p : players) {
            Message m = new Message( (short) 2, (short) p.id);
            u.send(m);
        }

        players.add( newP );

        /* There's a better way
        for(User user : users) {
            Message m = new Message( (short) 2, (short) newP.id);
            user.send(m);
        }
        */
       
        sendAll( new Message( (short) 2, (short) newP.id ) );
    }

    public void tick() {
        super.tick();

        for(Player p : players) {
            p.tick();

            if(!p.user.isConnected()) {
                players.remove(p);
                sendAll("Player " + p.user.name + " has left the game");
                sendAll( new Message( (short) 4, (short) p.id) );
            }
        }

        sendPosToAll();
    }

    public void sendPosToAll() {

        for(Player p : players) {

            for(User u : users) {
                Message m = new Message( (short) 3, (short) p.id);
                m.putShort(p.x);
                m.putShort(p.y);
                u.send(m);
            }

        }
    }

    public void parseMessage(ServerMessage message) {

        Message m = message.getMessage();
        User u = message.getFrom();

        if(m.getType() == 0) {
            for( User user : users ) {
                user.send(u.getName() + ": " + m.getString());
            }
            out(u.getName() + ": " + m.getString());
        }

        //if(m.getType() > 0) out("[R]");

        if(m.getType() == 1) {

            for(Player p : players) {

                if(p.user.equals(u)) {
                    p.up = m.getBoolean();
                }

            }

        }

        if(m.getType() == 2) {

            for(Player p : players) {

                if(p.user.equals(u)) {
                    p.down = m.getBoolean();
                }

            }

        }

        if(m.getType() == 3) {

            for(Player p : players) {

                if(p.user.equals(u)) {
                    p.left = m.getBoolean();
                }

            }

        }

        if(m.getType() == 4) {

            for(Player p : players) {

                if(p.user.equals(u)) {
                    p.right = m.getBoolean();
                }

            }

        }

    }

}