package PixelEngine.Game;

import PixelEngine.Network.*;

public class Tile
{
    public static int size = 10;
    
    public int x = 0;
    public int y = 0;
    
    public short id;
    
    public Level level;
    
    public Tile(short i) {
        id = i;
    }
    
    public void setLevel(Level l) {
        level = l;
    }
    
    public Message getPlaceMessage() {
        Message toRet = new Message( (short) MessageTypes.getId("TILE_PLACE"), id);
        
        toRet.putShort( (short) x );
        toRet.putShort( (short) y );
        
        return toRet;
    }
    
    public Message getDestroyMessage() {
        Message toRet = new Message( (short) MessageTypes.getId("TILE_REMOVE"), id);
        
        toRet.putShort( (short) x );
        toRet.putShort( (short) y );
        
        return toRet;
    }
    
    public boolean equals(Tile other) {
        if(level == null || other.level == null) return false;
        if( !level.equals(other.level) ) return false;
        
        if(x == other.x && y == other.y) return true;
        
        return false;
    }
    
}