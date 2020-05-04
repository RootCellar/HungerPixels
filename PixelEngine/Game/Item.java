package PixelEngine.Game;

public class Item extends Entity
{
    String name = "NULL";
    
    String type = "NULL";
    
    int idType = 0;
    
    boolean wasUsed = false;
    
    int weight = 0;
    int space = 0;
    
    public Item(int i) {
        idType = i;
    }
    
    public void setWeight(int w) {
        weight = w;
    }
    
    public void setSpace(int s) {
        space = s;
    }
    
    public int getId() { return id; }
    public String getName() { return name; }
    public int getWeight() { return weight; }
    public int getSpace() { return space; }
    
    public boolean canUse() {
        return wasUsed;
    }
    
    public void use(Mob m) {
        
    }
}