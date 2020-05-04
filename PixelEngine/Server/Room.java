package PixelEngine.Server;

import java.util.*;
import java.util.concurrent.*;

import PixelEngine.Game.*;
import PixelEngine.Network.*;

public class Room
{
    public Server server;

    public String name = "ROOM";

    public Level level = new Level();

    public ArrayList<User> users = new ArrayList<User>();

    public ConcurrentLinkedQueue<ServerMessage> messages = new ConcurrentLinkedQueue<ServerMessage>();

    public Room(Server s) {
        server = s;
    }

    public void sendLevelUpdates() {
        for(Message m : level.getUpdates()) {
            
            for(User u : users) {
                u.send(m);
            }
            
        }
    }

    public void sendAll(String s) {
        out(s);
        for(User u : users) {
            u.send(s);
        }
    }

    public void sendAll(Message m) {
        for(User u : users) {
            u.send(m);
        }
    }

    public void setName(String n) {
        name = n;
    }

    public void removeUser(User u) {
        users.remove(u);
    }

    public void addUser(User u) {
        users.add(u);
        u.setRoom(this);
        u.send("Welcome to " + name + "!");
    }

    public void tick() {
        removeDisconnected();
        parseAllMessages();
    }

    public void removeDisconnected() {
        for(User u : users) {
            if(!u.isConnected()) removeUser(u);
        }
    }

    public void receive(User u, Message m) {
        ServerMessage message = new ServerMessage(m, u);
        messages.offer(message);
    }

    public void parseMessage(ServerMessage message) {

    }

    public void parseAllMessages() {
        while( messages.size() > 0 ) parseMessage( messages.poll() );
    }

    public void out(String s) {
        server.out("[" + name + "] " + s);
    }

    public void debug(String s) {
        server.debug("[" + name + "] " + s);
    }

}