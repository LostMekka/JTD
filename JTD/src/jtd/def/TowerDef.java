/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jtd.def;

import jtd.def.EntityDef;
import jtd.def.GameDef.TowerType;
import jtd.effect.instant.InstantEffect;
import jtd.effect.timed.TimedEffectDef;
import jtd.def.ProjectileDef;
import jtd.entities.ParticleFactory;
import jtd.entities.Tower;
import org.newdawn.slick.Image;

/**
 *
 * @author LostMekka
 */
public class TowerDef extends EntityDef{
	
	public float range, damageRadius, reloadTime, damage, headLength, headMaxVel, headAcceleration, headIdleVel;
	public TimedEffectDef[] timedEffects;
	public InstantEffect[] instantEffects;
	public ProjectileDef projectileDef;
	public GameDef.TowerType towerType;
	public String name;
	public int level;
	public ParticleFactory[] idlePartFacts, shotParticleFactories;
	public float[] idlePartCooldowns;
	public int[] shotParticleCounts;
	public Tower.TargetingMode defaultTargetingMode;

	public TowerDef(Image[] sprites, float[] sizes, Tower.TargetingMode defaultTargetingMode,
			float range, float damageRadius, float reloadTime, float damage, 
			float headLength, float headMaxSpeed, float headAcceleration, float headIdleSpeed,
			TimedEffectDef[] timedEffects, InstantEffect[] instantEffects, 
			ProjectileDef projectileDef, TowerType towerType, String name, int level, 
			ParticleFactory[] idlePartFacts, float[] idlePartCooldowns, 
			ParticleFactory[] shotParticleFactory, int[] shotParticleCounts) {
		super(sprites, sizes);
		this.defaultTargetingMode = defaultTargetingMode;
		this.range = range;
		this.damageRadius = damageRadius;
		this.reloadTime = reloadTime;
		this.damage = damage;
		this.headLength = headLength;
		this.headMaxVel = headMaxSpeed;
		this.headAcceleration = headAcceleration;
		this.headIdleVel = headIdleSpeed;
		this.timedEffects = timedEffects;
		this.instantEffects = instantEffects;
		this.projectileDef = projectileDef;
		this.towerType = towerType;
		this.name = name;
		this.level = level;
		this.idlePartFacts = idlePartFacts;
		this.shotParticleFactories = shotParticleFactory;
		this.idlePartCooldowns = idlePartCooldowns;
		this.shotParticleFactories = shotParticleFactory;
		this.shotParticleCounts = shotParticleCounts;
	}

}
