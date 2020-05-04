package PixelEngine.Server;

import PixelEngine.Network.*;

public class ServerMessage
{
    Message message;
    User from;
    
    public ServerMessage(Message m, User u) {
        from = u;
        message = m;
    }
    
    public Message getMessage() { return message; }
    public User getFrom() { return from; }
}