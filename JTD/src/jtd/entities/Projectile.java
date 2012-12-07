/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jtd.entities;

import jtd.KillListener;
import java.util.LinkedList;
import jtd.PointF;
import jtd.TDGameplayState;
import jtd.effect.instant.InstantEffect;
import jtd.effect.timed.TimedEffect;
import org.newdawn.slick.Image;

/**
 *
 * @author LostMekka
 */
public class Projectile extends AnimatedEntity implements KillListener{

	public ProjectileDef projectileDef;
	public Mob target;
	public PointF targetLoc;
	public Tower attacker;
	public LinkedList<InstantEffect> instantEffects;
	public LinkedList<TimedEffect> timedEffects;
	
	private float lifeTime = 0f;
	private float[] particleCooldowns;

	public Projectile(
			ProjectileDef def, Mob target, Tower attacker, 
			LinkedList<InstantEffect> instantEffects, 
			LinkedList<TimedEffect> timedEffects, 
			PointF loc) {
		super(loc, def);
		this.projectileDef = def;
		this.target = target;
		target.addKillListener(this);
		targetLoc = target.loc;
		this.attacker = attacker;
		this.instantEffects = instantEffects;
		this.timedEffects = timedEffects;
		rotation = attacker.loc.getRotationTo(target.loc);
		particleCooldowns = new float[projectileDef.particleFactories.length];
		System.arraycopy(projectileDef.particleCooldowns, 0, particleCooldowns, 0, particleCooldowns.length);
	}
	
	@Override
	public void animatedEntityTick(float time) {
		// tick lifetime, kill if expired
		lifeTime += time;
		if(lifeTime >= projectileDef.lifeTime){
			kill(null);
			return;
		}
		// move to target
		loc.travelTo(targetLoc, time * projectileDef.speed, true);
		rotation = loc.getRotationTo(targetLoc);
		// if target is reached, deal damage and kill self
		if(loc.hammingDistanceTo(targetLoc) < 0.05f){
			if(attacker.towerDef.damageRadius > 0){
				TDGameplayState.get().dealAreaDamage(targetLoc, attacker, instantEffects, timedEffects);
			} else {
				if(target != null){
					TDGameplayState.get().dealDamage(target, attacker, instantEffects, timedEffects);
				}
			}
			kill(null);
		}
		// emit particles
		for(int i=0; i<particleCooldowns.length; i++){
			particleCooldowns[i] -= time;
			while(particleCooldowns[i] <= 0){
				particleCooldowns[i] += projectileDef.particleCooldowns[i];
				GAME.addParticle(projectileDef.particleFactories[i].createParticle(
						loc.clone(), rotation + 180f, projectileDef.force, projectileDef.randomForce));
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
