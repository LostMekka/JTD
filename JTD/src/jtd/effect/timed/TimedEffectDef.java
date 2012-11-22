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
	
	public float duration, cooldown;

	public TimedEffectDef(float duration, float cooldown) {
		this.duration = duration;
		this.cooldown = cooldown;
	}
	
	public void apply(Mob mob, Entity caster){}
	public void remove(Mob mob, Entity caster){}
	public void tick(float time, Mob mob, Entity caster){}
	
}
