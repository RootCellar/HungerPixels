package PixelEngine.Util;

public class DIST
{
    public static void main(String[] args) {
        double x = getDistance(0 , 0, 11000, 14000);
        
        double y = getDistance( 0, 63, x, 63 );
        
        System.out.println("DISTANCE: " + y);
    }
    
    public static double getDistance(double x1, double y1, double x2, double y2) {
        double a = Math.abs( x1 - x2 );
        double b = Math.abs( y1 - y2 );
        a*=a;
        b*=b;
        return Math.sqrt( a + b );
    }
}