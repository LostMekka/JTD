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
public abstract class AbstractInstantEffect {
	
	public float cooldown;

	public AbstractInstantEffect(float cooldown) {
		this.cooldown = cooldown;
	}
	
	public abstract void apply(Mob mob);
	
}
