package PixelEngine.Network;

import java.util.Iterator;

import PixelEngine.Util.*;

public class MessageTypes
{
    static Registry list = new Registry();
    
    static Outputter outputter;
    
    public MessageTypes(Outputter o) {
        outputter = o;
    }
    
    public static int getId(String name) {
        for( Property p : list.getProperties() ) {
            if(name.equals(p.getName()) ) return p.toInt();
        }
        return -32768;
    }
    
    public static void add(Enum e) {
        for(Property p : list.getProperties()) {
            if( e.name().equals(p.getName()) ) return;
        }
        
        list.add( new Property( e.name(), list.size() + "" ) );
        
        out( "Added " + e.name() + " " + (list.size() - 1) );
    }
    
    public static void add(Iterable<Enum> e) {
        for(Enum s : e) add(s);
    }
    
    public static void add(Enum[] e) {
        for(Enum s : e) add(s);
    }
    
    public static void out(String s) {
        //System.out.println(s);
        if(outputter != null) outputter.debug("[NET] " + s);
    }
}