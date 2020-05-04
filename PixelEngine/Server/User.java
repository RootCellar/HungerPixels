package PixelEngine.Server;

import PixelEngine.Network.*;
import PixelEngine.Util.*;

public class User implements SocketUser
{
    public String name = "GUEST";
    
    public SocketHandler socketHandler;
    
    public Room room;
    
    public Registry data = new Registry();
    public Registry gameData = new Registry();
    
    boolean pingCheck = false;
    
    long pingSent = 0;
    long ping = 999;
    
    public User(SocketHandler s) {
        socketHandler = s;
        
        s.setUser(this);
    }
    
    public long getPing() { return ping; }
    
    public void checkPing() {
        if(pingCheck) return;
        
        pingCheck = true;
        
        Message m = new Message( (short) GameNetMessage.PING_CHECK.getId(), (short) 0);
        
        pingSent = System.nanoTime();
        
        send(m);
    }
    
    public void pingReceive() {
        long rec = System.nanoTime();
        
        ping = rec - pingSent;
        
        ping /= 1000000;
        
        pingCheck = false;
        //checkPing();
    }
    
    public Registry getData() {
        return data;
    }
    
    public Registry getGameData() {
        return gameData;
    }
    
    public void disconnect() {
        socketHandler.close();
    }
    
    public boolean isConnected() {
        return socketHandler.isConnected();
    }
    
    public String getName() {
        return name;
    }
    
    public void setRoom(Room r) {
        //data.clear(); //Data may still be useful!
        gameData.clear(); //This....not so much
        
        if(room != null) room.removeUser(this);
        room = r;
        send("Moving you to a new room...");
    }
    
    public String getAddress() {
        return socketHandler.getAddress();
    }
    
    public SocketHandler getSocketHandler() {
        return socketHandler;
    }
    
    public void send(String s) {
        Message m = new Message( (short) GameNetMessage.CHAT.getId(), (short) 0);
        
        m.setString(s);
        
        send(m);
    }
    
    public void send(Message m) {
        socketHandler.sendMessage(m);
    }
    
    public void inputBytes(byte[] b) {
        //System.out.println("USER: R");
        Message m = new Message(b);
        
        if(m.getType() == GameNetMessage.PING_CHECK.getId()) {
            pingReceive();
        }
        else if(room != null) {
            room.receive( this, m );
        }
    }
    
}