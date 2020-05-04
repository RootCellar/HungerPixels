package PixelEngine.Game;

import java.util.ArrayList;

import PixelEngine.Network.*;

public class Inventory
{
    public static boolean netMode = false;
    
    protected ArrayList<Item> items = new ArrayList<Item>();
    
    protected ArrayList<Item> additions = new ArrayList<Item>();
    protected ArrayList<Item> removals = new ArrayList<Item>();
    
    boolean changed = false;
    boolean infinite = false;
    
    int totalWeight = 0;
    int spaceUsed = 0;
    
    int spaceCap = 50;
    int weightCap = 100;
    
    public void setSpaceCap(int s) { spaceCap = s; }
    public void setWieghtCap(int w) { weightCap = w; }
    
    public ArrayList<Message> getUpdates() {
        ArrayList<Message> updates = new ArrayList<Message>();
        
        for(Item i : additions) {
            Message m = new Message( (short) GameNetMessage.MOB_INV_ADD.getId(), (short) i.getId() );
            updates.add(m);
            additions.remove(i);
        }
        
        for(Item i : removals) {
            Message m = new Message( (short) GameNetMessage.MOB_INV_DEL.getId(), (short) i.getId() );
            updates.add(m);
            removals.remove(i);
        }
        
        return updates;
    }
    
    public void addItem(Item i) {
        if( !infinite && (totalWeight + i.getWeight() > weightCap || spaceUsed + i.getSpace() > spaceCap) ) {
            return;
        }
        
        items.add(i);
        if(netMode) additions.add(i);
        changed = true;
        
        totalWeight += i.getWeight();
        spaceUsed += i.getSpace();
    }
    
    public void remove(Item i) {
        items.remove(i);
        if(netMode) removals.add(i);
        changed = true;
        
        totalWeight -= i.getWeight();
        spaceUsed -= i.getSpace();
    }
    
    public void update() {
        int total = 0;
        
        for( Item i : items ) {
            total += i.getWeight();
        }
        
        totalWeight = total;
        
        total = 0;
        
        for( Item i : items ) {
            total += i.getSpace();
        }
        
        spaceUsed = total;
        
        changed = false;
    }
    
    public ArrayList<Item> getItems() { return items; }
}