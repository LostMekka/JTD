/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jtd;

import java.util.logging.Level;
import java.util.logging.Logger;
import jtd.level.LevelDataHolder;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

/**
 *
 * @author LostMekka
 */
public class GameplayGui {
	
	public PointI tlCorner = null, brCorner = null;
	
	private static final int boxSize = 20;
	private Image guiImage = null, mapImage = null;
	private PointI mapLoc = null;
	
	private void draw(Graphics g, Image i, int x, int y){
		g.drawImage(i, x*8, y*8);
	}
	
	private void drawLine(Graphics g, SpriteSheet s, int y, int sy, int a){
		int x = 0;
		draw(g, s.getSubImage(0, sy), x++, y);
		
		if((sy > 0) && (sy < s.getVerticalCount()-1)){
			x += a;
		} else {
			Image image = s.getSprite(1, sy);
			for(int i=0; i<a; i++) draw(g, image, x++, y);
		}
		
		draw(g, s.getSprite(2, sy), x++, y);
		
		Image image = s.getSprite(3, sy);
		for(int j=0; j<boxSize; j++) draw(g,image, x++, y);
		
		draw(g, s.getSprite(4, sy), x++, y);
	}
	
	public void updateGuiImage(GameContainer gc){
		Image image = AssetLoader.getImage("gui.png");
		SpriteSheet guiSheet = new SpriteSheet(image, 8, 8);
		int w = gc.getWidth();
		int h = gc.getHeight();
		int wit = w / 8;
		int hit = h / 8;
		int a = wit - 3 - boxSize;
		int c = hit - 4 - 2 * boxSize;
		try {
			guiImage = new Image(w, h);
			Graphics g = guiImage.getGraphics();
			guiSheet.startUse();
			int y = 0;
			drawLine(g, guiSheet, y++, 0, a);
			for(int i=0; i<boxSize; i++) drawLine(g, guiSheet, y++, 1, a);
			drawLine(g, guiSheet, y++, 2, a);
			for(int i=0; i<c; i++) drawLine(g, guiSheet, y++, 1, a);
			drawLine(g, guiSheet, y++, 2, a);
			for(int i=0; i<boxSize; i++) drawLine(g, guiSheet, y++, 1, a);
			drawLine(g, guiSheet, y++, 4, a);
			guiSheet.endUse();
			g.flush();
		} catch (SlickException ex) {
			Logger.getLogger(GameplayGui.class.getName()).log(Level.SEVERE, null, ex);
		}
		tlCorner = new PointI(5, 5);
		brCorner = new PointI(w - (2 + boxSize) * 8 - 1, h - 6);
	}
	
	public void updateMapImage(LevelDataHolder l){
		
	}
	
	public void draw(Graphics g){
		if(guiImage == null) return;
		g.drawImage(guiImage, 0, 0);
		if((mapImage == null) || (mapLoc == null)) return;
		g.drawImage(mapImage, mapLoc.x, mapLoc.y);
	}
	
}
