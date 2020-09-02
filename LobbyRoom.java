import java.util.*;

import PixelEngine.Game.*;
import PixelEngine.Network.*;
import PixelEngine.Server.*;
import PixelEngine.Util.*;

public class LobbyRoom extends Room
{

    ArrayList<HungerPlayer> players = new ArrayList<HungerPlayer>();

    public LobbyRoom(BasicServer s) {
        super(s);
        name = "Lobby Room";
        level.setName("Lobby");
    }

    public void removeUser(User u) {
        users.remove(u);

        for(HungerPlayer p : players) {

            if(p.user.equals(u) ) {

                for(HungerPlayer o : players) {
                    u.send( new Message( (short) GameNetMessage.MOB_REMOVE.getId(), (short) o.id) );
                }

                players.remove(p);

                sendAll("Player " + p.user.name + " has left the game");
                sendAll( new Message( (short) GameNetMessage.MOB_REMOVE.getId(), (short) p.id) );

                return;
            }
        }

    }

    public synchronized void addUser(User u) {
        out("Accepting a user...");
        users.add(u);
        u.setRoom(this);
        u.send("Welcome to " + name + "!");

        HungerPlayer newP = new HungerPlayer(u);
        
        //Tell the client about the players that exist
        for(HungerPlayer p : players) {
            Message m = new Message( (short) GameNetMessage.MOB_SPAWN.getId(), (short) p.id);
            u.send(m);
        }
        
        newP.x = 0;
        newP.y = 0;

        players.add( newP );

        /* There's a better way
        for(User user : users) {
        Message m = new Message( (short) 2, (short) newP.id);
        user.send(m);
        }
        */

        sendAll( new Message( (short) GameNetMessage.MOB_SPAWN.getId() , (short) newP.id ) );

        //Tell the client their id
        u.send( new Message( (short) GameNetMessage.SELF_SET.getId(), (short) newP.id ) );
    }

    public void tick() {
        super.tick();

        for(HungerPlayer p : players) {
            p.user.checkPing();
            
            p.tick();

            if(p.x > 500) p.x = 500;
            if(p.x < -500) p.x = -500;

            if(p.y > 500) p.y = 500;
            if(p.y < -500) p.y = -500;
        }

        sendPosToAll();
    }

    public void sendPosToAll() {

        for(HungerPlayer p : players) {

            if(!p.posUpdated) continue;

            Message m = new Message( (short) GameNetMessage.MOB_POS.getId(), (short) p.id);
            m.putShort((short)p.x);
            m.putShort((short)p.y);

            for(User u : users) {
                u.send(m);
            }

        }
    }

    public void parseString(ServerMessage message) {
        Message m = message.getMessage();
        User u = message.getFrom();

        String stuff = m.getString();

        if( stuff.equals("/game") ) {
            ( (BasicServer) server ).putIntoGame(u);
        }
        else if(stuff.equals("/ping")) {
            u.send("Ping : " + u.getPing());
        }
        else {
            for( User user : users ) {
                user.send(u.getName() + ": " + m.getString());
            }
            out(u.getName() + ": " + m.getString());
        }
    }

    public void parseMessage(ServerMessage message) {

        Message m = message.getMessage();
        User u = message.getFrom();
        
        //out(m.getType() + "");

        if(m.getType() == GameNetMessage.CHAT.getId()) {
            parseString(message);

            /*
            for( User user : users ) {
            user.send(u.getName() + ": " + m.getString());
            }
            out(u.getName() + ": " + m.getString());
             */
        }

        //if(m.getType() > 0) out("[R]");

        if(m.getType() == GameNetMessage.KEY_W.getId()) {

            for(HungerPlayer p : players) {

                if(p.user.equals(u)) {
                    p.up = m.getBoolean();
                }

            }

        }

        if(m.getType() == GameNetMessage.KEY_S.getId()) {

            for(HungerPlayer p : players) {

                if(p.user.equals(u)) {
                    p.down = m.getBoolean();
                }

            }

        }

        if(m.getType() == GameNetMessage.KEY_A.getId()) {

            for(HungerPlayer p : players) {

                if(p.user.equals(u)) {
                    p.left = m.getBoolean();
                }

            }

        }

        if(m.getType() == GameNetMessage.KEY_D.getId()) {

            for(HungerPlayer p : players) {

                if(p.user.equals(u)) {
                    p.right = m.getBoolean();
                }

            }

        }

    }

}