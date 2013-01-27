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
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.state.StateBasedGame;

/**
 *
 * @author LostMekka
 */
public abstract class Entity {
	
	public static final Random RANDOM = new Random();
	
	public PointD loc;
	public double rotation = 0f, sizeMultiplier = 1f, spriteAlpha = 1f;
	public Image currSprite = null;
	
	private LinkedList<KillListener> killListeners = new LinkedList<>();
	private boolean initialTick = true;

	public Entity(PointD loc) {
		this.loc = loc;
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
		return loc.getPointI(getSize());
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
		if(currSprite != null){
			currSprite.setAlpha((float)spriteAlpha);
			transformator.drawImage(currSprite, loc, getSize() * sizeMultiplier, rotation);
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
	public int getSize(){return 1;}
		
}
