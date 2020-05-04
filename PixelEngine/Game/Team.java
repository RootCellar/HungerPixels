package PixelEngine.Game;

import java.util.ArrayList;
public class Team
{
    public String name = "NOT  SET";
    public int r = 255;
    public int g = 255;
    public int b = 255;
    public Level level;
    
    
    public void setLevel(Level l) {
        level=l;
    }
    
    public void setup() {
        
    }
    
    public ArrayList<Mob> getMobsOnTeam() {
        ArrayList<Mob> mobs = new ArrayList<Mob>();
        for(int i=0; i<level.entities.size(); i++) {
            Entity e = level.entities.get(i);
            
            if(e instanceof Mob) {
                Mob m = (Mob)e;
                
                if(m.team==null) continue;
                
                if(m.team.equals(this)) {
                    mobs.add(m);
                }
                
            }
            
        }
        
        return mobs;
    }
    
    public void tick() {
        
    }
}