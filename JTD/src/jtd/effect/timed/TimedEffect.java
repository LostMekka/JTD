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
public final class TimedEffect{
	
	public double timeLeft;
	public Entity caster;
	public TimedEffectDef def;

	public TimedEffect(Entity caster, TimedEffectDef def) {
		this.caster = caster;
		this.def = def;
		timeLeft = def.duration;
	}
		
	public void apply(Mob mob){
		def.apply(mob, caster);
	}
	
	public void remove(Mob mob){
		def.remove(mob, caster);
	}
	
	public final void tick(double time, Mob mob){
		timeLeft -= time;
		if(timeLeft <= 0){
			mob.removeTimedEffect(this);
		} else {
			def.tick(time, mob, caster);
		}
	}
	
}
