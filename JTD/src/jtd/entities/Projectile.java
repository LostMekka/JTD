/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jtd.entities;

import java.util.LinkedList;
import jtd.KillListener;
import jtd.PointF;
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
	public PointF targetLoc;
	public Tower attacker;
	public LinkedList<InstantEffect> instantEffects;
	public LinkedList<TimedEffectDef> timedEffects;
	
	private float lifeTime = 0f;
	private float[] particleCooldowns;

	public Projectile(
			ProjectileDef def, Mob target, Tower attacker, 
			LinkedList<InstantEffect> instantEffects, 
			LinkedList<TimedEffectDef> timedEffects, 
			PointF loc) {
		super(loc, def);
		this.def = def;
		target.addKillListener(this);
		if(def.isHoming){
			this.target = target;
			targetLoc = target.loc;
		} else {
			this.target = null;
			targetLoc = target.loc.clone();
		}
		this.attacker = attacker;
		this.instantEffects = instantEffects;
		this.timedEffects = timedEffects;
		rotation = attacker.loc.getRotationTo(target.loc);
		particleCooldowns = new float[this.def.particleFactories.length];
		System.arraycopy(this.def.particleCooldowns, 0, particleCooldowns, 0, particleCooldowns.length);
	}
	
	@Override
	public void animatedEntityTick(float time) {
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
				TDGameplayState.get().dealAreaDamage(targetLoc, attacker, instantEffects, timedEffects);
			} else {
				if(target != null){
					TDGameplayState.get().dealDamage(target, attacker, instantEffects, timedEffects, rotation);
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
				GAME.addParticle(def.particleFactories[i], loc.clone(), rotation + 180f);
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
