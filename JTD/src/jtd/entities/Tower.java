/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jtd.entities;

import com.sun.org.glassfish.external.arc.Stability;
import java.util.Comparator;
import java.util.LinkedList;
import jtd.CoordinateTransformator;
import jtd.GameCtrl;
import jtd.KillListener;
import jtd.PointD;
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
public class Tower extends AnimatedEntity implements KillListener{

	public enum TargetingMode{
		nearest, leatHealth, random
	}
	
	private static final Comparator<Mob> NEAREST_COMPARATOR = new Comparator<Mob>() {
		@Override
		public int compare(Mob o1, Mob o2) {
			double d1 = o1.getDistanceStillToWalk();
			double d2 = o2.getDistanceStillToWalk();
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
	
	private enum State{ idle, charging, reloading }
	
	public static final double IDLE_COOLDOWN_TIME = 2f;
	public static final double IDLE_COOLDOWN_RANDOM_TIME = 14f;
	
	public TowerDef def;
	public Mob target = null;
	public TargetingMode targetingMode;
	public int kills = 0, cumulativeCost;
	
	private double shotCooldown, headVel, idleCounter, lastTargetDirection;
	private double[] instantEffectCooldowns, timedEffectCooldowns, idleParticleCooldowns;
	private int currShotOffset = 0;
	private AnimatedEntity head;
	private State state = State.idle;

	private Tower(TowerDef towerDef, PointI loc, int cost, double headDir) {
		super(loc.getPointD(towerDef.size));
		if(def.headIdleAnimations == null){
			head = null;
		} else {
			head = new AnimatedEntity(this.loc);
			head.rotation = headDir;
			head.setAnimationSet(def.headIdleAnimations, true);
		}
		cumulativeCost = cost;
		lastTargetDirection = headDir;
		headVel = 0f;
		updateTowerDef(towerDef);
		resetIdleCounter();
		targetingMode = def.defaultTargetingMode;
		setAnimationSet(def.baseIdleAnimations, true);
	}

	public Tower(TowerDef def, PointI loc) {
		this(def, loc, def.cost, RANDOM.nextFloat() * 360d);
	}
	
	public Tower(TowerDef def, Tower parent) {
		this(def, parent.getPointI(), def.cost + parent.cumulativeCost, parent.head.rotation);
	}

	public double getHeadDir() {
		if(head == null) return 0d;
		return head.rotation;
	}

	public final void updateTowerDef(TowerDef towerDef){
		if(this.def == towerDef) return;
		// update def references
		this.def = towerDef;
		// reset shot cooldown
		shotCooldown = towerDef.reloadTime;
		// init instant effect cooldowns
		instantEffectCooldowns = new double[towerDef.instantEffects.length];
		for(int i=0; i<instantEffectCooldowns.length; i++){
			instantEffectCooldowns[i] = towerDef.instantEffects[i].cooldown;
		}
		// init timed effect cooldowns
		timedEffectCooldowns = new double[towerDef.timedEffects.length];
		for(int i=0; i<timedEffectCooldowns.length; i++){
			timedEffectCooldowns[i] = towerDef.timedEffects[i].cooldown;
		}
		// init idle particle cooldowns
		idleParticleCooldowns = new double[towerDef.idlePartCooldowns.length];
		System.arraycopy(towerDef.idlePartCooldowns, 0, idleParticleCooldowns, 0, idleParticleCooldowns.length);
	}
	
	private void requestTarget(){
		target = GameCtrl.get().giveTarget(this);
		if(target != null){
			target.addKillListener(this);
			resetIdleCounter();
		}
	}
	
	private void resetIdleCounter(){
		idleCounter = IDLE_COOLDOWN_TIME + RANDOM.nextFloat() * IDLE_COOLDOWN_RANDOM_TIME;
	}

	private double turnTo(double direction, double tickTime){
		double dirDiff = direction - head.rotation;
		while(dirDiff < -180f) dirDiff += 360f;
		while(dirDiff > 180f) dirDiff -= 360f;
		double sign = Math.signum(dirDiff);
		// already done?
		if(dirDiff * sign <= 0.5d){
			headVel = 0d;
			return 0d;
		}
		// already time to decelerate?
		if(dirDiff * sign <= headVel * headVel / def.headAcceleration * 0.5f){
			// decelerate
			headVel -= sign * def.headAcceleration * tickTime;
			if(headVel * sign < 0.5d) headVel = 0f;
		} else {
			// accelerate if still possible
			headVel += sign * def.headAcceleration * tickTime;
			if(headVel * sign > def.headMaxVel) headVel = sign * def.headMaxVel;
		}
		return dirDiff;
	}
	
	private double accelerateTo(double velocity, double tickTime){
		double sign = Math.signum(velocity - headVel);
		headVel += sign * def.headAcceleration * tickTime;
		if(headVel * sign > velocity * sign){
			headVel = velocity;
		}
		return velocity - headVel;
	}
	
	@Override
	public void animationEnded() {
		switch(state){
			case charging: shoot(); break;
			case reloading:
				state = State.idle;
				setAnimationSet(def.baseIdleAnimations, true);
				if(head != null) head.setAnimationSet(def.baseIdleAnimations, true);
				break;
		}
	}

	public void shoot(){
		PointD shotStart;
		if((head == null) || (def.shotOffsets == null)){
			shotStart = loc.clone();
		} else {
			shotStart = def.shotOffsets[currShotOffset].clone();
			currShotOffset = (currShotOffset + 1) % def.shotOffsets.length;
			shotStart.rotate(head.rotation);
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
				if(head == null){
					// tower has no head. deal area damage around tower
					GameCtrl.get().dealAreaDamage(loc, this, instantEffects, timedEffects);
				} else {
					// tower has a head. deal area damage around target
					GameCtrl.get().dealAreaDamage(target.loc, this, instantEffects, timedEffects);
				}
			} else {
				double shotDir;
				if(head == null){
					// tower has no head. direction must be calculated.
					shotDir = loc.getRotationTo(target.loc);
				} else {
					shotDir = head.rotation;
				}
				GameCtrl.get().dealDamage(target, this, instantEffects, timedEffects, shotDir);
			}
		} else {
			// shoot projectile
			Projectile p = new Projectile(def.projectileDef, target, this, 
					instantEffects, timedEffects, loc.clone());
			GameCtrl.get().addProjectile(p);
		}
		// add shot particles
		double dir = 0d;
		if(head != null) dir = head.rotation;
		for(int i=0; i<def.shotParticleFactories.length; i++){
			for(int n=0; n<def.shotParticleCounts[i]; n++){
				GameCtrl.get().addParticle(def.shotParticleFactories[i], shotStart.clone(), dir);
			}
		}
		// enter reloading state or idle, if reload animation does not exist
		shotCooldown += def.reloadTime;
		if(def.baseReloadAnimations == null){
			state = State.idle;
			setAnimationSet(def.baseIdleAnimations, true);
		} else {
			state = State.reloading;
			setAnimationSet(def.baseReloadAnimations, false);
		}
	}
	
	@Override
	public void animatedEntityTick(double time) {
		// aqquire new target if necessary
		if((state == State.idle) && 
				((target == null) || 
				(loc.distanceTo(target.loc) - target.def.radius > def.range))){
			requestTarget();
		}
		double targetDirDiff = 180f;
		if(head != null){
			// compute tower rotation
			head.rotation += headVel * time;
			if(head.rotation < 0f) head.rotation += 360;
			if(head.rotation > 360f) head.rotation -= 360;
			// targeting
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
		}
		if(state == State.idle){
			// shot cooldowns / reload times
			shotCooldown -= time;
			for(int i=0; i<instantEffectCooldowns.length; i++) instantEffectCooldowns[i] -= time;
			for(int i=0; i<timedEffectCooldowns.length; i++) timedEffectCooldowns[i] -= time;
			// shooting
			if(shotCooldown <= 0f){
				// if tower has no target or tower does not face target, delay shot
				if(((target == null) || (Math.abs(targetDirDiff) > 5f)) && (head != null)){
					shotCooldown = 0f;
				} else {
					// all green. initiate shot
					if(def.baseChargeAnimations == null){
						shoot();
					} else {
						state = State.charging;
						setAnimationSet(def.baseChargeAnimations, false);
						if(head != null) head.setAnimationSet(def.headChargeAnimations, false);
					}
					
					
				}
			}
			// idle particles
			for(int i=0; i<idleParticleCooldowns.length; i++){
				idleParticleCooldowns[i] -= time;
				while(idleParticleCooldowns[i] <= 0){
					idleParticleCooldowns[i] += def.idlePartCooldowns[i];
					GameCtrl.get().addParticle(def.idlePartFacts[i], loc.clone(), 0f);
				}
			}
		}
	}

	@Override
	public void entityDraw(GameContainer gc, StateBasedGame sbg, 
			Graphics grphcs, CoordinateTransformator transformator) {
		// draw head
		head.draw(gc, sbg, grphcs, transformator);
	}

	@Override
	public void EntityKilled(Entity entity, Entity killer) {
		if(entity == target) target = null;
	}
	
}
