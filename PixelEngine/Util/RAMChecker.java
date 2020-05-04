package PixelEngine.Util;

public class RAMChecker 
{
    public long totalMem = 0;
    public long freeMem = 0;
    public long maxMem = 0;
    public long used = 0;
    
    double percentUsed = 0;
    
    public void calc() {
        totalMem = Runtime.getRuntime().totalMemory();
        freeMem = Runtime.getRuntime().freeMemory();
        maxMem = Runtime.getRuntime().maxMemory();
        
        used = maxMem - freeMem;
        
        percentUsed = ((double) used) / ((double) maxMem);
        percentUsed *= 100;
    }
}