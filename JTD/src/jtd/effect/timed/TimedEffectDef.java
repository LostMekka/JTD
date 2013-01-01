/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jtd.effect.timed;

import jtd.entities.Entity;
import jtd.entities.Mob;

/**
 *
 * @author LostMekka
 */
public abstract class TimedEffectDef {
	
	public double duration, cooldown;

	public TimedEffectDef(double duration, double cooldown) {
		this.duration = duration;
		this.cooldown = cooldown;
	}
	
	@Override
	public String toString() {
		return getString() + "(" + duration + "s)";
	}
	
	public void apply(Mob mob, Entity caster){}
	public void remove(Mob mob, Entity caster){}
	public void tick(double time, Mob mob, Entity caster){}
	public abstract String getString();
	
}
