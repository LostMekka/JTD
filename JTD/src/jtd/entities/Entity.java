/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jtd.entities;

import java.util.LinkedList;
import java.util.Random;
import jtd.CoordinateTransformator;
import jtd.GameCtrl;
import jtd.KillListener;
import jtd.PointD;
import jtd.PointI;
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
	
	public static final Random RANDOM = new Random();
	
	public PointD loc;
	public double rotation = 0f, sizeMultiplier = 1f, spriteAlpha = 1f;
	public int currSprite = 0, entitySize = 1;
	public EntityDef def;
	
	private LinkedList<KillListener> killListeners = new LinkedList<>();
	private boolean initialTick = true;

	public Entity(PointD loc, EntityDef def) {
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
	
	public PointI getPointI(){
		return loc.getPointI(entitySize);
	}
	
	public final void kill(Entity killer){
		GameCtrl.get().EntityKilled(this, killer);
		for(KillListener l:killListeners) l.EntityKilled(this, killer);
	}
	
	public final void rotate(double amount){
		while(amount < 0) amount += 360f;
		rotation = (rotation + amount) % 360f;
	}
	
	public final void draw(
			GameContainer gc, StateBasedGame sbg, 
			Graphics grphcs, CoordinateTransformator transformator){
		if(!GameCtrl.get().isVisible(this)) return;
		if((currSprite >= 0) && (currSprite < def.sprites.length)){
			def.sprites[currSprite].setAlpha((float)spriteAlpha);
			transformator.drawImage(def.sprites[currSprite], loc, 
					def.sizes[currSprite] * sizeMultiplier * (double)entitySize, 
					rotation);
		}
		entityDraw(gc, sbg, grphcs, transformator);
	}
	
	public final void tick(double time){
		entityTick(time);
		if(initialTick){
			initialTick = false;
			entityInitialTick();
		}
	}
	
	public void entityTick(double time){}
	public void entityInitialTick(){}
	public void entityDraw(
			GameContainer gc, StateBasedGame sbg, 
			Graphics grphcs, CoordinateTransformator transformator){}
		
}
