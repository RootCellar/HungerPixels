package PixelEngine.Server;

import PixelEngine.Game.*;
import PixelEngine.Network.*;
import PixelEngine.Util.*;
import PixelEngine.Logging.*;

public class Server implements Runnable, Outputter
{
    //Logging
    Logger logger = new Logger("Server", "Server");
    Logger debugLog = new Logger("Server", "Debug");
    
    //Useful Objects
    public ServerSocketHandler serverSocket;
    public Registry registry = new Registry();
    public MessageTypes messageTypes = new MessageTypes(this);

    //Variables
    public boolean going = false;
    public double TPS = 50;
    public int waitTime = 1;

    public int ticks = 0;
    public int ticks2 = 0;

    public Server() {
        MessageTypes.add(GameNetMessage.values());
        
        serverSocket = new ServerSocketHandler(this);
        
        Inventory.netMode = true;
        Level.MODE_NET = true;
        Level.o = this;
        
        //SocketHandler.setWaitTime(0);
        
        registry.add( new Property("HOSTNAME", "SERVER") );
        
        try{
            serverSocket.setup();
        }catch(Exception e) {
            out("Can't set up server socket handler");
        }
    }

    public void setTps(int t) {
        TPS = t;
    }

    public double getTps() { return TPS; }

    public void start() {
        if(!going) {
            new Thread(this).start();
            going = true;
        }
    }

    public void stop() {
        going = false;   
    }

    public void addSocket(SocketHandler s) {
        out("Sending Bytes");
        s.sendString("Hello! Your connection has been received.");
        s.close();
    }
    
    public void setup() {
        
    }

    public void run() {
        out("Setting up main loop...");

        long last = System.nanoTime();
        double unprocessed = 0;
        double nsPerTick = 1000000000.0 / TPS;
        long lastFrame = System.nanoTime();
        long checkTime = System.nanoTime();
        
        setup();

        int eInRow = 0; //Keep track of how many loops are interrupted in a row

        out("Running...");

        while(going) {
            try{
                nsPerTick = 1000000000.0 / TPS;

                waitTime = 1;

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

                Thread.sleep(waitTime);

                if( System.nanoTime() - checkTime > 1000000000) {
                    ticks = ticks2;
                    ticks2=0;
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
        
    }

    public void out(String s) {
        System.out.println("[SERVER] " + s);
        logger.log(s);
    }
    
    public void debug(String s) {
        debugLog.log(s);
    }

}