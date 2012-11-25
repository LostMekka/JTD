/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jtd.effect.instant;

import jtd.entities.Mob;

/**
 *
 * @author LostMekka
 */
public class ArmorDrainEffect extends InstantEffect{

	public float amount;

	public ArmorDrainEffect(float amount, float cooldown) {
		super(cooldown);
		this.amount = amount;
	}

	@Override
	public void apply(Mob mob) {
		mob.armorOffset -= amount;
		if(mob.armorOffset < 0) mob.armorOffset = 0;
	}
	
}
