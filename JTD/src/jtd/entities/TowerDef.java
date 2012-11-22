/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jtd.entities;

import jtd.effect.instant.InstantEffect;
import jtd.effect.timed.TimedEffectDef;

/**
 *
 * @author LostMekka
 */
public class TowerDef {
	
	public float range, damageRadius, reloadTime, damage;
	public TimedEffectDef[] timedEffects;
	public InstantEffect[] instantEffects;
	public ProjectileDef projectileDef;

	public TowerDef(float range, float damageRadius, float reloadTime, float damage, TimedEffectDef[] timedEffects, InstantEffect[] instantEffects, ProjectileDef projectileDef) {
		this.range = range;
		this.damageRadius = damageRadius;
		this.reloadTime = reloadTime;
		this.damage = damage;
		this.timedEffects = timedEffects;
		this.instantEffects = instantEffects;
		this.projectileDef = projectileDef;
	}
	
}
