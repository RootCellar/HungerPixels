package PixelEngine.Input;

import java.util.ArrayList;
import java.awt.event.*;

import PixelEngine.Screen.*;

public class InputListener implements KeyListener
{
    public class Key
    {
        public boolean down = false;
        public boolean wasDown = false;
        public boolean wasUp = false;
        public void Key()
        {
            keys.add(this);
        }

        public void toggle(boolean pressed) {
            if(pressed && !down) wasDown=true;
            if(!pressed) wasUp = true;
            down = pressed;
        }

        public boolean wasDown() {
            if(wasDown) {
                wasDown = false;
                return true;
            }
            return false;
        }
        
        public boolean wasUp() {
            if(wasUp) {
                wasUp = false;
                return true;
            }
            return false;
        }
    }
    
    public KeyUser user;

    public ArrayList<Key> keys = new ArrayList<Key>();
    
    public Key aup = new Key();
    public Key adown = new Key();
    public Key aleft = new Key();
    public Key aright = new Key();

    public Key up = new Key();
    public Key down = new Key();
    public Key left = new Key();
    public Key right = new Key();
    public Key space = new Key();
    public Key j = new Key();
    public Key k = new Key();
    public Key l = new Key();
    public Key p = new Key();
    public Key t = new Key();
    public Key c = new Key();
    public Key v = new Key();
    public Key y = new Key();
    public Key u = new Key();
    public Key i = new Key();
    public Key z = new Key();
    public Key x = new Key();
    public Key e = new Key();
    public Key b = new Key();
    public Key n = new Key();
    public Key m = new Key();

    public InputListener(PixelCanvas c) {
        c.addKeyListener(this);
    }

    public void toggle(KeyEvent ke, boolean pressed) {
        //System.out.println(ke);
        if(ke.getKeyCode() == KeyEvent.VK_W) up.toggle(pressed);
        if(ke.getKeyCode() == KeyEvent.VK_S) down.toggle(pressed);
        if(ke.getKeyCode() == KeyEvent.VK_A) left.toggle(pressed);
        if(ke.getKeyCode() == KeyEvent.VK_D) right.toggle(pressed);
        if(ke.getKeyCode() == KeyEvent.VK_SPACE) space.toggle(pressed);
        if(ke.getKeyCode() == KeyEvent.VK_J) j.toggle(pressed);
        if(ke.getKeyCode() == KeyEvent.VK_K) k.toggle(pressed);
        if(ke.getKeyCode() == KeyEvent.VK_L) l.toggle(pressed);
        if(ke.getKeyCode() == KeyEvent.VK_P) p.toggle(pressed);
        if(ke.getKeyCode() == KeyEvent.VK_T) t.toggle(pressed);
        if(ke.getKeyCode() == KeyEvent.VK_C) c.toggle(pressed);
        if(ke.getKeyCode() == KeyEvent.VK_V) v.toggle(pressed);
        if(ke.getKeyCode() == KeyEvent.VK_I) i.toggle(pressed);
        if(ke.getKeyCode() == KeyEvent.VK_U) u.toggle(pressed);
        if(ke.getKeyCode() == KeyEvent.VK_Y) y.toggle(pressed);
        if(ke.getKeyCode() == KeyEvent.VK_Z) z.toggle(pressed);
        if(ke.getKeyCode() == KeyEvent.VK_X) x.toggle(pressed);
        if(ke.getKeyCode() == KeyEvent.VK_E) e.toggle(pressed);
        if(ke.getKeyCode() == KeyEvent.VK_B) b.toggle(pressed);
        if(ke.getKeyCode() == KeyEvent.VK_N) n.toggle(pressed);
        if(ke.getKeyCode() == KeyEvent.VK_M) m.toggle(pressed);
        
        if(ke.getKeyCode() == KeyEvent.VK_RIGHT) aright.toggle(pressed);
        if(ke.getKeyCode() == KeyEvent.VK_LEFT) aleft.toggle(pressed);
        if(ke.getKeyCode() == KeyEvent.VK_DOWN) adown.toggle(pressed);
        if(ke.getKeyCode() == KeyEvent.VK_UP) aup.toggle(pressed);
    }

    public void keyPressed(KeyEvent ke) {
        toggle(ke,true);
        
        if(user != null) user.keyPressed(ke);
    }

    public void keyReleased(KeyEvent ke) {
        toggle(ke,false);
        
        if(user != null) user.keyReleased(ke);
    }

    public void keyTyped(KeyEvent ke) {

    }
}