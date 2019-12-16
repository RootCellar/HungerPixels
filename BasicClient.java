import java.awt.*;

import java.util.*;
import java.awt.event.*;

import PixelEngine.Client.*;
import PixelEngine.Screen.*;
import PixelEngine.Network.*;
import PixelEngine.Util.*;
import PixelEngine.Game.*;

public class BasicClient extends Client
{
    public static void main(String[] args) {
        new BasicClient().start();
    }

    double mouseX = 0;
    double mouseY = 0;

    public ArrayList<HungerPlayer> players = new ArrayList<HungerPlayer>();
    public ArrayList<Projectile> projectiles = new ArrayList<Projectile>();
    public HungerPlayer self;

    StatusInfo status = new StatusInfo();

    public BasicClient() {
        super();

        MessageTypes.add(HungerNet.values());
    }

    public void setup() {
        super.setup();

        registry.get("TPS").setValue("50");
    }

    public void keyPressed(KeyEvent ke) {
        super.keyPressed(ke);

        if(sHandler == null) return;

        if(ke.getKeyCode() == KeyEvent.VK_W) {
            Message m = new Message( (short) GameNetMessage.KEY_W.getId(), (short) 0 );
            m.putBoolean(true);
            sHandler.sendMessage(m);
        }

        if(ke.getKeyCode() == KeyEvent.VK_S) {
            Message m = new Message( (short) GameNetMessage.KEY_S.getId(), (short) 0 );
            m.putBoolean(true);
            sHandler.sendMessage(m);
        }

        if(ke.getKeyCode() == KeyEvent.VK_A) {
            Message m = new Message( (short) GameNetMessage.KEY_A.getId(), (short) 0 );
            m.putBoolean(true);
            sHandler.sendMessage(m);
        }

        if(ke.getKeyCode() == KeyEvent.VK_D) {
            Message m = new Message( (short) GameNetMessage.KEY_D.getId(), (short) 0 );
            m.putBoolean(true);
            sHandler.sendMessage(m);
        }

        if(ke.getKeyCode() == KeyEvent.VK_SPACE) {
            Message m = new Message( (short) GameNetMessage.KEY_SPACE.getId(), (short) 0 );
            m.putBoolean(true);
            sHandler.sendMessage(m);
        }
    }

    public void keyReleased(KeyEvent ke) {
        super.keyReleased(ke);

        if(sHandler == null) return;

        if(ke.getKeyCode() == KeyEvent.VK_W) {
            Message m = new Message( (short) GameNetMessage.KEY_W.getId(), (short) 0 );
            m.putBoolean(false);
            sHandler.sendMessage(m);
        }

        if(ke.getKeyCode() == KeyEvent.VK_S) {
            Message m = new Message( (short) GameNetMessage.KEY_S.getId(), (short) 0 );
            m.putBoolean(false);
            sHandler.sendMessage(m);
        }

        if(ke.getKeyCode() == KeyEvent.VK_A) {
            Message m = new Message( (short) GameNetMessage.KEY_A.getId(), (short) 0 );
            m.putBoolean(false);
            sHandler.sendMessage(m);
        }

        if(ke.getKeyCode() == KeyEvent.VK_D) {
            Message m = new Message( (short) GameNetMessage.KEY_D.getId(), (short) 0 );
            m.putBoolean(false);
            sHandler.sendMessage(m);
        }

        if(ke.getKeyCode() == KeyEvent.VK_SPACE) {
            Message m = new Message( (short) GameNetMessage.KEY_SPACE.getId(), (short) 0 );
            m.putBoolean(false);
            sHandler.sendMessage(m);
        }
    }

    public void inputBytes(byte[] b) {
        super.inputBytes(b);
    }

    public void parseMessage(Message m) {
        if(m.getType() == MessageTypes.getId("PROJECTILE_SPAWN")) {
            Projectile p = new Projectile(null);
            p.id = m.getId();
            projectiles.add(p);
        }

        if(m.getType() == MessageTypes.getId("PROJECTILE_DESPAWN")) {
            short toRem = m.getId();
            for(int i=0; i<projectiles.size(); i++) {
                if(projectiles.get(i).id == toRem) projectiles.remove(i);
                i--;
            }
        }
        
        if(m.getType() == MessageTypes.getId("PROJECTILE_MOVE")) {
            short projId = m.getId();
            for(Projectile p: projectiles) {
                if(p.id == projId) {
                    p.x = m.readShort();
                    p.y = m.readShort();
                }
            }
        }

        if(m.getType() == GameNetMessage.PING_CHECK.getId()) {
            sHandler.sendMessage( new Message((short) GameNetMessage.PING_CHECK.getId(), (short) 0 ) );
        }

        if(m.getType() == GameNetMessage.CHAT.getId()) {
            out("[R] " + m.getString());
        }

        if(m.getType() == GameNetMessage.MOB_SPAWN.getId() ) {
            HungerPlayer p = new HungerPlayer(null);

            p.id = m.getId();

            players.add(p);
        }

        if(m.getType() == GameNetMessage.MOB_POS.getId() ) {

            for(HungerPlayer p : players) {

                if(m.getId() == p.id) {
                    p.x = m.readShort();
                    p.y = m.readShort();

                    break;
                }

            }

        }

        if(m.getType() == GameNetMessage.MOB_REMOVE.getId() ) {
            /*
            for(HungerPlayer p : players) {
            if(m.getId() == p.id) players.remove(p);
            }
             */
            for(int i=0; i<players.size(); i++) {
                HungerPlayer p = players.get(i);
                if(m.getId() == p.id) { players.remove(i); i--; }
            }
        }

        if( m.getType() == GameNetMessage.SELF_SET.getId() ) {

            self = null;

            for(HungerPlayer p : players) {

                if(p.id == m.getId() ) {
                    self = p;
                    break;
                }

            }

        }

        if( m.getType() == GameNetMessage.MOB_HP.getId() ) {
            for(HungerPlayer p : players) {
                if(m.getId() == p.id) p.hp = m.readShort();
            }
        }

        if( m.getType() == GameNetMessage.MOB_HUNGER.getId() ) {
            for(HungerPlayer p : players) {
                if(m.getId() == p.id) p.hunger = m.readShort();
            }
        }

        if( m.getType() == GameNetMessage.MOB_THIRST.getId() ) {
            for(HungerPlayer p : players) {
                if(m.getId() == p.id) p.thirst = m.readShort();
            }
        }

    }

    public void tick() {
        //if(sHandler == null && chatOpen) typing = true;
        super.tick();

        if(self != null) status.setMessage( self.x + ", " + self.y );
        else status.setMessage("");

        Point p = mouseHelper.getPoint();

        mouseX = p.getX();
        mouseY = p.getY();

        //registry.get("TPS").setValue("50");
        //registry.get("FPS").setValue("100");

        if(input.k.wasDown() && !typing) registry.get("FPS").setValue("10000");
        if(input.j.wasDown() && !typing) registry.get("FPS").setValue("100");

        if(sHandler == null) {
            typing = true;
            chatOpen = true;
        }

        if(sHandler != null && sHandler.isConnected() == false) {
            out("Lost Connection");
            sHandler = null;

            self = null;
            while(players.size() > 0) players.remove(0);
            while(projectiles.size() > 0 ) projectiles.remove(0);
            while(messages.size() > 0) messages.poll();
        }
        
        if(sHandler != null) sHandler.setWaitTime(0);

        chatBox.x = 10;
        chatBox.y = canvas.HEIGHT - 50;
        chatBox.width = (int) ( ( (double) canvas.WIDTH ) * 0.4 );
        //chatBox.height = 300;
        chatBox.height = (canvas.HEIGHT - 100 );

        chatBox.entered = message;
    }

    public void render() {
        long startClear = System.nanoTime();
        canvas.clear();
        long end = System.nanoTime();

        long time = end - startClear;

        registry.add( new Property("TIME_CLEAR", time + "") );

        if(self != null) {
            canvas.xo = self.x;
            canvas.yo = self.y;
        }

        if(chatOpen) chatBox.render(canvas);

        canvas.offset = false;

        int SPACING = 100;

        if(self != null) {
            int farLeft = (int) (-3000);
            int top = (int) (-3000);

            int farRight = (int) (3000);
            int bottom = (int) (3000);

            while(farLeft % SPACING != 0) farLeft ++;
            while(top % SPACING != 0) top ++;

            while(farRight % SPACING != 0) farRight --;
            while(bottom % SPACING != 0) bottom --;

            for(int i = farLeft; i < farRight; i += SPACING) {
                for(int k = top; k < bottom; k += SPACING) {
                    canvas.drawPixel(i - (self.x % SPACING), k - (self.y % SPACING), 255, 255, 255);
                }
            }
        }

        //Renders that don't use offset
        if(typing && chatOpen) canvas.drawSquare(chatBox.x, chatBox.y - 14, chatBox.x + 2, chatBox.y + 10, 255, 0, 0 );

        canvas.offset = true;

        canvas.drawCircle( mouseX, mouseY, 255, 255, 255, 10 );

        //canvas.drawCircle( mouseX, mouseY, 255, 255, 255, 10 );

        for(HungerPlayer p : players) {
            //canvas.drawCircle(p.x, p.y, 255, 0, 0, 5 );
            p.doLifeBar();
            p.render(canvas);
        }
        
        for(Projectile p : projectiles) {
            p.render(canvas);
        }

        registry.add( new Property("CANVAS", canvas.SET_TIMES + "" ) );

        canvas.render();
    }

    public void draw(Graphics g) {
        if(chatOpen) chatBox.draw(g);

        status.setPos(canvas.WIDTH - 100, 15);

        status.draw(g);

        g.setColor(Color.GREEN);

        for(int i = 0; i < registry.getProperties().size(); i++) {
            Property p = registry.getProperties().get(i);

            g.drawString( p.getName() + ": " + p.getValue() , 250, 15 + (i*15) );
        }

        g.drawString("TPS: " + ticks, 15, 15);
        g.drawString("FPS: " + frames, 15, 30 );

        Point p = mouseHelper.getPoint();
        g.drawString("MouseX: " + p.getX(), 100, 15 );
        g.drawString("MouseY: " + p.getY(), 100, 30 );

        if(self != null) {
            g.drawString("HP : " + self.hp + " / " + self.maxHp, 100, 45);
            g.drawString("HUNGER : " + self.hunger + " / " + self.maxHunger, 100, 60);
            g.drawString("THIRST : " + self.thirst + " / " + self.maxThirst, 100, 75);
        }
    }
}