/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jtd.entities;

import java.util.LinkedList;
import java.util.Random;
import jtd.CoordinateTransformator;
import jtd.KillListener;
import jtd.PointF;
import jtd.TDGameplayState;
import jtd.def.EntityDef;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.StateBasedGame;

/**
 *
 * @author LostMekka
 */
public abstract class Entity {
	
	public static final TDGameplayState GAME = TDGameplayState.get();	
	public static final Random RANDOM = new Random();
	
	public PointF loc;
	public float rotation = 0f, sizeMultiplier = 1f, spriteAlpha = 1f;
	public int currSprite = 0;
	public EntityDef def;
	
	private LinkedList<KillListener> killListeners = new LinkedList<>();
	private boolean initialTick = true;

	public Entity(PointF loc, EntityDef def) {
		this.loc = loc;
		this.def = def;
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
		while(amount < 0) amount += 360f;
		rotation = (rotation + amount) % 360f;
	}
	
	public final void draw(
			GameContainer gc, StateBasedGame sbg, 
			Graphics grphcs, CoordinateTransformator transformator){
		if((currSprite >= 0) && (currSprite < def.sprites.length)){
			def.sprites[currSprite].setAlpha(spriteAlpha);
			transformator.drawImage(def.sprites[currSprite], loc, def.sizes[currSprite] * sizeMultiplier, rotation);
		}
		entityDraw(gc, sbg, grphcs, transformator);
	}
	
	public final void tick(float time){
		entityTick(time);
		if(initialTick){
			initialTick = false;
			entityInitialTick();
		}
	}
	
	public void entityTick(float time){}
	public void entityInitialTick(){}
	public void entityDraw(
			GameContainer gc, StateBasedGame sbg, 
			Graphics grphcs, CoordinateTransformator transformator){}
		
}
