package PixelEngine.Network;

public enum GameNetMessage
{
    PING_CHECK,
    NAME_SET,
    CHAT,
    SELF_SET,
    ENTITY_SPAWN,
    ENTITY_REMOVE,
    MOB_SPAWN,
    MOB_REMOVE,
    MOB_RESIZE,
    MOB_POS,
    MOB_HP,
    MOB_STAMINA,
    MOB_HUNGER,
    MOB_THIRST,
    MOB_INV_ADD,
    MOB_INV_DEL,
    ITEM_SPAWN,
    ITEM_REMOVE,
    TILE_PLACE,
    TILE_REMOVE,
    KEY_W,
    KEY_A,
    KEY_S,
    KEY_D,
    KEY_SPACE,
    MOUSE_POS,
    ;
    
    String name;
    short id;
    
    GameNetMessage() {
        name = name();
        id = (short) ordinal();
        
        //System.out.println( "GAME NET: " + getId() + " " + getName() );
    }
    
    public String getName() { return name; }
    public int getId() { return id; }
}
