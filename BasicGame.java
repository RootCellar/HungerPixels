import PixelEngine.Screen.*;
import PixelEngine.Game.*;
import PixelEngine.Input.*;
import PixelEngine.Util.*;

import java.awt.Graphics;

public class BasicGame extends Game
{
    public static void main(String[] args) {
        new BasicGame();
    }
    
    PixelBar bar = new PixelBar();
    
    public BasicGame() {
        super();
        
        engine.debug("Starting engine...");
        
        engine.start();
    }
    
    public void revivePlayer() {
        player.x = 0;
        player.y = 0;
        player.revive();
    }
    
    public void tick() {
        InputListener in = engine.getInput();
        
        if( player.isAlive == false ) {
            revivePlayer();
        }
        
        if(in.up.down) {
            player.subY( player.speed );
        }
        
        if(in.down.down) {
            player.addY( player.speed );
        }
        
        if(in.left.down) {
            player.subX( player.speed );
        }
        
        if(in.right.down) {
            player.addX( player.speed );
        }
        
        if(in.v.wasDown()) {
            engine.setFps(10000);
            engine.debug("User wants turbo mode!");
        }
        
        if(in.y.wasDown()) {
            engine.setFps(100);
            engine.debug("User wants normal mode");
        }
        
        if(in.u.wasDown()) {
            engine.getScreen().ZOOM /= 2;
        }
        
        if(in.i.wasDown()) {
            engine.getScreen().ZOOM *= 2;
        }
        
        if(in.e.wasDown()) {
            engine.getScreen().ZOOM = 1;
        }
        
        if(in.p.down) {
            player.damage(1);
        }
        
        //player.hp = Randomizer.nextDouble(1, player.maxHp);
        
        engine.getLevel().tick();
    }
    
    public void render() {
        
        if( engine.getScreen().ZOOM < 0.125 ) engine.getScreen().ZOOM = 0.125;
        
        if( engine.getScreen().ZOOM > 8 ) engine.getScreen().ZOOM = 8;
        
        PixelCanvas c = engine.getScreen();
        
        Level level = engine.getLevel();
        
        c.clear();
        //engine.getScreen().randomize();
        
        c.setCenter( 0, 0 );
        
        c.setCenter( player.x, player.y );
        
        engine.renderBorder();
        
        engine.renderEntities();
        
        ///*
        c.drawCircle(player.x, player.y, 255, 255, 255, player.size);
        
        c.drawCircle(player.x, player.y, 255, 255, 255, player.size * 20);
        
        c.drawLine(player.x, player.y, level.xBound / 2, level.yBound / 2, 255, 255, 255);
        
        c.drawLine( player.x, player.y, 0, 0, 255, 0, 0, 100 );
        c.drawLine( player.x, player.y, level.xBound, 0, 0, 255, 0, 100 );
        c.drawLine( player.x, player.y, 0, level.yBound, 0, 0, 255, 100 );
        c.drawLine( player.x, player.y, level.xBound, level.yBound, 255, 255, 0, 100 );
        
        c.drawLine( player.x, player.y, level.xBound / 2, 0, 255, 0, 255, 100 );
        c.drawLine( player.x, player.y, level.xBound / 2, level.yBound, 0, 255, 255, 100 );
        //*/
        
        c.render();
    }
    
    public void draw(Graphics g) {
        g.drawString("Time: " + System.nanoTime(), 50, 60);
        g.drawString("ZOOM: " + engine.getScreen().ZOOM, 50, 80);
    }
}