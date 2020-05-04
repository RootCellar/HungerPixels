package PixelEngine.Network;

import java.net.*;
import java.io.*;

import PixelEngine.Util.*;
import PixelEngine.Logging.*;

public class SocketHandler implements Runnable
{
    private static Logger log = new Logger("SocketHandler", "Log");
    
    private static int waitTime = 1;

    private boolean going = false;
    private boolean connected=true;

    private static boolean globalGoing = true;

    private int BYTES_RECEIVED = 0;
    private int BYTES_SENT = 0;

    private Socket client;

    private Thread thread;

    private SocketUser user;
    private InputUser user2;

    DataOutputStream out;
    DataInputStream in;
    public SocketHandler(Socket s) {
        client=s;
        connected=true;

        try{
            out = new DataOutputStream(client.getOutputStream());
        }catch(Exception e) {
            out("Failed to Construct");
            close();
        }
    }

    public static SocketHandler getNew(String ip, int port) {
        try{
            return new SocketHandler( new Socket(ip, port) );
        }catch(Exception e) {
            return null;
        }
    }

    public int getByteReceiveCount() {
        return BYTES_RECEIVED;
    }

    public int getByteSentCount() {
        return BYTES_SENT;
    }

    public void start() {
        thread=new Thread(this);
        thread.start();
        going=true;
    }

    public void setUser(SocketUser u) {
        user = u;
    }

    public void setUser2(InputUser u) {
        user2 = u;
    }

    public void end() {
        client = null;
        thread = null;
        out = null;
    }

    public String getAddress() {
        return client.getRemoteSocketAddress().toString();
    }

    public void check() {
        thread.interrupt();
    }

    public boolean isGoing() {
        return going;
    }

    public boolean isConnected() {
        return connected;
    }

    public void sendMessage(Message message) {
        message.trim();

        sendBytes( message.getData() );
    }

    public void sendBytes(byte[] b) {
        if(!connected) return;
        try{
            out.writeShort(b.length);
            out.write(b, 0, b.length);

            BYTES_SENT += b.length;

            out.flush();
        }catch(IOException e) {
            
        }catch(Exception e) {
            out("COULD NOT SEND BYTES");
            out( e.getMessage() );
        }
    }

    public void sendString(String s, short i, short t) {
        Message m = new Message(t, i);

        m.setString(s);

        sendMessage( m );
    }

    public void sendString(String s) {
        sendString(s, (short) GameNetMessage.CHAT.getId(), (short) 0);
    }

    public void close() {
        try{
            client.close();
        }catch(Exception e) {

        }
        going=false;
        connected=false;
    }

    public synchronized void run() {
        out("Running...");
        in=null;
        try{
            in = new DataInputStream( client.getInputStream() );
        }catch(Exception e) {
            out("Couldn't get input stream");
            going = false;
            connected = false;
            return;
        }

        while(going && globalGoing) {

            try{
                Thread.sleep(waitTime);
            }catch(Exception e) {

            }

            if(user!=null) read();

        }
        going=false;
        connected=false;
    }

    public void read() {
        try{
            
            //out("Looking for bytes");

            short size = in.readShort();

            byte[] bytes = new byte[size];

            int count = in.read(bytes, 0, size);
            
            //out("Read some bytes");

            if(size != count) {
                out("RECEIVED WRONG NUMBER OF BYTES. DISCARDING...");

                throw new IOException("WRONG_BYTES");
            }

            user.inputBytes(bytes);

            BYTES_RECEIVED += count;

        }catch(IOException e) {
            connected=false;
            going=false;
        }catch(Exception e) {
            out(e.getMessage());

            out("EXCEPTION ON READ");
        }
    }

    public void out(String s) {
        if(user2 != null) {
            user2.inputText("[SOCKET HANDLER] " + s);
        }
        
        log.log("[SOCKET HANDLER] " + s);
        
        //System.out.println("[SOCKET HANDLER] " + s);
    }

    public static void setWaitTime(int x) {
        waitTime=x;
    }

    public static void setGoing(boolean b) {
        globalGoing=b;
    }
}