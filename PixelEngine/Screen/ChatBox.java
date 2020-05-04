package PixelEngine.Screen;

import java.util.ArrayList;
import java.awt.*;

public class ChatBox
{
    public static final int CHAR_WIDTH = 8;

    //Objects
    public ArrayList<String> messages = new ArrayList<String>();
    public String entered = "";

    //Vars
    public int maxMessages = 100;
    public int height = 300;
    public int width = 300;
    public int x = 100;
    public int y = 100;

    public ChatBox() {

    }

    public void addMessage(String s) {
        messages.add(0, s);
        tick();
    }

    public void tick() {
        while(messages.size() > maxMessages) {
            messages.remove(maxMessages - 1);
        }
    }

    public ArrayList<String> splitIntoLines(String s) {
        ArrayList<String> toRet = new ArrayList<String>();

        while(s.length() * CHAR_WIDTH > width) {
            String rem = s.substring(0, width / CHAR_WIDTH);
            s = s.substring(width / CHAR_WIDTH);

            toRet.add(0,rem);
        }

        toRet.add(0,s);

        return toRet;
    }

    public void draw(Graphics g) {

        g.setColor( Color.GREEN );

        g.drawString(entered, x + 5, y );

        int dx = x + 5;
        int dy = ( y - 25 );

        for( int i=0; i< messages.size(); i++ ) {

            ArrayList<String> lines = splitIntoLines(messages.get(i));
            
            for( int k=0; k<lines.size(); k++ ) {
                dy -= 15;

                if(dy < y - height) break;

                //g.drawString( messages.get(i), dx, dy );
                g.drawString( lines.get(k), dx, dy );
            }
            
        }

    }

    public void render(PixelCanvas c) {
        c.offset = false;

        c.drawLine(x, y - 15, x + width, y - 15, 255, 255, 255);

        c.drawLine(x, y - height, x, y - 15, 255, 255, 255);

        c.offset = true;
    }

}
