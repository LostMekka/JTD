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
public class SlowEffect<DefType extends SlowEffect.SlowEffectDef> extends AbstractTimedEffect<DefType>{
	
	public class SlowEffectDef extends TimedEffectDef{
		public float speedup;
		public SlowEffectDef(float damage, float duration, float speedup) {
			super(duration, speedup);
			this.speedup = damage;
		}
	}

	public SlowEffect(Entity caster, DefType def) {
		super(caster, def);
	}

	@Override
	public void apply(Mob mob) {
		mob.speed *= def.speedup;
	}

	@Override
	public void remove(Mob mob) {
		mob.speed /= def.speedup;
	}

}
