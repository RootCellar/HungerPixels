package PixelEngine.Game;

import java.util.ArrayList;

import PixelEngine.Screen.*;
import PixelEngine.Network.*;

public class Entity
{
    public double x = 0;
    public double y = 0;
    public boolean posUpdated = false;

    public double size = 0;
    public Level level;
    public Team team = new Team();
    
    public int type = 0;

    public boolean visible = true;
    public static boolean DEBUG_DRAW = false;
    public static short idPoint = -32768;

    public short id = idPoint++;
    
    public Entity() {
        if(idPoint > 32760) idPoint = -32768;
    }
    
    public Message getSpawnMessage() {
        return new Message( (short) MessageTypes.getId("ENTITY_SPAWN"), id);
    }
    
    public Message getDespawnMessage() {
        return new Message( (short) MessageTypes.getId("ENTITY_REMOVE"), id);
    }
    
    public void forceUpdate() {
        posUpdated = true;
    }
    
    public void passUpdate(Message m) {
        
    }
    
    public ArrayList<Message> getUpdates() {
        ArrayList<Message> updates = new ArrayList<Message>();
        
        if(posUpdated) {
            Message m = new Message( (short) GameNetMessage.MOB_POS.getId(), id);
            
            m.putShort((short) x);
            m.putShort((short) y);
            
            updates.add(m);
            posUpdated = false;
        }
        
        return updates;
    }

    public void remove() {
        level.remove(this);
    }

    public void addX(double a) {
        x+=a;
        posUpdated = true;
    }

    public void subX(double a) {
        x-=a;
        posUpdated = true;
    }

    public void addY(double a) {
        y+=a;
        posUpdated = true;
    }

    public void subY(double a) {
        y-=a;
        posUpdated = true;
    }

    public static int nextId() {
        if(idPoint > 32760 ) idPoint = -32768;
        return idPoint++;
    }

    public void tick() {
        
    }

    public void setLevel(Level l) {
        level=l;
    }   

    public void render(PixelCanvas c) {
        if(!visible) return;

        c.drawPixel(x, y, 128, 128, 128);
    }
}