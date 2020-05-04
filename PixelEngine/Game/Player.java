package PixelEngine.Game;

import PixelEngine.Screen.*;

public class Player extends Mob
{
    public int kills = 0;
    
    public double dx = 0;
    public double dy = 0;

    public Player() {
        super();
        maxHp = 100;
        //hp = maxHp;
        hp = 1;
        regen = maxHp / 1000;
        team = new Team();
        speed = 2;
        size = 15;
        y = 500;
    }
    
    public void damage(double a) {
        hp-=a;
        damageTime = 400;
        checkHp();
    }

    public void damage(double a, Mob m) {
        damage(a);
        if(hp<=0) die(m);
    }

    public void die(Mob m) {
        m.killed(this);
        if(isAlive) {
            die();
        }
    }

    public void die() {
        isAlive=false;
        level.remove(this);
    }

    public void killed(Mob m) {
        kills++;
    }

    public void render(PixelCanvas c) {
        c.drawSquare((int)x-3, (int)y-3, (int)x+3, (int)y+3, 255, 255, 255);

        //c.drawCircle(x, y, 255, 255, 255, size);
        
        //c.drawCircle(x, y, 255, 255, 255, size * 20);
        
        //c.drawLine(x, y, level.xBound / 2, level.yBound / 2, 255, 255, 255);
        
        //c.drawLine( x, y, 0, 0, 255, 0, 0, 100 );
        //c.drawLine( x, y, level.xBound, 0, 0, 255, 0, 100 );
        //c.drawLine( x, y, 0, level.yBound, 0, 0, 255, 100 );
        //c.drawLine( x, y, level.xBound, level.yBound, 255, 255, 0, 100 );
        
        //c.drawLine( x, y, level.xBound / 2, 0, 255, 0, 255, 100 );
        //c.drawLine( x, y, level.xBound / 2, level.yBound, 0, 255, 255, 100 );
        //c.drawLine( x, y, level.xBound, level.yBound / 2, 0, 0, 255, 100 );
        
        HPbar.render(c);
    }

    public void tick() {
        super.tick();
        
        HPbar.set( x - 20, y + 15, 40, 10 );
        
        dx = x;
        dy = y;
        
    }
}