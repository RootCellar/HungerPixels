package PixelEngine.Screen;

import java.awt.*;

public class StatusInfo
{

    int dx = 0;
    int dy = 0;

    String message = "{NOT SET}";

    int red = 255;
    int green = 255;
    int blue = 255;

    public void setMessage(String s) {
        message = s;
    }

    public void setPos(int x, int y) {
        dx = x;
        dy = y;
    }

    public void setColor(int r, int b, int g) {
        red = r;
        blue = b;
        green = g;
    }

    public void draw(Graphics g) {
        g.setColor( new Color(red, green, blue) );  

        g.drawString(message, dx, dy);
    }

}