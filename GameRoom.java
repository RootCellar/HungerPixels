import java.util.*;

import PixelEngine.Game.*;
import PixelEngine.Network.*;
import PixelEngine.Server.*;
import PixelEngine.Util.*;

public class GameRoom extends Room
{

    private enum State {
        PRE_GAME,
        GAME,
        POST_GAME,
        ;
    }

    //Game Data
    State state = State.PRE_GAME;

    short WALL = 15000;
    int wallRemCount = 0;

    short tickCount = 0;

    ArrayList<HungerPlayer> players = new ArrayList<HungerPlayer>();
    
    ArrayList<Projectile> projectiles = new ArrayList<Projectile>();
    
    //ArrayList<Mob> mobs = new ArrayList<Mob>();

    public GameRoom(BasicServer s) {
        super(s);
        name = "Game Room";
        level.setName("Game");

        setState(State.PRE_GAME);
    }

    private void setState(State s) {
        state = s;
        out( "STATE CHANGE: " + s.name() );
    }

    public synchronized void addUser(User u) {
        out("Accepting a user...");
        users.add(u);
        u.setRoom(this);
        u.send("Welcome to " + name + "!");

        HungerPlayer newP = new HungerPlayer(u);

        //Tell the client about the players that exist
        for(HungerPlayer p : players ) {
            Message m = new Message( (short) GameNetMessage.MOB_SPAWN.getId(), (short) p.id);
            u.send(m);

            p.posUpdated = true;
            p.hpUpdated = true;
            p.hungerUpdated = true;
            p.thirstUpdated = true;
        }
        
        //Tell the client about the projectiles that exist
        for(Projectile proj : projectiles) {
            Message m = new Message( (short) MessageTypes.getId("PROJECTILE_SPAWN"), (short) proj.id);
            u.send(m);
            
            proj.posUpdated = true;
        }
        
        newP.x = 0;
        newP.y = 0;

        players.add( newP );
        level.add(newP);
        
        newP.posUpdated = true;
        newP.hpUpdated = true;
        newP.hungerUpdated = true;
        newP.thirstUpdated = true;

        /* There's a better way
        for(User user : users) {
        Message m = new Message( (short) 2, (short) newP.id);
        user.send(m);
        }
         */

        sendAll( new Message( (short) GameNetMessage.MOB_SPAWN.getId(), (short) newP.id ) );

        //Tell the client their id
        u.send( new Message( (short) GameNetMessage.SELF_SET.getId(), (short) newP.id ) );
        
        sendAll("Player " + newP.user.name + " has joined the game");
    }

    public void removeUser(User u) {
        users.remove(u);

        for(HungerPlayer p : players) {
            if(p.user.equals(u) ) {

                for(HungerPlayer o : players) {
                    u.send( new Message( (short) GameNetMessage.MOB_REMOVE.getId(), (short) o.id) );
                }
                
                for(Projectile proj : projectiles) {
                    u.send( new Message( (short) MessageTypes.getId("PROJECTILE_DESPAWN"), (short) proj.id) );
                }

                players.remove(p);
                level.remove(p);

                sendAll("Player " + p.user.name + " has left the game");
                sendAll( new Message( (short) GameNetMessage.MOB_REMOVE.getId(), (short) p.id) );

                return;
            }
        }

    }
    
    public void spawnProjectile(Projectile p) {
        projectiles.add(p);
        level.add(p);
        p.setDamage(30);
        Message toSend = new Message((short)MessageTypes.getId("PROJECTILE_SPAWN"), p.id);
        for(User u : users) u.send(toSend);
    }
    
    public void checkProjectiles() {
        check: for(int i=0; i<projectiles.size(); i++) {
            Projectile p = projectiles.get(i);
            for(Projectile p2 : level.getProjectiles()) {
                if(p.equals(p2)) continue check;
            }
            projectiles.remove(p);
            i--;
            Message toSend = new Message((short)MessageTypes.getId("PROJECTILE_DESPAWN"), p.id);
            for(User u : users) u.send(toSend);
        }
    }
    
    /*
    public void removeDisconnected() {
        for(int i=0; i<users.size(); i++) {
            if(!users.get(i).isConnected()) removeUser(users.get(i));
            i--;
        }
    }
    */

    public void tick() {
        //super.tick();
        removeDisconnected();
        parseAllMessages();
        
        checkProjectiles();

        tickCount++;

        if(state == State.GAME) {
            wallRemCount ++;

            if(wallRemCount > 9 ) { 
                WALL --;
                wallRemCount = 0;
            }

            if(WALL < 500) WALL = 500;
        }
        
        level.tick();

        //for(HungerPlayer p : players) {
        for(int i=0; i<players.size(); i++) {
            HungerPlayer p = players.get(i);
            //level.tick();
            //p.tick();

            if(p.isAlive == false) {
                //removeUser(p.user);
                ( (BasicServer) server ).putIntoLobby(p.user);
                sendAll("Player " + p.user.name + " has been murdered");
                i--;
            }

            if(p.attacking) {
                Projectile proj = new Projectile(p);
                proj.x = p.x;
                proj.y = p.y;
                proj.setByRot(p.rot, 5);
                //proj.setOffset(7);
                spawnProjectile(proj);

                for(HungerPlayer p2 : players) {

                    if( !p.equals(p2) && DIST.getDistance( p.x, p.y, p2.x, p2.y ) < 25 ) {

                        p2.damage(5);

                    }

                }

                p.attacking = false;

            }

            if(p.x > WALL) p.x = WALL;
            if(p.x < -1 * WALL) p.x = (short) ( -1 * WALL );

            if(p.y > WALL) p.y = WALL;
            if(p.y < -1 * WALL) p.y = (short) ( -1 * WALL );
        }

        sendPosToAll();

        if(tickCount > 999) tickCount = 0;

    }

    public void sendPosToAll() {
        /*
        for(Message m : level.getUpdates()) {
            for(User u : users) u.send(m);
        }
        */
        
        ///*
        
        for(Projectile p : level.getProjectiles()) {
            //if(p.posUpdated) {
                Message toSend = new Message((short)MessageTypes.getId("PROJECTILE_MOVE"), p.id);
                toSend.putShort((short)p.x);
                toSend.putShort((short)p.y);
                for(User u : users) u.send(toSend);
                p.posUpdated = false;
            //}
        }
        
        for(HungerPlayer p : players) {

            if(tickCount % 90 == 0) {
                p.posUpdated = true;
                p.hpUpdated = true;
                p.hungerUpdated = true;
                p.thirstUpdated = true;
            }

            for(Message m : p.getUpdates()) {
                for(User u : users) u.send(m);
            }

            /*
            if(p.posUpdated) {
            Message m = new Message( (short) GameNetMessage.MOB_POS.getId(), (short) p.id);
            m.putShort((short)p.x);
            m.putShort((short)p.y);

            for(User u : users) {
            u.send(m);
            }

            p.posUpdated = false;
            }

            if(p.hpUpdated) {
            Message m = new Message( (short) GameNetMessage.MOB_HP.getId(), (short) p.id);
            m.putShort( (short) p.hp);

            for(User u : users) u.send(m);

            p.hpUpdated = false;
            }

            if(p.hungerUpdated) {
            Message m = new Message( (short) GameNetMessage.MOB_HUNGER.getId(), (short) p.id);
            m.putShort( (short) p.hunger);

            for(User u : users) u.send(m);

            p.hungerUpdated = false;
            }

            if(p.thirstUpdated) {
            Message m = new Message( (short) GameNetMessage.MOB_THIRST.getId(), (short) p.id);
            m.putShort( (short) p.hunger);

            for(User u : users) u.send(m);

            p.thirstUpdated = false;
            }
            */

        }
        
        //*/
    }

    public void parseString(ServerMessage message) {
        Message m = message.getMessage();
        User u = message.getFrom();

        String stuff = m.getString();

        if( stuff.equals("/lobby") ) {
            ( (BasicServer) server ).putIntoLobby(u);
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

        if(m.getType() == GameNetMessage.CHAT.getId()) {
            parseString(message);

            /*
            for( User user : users ) {
            user.send(u.getName() + ": " + m.getString());
            }
            out(u.getName() + ": " + m.getString());
             */
        }
        
        if(m.getType() == MessageTypes.getId("PLAYER_ROTATION")) {
            for(HungerPlayer p : players) {
                if(p.user.equals(u)) {
                    p.rot = ( (double)m.readShort()) / 10;
                }
            }
        }

        if(m.getType() == GameNetMessage.KEY_SPACE.getId() ) {

            for(HungerPlayer p : players) {

                if(p.user.equals(u)) {
                    p.attacking = m.getBoolean();
                }

            }

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