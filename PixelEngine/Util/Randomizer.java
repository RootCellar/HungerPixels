package PixelEngine.Util;

import java.util.Random;

public class Randomizer
{
    static Random rand = new Random();
    
    static Random randSeeds = new Random();
    
    static boolean AUTO_RESEED = true;
    
    public static void reSeed() {
        rand.setSeed( randSeeds.nextLong() );
    }
    
    public static boolean nextBoolean() {
        if(AUTO_RESEED) reSeed();
        
        return rand.nextBoolean();
    }
    
    public static int nextInt(int min, int max) {
        if(AUTO_RESEED) reSeed();
        
        if(min < 0 || max < 0) return -1;
        if(min >= max) return -1;
        
        return min + rand.nextInt( ( max - min ) + 1 );
    }
    
    public static double nextDouble(double max) {
        if(AUTO_RESEED) reSeed();
        
        return rand.nextDouble() * max;
    }
    
    public static double nextDouble(double min, double max) {
        if(AUTO_RESEED) reSeed();
        
        if(min < 0 || max < 0) return -1.0;
        if(min >= max) return -1.0;
        
        return min + nextDouble( ( max - min ) );
    }
}