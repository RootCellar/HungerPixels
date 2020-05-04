package PixelEngine.Client;

import java.awt.event.*;
import java.awt.*;
import java.util.*;
import java.util.concurrent.*;

import PixelEngine.Game.*;
import PixelEngine.Network.*;
import PixelEngine.Util.*;
import PixelEngine.Logging.*;
import PixelEngine.Screen.*;
import PixelEngine.Input.*;

public class Client implements Runnable, KeyUser, PixelCanvasUser, InputUser, SocketUser, Outputter
{
    //Constants, if needed
    static char[] legalChars = " 1234567890abcdefghijklmnopqrstuvwxyz!@#$%^&*(){}[]-=_+:;<>,.?/'\"".toCharArray();
    
    //Logging
    public Logger logger = new Logger("Client","Client");
    public Logger debugLog = new Logger("Client","Debug");
    
    //Important Objects
    public MessageTypes messageTypes = new MessageTypes(this);
    public SocketHandler sHandler;
    public PixelCanvas canvas = new PixelCanvas();
    public InputListener input = new InputListener(canvas);
    public Registry registry = new Registry();
    public ChatBox chatBox = new ChatBox();
    public MouseHelper mouseHelper = new MouseHelper(canvas);
    
    //Use ConcurrentLinkedQueue to avoid exceptions due to multithreading
    public ConcurrentLinkedQueue<Message> messages = new ConcurrentLinkedQueue<Message>();
    
    //Variables
    public boolean going = false;
    public boolean typing = false;
    public boolean infoTab = false;
    public boolean chatOpen = true;
    public String message = "";
    public int waitTime = 1;
    
    public int ticks = 0;
    public int ticks2 = 0;
    public int frames = 0;
    public int frames2 = 0;
    
    public Client() {
        out("Client Created");
        
        canvas.user = this;
        Inventory.netMode = true;
        Level.MODE_NET = true;
        
        MessageTypes.add(GameNetMessage.values());
    }

    public void keyPressed(KeyEvent ke) {
        if(ke.getKeyCode() == KeyEvent.VK_T && !typing) {
            chatOpen = !chatOpen;
            typing = false;
        }
        
        if(ke.getKeyCode() == KeyEvent.VK_ESCAPE) {
            typing = false;
            message = "";
        }
        else if(ke.getKeyCode() == KeyEvent.VK_ENTER) {
            typing = true;

            //out("Typing: " + typing);

            if( typing == true && message.length() > 0 ) {
                inputText(message);
                message = "";
                //typing = false;
            }

        }

        if(typing) {

            if(ke.getKeyCode() == KeyEvent.VK_ESCAPE) {
                infoTab = true;
                out("Info: true");
            }
            else if(ke.getKeyCode() == KeyEvent.VK_BACK_SPACE && message.length() > 0 ) {
                message = message.substring(0, message.length() - 1 );
            }
            else {

                for( char c : legalChars) {

                    if( (c+"").equalsIgnoreCase( (ke.getKeyChar()+"") ) ) {
                        
                        message += ke.getKeyChar();
                        break;
                        
                    }

                }

            }
            
        }
        
    }

    public void keyReleased(KeyEvent ke) {
        /*
        if(ke.getKeyCode() == KeyEvent.VK_ESCAPE) {
        infoTab = false;
        out("Info: false");
        }
         */
    }

    public void inputText(String s) {
        //out("COMMAND: " + s);

        if( Command.is("@help", s) ) {
            out("@ - Commands for Client");
            out("@connect <address> <port> - connect to a server");
            out("@list - list registry");
            out("@quit - Exit, may not be safe");
        }

        else if( Command.is("@list", s) ) {
            for( Property p : registry.getProperties() ) {
                out( "NAME: " + p.getName() + " VALUE: " + p.getValue() );
            }
        }

        else if( Command.is("@quit", s) ) {
            System.exit(0);
        }

        else if( Command.is("@connect", s) ) {
            ArrayList<String> args = Command.getArgs(s);

            if(args.size() < 3) {
                out("Too few args!");
                return;
            }

            int port;
            try{
                port = Integer.parseInt( args.get(2) );
            }catch(Exception e) {
                out("Expected an int!");
                return;
            }

            SocketHandler so = SocketHandler.getNew( args.get(1), port );
            if(so != null) setSocketHandler(so);
            else out("Couldn't create connection");

        }
        else if(sHandler != null && s.indexOf("@") != 0 ) {
            //out("[S]");
            Message m = new Message( (short) GameNetMessage.CHAT.getId() , (short) 0 );
            m.setString(s);
            sHandler.sendMessage(m);
        }
    }

    public void inputBytes(byte[] b) {
        Message m = new Message(b);
        messages.offer(m);
    }
    
    public void parseMessage(Message m) {
        
    }
    
    public void parseMessages() {
        while(messages.size() > 0) parseMessage( messages.poll() );
    }

    public void setSocketHandler(SocketHandler handle) {
        out("Setting socket handler...");
        sHandler = handle;
        handle.setUser(this);
        sHandler.start();
    }

    public void start() {
        if(!going) {
            new Thread(this).start();
            going = true;
        }
    }

    public void stop() {
        going = false;
    }

    public void run() {
        out("Running...");

        setup();

        out("Setting up main loop...");

        double TPS = registry.get("TPS").toInt();
        double FPS = registry.get("FPS").toInt();

        long last = System.nanoTime();
        double unprocessed = 0;
        double nsPerTick = 1000000000.0 / TPS;
        double nsPerFrame = 1000000000.0 / FPS;

        long lastFrame = System.nanoTime();
        long checkTime = System.nanoTime();
        //setup();

        int eInRow = 0; //Keep track of how many loops are interrupted in a row

        out("Running...");

        while(going) {
            try{
                TPS = registry.get("TPS").toInt();
                FPS = registry.get("FPS").toInt();
                
                nsPerTick = 1000000000.0 / (double) TPS;
                nsPerFrame = 1000000000.0 / (double) FPS;

                waitTime = 1;
                if(FPS > 100) waitTime = 0;

                long now = System.nanoTime();
                unprocessed += (now-last) / nsPerTick;
                last=now;

                boolean ticked = false;

                while(unprocessed>=1) {
                    long TICK_BEGIN = System.nanoTime();
                    tick();
                    long TICK_END = System.nanoTime();
                    
                    registry.add( new Property("TIME_TICK", (TICK_END - TICK_BEGIN) + "" ) );
                    
                    ticks2++;
                    unprocessed-=1;
                    ticked = true;
                }

                Thread.sleep(waitTime);

                if( ( System.nanoTime() - lastFrame ) >= nsPerFrame ) {
                    lastFrame = System.nanoTime();
                    
                    long RENDER_START = System.nanoTime();
                    render();
                    long RENDER_END = System.nanoTime();
                    
                    registry.add( new Property("TIME_RENDER", (RENDER_END - RENDER_START) + "" ) );
                    
                    frames2++;
                    //lastFrame = System.nanoTime();
                }

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
                //out("Exception in main thread");

                eInRow ++; //Increment interruption counter

                if(eInRow > 10) { //Too many interruptions in a row, exit
                    out("Too many exceptions. Exiting...");

                    going = false;
                }
            }
        }

        going=false;
    }

    /**
     * Very, very important method.
     * Set up properties and other stuff
     */
    public void setup() {
        out("Setting up...");

        input.user = this;

        registry.add( new Property( "TPS", "100" ) );
        registry.add( new Property( "FPS", "100" ) );
        registry.add( new Property( "USER_NAME", "GUEST" ) );
        
        try{
            registry.save("Client");
        }catch(Exception e) {
            e.printStackTrace();
            out("Can't save registry");
        }

    }

    public void tick() {
        parseMessages();
    }

    public void render() {
        canvas.clear();

        //Other Stuff

        canvas.render();
    }

    public void draw(Graphics g) {

    }

    public void out(String s) {
        logger.log(s);

        System.out.println("[C] " + s);
        chatBox.addMessage("[C] " + s);
    }

    public void debug(String s) {
        System.out.println("[CLIENT] [DEBUG] " + s);

        debugLog.log(s);
    }

}