/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jtd.entities;

import jtd.Point;

/**
 *
 * @author LostMekka
 */
public class Tower extends Entity{

	public TowerDef def;
	private float cooldown;
	private float[] EffectCooldowns;
	private Mob target = null;

	public Tower(TowerDef def, Point loc) {
		super(loc);
		this.def = def;
		cooldown = def.shotCooldown;
	}
	
	@Override
	public void tick(float time) {
		
		
		// TODO: targeting
		Mob target = null;
		
	}
	
	
}
