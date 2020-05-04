package PixelEngine.Screen;

import java.awt.*;

public class MouseHelper
{
    PixelCanvas canvas;

    double zoom = 1;

    double wasX = 0;
    double wasY = 0;

    public MouseHelper(PixelCanvas c) {
        canvas = c;
    }

    public void setZoom(double z) {
        zoom = z; 
    }

    public Point getPoint() {
        Point mousePos = canvas.getMousePosition();

        if(mousePos == null) {
            mousePos = new Point((int)wasX, (int)wasY);
            return mousePos;
        }

        double newX = mousePos.getX();
        double newY = mousePos.getY();
        
        newX -= canvas.xd;
        newY -= canvas.yd;
        
        newX += canvas.xo;
        newY += canvas.yo;

        //mousePos.translate((int) (dx) , (int) (dy) );
        
        wasX = newX;
        wasY = newY;
        
        mousePos.setLocation(wasX, wasY);

        /*

        //May or may not work properly
        mousePos.setLocation( mousePos.getX() * zoom, mousePos.getY() * zoom );

         */

        return mousePos;

    }

}