import PixelEngine.Server.*;

public class Player
{
    public double maxHp = 100;
    public double hp = 100;
    
    public double maxEnergy = 100;
    public double energy = 100;
    
    public short x = 0;
    public short y = 0;
    
    public static short idPoint = -32000;
    public short id = idPoint++;
    
    public User user;
    
    public boolean up = false;
    public boolean down = false;
    public boolean left = false;
    public boolean right = false;
    
    public Player(User u) {
        if(idPoint > 32000) idPoint = -32000;
        
        user = u;
    }
    
    public void tick() {
        if(up) y--;
        if(down) y++;
        if(left) x--;
        if(right) x++;
    }
}