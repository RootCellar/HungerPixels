package PixelEngine.Game;

import PixelEngine.Input.*;
import PixelEngine.Screen.*;
import PixelEngine.Logging.*;
import PixelEngine.Util.*;

import java.awt.*;

public class PixelEngine implements Runnable, PixelCanvasUser
{
    private Game game;

    private PixelCanvas screen;

    private InputListener input;

    private Level level = new Level();

    private Logger toLog = new Logger("Engine", "Engine");
    
    private Logger gameLog = new Logger("Game", "Game");

    private Player player = new Player();

    private Menu menu;

    private int TPS = 50;
    private int FPS = 100;
    private int waitTime = 1;

    private boolean going = false;

    public boolean DEBUG = true;

    private int ticks = -1;
    private int frames = -1;
    private int ticks2 = 0;
    private int frames2 = 0;

    private long startTime = System.nanoTime();

    public PixelEngine(Game g) {
        out("Constructing...");

        game = g;
        
        out("Creating Screen...");
        
        screen = new PixelCanvas();

        screen.user = this;
        
        out("Setting up keyboard input...");

        input = new InputListener(screen);

        //out("Placing player in the level...");
        
        //level.add(player);
        
        out("Setting start menu...");
        
        setMenu( new Menu(this) );
        
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu( Menu m ) {
        menu = m;

        out("Changed Menu");
    }
    
    public void debug(String s) {
        out("Debugging for game: " + s);
        
        gameLog.log(s);
    }

    private void log(String s) {
        toLog.log(s);
    }

    private void out(String s) {
        System.out.println("[PIXEL ENGINE] " + s);
        log("PRINT: " + s);
    }

    public Player getPlayer() { return player; }

    public PixelCanvas getScreen() { return screen; }

    public Level getLevel() { return level; }

    public InputListener getInput() { return input; }

    public void setPlayer(Player p) {
        level.remove(player);

        player = p;

        level.add(player);

        level.tick();
    }

    public void setTps(int t) {
        TPS = t;
    }

    public void setFps(int f) {
        FPS = f;
    }

    public int getTps() {
        return ticks;
    }

    public int getFps() {
        return frames;
    }

    public void start() {
        if(going) return;

        out("Starting...");

        going = true;
        new Thread(this).start();
    }
    
    public void stop() {
        going = false;
    }

    public void renderBorder() {
        for(int i=-1 * (int)level.xBound; i<level.xBound; i+=50) {
            for(int k=-1 * (int)level.yBound; k<level.yBound; k+=50) {
                screen.drawPixel(i, k, 255, 255, 255);
            }
        }   

        for(int i=-1 * (int)level.xBound; i<level.xBound; i++) { //Top Border
            screen.drawPixel(i, -1 * level.yBound, 255, 255, 255);
        }

        for(int i=-1 * (int)level.xBound; i<level.xBound; i++) { //Bottom Border
            screen.drawPixel(i, level.yBound, 255, 255, 255);
        }

        for(int i=-1 * (int)level.yBound; i<level.yBound; i++) { //Left Border
            screen.drawPixel(-1 * level.xBound, i, 255, 255, 255);
        }

        for(int i=-1 * (int)level.yBound; i<level.yBound; i++) { //Right Border
            screen.drawPixel(level.xBound, i, 255, 255, 255);
        }
    }

    public void renderEntities() {
        for( Entity e : level.entities ) {
            e.render(screen);
        }
        
        for(Projectile p : level.projectiles) {
            p.render(screen);
        }

        //if(level.entities.size() < 1) out("NO ENTITIES");
    }

    public void run() {

        out("Setting up main loop...");

        long last = System.nanoTime();
        double unprocessed = 0;
        double nsPerTick = 1000000000.0 / (double)TPS;
        double nsPerFrame = 1000000000.0 / (double)FPS;
        long lastFrame = System.nanoTime();
        long checkTime = System.nanoTime();
        //setup();

        int eInRow = 0; //Keep track of how many loops are interrupted in a row
        
        //Set up Game
        out("Setting up game...");
        game.setup();

        out("Running...");

        while(going) {
            try{
                nsPerTick = 1000000000.0 / (double) TPS;
                nsPerFrame = 1000000000.0 / (double) FPS;

                waitTime = 1;
                if(FPS>100) waitTime = 0;

                long now = System.nanoTime();
                unprocessed += (now-last) / nsPerTick;
                last=now;

                boolean ticked = false;

                while(unprocessed>=1) {
                    tick();
                    ticks2++;
                    unprocessed-=1;
                    ticked = true;
                }

                if( ( System.nanoTime() - lastFrame ) >= nsPerFrame ) {
                    lastFrame = System.nanoTime();
                    render();
                    frames2++;
                    //lastFrame = System.nanoTime();
                }

                Thread.sleep(waitTime);

                if( System.nanoTime() - checkTime > 1000000000) {
                    ticks = ticks2;
                    frames = frames2;
                    ticks2=0;
                    frames2=0;
                    checkTime = System.nanoTime();
                }

                eInRow = 0;

            }catch(Exception e) {
                e.printStackTrace();
                out("Exception in main thread");

                eInRow ++; //Increment interruption counter

                if(eInRow > 10) { //Too many interruptions in a row, exit
                    out("Too many exceptions. Exiting...");

                    going = false;
                }
            }
        }

        going=false;
    }

    public void tick() {
        if( menu != null ) menu.tick();
        else game.tick();
    }

    public void render() {
        if( menu == null ) game.render();
        else {
            screen.clear();
            screen.render();
        }
    }

    public void draw(Graphics g) {
        if( menu == null ) game.draw(g);
        else menu.draw(g);

        g.drawString("TPS: "+ticks, 0, 30);
        g.drawString("FPS: "+frames, 0, 40);
    }
}