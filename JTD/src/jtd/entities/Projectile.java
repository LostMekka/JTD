/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jtd.entities;

import java.util.LinkedList;
import jtd.PointF;
import jtd.effect.instant.InstantEffect;
import jtd.effect.timed.TimedEffect;

/**
 *
 * @author LostMekka
 */
public class Projectile extends Entity{

	public ProjectileDef def;
	public Mob target;
	public Tower attacker;
	public LinkedList<InstantEffect> instantEffects;
	public LinkedList<TimedEffect> timedEffects;
	public float lifeTime;

	public Projectile(ProjectileDef def, Mob target, Tower attacker, LinkedList<InstantEffect> instantEffects, LinkedList<TimedEffect> timedEffects, PointF loc) {
		super(loc);
		this.def= def;
		this.target = target;
		this.attacker = attacker;
		this.instantEffects = instantEffects;
		this.timedEffects = timedEffects;
	}
	
	@Override
	public void tick(float time) {
		
	}
	
}
