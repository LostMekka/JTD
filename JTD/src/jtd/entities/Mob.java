/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jtd.entities;

import java.util.Iterator;
import java.util.LinkedList;
import jtd.PointF;
import jtd.effect.instant.InstantEffect;
import jtd.effect.timed.TimedEffect;
import jtd.level.Path;

/**
 *
 * @author LostMekka
 */
public class Mob extends Entity {

	public MobDef def;
	public float speedMultiplier = 1f, armorOffset = 0f, bonusDamage = 0f;
	public float hp, shield;
	
	private float[] dmgCounters;
	private Path.PathIterator path;
	private PointF pathTarget;
	private LinkedList<TimedEffect> effects = new LinkedList<>(), effectsToRemove = new LinkedList<>();
	
	public Mob(MobDef mobDef, Path p){
		super(p.getStart().clone(), mobDef);
		this.def = mobDef;
		hp = mobDef.maxHP;
		shield = mobDef.maxShield;
		path = p.getPointIterator(0.1f);
		PointF tmp = path.next();
		pathTarget = path.next();
		rotation = tmp.getRotationTo(pathTarget);
		dmgCounters = new float[def.hitParticleFacts.length];
	}
	
	public float getDistanceStillToWalk(){
		return loc.distanceTo(pathTarget) + path.getDistanceLeft();
	}
	
	public void applyInstantEffect(InstantEffect effect){
		effect.apply(this);
	}
	
	public void applyTimedEffect(TimedEffect effect){
		effects.add(effect);
		effect.apply(this);
	}
	
	public void removeTimedEffect(TimedEffect effect){
		effectsToRemove.add(effect);
		effect.remove(this);
	}
	
	public void damage(float damage, Entity attacker){
		damage(damage, attacker, null);
	}
	
	public void damage(float damage, Entity attacker, Float direction){
		damage += bonusDamage;
		damage -= def.armor + armorOffset;
		if(damage <= 0) return;
		if(damage <= shield){
			shield -= damage;
		} else {
			hp -= damage - shield;
			shield = 0;
		}
		if(direction != null){
			// add hit particles
			for(int i=0; i<def.hitParticleFacts.length; i++){
				dmgCounters[i] += damage;
				while(dmgCounters[i] >= def.damagePerParticle[i]){
					dmgCounters[i] -= def.damagePerParticle[i];
					GAME.addParticle(def.hitParticleFacts[i], loc.clone(), direction);
				}
			}
		}
		if(hp <= 0){
			// add death particles
			for(int i=0; i<def.deathParticleFacts.length; i++){
				for(int n=0; n<def.deathParticleCounts[i]; n++){
					float rot = 360f * RANDOM.nextFloat();
					GAME.addParticle(def.deathParticleFacts[i], loc.clone(), rot);
				}
			}
			// kill mob
			kill(attacker);
		}
	}

	@Override
	public void entityTick(float time) {
		// tick effects
		for(TimedEffect e:effects) e.tick(time, this);
		for(TimedEffect e:effectsToRemove) effects.remove(e);
		effectsToRemove.clear();
		// regen hp
		hp += time * def.hpRegen;
		if(hp > def.maxHP) hp = def.maxHP;
		if(hp <= 0) kill(null);
		// regen shield
		shield += time * def.shieldRegen;
		if(shield > def.maxShield) shield = def.maxShield;
		if(shield < 0) shield = 0;
		// move mob
		if(pathTarget != null){
			float travel = time * def.speed * speedMultiplier;
			while(travel > 0){
				travel -= loc.travelTo(pathTarget, travel, true);
				if(travel > 0){
					pathTarget = path.next();
					// if we have reached the last node in the path, wander around randomly
					if(pathTarget == null){
						pathTarget = loc.clone();
						float d = RANDOM.nextFloat() * 1.5f + 0.5f;
						float a = RANDOM.nextFloat() * 360f;
						pathTarget.x += d * (float)Math.cos(a);
						pathTarget.y += d * (float)Math.sin(a);
						GAME.movePointIntoLevl(pathTarget);
					}
					// set proper direction
					rotation = loc.getRotationTo(pathTarget);
				}
			}
		}
		// let the def tick (for special mobs only)
		def.tick(this, time);
	}

}
