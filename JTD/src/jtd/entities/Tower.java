/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jtd.entities;

import java.util.Comparator;
import java.util.LinkedList;
import jtd.CoordinateTransformator;
import jtd.KillListener;
import jtd.PointF;
import jtd.PointI;
import jtd.def.TowerDef;
import jtd.effect.instant.InstantEffect;
import jtd.effect.timed.TimedEffectDef;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.StateBasedGame;

/**
 *
 * @author LostMekka
 */
public class Tower extends Entity implements KillListener{

	public enum TargetingMode{
		nearest, leatHealth, random
	}
	
	private static final Comparator<Mob> NEAREST_COMPARATOR = new Comparator<Mob>() {
		@Override
		public int compare(Mob o1, Mob o2) {
			float d1 = o1.getDistanceStillToWalk();
			float d2 = o2.getDistanceStillToWalk();
			if(d1 > d2) return 1;
			if(d1 < d2) return -1;
			return 0;
		}
	};
	private static final Comparator<Mob> LEAST_HEALTH_COMPARATOR = new Comparator<Mob>() {
		@Override
		public int compare(Mob o1, Mob o2) {
			if(o1.hp > o2.hp) return 1;
			if(o1.hp < o2.hp) return -1;
			return 0;
		}
	};
	private static final Comparator<Mob> RANDOM_COMPARATOR = new Comparator<Mob>() {
		@Override
		public int compare(Mob o1, Mob o2) {
			return RANDOM.nextInt(2) * 2 - 1;
		}
	};
	
	public Comparator<Mob> getComparator(){
		switch(targetingMode){
			case leatHealth: return LEAST_HEALTH_COMPARATOR;
			case nearest: return NEAREST_COMPARATOR;
			case random: return RANDOM_COMPARATOR;
			default: return null;
		}
	}
	
	public static final float IDLE_COOLDOWN_TIME = 2f;
	public static final float IDLE_COOLDOWN_RANDOM_TIME = 14f;
	
	public TowerDef def;
	public Mob target = null;
	public TargetingMode targetingMode;
	
	private float shotCooldown, headDir, headVel, idleCounter, lastTargetDirection;
	private float[] instantEffectCooldowns, timedEffectCooldowns, idleParticleCooldowns;
	private int currShotOffset = 0;

	public Tower(TowerDef def, PointI loc) {
		super(loc.getPointF(def.size), def);
		entitySize = def.size;
		headDir = RANDOM.nextFloat() * 360f;
		lastTargetDirection = headDir;
		headVel = 0f;
		updateTowerDef(def);
		resetIdleCounter();
		targetingMode = def.defaultTargetingMode;
	}

	public float getHeadDir() {
		return headDir;
	}

	public final void updateTowerDef(TowerDef towerDef){
		if(this.def == towerDef) return;
		// update def references
		this.def = towerDef;
		super.def = towerDef;
		// reset shot cooldown
		shotCooldown = towerDef.reloadTime;
		// init instant effect cooldowns
		instantEffectCooldowns = new float[towerDef.instantEffects.length];
		for(int i=0; i<instantEffectCooldowns.length; i++){
			instantEffectCooldowns[i] = towerDef.instantEffects[i].cooldown;
		}
		// init timed effect cooldowns
		timedEffectCooldowns = new float[towerDef.timedEffects.length];
		for(int i=0; i<timedEffectCooldowns.length; i++){
			timedEffectCooldowns[i] = towerDef.timedEffects[i].cooldown;
		}
		// init idle particle cooldowns
		idleParticleCooldowns = new float[towerDef.idlePartCooldowns.length];
		System.arraycopy(towerDef.idlePartCooldowns, 0, idleParticleCooldowns, 0, idleParticleCooldowns.length);
	}
	
	private void requestTarget(){
		target = GAME.giveTarget(this);
		if(target != null){
			target.addKillListener(this);
			resetIdleCounter();
		}
	}
	
	private void resetIdleCounter(){
		idleCounter = IDLE_COOLDOWN_TIME + RANDOM.nextFloat() * IDLE_COOLDOWN_RANDOM_TIME;
	}

	private float turnTo(float direction, float tickTime){
		float dirDiff = direction - headDir;
		while(dirDiff < -180f) dirDiff += 360f;
		while(dirDiff > 180f) dirDiff -= 360f;
		float sign = Math.signum(dirDiff);
		// already time to decelerate?
		if(dirDiff * sign <= headVel * headVel / def.headAcceleration * 0.5f){
			// decelerate
			headVel -= sign * def.headAcceleration * tickTime;
			if(headVel * sign < 0f) headVel = 0f;
		} else {
			// accelerate if still possible
			headVel += sign * def.headAcceleration * tickTime;
			if(headVel * sign > def.headMaxVel) headVel = sign * def.headMaxVel;
		}
		return dirDiff;
	}
	
	private float accelerateTo(float velocity, float tickTime){
		float sign = Math.signum(velocity - headVel);
		headVel += sign * def.headAcceleration * tickTime;
		if(headVel * sign > velocity * sign){
			headVel = velocity;
		}
		return velocity - headVel;
	}
	
	public void shoot(){
		PointF shotStart;
		if(def.shotOffsets == null){
			shotStart = loc.clone();
		} else {
			shotStart = def.shotOffsets[currShotOffset].clone();
			currShotOffset = (currShotOffset + 1) % def.shotOffsets.length;
			shotStart.rotate(headDir);
			shotStart.add(loc);
		}
		// fill effects
		LinkedList<InstantEffect> instantEffects = new LinkedList<>();
		LinkedList<TimedEffectDef> timedEffects = new LinkedList<>();
		for(int i=0; i<instantEffectCooldowns.length; i++){
			if(instantEffectCooldowns[i] <= 0){
				instantEffectCooldowns[i] = def.instantEffects[i].cooldown;
				instantEffects.add(def.instantEffects[i]);
			}
		}
		for(int i=0; i<timedEffectCooldowns.length; i++){
			if(timedEffectCooldowns[i] <= 0){
				timedEffectCooldowns[i] = def.timedEffects[i].cooldown;
				timedEffects.add(def.timedEffects[i]);
			}
		}
		// shoot
		if(def.projectileDef == null){
			// no projectile! deal damage immediately
			if(def.damageRadius > 0f){
				GAME.dealAreaDamage(target.loc, this, instantEffects, timedEffects);
			} else {
				GAME.dealDamage(target, this, instantEffects, timedEffects, headDir);
			}
		} else {
			// shoot projectile
			GAME.shoot(shotStart, this, target, instantEffects, timedEffects);
		}
		// add shot particles
		for(int i=0; i<def.shotParticleFactories.length; i++){
			for(int n=0; n<def.shotParticleCounts[i]; n++){
				GAME.addParticle(def.shotParticleFactories[i], shotStart.clone(), headDir);
			}
		}
	}
	
	@Override
	public void entityTick(float time) {
		// compute tower rotation
		headDir += headVel * time;
		if(headDir < 0f) headDir += 360;
		if(headDir > 360f) headDir -= 360;
		// aqquire new target if necessary
		if((target == null) || (loc.distanceTo(target.loc) - target.def.radius > def.range)){
			requestTarget();
		}
		// targeting
		float targetDirDiff = 180f;
		if(target == null){
			// no target. rotate to last target direction and spin sweep after the timeout
			idleCounter -= time;
			turnTo(lastTargetDirection, time);
			if(idleCounter <= 0f){
				lastTargetDirection = RANDOM.nextFloat() * 360f;
				resetIdleCounter();
			}
		} else {
			// there is a target. turn towards it and set its direction and the angle left to turn
			// (used by the shooting part later)
			lastTargetDirection = loc.getRotationTo(target.loc);
			targetDirDiff = turnTo(lastTargetDirection, time);
		}
		// shot cooldowns / reload times
		shotCooldown -= time;
		for(int i=0; i<instantEffectCooldowns.length; i++) instantEffectCooldowns[i] -= time;
		for(int i=0; i<timedEffectCooldowns.length; i++) timedEffectCooldowns[i] -= time;
		// shooting
		if(shotCooldown <= 0f){
			// if tower has no target or tower does not face target, delay shot
			if((target == null) || (Math.abs(targetDirDiff) > 5f)){
				shotCooldown = 0f;
			} else {
				shoot();
				shotCooldown += def.reloadTime;
			}
		}
		// idle particles
		for(int i=0; i<idleParticleCooldowns.length; i++){
			idleParticleCooldowns[i] -= time;
			while(idleParticleCooldowns[i] <= 0){
				idleParticleCooldowns[i] += def.idlePartCooldowns[i];
				GAME.addParticle(def.idlePartFacts[i], loc.clone(), 0f);
			}
		}
	}

	@Override
	public void entityDraw(
			GameContainer gc, StateBasedGame sbg, 
			Graphics grphcs, CoordinateTransformator transformator) {
		// draw head
		if(def.sprites[1] != null){
			transformator.drawImage(def.sprites[1], loc, def.sizes[1] * (float)entitySize, headDir);
		}
	}

	@Override
	public void EntityKilled(Entity entity, Entity killer) {
		if(entity == target) target = null;
	}
	
}
