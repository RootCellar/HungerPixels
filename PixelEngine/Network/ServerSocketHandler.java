package PixelEngine.Network;

import java.net.*;
import java.io.*;
import java.util.ArrayList;

import PixelEngine.Server.*;
import PixelEngine.Util.*;

public class ServerSocketHandler implements Runnable
{
    private ServerSocket socket;
    private boolean setup;
    private boolean done;
    private boolean finished=true;
    private int port;
    private int waitTime = 10;
    Thread t;
    Server server;

    public ServerSocketHandler(Server s) {
        server = s;
        //setup();
    }

    public void end() {
        server = null;
        socket = null;
        t = null;
    }

    public boolean isFinished() {
        return finished;
    }

    public ServerSocket getSocket() {
        return socket;
    }

    public String getAddress() {
        try{
            return socket.getInetAddress().getLocalHost()+":"+port;
        }catch(Exception e) {
            return "";
        }
    }

    public void setup() throws Exception {
        if(!finished) {
            return;
        }

        finished=false;
        setup=false;
        done=false;
        port=-1;
        socket=null;
        for(int i=10000; i<65535; i++) {
            try{
                out("Trying port " + i + "...");
                socket = new ServerSocket(i);
                setup=true;
                port=i;
                setTimeout(1);
                startThread();
                return;
            }catch(Exception e) {
                setup = false;
                socket=null;
                out("Port " + i + " didn't work");
            }
        }
        finished=true;
        throw new Exception("Could not find a valid port");
    }

    public void setup(int p) throws Exception {
        if(!finished) return;

        finished=false;
        setup=false;
        done=false;
        port=-1;
        socket=null;

        try{
            socket = new ServerSocket(p);
            setup=true;
            port=p;
            setTimeout(1);
            startThread();
            return;
        }catch(Exception e) {
            setup = false;
            socket=null;
            finished=true;
            throw new Exception("Could not find a valid port");
        }
    }

    public int getPort() {
        return port;
    }

    public boolean isSetup() {
        return setup;
    }

    public boolean isDone() {
        return done;
    }

    public void setTimeout(int x) throws SocketException {
        socket.setSoTimeout(x);
    }

    public void startThread() {
        done=false;
        t = new Thread(this);
        t.start();
        //new Thread(this).start();
    }

    public void setWaitTime(int x) {
        waitTime=x;
    }

    public synchronized void run() {
        Socket client;
        finished=false;

        out("Found a valid port");

        out("Hosting on " + getAddress() );

        while(!done) {
            try{
                client=socket.accept();
                if(client!=null) {
                    //out("A client has connected");

                    SocketHandler so = new SocketHandler(client);

                    out("Connection received. IP: " + so.getAddress() );

                    so.sendString("Received connection");

                    server.addSocket( so );
                }
            }catch(Exception e) {

            }

            if(done) {
                finished = true;
                return;
            }

            try{
                Thread.sleep(waitTime);
            }catch(Exception e) {

            }
        }
        done=true;
        finished=true;
    }

    public void close() {
        try{
            socket.close();
            port=-1;
        }catch(Exception e) {

        }
    }

    public void stop() {
        close();
        done=true;
    }

    public void out(String s) {
        server.out("[SERVER SOCKET HANDLER] " + s);
    }
}