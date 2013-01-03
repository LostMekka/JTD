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
	
	public double speedup;
	
	public SlowEffectDef(double speedup, double duration, double cooldown) {
		super(duration, cooldown);
		this.speedup = speedup;
	}

	@Override
	public void apply(Mob mob, Entity caster) {
		mob.speedMultiplier *= speedup;
		mob.animationSpeed *= speedup;
	}

	@Override
	public void remove(Mob mob, Entity caster) {
		mob.speedMultiplier /= speedup;
		mob.animationSpeed /= speedup;
	}

	@Override
	public String getString() {
		return String.format("speedup by %1.2f", speedup);
	}

}
