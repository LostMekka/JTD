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
public abstract class InstantEffect {
	
	public double cooldown;

	public InstantEffect(double cooldown) {
		this.cooldown = cooldown;
	}

	@Override
	public String toString() {
		return getString();
	}
	
	public abstract void apply(Mob mob);
	protected abstract String getString();
	
}
