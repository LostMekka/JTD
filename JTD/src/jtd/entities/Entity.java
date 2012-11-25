/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jtd.entities;

import java.util.LinkedList;
import java.util.Random;
import jtd.PointF;
import jtd.TDGameplayState;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.state.StateBasedGame;

/**
 *
 * @author LostMekka
 */
public abstract class Entity {
	
	public static final Random random = new Random();
	
	public PointF loc;
	public float rotation = 0, scale = 1;
	public int currSprite = 0;
	public Image[] sprites;
	private LinkedList<KillListener> killListeners = new LinkedList<>();

	public Entity(PointF loc, int spriteCount) {
		this.loc = loc;
		sprites = new Image[spriteCount];
	}
	
	public final boolean addKillListener(KillListener l){
		if(!killListeners.contains(l)){
			killListeners.add(l);
			return true;
		}
		return false;
	}
	
	public final boolean removeKillListener(KillListener l){
		return killListeners.remove(l);
	}
	
	public final void kill(Entity killer){
		TDGameplayState.get().EntityKilled(this, killer);
		for(KillListener l:killListeners) l.EntityKilled(this, killer);
	}
	
	public final void rotate(float amount){
		while(amount < 0) amount += 2f * (float)(Math.PI);
		rotation = (rotation + amount) % (float)(2d * Math.PI);
	}
	
	public final void draw(GameContainer gc, StateBasedGame sbg, Graphics grphcs){
		if((currSprite >= 0) && (currSprite < sprites.length)){
			if(sprites[currSprite] != null){
				sprites[currSprite].setRotation(rotation);
				sprites[currSprite].draw(loc.x, loc.y, scale);
			}
		}
		entityDraw(gc, sbg, grphcs);
	}
	
	public void entityDraw(GameContainer gc, StateBasedGame sbg, Graphics grphcs){}
	
	public abstract void tick(float time);
	
}
