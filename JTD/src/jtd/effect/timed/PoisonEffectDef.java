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
	
	public float damage;

	public PoisonEffectDef(float damage, float duration, float cooldown) {
		super(duration, cooldown);
		this.damage = damage;
	}

	@Override
	public void tick(float time, Mob mob, Entity caster) {
		mob.damage(time * damage, caster);
	}
	
}
