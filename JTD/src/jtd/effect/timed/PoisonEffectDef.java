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
public class PoisonEffectDef extends TimedEffectDef{
	
	public double damage;

	public PoisonEffectDef(double damage, double duration, double cooldown) {
		super(duration, cooldown);
		this.damage = damage;
	}

	@Override
	public void tick(double time, Mob mob, Entity caster) {
		mob.damage(time * damage, caster);
	}
	
	@Override
	public String getString() {
		return String.format("%1.2f poison dmg", damage);
	}

}
