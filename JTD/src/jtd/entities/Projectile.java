/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jtd.entities;

import java.util.LinkedList;
import jtd.GameCtrl;
import jtd.KillListener;
import jtd.PointD;
import jtd.TDGameplayState;
import jtd.def.ProjectileDef;
import jtd.effect.instant.InstantEffect;
import jtd.effect.timed.TimedEffectDef;

/**
 *
 * @author LostMekka
 */
public class Projectile extends AnimatedEntity implements KillListener{

	public ProjectileDef def;
	public Mob target;
	public PointD targetLoc;
	public Tower attacker;
	public LinkedList<InstantEffect> instantEffects;
	public LinkedList<TimedEffectDef> timedEffects;
	
	private double lifeTime = 0f;
	private double[] particleCooldowns;

	public Projectile(
			ProjectileDef def, Mob target, Tower attacker, 
			LinkedList<InstantEffect> instantEffects, 
			LinkedList<TimedEffectDef> timedEffects, 
			PointD loc) {
		super(loc);
		this.def = def;
		target.addKillListener(this);
		if(def.isHoming){
			this.target = target;
			targetLoc = target.loc;
		} else {
			this.target = null;
			targetLoc = attacker.loc.clone();
			targetLoc.travelTo(target.loc, attacker.def.range, true);
		}
		this.attacker = attacker;
		this.instantEffects = instantEffects;
		this.timedEffects = timedEffects;
		setAnimationSet(def.animations, true);
		rotation = attacker.loc.getRotationTo(target.loc);
		particleCooldowns = new double[this.def.particleFactories.length];
		System.arraycopy(this.def.particleCooldowns, 0, particleCooldowns, 0, particleCooldowns.length);
	}
	
	@Override
	public void animatedEntityTick(double time) {
		// tick lifetime, kill if expired
		lifeTime += time;
		if(lifeTime >= def.lifeTime){
			kill(null);
			return;
		}
		// move to target
		loc.travelTo(targetLoc, time * def.speed, true);
		// if target is reached, deal damage and kill self
		if(loc.hammingDistanceTo(targetLoc) < 0.1f){
			if(attacker.def.damageRadius > 0){
				GameCtrl.get().dealAreaDamage(targetLoc, attacker, instantEffects, timedEffects);
			} else {
				if(target != null){
					GameCtrl.get().dealDamage(target, attacker, instantEffects, timedEffects, rotation);
				}
			}
			kill(null);
		} else {
			rotation = loc.getRotationTo(targetLoc);
		}
		// emit particles
		for(int i=0; i<particleCooldowns.length; i++){
			particleCooldowns[i] -= time;
			while(particleCooldowns[i] <= 0){
				particleCooldowns[i] += def.particleCooldowns[i];
				GameCtrl.get().addParticle(def.particleFactories[i], loc.clone(), rotation + 180f);
			}
		}
	}

	@Override
	public void EntityKilled(Entity entity, Entity killer) {
		if(entity == target){
			target = null;
		}
	}
	
}
