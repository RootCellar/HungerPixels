package PixelEngine.Screen;

import java.awt.*;
import java.awt.image.*;
import javax.swing.JFrame;
import java.awt.event.*;
import javax.imageio.ImageIO;
import java.io.File;

import PixelEngine.Util.*;

public class PixelCanvas extends Canvas implements Runnable
{
    public int WIDTH = 700;
    public int HEIGHT = 700;

    BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
    int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
    JFrame frame;
    BufferStrategy bs = getBufferStrategy();
    int targetfps = 30;
    boolean going = false;
    double Pfps = 0;
    long timeSinceLast = System.nanoTime();
    int frames = 0;
    int fps = 0;
    public PixelCanvasUser user;
    boolean drawFPS = false;
    long timeSinceCheck = System.nanoTime();

    public double ZOOM = 1;

    public double xo = 0;
    public double yo = 0;

    public int xd = WIDTH/2;
    public int yd = HEIGHT/2;

    public boolean offset = true;
    
    public boolean AUTO_CLEAR = true;
    
    public int SET_TIMES = 0;

    public void setCenter( double one, double two) {
        xo = one;
        yo = two;
    }

    public void resize() {
        image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
    }

    public void setSize(int n) {
        frame.setMinimumSize(new Dimension(n,n));
    }

    public void setTargetFps(int f) {
        targetfps=f;
    }

    public void start() {
        new Thread(this).start();
        going=true;
    }

    public void stop() {
        going=false;
    }

    public void run() {
        going = true;
        long time = System.nanoTime();
        while(going) {
            try{
                Thread.sleep(1);
            }catch(Exception e) {

            }

            try{
                if(System.nanoTime() - time >= 1000000000/targetfps) {
                    draw();
                    time = System.nanoTime();
                }
            }catch(Exception e) {

            }
        }
        going=false;
    }

    public void draw() {
        long time1 = System.nanoTime();
        render();
        long time2 = System.nanoTime();
        if(System.nanoTime() - timeSinceLast > 1000000000) {
            Pfps = 1000000000.0 / ( (double)( time2 - time1 ) );
            fps = frames;
            frames = 0;
            timeSinceLast = System.nanoTime();
        }
    }   

    public void drawFps(Graphics g) {
        g.setColor(Color.RED);
        //g.drawString("PFPS: "+Pfps,10,10);
        if(drawFPS) g.drawString("FPS: "+fps,10,20);
    }

    public void render() {

        if(System.nanoTime() - timeSinceCheck >= 500000000) {
            if(WIDTH != getWidth() || HEIGHT != getHeight()) {
                WIDTH = getWidth();
                HEIGHT = getHeight();
                resize();
                timeSinceCheck = System.nanoTime();

                xd = WIDTH/2;
                yd = HEIGHT/2;
            }
        }

        bs = getBufferStrategy();
        if (bs == null) {
            createBufferStrategy(2);
            requestFocus();
            return;
        }

        Graphics g = bs.getDrawGraphics();

        g.setColor(Color.BLUE);
        //if(AUTO_CLEAR) g.fillRect(0,0,getWidth(),getHeight());

        g.drawImage(image,0,0,WIDTH,HEIGHT,null);

        if(user!=null) user.draw(g);

        drawFps(g);

        g.dispose();
        bs.show();
        frames++;
        SET_TIMES = 0;
    }

    public void clear() {
        /*
        for(int y=0; y< HEIGHT; y++) {
            for(int x=0; x < WIDTH; x++) {
                pixels[x+y*WIDTH]=0;
                //setPixel( x, y, 0, 0, 0 );
            }
        }
        */
        
        for(int i=0; i<pixels.length; i++) pixels[i] = 0;
    }

    public int getByPos(int x, int y, int w) {
        return x + y * w;
    }

    public void setPixel(int x, int y, int c) {
        try{
            if(x<0 || y<0) return;
            if(x>=WIDTH || y>=HEIGHT) return;
            int i = x + y * WIDTH;
            pixels[i]=c;
        }catch(Exception e) {

        }
    }

    public void setPixel(int x, int y, int r, int g, int b) {
        try{
            if(x<0 || y<0) return;
            if(x>=WIDTH || y>=HEIGHT) return;
            
            //int i = x + y * WIDTH;
            //pixels[i]=getColor(r,g,b);
            
            pixels[x + y * WIDTH]=getColor(r,g,b);
            
            SET_TIMES ++;
        }catch(Exception e) {

        }
    }

    //Faster than the old way
    ///*
    public static int getColor(int r, int g, int b) {
        //int r2=r<<16;
        //int g2=g<<8;
        //int b2=b;

        try{
            return (r << 16)+(g << 8)+(b);
        }catch(Exception e) {
            return 0;
        }
    }
    //*/

    /* Original Method
     * May be slower
    public static int getColor(int r, int g, int b) {
    int r2=r<<16;
    int g2=g<<8;
    int b2=b;

    try{
    return r2+g2+b2;
    }catch(Exception e) {
    return 0;
    }
    }
     */

    public void randomize() {
        for(int y=0; y< HEIGHT; y++) {
            for(int x=0; x < WIDTH; x++) {
                pixels[x+y*WIDTH]=(int)(Math.random()*16777215.0);
                //pixels[x+y*WIDTH]=0;
            }
        }
    }

    public PixelCanvas() {
        frame = new JFrame("Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.add(this, BorderLayout.CENTER);
        frame.pack();
        //frame.setResizable(false);
        frame.setMinimumSize(new Dimension(500,500));
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public void drawPixel(double x, double y, int r, int g, int b) {
        drawPixel( (int)Math.round(x), (int)Math.round(y), r, g, b);
        //screen.setPixel((x-xo)+xd, (y-yo)+yd, r, g, b);
        //if( Level.getDistance(xo, yo, x, y) < renderDistance) screen.setPixel((x-xo)+xd, (y-yo)+yd, r, g, b);
    }

    public void drawPixel(int x, int y, int r, int g, int b) {
        //Regular Render
        //screen.setPixel(( x -  ( xo ) ) + xd, ( y - ( yo ) ) + yd, r, g, b);
        if(offset) {
            x/=ZOOM;
            y/=ZOOM;

            x -= xo / ZOOM;
            y -= yo / ZOOM;

            x+= xd;
            y+= yd;
        }

        setPixel(x, y, r, g, b);

        //TEST RENDER
        //screen.setPixel(( (y) - yo)+xd, ( (x * -1) - xo)+yd, r, g, b);
        //if( Level.getDistance(xo, yo, x, y) < renderDistance) screen.setPixel((x-xo)+xd, (y-yo)+yd, r, g, b);
    }

    public void drawCircle(double x2, double y2, int r, int g, int b, double radius2) {
        int x = (int)Math.round(x2);
        int y = (int)Math.round(y2);
        int radius = (int)Math.round(radius2);

        for(int i=0; i<=90; i+=1) {

            double angle = ( (double) i ) / 1;
            double mult = Math.sin( Math.toRadians(angle) );
            int yAdd = (int)(mult * radius);
            mult = Math.cos( Math.toRadians(angle) );
            int xAdd = (int)(mult * radius);

            drawPixel( x+xAdd, y+yAdd, r,g,b);
            drawPixel( x-xAdd, y+yAdd, r,g,b);
            drawPixel( x+xAdd, y-yAdd, r,g,b);
            drawPixel( x-xAdd, y-yAdd, r,g,b);
        }
    }

    public void drawSquare(int x1, int y1, int x2, int y2, int r, int g, int b) {
        if(x1>x2 || y1>y2) return;

        for(int i=x1; i<=x2; i++) {
            for(int k=y1; k<=y2; k++) {
                drawPixel( i, k, r, g, b);
            }
        }
    }

    public void drawLine(double x1, double y1, double x2, double y2, int r, int g, int b, int count) {
        x1 *= -1;
        y1 *= -1;
        x2 *= -1;
        y2 *= -1;

        double y = 0;

        double slope;
        if(y1!=y2) slope = (double)Math.abs(y1-y2) / (double)Math.abs(x1-x2);
        else slope = 0;

        if(y1>y2) slope *=-1;

        double difference = Math.abs(x1 - x2);

        double begin;
        double end;

        if(difference<0) {
            begin = difference;
            end = 0;
        }
        else {
            begin = 0;
            end = difference;
        }

        double toAdd = end - begin;

        if(count<1) count = (int) Math.round( DIST.getDistance( x1, y1, x2, y2) );

        toAdd /= (double)count;

        if( x1 == x2 ) {
            for(double i = Math.min(y1, y2); i < Math.max(y1, y2); i += Math.abs( y1 - y2 ) / count ) {
                drawPixel( (x1 * -1), (i * -1), r, g, b );
            }
        }

        for(double i = begin; i<end; i+=toAdd) {
            y = i * slope;
            y *= -1;

            if(x1>x2) drawPixel( (x1*-1)+i, (y1*-1)+y, r, g, b);
            else drawPixel( (x1*-1)-i, (y1*-1)+y, r, g, b);
        }
    }

    public void drawLine(double x1, double y1, double x2, double y2, int r, int g, int b) {
        //drawLine(x1, y1, x2, y2, r, g, b, 100);

        drawLine(x1, y1, x2, y2, r, g, b, (int) DIST.getDistance( x1, y1, x2, y2 ) );
    }
}