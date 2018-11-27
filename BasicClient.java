import java.awt.*;

import java.util.*;
import java.awt.event.*;

import PixelEngine.Client.*;
import PixelEngine.Screen.*;
import PixelEngine.Network.*;

public class BasicClient extends Client
{
    public static void main(String[] args) {
        new BasicClient().start();
    }

    double mouseX = 0;
    double mouseY = 0;

    public ArrayList<Player> players = new ArrayList<Player>();

    public BasicClient() {
        super();

        canvas.user = this;
    }

    public void keyPressed(KeyEvent ke) {
        super.keyPressed(ke);

        if(sHandler == null) return;

        if(ke.getKeyCode() == KeyEvent.VK_W) {
            Message m = new Message( (short) 1, (short) 0 );
            m.putBoolean(true);
            sHandler.sendMessage(m);
        }

        if(ke.getKeyCode() == KeyEvent.VK_S) {
            Message m = new Message( (short) 2, (short) 0 );
            m.putBoolean(true);
            sHandler.sendMessage(m);
        }

        if(ke.getKeyCode() == KeyEvent.VK_A) {
            Message m = new Message( (short) 3, (short) 0 );
            m.putBoolean(true);
            sHandler.sendMessage(m);
        }

        if(ke.getKeyCode() == KeyEvent.VK_D) {
            Message m = new Message( (short) 4, (short) 0 );
            m.putBoolean(true);
            sHandler.sendMessage(m);
        }
    }

    public void keyReleased(KeyEvent ke) {
        super.keyReleased(ke);

        if(sHandler == null) return;

        if(ke.getKeyCode() == KeyEvent.VK_W) {
            Message m = new Message( (short) 1, (short) 0 );
            m.putBoolean(false);
            sHandler.sendMessage(m);
        }

        if(ke.getKeyCode() == KeyEvent.VK_S) {
            Message m = new Message( (short) 2, (short) 0 );
            m.putBoolean(false);
            sHandler.sendMessage(m);
        }

        if(ke.getKeyCode() == KeyEvent.VK_A) {
            Message m = new Message( (short) 3, (short) 0 );
            m.putBoolean(false);
            sHandler.sendMessage(m);
        }

        if(ke.getKeyCode() == KeyEvent.VK_D) {
            Message m = new Message( (short) 4, (short) 0 );
            m.putBoolean(false);
            sHandler.sendMessage(m);
        }
    }

    public void inputBytes(byte[] b) {
        super.inputBytes(b);
        
    }
    
    public void parseMessage(Message m) {
        if(m.getType() == 0) {
            out("[R] " + m.getString());
        }

        if(m.getType() == 2) {
            Player p = new Player(null);

            p.id = m.getId();

            players.add(p);
        }

        if(m.getType() == 3) {

            for(Player p : players) {

                if(m.getId() == p.id) {
                    p.x = m.readShort();
                    p.y = m.readShort();

                    break;
                }

            }

        }

        if(m.getType() == 4) {
            for(Player p : players) {
                if(m.getId() == p.id) players.remove(p);
            }
        }
    }

    public void tick() {
        //if(sHandler == null && chatOpen) typing = true;
        super.tick();

        Point p = mouseHelper.getPoint();

        mouseX = p.getX();
        mouseY = p.getY();

        registry.get("TPS").setValue("50");
        registry.get("FPS").setValue("100");

        if(sHandler == null) {
            typing = true;
            chatOpen = true;
        }

        if(sHandler != null && sHandler.isConnected() == false) {
            out("Lost Connection");
            sHandler = null;
        }

        chatBox.x = 10;
        chatBox.y = canvas.HEIGHT - 50;
        chatBox.width = (int) ( ( (double) canvas.WIDTH ) * 0.4 );
        //chatBox.height = 300;
        chatBox.height = (canvas.HEIGHT - 100 );

        chatBox.entered = message;
    }

    public void render() {
        canvas.clear();

        if(chatOpen) chatBox.render(canvas);

        canvas.offset = false;

        //Renders that don't use offset
        if(typing && chatOpen) canvas.drawSquare(chatBox.x, chatBox.y - 14, chatBox.x + 2, chatBox.y + 10, 255, 0, 0 );

        canvas.offset = true;

        canvas.drawCircle( mouseX, mouseY, 255, 255, 255, 10 );

        for(Player p : players) {
            canvas.drawCircle(p.x, p.y, 255, 0, 0, 5 );
        }

        canvas.render();
    }

    public void draw(Graphics g) {
        if(chatOpen) chatBox.draw(g);

        g.drawString("TPS: " + ticks, 15, 15);
        g.drawString("FPS: " + frames, 15, 30 );

        Point p = mouseHelper.getPoint();

        g.drawString("MouseX: " + p.getX(), 100, 15 );
        g.drawString("MouseY: " + p.getY(), 100, 30 );
    }
}