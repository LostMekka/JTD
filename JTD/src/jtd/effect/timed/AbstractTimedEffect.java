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
public abstract class AbstractTimedEffect<DefClass extends AbstractTimedEffect.TimedEffectDef>{
	
	public abstract class TimedEffectDef {
		public float duration, cooldown;
		public TimedEffectDef(float duration, float cooldown) {
			this.duration = duration;
			this.cooldown = cooldown;
		}
	}

	public float timeLeft;
	public Entity caster;
	public DefClass def;

	public AbstractTimedEffect(Entity caster, DefClass def) {
		this.caster = caster;
		this.def = def;
		timeLeft = def.duration;
	}
		
	public final void tick(float time, Mob mob){
		timeLeft -= time;
		if(timeLeft <= 0){
			mob.removeTimedEffect(this);
		} else {
			effectTick(time, mob);
		}
	}
	
	public void apply(Mob mob){}
	public void remove(Mob mob){}
	protected void effectTick(float time, Mob mob){}
	
}
