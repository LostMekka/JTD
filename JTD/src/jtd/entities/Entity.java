/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jtd.entities;

import java.util.LinkedList;
import jtd.PointF;

/**
 *
 * @author LostMekka
 */
public abstract class Entity {
	
	public PointF loc;
	private LinkedList<KillListener> killListeners = new LinkedList<>();

	public Entity(PointF loc) {
		this.loc = loc;
	}
	
	public boolean addKillListener(KillListener l){
		if(!killListeners.contains(l)){
			killListeners.add(l);
			return true;
		}
		return false;
	}
	
	public boolean removeKillListener(KillListener l){
		return killListeners.remove(l);
	}
	
	public void kill(Entity killer){
		for(KillListener l:killListeners) l.EntityKilled(this, killer);
	}
	
	public abstract void tick(float time);
	
}
