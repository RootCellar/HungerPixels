import PixelEngine.Server.*;

import PixelEngine.Game.*;
import PixelEngine.Screen.*;
import PixelEngine.Network.*;

public class HungerPlayer extends Player
{

    public User user;
    
    Inventory inventory = new Inventory();

    public PixelBar hungerbar = new PixelBar();
    public PixelBar thirstbar = new PixelBar();

    public boolean up = false;
    public boolean down = false;
    public boolean left = false;
    public boolean right = false;

    public boolean attacking = false;

    public HungerPlayer(User u) {
        if(idPoint > 32000) idPoint = -32000;

        maxHp = 100;
        hp = 100;
        regen = 0.001;

        speed = 3;

        user = u;
        
        team = new Team();
    }
    
    /*
     * PixelEngine Player.die() method results in NullPointerException
     * Fix by replacing
     */
    public void die() {
        isAlive=false;
        if(level != null) level.remove(this);
    }
    
    public void damage(double a) {
        hp-=a;
        damageTime = 400;
        checkHp();
        hpUpdated = true;
    }

    public void damage(double a, Mob m) {
        damage(a);
        if(hp<=0) die(m);
    }
    
    public Message getSpawnMessage() {
        return new Message( (short) MessageTypes.getId("MOB_SPAWN"), id);
    }
    
    public Message getDespawnMessage() {
        return new Message( (short) MessageTypes.getId("MOB_REMOVE"), id);
    }

    public void regen() {
        if( hp < maxHp && (hunger > 25 && thirst > 25 ) ) {
            //int hpWas = (int) hp;
            
            hp += regen * maxHp;
            if(hp >= maxHp) hp = maxHp;
            
            //if( (int) hp == hpWas ) hpUpdated = true; //That's not right
            hpUpdated = true;

            addHunger(-0.05);
            addThirst(-0.05);
        }
        else if(hp >= maxHp) hp = maxHp;
    }

    public void tick() {
        super.tick();

        /*
        if(up) y -= speed;
        if(down) y += speed;
        if(left) x -= speed;
        if(right) x += speed;
         */

        if(up) subY(speed);
        if(down) addY(speed);
        if(left) subX(speed);
        if(right) addX(speed);
        
        if( up || down || left || right ) {
            addHunger(-0.01);
            addThirst(-0.01);
        }

        addHunger(-0.001);
        addThirst(-0.001);
        
        if( hunger < 10 ) damage(0.01);
        if( thirst < 10 ) damage(0.01);

        doLifeBar();
    }

    public void doLifeBar() {

        HPbar.set( x - 15, y + 15, 30, 10);
        HPbar.setProgress(hp, maxHp);

        hungerbar.set( x - 15, y + 30, 30, 10);
        hungerbar.setProgress(hunger, maxHunger);

        hungerbar.rc = 127;
        hungerbar.gc = 127;

        thirstbar.set( x - 15, y + 45, 30, 10);
        thirstbar.setProgress(thirst, maxThirst);

        thirstbar.gc = 0;
        thirstbar.bc = 255;
    }

    public void render(PixelCanvas c) {
        c.drawCircle(x, y, 255, 0, 0, 15);

        HPbar.render(c);
        hungerbar.render(c);
        thirstbar.render(c);
    }
}