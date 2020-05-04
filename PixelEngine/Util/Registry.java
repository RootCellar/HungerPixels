package PixelEngine.Util;

import java.util.ArrayList;
import java.io.*;

import PixelEngine.Util.Property;

public class Registry
{
    static {
        new File("Registry").mkdir();
    }
    
    ArrayList<Property> properties = new ArrayList<Property>();

    public Registry() {
        
    }
    
    public int size() { return properties.size(); }

    public void clear() {
        while(properties.size() > 0) properties.remove(0);
    }

    public ArrayList<Property> getProperties() {
        return properties;
    }
    
    public boolean has(String n) {
        for( Property p : properties ) {

            if(p.getName().equals(n) ) return true;

        }

        return false;
    }

    public Property get(String n) {
        for( Property p : properties ) {

            if(p.getName().equals(n) ) return p;

        }

        return new Property( "NULL", "NULL" );
    }

    public void add( Property newProp ) {
        for( Property p : properties ) {

            if(p.getName().equals( newProp.getName() ) ) {
                p.setValue( newProp.getValue() );
                return;
            }

        }

        properties.add(newProp);
    }

    public void save(String name) throws Exception {
        FileWriter fw;
        
        File file = new File("Registry/" + name + ".properties");
        
        if(!file.exists()) {
            file.createNewFile();
        }

        fw = new FileWriter(file);

        for(Property p : properties) {
            fw.write( p.getName() + ": " + p.getValue() );
            fw.write( System.getProperty("line.separator") );
        }
        
        fw.close();

    }
}