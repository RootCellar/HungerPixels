package PixelEngine.Game;

import PixelEngine.Input.*;

import java.awt.*;

public class EngineAboutMenu extends Menu
{
    public EngineAboutMenu(PixelEngine e) {
        super(e);
    }
    
    public void tick() {
        InputListener in = engine.getInput();
        
        if(in.space.wasDown()) {
            if(choice == 0) engine.setMenu( new Menu(engine) );
        }
    }
    
    public void draw(Graphics g) {
        
        g.drawString("Welcome!", 50, 80);
        g.drawString("This game was created with PixelEngine", 50, 100);
        g.drawString("The engine was created by Darian Marvel", 50, 120);
        g.drawString("Development Started: 10/3/18", 50, 140);
        
        
        g.drawString("Space to exit this menu", 50, 200);
    }
}