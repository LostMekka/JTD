/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jtd;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import jtd.def.TowerDef;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

/**
 *
 * @author LostMekka
 */
public final class PlayerAction {
	
	public enum Type{
		sell, buy, upgrade
	}
	
	public Type type;
	public TowerDef def;
	public Image icon;
	
	private static Image defaultIcon = AssetLoader.getImage("defaultIcon.png");
	private static Image sellIcon = AssetLoader.getImage("sellIcon.png");
	private static HashMap<TowerDef, Image> icons = new HashMap<>();

	public PlayerAction(Type type) {
		this.type = type;
		def = null;
		if(type == Type.sell){
			icon = sellIcon;
		} else {
			icon = defaultIcon;
		}
	}

	public PlayerAction(TowerDef def) {
		type = Type.buy;
		this.def = def;
		if(def == null){
			icon = defaultIcon;
		} else {
			if(icons.containsKey(def)){
				icon = icons.get(def);
			} else {
				icon = generateIcon(def);
				icons.put(def, icon);
			}
		}
	}
	
	private Image generateIcon(TowerDef def){
		Image body = def.baseIdleAnimations[0].images[0];
		Image head = null;
		if(def.headIdleAnimations != null) head = def.headIdleAnimations[0].images[0];
		int w = Math.max(body.getWidth(), head.getWidth());
		int h = Math.max(body.getHeight(), head.getHeight());
		int s = Math.max(w, h);
		Image ans = null;
		try {
			ans = new Image(s, s);
			Graphics g = ans.getGraphics();
			Graphics.setCurrent(g);
			g.setBackground(Color.gray);
			g.clear();
			body.setRotation(0f);
			body.drawCentered(s/2, s/2);
			if(head != null){
				head.setRotation(0f);
				head.drawCentered(s/2, s/2);
			}
			g.flush();
		} catch (SlickException ex) {
			Logger.getLogger(PlayerAction.class.getName()).log(Level.SEVERE, null, ex);
		}
		return ans;
	}
	
}
