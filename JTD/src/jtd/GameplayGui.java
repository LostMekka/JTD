/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jtd;

import com.sun.org.apache.bcel.internal.generic.AALOAD;
import java.util.logging.Level;
import java.util.logging.Logger;
import jtd.entities.Mob;
import jtd.entities.Tower;
import jtd.level.LevelDataHolder;
import org.newdawn.slick.Color;
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
	
	public static final float GUI_SCALE = 3f;
	public static final int BOX_SIZE = 8;
	
	public PointD tlCorner = null, brCorner = null;
	
	private Image guiImage = null, mapImage = null;
	private PointI mapLoc = null, mapLUCorner = null, infoTextLocation = null;
	private int fieldsPerPixel = 1, pixelsPerField = 1, textOffset = 0;
	
	private void draw(Graphics g, Image i, float x, float y, float ox, float oy){
		g.drawImage(i, x*8 + ox/GUI_SCALE, y*8 + oy/GUI_SCALE);
	}
	
	private void drawLine(Graphics g, SpriteSheet s, float y, int sy, float a, float ox, float oy){
		int x = 0;
		draw(g, s.getSubImage(0, sy), x++, y, 0, oy);
		
		if((sy > 0) && (sy < s.getVerticalCount()-1)){
			x += (int)a + 1;
		} else {
			Image image = s.getSprite(1, sy);
			for(int i=0; i<=a; i++) draw(g, image, x++, y, 0, oy);
		}
		
		draw(g, s.getSprite(2, sy), x++, y, ox, oy);
		
		Image image = s.getSprite(3, sy);
		for(int j=0; j<BOX_SIZE; j++) draw(g,image, x++, y, ox, oy);
		
		draw(g, s.getSprite(4, sy), x++, y, ox, oy);
	}
	
	public void updateGuiImage(GameContainer gc){
		Image image = AssetLoader.getImage("gui.png");
		SpriteSheet guiSheet = new SpriteSheet(image, 8, 8);
		int w = gc.getWidth();
		int h = gc.getHeight();
		float tileSize = 8 * GUI_SCALE;
		float wit = w / tileSize;
		float hit = h / tileSize;
		float a = wit - 3 - BOX_SIZE;
		float c = hit - 4 - 2 * BOX_SIZE;
		float ox = w % tileSize - tileSize;
		float oy = h % tileSize - tileSize;
		try {
			guiImage = new Image(w, h);
			Graphics g = guiImage.getGraphics();
			Graphics.setCurrent(g);
			g.scale(GUI_SCALE, GUI_SCALE);
			guiSheet.startUse();
			int y = 0;
			drawLine(g, guiSheet, y++, 0, a, ox, 0);
			for(int i=0; i<BOX_SIZE; i++) drawLine(g, guiSheet, y++, 1, a, ox, 0);
			drawLine(g, guiSheet, y++, 2, a, ox, 0);
			for(int i=0; i<=c; i++) drawLine(g, guiSheet, y++, 1, a, ox, 0);
			drawLine(g, guiSheet, y++, 2, a, ox, oy);
			for(int i=0; i<BOX_SIZE; i++) drawLine(g, guiSheet, y++, 1, a, ox, oy);
			drawLine(g, guiSheet, y++, 4, a, ox, oy);
			guiSheet.endUse();
			g.flush();
		} catch (SlickException ex) {
			Logger.getLogger(GameplayGui.class.getName()).log(Level.SEVERE, null, ex);
		}
		float border = 5 * GUI_SCALE;
		tlCorner = new PointD(border + 1, border + 1);
		brCorner = new PointD(w - (2 + BOX_SIZE) * tileSize, h - border);
		mapLoc = new PointI((int)((a + 2f) * tileSize), (int)(tileSize * 0.85f));
		infoTextLocation = new PointI((int)((a + 2f) * tileSize), (int)((BOX_SIZE + 2f) * tileSize));
	}
	
	public void updateMapImage(LevelDataHolder l){
		int s = (int)(BOX_SIZE * 8f * GUI_SCALE);
		int maxSize = l.w;
		if(l.w < l.h) maxSize = l.h;
		fieldsPerPixel = maxSize / (s + 1) + 1;
		pixelsPerField = s / (maxSize + 1) + 1;
		int xo = 0;
		int yo = 0;
		if(l.h > l.w) xo = s/2 - s*l.w/(2*l.h);
		if(l.h < l.w) yo = s/2 - s*l.h/(2*l.w);
		mapLUCorner = new PointI(mapLoc.x + xo, mapLoc.y + yo);
		try {
			if(mapImage == null) mapImage = new Image(s, s);
			Graphics g = mapImage.getGraphics();
			Graphics.setCurrent(g);
			g.setBackground(Color.black);
			g.clear();
			// draw terrain
			for(int x=0; x<l.w; x+=fieldsPerPixel){
				for(int y=0; y<l.h; y+=fieldsPerPixel){
					g.setColor(l.getTileColor(x, y, fieldsPerPixel));
					g.fillRect(
							x * pixelsPerField / fieldsPerPixel + xo, 
							y * pixelsPerField / fieldsPerPixel + yo, 
							pixelsPerField, pixelsPerField);
				}
			}
			// draw towers
			g.setColor(new Color(0.3f, 1f, 0.3f, 1f));
			for(Tower t:l.towers){
				PointI p = t.getPointI();
				float n = t.def.size * pixelsPerField / fieldsPerPixel;
				g.fillRect(p.x * pixelsPerField / fieldsPerPixel + xo, 
						p.y * pixelsPerField / fieldsPerPixel + yo, 
						n, n);
			}
			// draw enemies
			g.setColor(new Color(0.8f, 0f, 0f, 0.5f));
			for(Mob m:l.mobs){
				PointI p = m.getPointI();
				float n = m.def.size * pixelsPerField / fieldsPerPixel;
				g.fillRect(p.x * pixelsPerField / fieldsPerPixel + xo, 
						p.y * pixelsPerField / fieldsPerPixel + yo, 
						n, n);
			}
			// TODO: draw map
			g.flush();
		} catch (SlickException ex) {
			Logger.getLogger(GameplayGui.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	public double getWindowWidth(){
		return brCorner.x - tlCorner.x;
	}
	
	public double getWindowHeight(){
		return brCorner.y - tlCorner.y;
	}
	
	public double getWindowCenterX(){
		return (brCorner.x + tlCorner.x) / 2f;
	}
	
	public double getWindowCenterY(){
		return (brCorner.y + tlCorner.y) / 2f;
	}
	
	public void draw(Graphics g, TDGameplayState game){
		if(guiImage == null) return;
		g.drawImage(guiImage, 0, 0);
		if((mapImage == null) || (mapLoc == null)) return;
		g.drawImage(mapImage, mapLoc.x, mapLoc.y);
		PointD p1 = game.transformPointBack(tlCorner.x, tlCorner.y);
		PointD p2 = game.transformPointBack(brCorner.x, brCorner.y);
		double x = mapLUCorner.x + (p1.x + 0.5d) * pixelsPerField / fieldsPerPixel;
		double y = mapLUCorner.y + (p1.y + 0.5d) * pixelsPerField / fieldsPerPixel;
		double w = (p2.x - p1.x - 0.1d) * pixelsPerField / fieldsPerPixel;
		double h = (p2.y - p1.y - 0.1d) * pixelsPerField / fieldsPerPixel;
		g.setColor(new Color(1f, 1f, 1f, 0.8f));
		g.drawRect((float)x, (float)y, (float)w, (float)h);
		textOffset = 0;
	}
	
	public void drawInfoText(Graphics g, String s, int lineHeight){
		g.drawString(s, infoTextLocation.x, infoTextLocation.y + textOffset);
		textOffset += lineHeight;
	}
	
}
