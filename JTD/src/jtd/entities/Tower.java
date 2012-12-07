/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jtd.entities;

import java.util.LinkedList;
import jtd.CoordinateTransformator;
import jtd.KillListener;
import jtd.PointF;
import jtd.effect.instant.InstantEffect;
import jtd.effect.timed.TimedEffect;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.StateBasedGame;

/**
 *
 * @author LostMekka
 */
public class Tower extends Entity implements KillListener{

	public TowerDef towerDef;
	public Mob target = null;
	public PointF lastTargetLocation;
	
	private float shotCooldown;
	private float[] instantEffectCooldowns, timedEffectCooldowns, idleParticleCooldowns;

	public Tower(TowerDef def, PointF loc) {
		super(loc, def);
		lastTargetLocation = new PointF(1f, 0f);
		lastTargetLocation.rotate(RANDOM.nextFloat() * 360f);
		lastTargetLocation.x += loc.x;
		lastTargetLocation.y += loc.y;
		updateTowerDef(def);
	}
	
	public final void updateTowerDef(TowerDef towerDef){
		if(this.towerDef == towerDef) return;
		// update def references
		this.towerDef = towerDef;
		this.entityDef = towerDef;
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
			lastTargetLocation = target.loc;
		}
	}
	
	@Override
	public void entityTick(float time) {
		// targeting
		if(target == null){
			requestTarget();
		} else {
			if(loc.distanceTo(target.loc) > towerDef.range){
				lastTargetLocation = target.loc.clone();
				requestTarget();
			}
		}
		// if target is still null, spin head
		if(target == null){
			lastTargetLocation.x -= loc.x;
			lastTargetLocation.y -= loc.y;
			lastTargetLocation.rotate(time * 40f);
			lastTargetLocation.x += loc.x;
			lastTargetLocation.y += loc.y;
		}
		// shot cooldowns
		shotCooldown -= time;
		for(int i=0; i<instantEffectCooldowns.length; i++){
			instantEffectCooldowns[i] -= time;
		}
		for(int i=0; i<timedEffectCooldowns.length; i++){
			timedEffectCooldowns[i] -= time;
		}
		// shooting
		while(shotCooldown <= 0f){
			if(target == null){
				shotCooldown = 0f;
				break;
			}
			LinkedList<InstantEffect> instantEffects = new LinkedList<>();
			LinkedList<TimedEffect> timedEffects = new LinkedList<>();
			for(int i=0; i<instantEffectCooldowns.length; i++){
				if(instantEffectCooldowns[i] <= 0){
					instantEffectCooldowns[i] -= towerDef.instantEffects[i].cooldown;
					instantEffects.add(towerDef.instantEffects[i]);
				}
			}
			for(int i=0; i<timedEffectCooldowns.length; i++){
				if(timedEffectCooldowns[i] <= 0){
					timedEffectCooldowns[i] -= towerDef.timedEffects[i].cooldown;
					timedEffects.add(new TimedEffect(this, towerDef.timedEffects[i]));
				}
			}
			PointF p = loc.clone();
			p.travelTo(lastTargetLocation, towerDef.headLength, false);
			float shotDir = loc.getRotationTo(lastTargetLocation);
			GAME.shoot(p, this, target, instantEffects, timedEffects);
			for(int i=0; i<towerDef.shotParticleFactories.length; i++){
				for(int n=0; n<towerDef.shotParticleCounts[i]; n++){
					GAME.addParticle(towerDef.shotParticleFactories[i].createParticle(
							p.clone(), shotDir, towerDef.shotForce, towerDef.shotRandomForce));
				}
			}
			shotCooldown += towerDef.reloadTime;
		}
		// idle particles
		for(int i=0; i<idleParticleCooldowns.length; i++){
			idleParticleCooldowns[i] -= time;
			while(idleParticleCooldowns[i] <= 0){
				idleParticleCooldowns[i] += towerDef.idlePartCooldowns[i];
				GAME.addParticle(towerDef.idlePartFacts[i].createParticle(
						loc.clone(), 0f, towerDef.idleParticleForce, towerDef.idleParticleRandomForce));
			}
		}
	}

	@Override
	public void entityDraw(
			GameContainer gc, StateBasedGame sbg, 
			Graphics grphcs, CoordinateTransformator transformator) {
		// draw head
		if(towerDef.sprites[1] != null){
			if(target == null){
				transformator.drawImage(towerDef.sprites[1], loc, sizeInTiles, loc.getRotationTo(lastTargetLocation));
			} else {
				transformator.drawImage(towerDef.sprites[1], loc, sizeInTiles, loc.getRotationTo(target.loc));
			}
		}
	}

	@Override
	public void EntityKilled(Entity entity, Entity killer) {
		if(entity == target){
			lastTargetLocation = target.loc.clone();
			target = null;
		}
	}
	
}
