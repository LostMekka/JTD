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
public class BonusDamageEffect extends TimedEffectDef{
	
	public double bonusDamage;

	public BonusDamageEffect(double bonusDamage, double duration, double cooldown) {
		super(duration, cooldown);
		this.bonusDamage = bonusDamage;
	}

	@Override
	public void apply(Mob mob, Entity caster) {
		mob.bonusDamage += bonusDamage;
	}

	@Override
	public void remove(Mob mob, Entity caster) {
		mob.bonusDamage -= bonusDamage;
	}

	@Override
	public String getString() {
		return "" + bonusDamage + "bonus damage";
	}

}
