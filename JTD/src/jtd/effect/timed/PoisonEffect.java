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
public class PoisonEffect<DefType extends PoisonEffect.PoisonEffectDef> extends AbstractTimedEffect<DefType>{
	
	public class PoisonEffectDef extends TimedEffectDef{
		public float damage;
		public PoisonEffectDef(float damage, float duration, float cooldown) {
			super(duration, cooldown);
			this.damage = damage;
		}
	}

	public PoisonEffect(Entity caster, DefType def) {
		super(caster, def);
	}

	@Override
	public void effectTick(float time, Mob mob) {
		mob.damage(time * def.damage, caster);
	}
	
}
