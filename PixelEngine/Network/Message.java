package PixelEngine.Network;

public class Message {
    
    public static int RESIZE_CHUNK = 32;
    
    public static int READ_START = 4;
    
    public byte[] data = new byte[RESIZE_CHUNK];
    
    public int used = 0;
    
    public int current = 0;
    
    public int currentRead = 0;
    
    public short id = -32768;
    
    public short type = -32768;
    
    public Message(byte[] bytes) {
        data = bytes;
        
        setupData();
        
        currentRead = READ_START;
    }
    
    public Message(short t, short i) {
        putShort( t );
        putShort( i );
        
        id = i;
        type = t;
    }
    
    public void setupData() {
        short i = 0;
        short t = 0;
        
        short u = 0;
        
        t = (short) ( data[0] & 0xff );
        //System.out.println(t);
        u = (short) ( ( data[1] << 8 ) & 0xff00 );
        //System.out.println(u);
        t = (short) ( t | u );
        //System.out.println(t);
        
        i = (short) ( data[2] & 0xff );
        //System.out.println(i);
        u = (short) ( ( data[3] << 8 ) & 0xff00 );
        //System.out.println(u);
        i = (short) ( i | u );
        //System.out.println(i);
        
        id = i;
        type = t;
    }
    
    public short getType() {
        return type;
    }
    
    public short getId() {
        return id;
    }
    
    public byte[] getData() {
        return data;
    }
    
    public void setData(int i, byte b) {
        if(i < 0 || i > data.length - 1) return;
        
        data[i] = b;
    }
    
    public void reset() {
        data = new byte[RESIZE_CHUNK];
        used = 0;
        current = 0;
        
        putShort(type);
        putShort(id);
    }
    
    public void use(int n) {
        used += n;
        current += n;
    }
    
    public void read(int n) {
        currentRead += n;
    }
    
    public boolean canRead(int n) {
        if( ( currentRead + n ) - 1 > data.length - 1) return false;
        
        return true;
    }
    
    public void allocate(int need) {
        if(data.length - used >= need) return;
        
        byte[] temp = new byte[ data.length ];
        
        System.arraycopy( data, 0, temp, 0, data.length );
        
        data = new byte[ temp.length + RESIZE_CHUNK ];
        
        System.arraycopy( temp, 0, data, 0, temp.length);
    }
    
    public void trim() {
        byte[] temp = new byte[ data.length ];
        
        System.arraycopy( data, 0, temp, 0, data.length );
        
        data = new byte[ used ];
        
        System.arraycopy( temp, 0, data, 0, data.length);
    }
    
    public void putShort(short n) {
        allocate(2);
        
        data[ current ] = (byte) ( n & 0xff );
        data[ current + 1 ] = (byte) ( ( n >> 8 ) & 0xff ) ;
        
        use(2);
    }
    
    public short readShort() {
        if(!canRead(2)) return -32768;
        
        short toRet = 0;
        short u = 0;
        
        toRet = (short) ( data[ currentRead ] & 0xff );
        u = (short) ( ( data[ currentRead + 1 ] << 8 ) & 0xff00 );
        toRet = (short) ( toRet | u );
        
        read(2);
        
        return toRet;
    }
    
    public void putByte(byte b) {
        allocate(1);
        
        data[ current ] = b;
        
        use(1);
    }
    
    public void putBoolean(boolean b) {
        if(b) putByte( (byte) 1);
        else putByte( (byte) 0);
    }
    
    public boolean getBoolean() {
        if( !canRead(1) ) return false;
        
        boolean toRet = false;
        
        if( data[ currentRead ] == 1 ) toRet = true;
        
        read(1);
        
        return toRet;
    }
    
    public void setString(String s) {
        reset();
        
        byte[] stringBytes = new byte[ s.length() ];
        
        for(int i=0; i<stringBytes.length; i++) {
            stringBytes[i] = (byte) s.charAt(i);
        }
        
        for(int i=0; i<stringBytes.length; i++) {
            if(stringBytes[ i ] != 0) putByte( stringBytes[ i ] );
        }
    }
    
    public String getString() {
        byte[] stringBytes = new byte[data.length - 4];
        
        for(int i=4; i<data.length; i++) {
            stringBytes[ i - 4 ] = data[ i ];
        }
        
        String s = "";
        
        for(int i=0; i<stringBytes.length; i++) {
            s += (char) stringBytes[i];
        }
        
        return s;
    }
}