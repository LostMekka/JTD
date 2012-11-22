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
public class SlowEffectDef extends TimedEffectDef{
	
	public float speedup;
	
	public SlowEffectDef(float speedup, float duration, float cooldown) {
		super(duration, cooldown);
		this.speedup = speedup;
	}

	@Override
	public void apply(Mob mob, Entity caster) {
		mob.speed *= speedup;
	}

	@Override
	public void remove(Mob mob, Entity caster) {
		mob.speed /= speedup;
	}

}
