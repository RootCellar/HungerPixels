package PixelEngine.Screen;

public class PixelBar
{
    double percent = 1;
    double outOf=0;
    double has=0;
    int width = 30;
    int height = 10;
    
    double x = -100;
    double y = -100;
    
    public int rc = 0;
    public int gc = 255;
    public int bc = 0;
    
    public int roc = 255;
    public int goc = 0;
    public int boc = 0;
    
    static boolean DRAW = true;
    public void render(PixelCanvas c) {
        if(!DRAW) return;
        double howMuch = ( percent / 100.0 ) * ((double)width);
        int howMuch2 = (int)howMuch;
        
        for(int i=0; i<width; i++) {
            for(int k=0; k<height; k++) {
                //if(i>howMuch2) g.drawPixel(x+i, y+k, 255-rc, 255-gc, 255-bc);
                if(i>howMuch2) c.drawPixel( (int) (x+i), (int) (y+k), roc, goc, boc);
                else c.drawPixel(x+i, y+k, rc, gc, bc);
            }
        }
    }
    
    public void setProgress(double have, double need) {
        has = have;
        outOf = need;
        
        calcPercent();
    }
    
    public void set(double one, double two, int w, int h) {
        x = one;
        y = two;
        width = w;
        height = h;
    }
    
    public void calcPercent() {
        try{  
            percent = ( has / outOf ) * 100.0;
        }catch(Exception e) {
            percent = 100.0;
        }
    }
}