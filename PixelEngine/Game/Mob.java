package PixelEngine.Game;

import java.util.ArrayList;

import PixelEngine.Screen.*;
import PixelEngine.Network.*;

public class Mob extends Entity
{

    //Stats that should support most games
    public String name = "Mob";

    public double hp = 100;
    public double maxHp = 100;
    public boolean hpUpdated = false;

    public double stamina = 100;
    public double maxStamina = 100;
    public boolean staminaUpdated = false;

    public double hunger = 100;
    public double maxHunger = 100;
    public boolean hungerUpdated = false;

    public double thirst = 100;
    public double maxThirst = 100;
    public boolean thirstUpdated = false;

    public double regen = 0.01;
    public int damageTime = 0;

    public double rot = 0;
    public int dir = 0;

    public double speed = 1;
    public PixelBar HPbar = new PixelBar();
    public boolean isAlive=true;

    public Inventory inventory = new Inventory();
    
    public Mob() {
        super();
    }
    
    public void forceUpdate() {
        super.forceUpdate();
        
        hpUpdated = true;
        staminaUpdated = true;
        hungerUpdated = true;
        thirstUpdated = true;
    }
    
    public Message getSpawnMessage() {
        return new Message( (short) MessageTypes.getId("MOB_SPAWN"), id);
    }
    
    public Message getDespawnMessage() {
        return new Message( (short) MessageTypes.getId("MOB_REMOVE"), id);
    }
    
    public ArrayList<Message> getUpdates() {
        ArrayList<Message> updates = new ArrayList<Message>();
        
        for( Message m : super.getUpdates() ) {
            updates.add(m);
        }
        
        for(Message m : inventory.getUpdates() ) {
            updates.add(m);
        }
        
        if(hpUpdated) {
            Message m = new Message( (short) GameNetMessage.MOB_HP.getId(), id );
            m.putShort( (short) hp );
            updates.add(m);
            hpUpdated = false;
        }
        
        if(staminaUpdated) {
            Message m = new Message( (short) GameNetMessage.MOB_STAMINA.getId(), id );
            m.putShort( (short) stamina );
            updates.add(m);
            staminaUpdated = false;
        }
        
        if(hungerUpdated) {
            Message m = new Message( (short) GameNetMessage.MOB_HUNGER.getId(), id );
            m.putShort( (short) hunger );
            updates.add(m);
            hungerUpdated = false;
        }
        
        if(thirstUpdated) {
            Message m = new Message( (short) GameNetMessage.MOB_THIRST.getId(), id );
            m.putShort( (short) thirst );
            updates.add(m);
            thirstUpdated = false;
        }
        
        return updates;
    }

    public static double[] findPosByAngle(double x1, double y1, double r, double d) {
        double[] toRet = new double[2];

        while(r >= 360) {
            r-=360;
        }

        while(r < 0) {
            r+=360;
        }

        if(r % 90 == 0) {

            if(r==90) {
                toRet[0] = d;
                toRet[1] = 0;
                return toRet;
            }

            if(r==180) {
                toRet[0] = 0;
                toRet[1] = d;
                return toRet;
            }

            if(r==270) {
                toRet[0] = d * -1;
                toRet[1] = 0;
                return toRet;
            }

            if(r==360) {
                toRet[0] = 0;
                toRet[1] = d * -1;
                return toRet;
            }

        }

        int c = 0;
        while(r >= 90 ) {
            r-=90;
            c++;
        }

        if(c==1 || c==3) r= 90 - r;

        double x2 = d * Math.sin( Math.toRadians(r) );
        double y2 = d * Math.cos( Math.toRadians(r) );

        if(c==0) {
            y2*=-1;
        }

        if(c==1) {
            //y2*=-1;

        }

        if(c==2) {
            //y2*=-1;
            x2*=-1;
        }

        if(c==3) {
            x2*=-1;
            y2*=-1;
        }

        toRet[0] = x2;
        toRet[1] = y2;

        return toRet;
    }

    public void regen() {
        if(hp < maxHp) {
            hp += regen * maxHp;
            hpUpdated = true;
        }
        else hp = maxHp;
    }

    public void revive() {
        isAlive=true;
        hp=maxHp;
        level.add(this);
    }

    public void killed(Mob m) {

    }

    public void die(Mob m) {
        m.killed(this);
        if(isAlive) {
            die();
        }
    }

    public void die() {
        isAlive=false;
        if(level != null) level.remove(this);
    }

    public void checkHp() {
        if(hp<=0) {
            die();
            hp=0;
        }

        if(hp>=maxHp) hp=maxHp;
    }

    public void checkStats() {
        if(hunger > maxHunger) hunger = maxHunger;
        if(thirst > maxThirst) thirst = maxThirst;
        if(stamina > maxStamina) stamina = maxStamina;

        if(hunger < 0) hunger = 0;
        if(thirst < 0) thirst = 0;
        if(stamina < 0) stamina = 0;
    }

    public void damage(double a) {
        hp-=a;
        if(hp < 0 ) { 
            hp = 0;
            die();
        }
        damageTime = 400;
        hpUpdated = true;
        checkHp();
    }

    public void damage(double a, Mob m) {
        damage(a);
        if(hp<=0) die(m);
    }

    public void addHp(double a) {
        hp += a;
        checkHp();
        hpUpdated = true;
    }

    public void addHunger(double a) {
        hunger += a;
        hungerUpdated = true;
    }

    public void addThirst(double a) {
        thirst += a;
        thirstUpdated = true;
    }

    public void addStamina(double a) {
        stamina += a;
        staminaUpdated = true;
    }

    public void collision() {

    }

    public void tick() {
        regen();

        checkHp();
        checkStats();

        doLifeBar();
        
        damageTime--;
        if(damageTime < 0) damageTime = 0;
    }

    public void doLifeBar() {

        HPbar.set( x - 10, y + 15, 20, 25);
        HPbar.setProgress(hp, maxHp);

    }
}