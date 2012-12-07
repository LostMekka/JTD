/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jtd.entities;

import jtd.GameDef;
import jtd.GameDef.TowerType;
import jtd.effect.instant.InstantEffect;
import jtd.effect.timed.TimedEffectDef;
import jtd.entities.ProjectileDef;
import org.newdawn.slick.Image;

/**
 *
 * @author LostMekka
 */
public class TowerDef extends EntityDef{
	
	public float range, damageRadius, reloadTime, damage, headLength;
	public float shotForce, shotRandomForce, idleParticleForce, idleParticleRandomForce;
	public TimedEffectDef[] timedEffects;
	public InstantEffect[] instantEffects;
	public ProjectileDef projectileDef;
	public GameDef.TowerType towerType;
	public String name;
	public int level;
	public ParticleFactory[] idlePartFacts, shotParticleFactories;
	public float[] idlePartCooldowns;
	public int[] shotParticleCounts;

	public TowerDef(Image[] sprites, float[] sizes, 
			float range, float damageRadius, float reloadTime, float damage, float headLength,
			TimedEffectDef[] timedEffects, InstantEffect[] instantEffects, 
			ProjectileDef projectileDef, TowerType towerType, String name, int level, 
			ParticleFactory[] idlePartFacts, float[] idlePartCooldowns, float idleParticleForce, float idleParticleRandomForce, 
			ParticleFactory[] shotParticleFactory, int[] shotParticleCounts, float shotForce, float shotRandomForce) {
		super(sprites, sizes);
		this.range = range;
		this.damageRadius = damageRadius;
		this.reloadTime = reloadTime;
		this.damage = damage;
		this.headLength = headLength;
		this.shotForce = shotForce;
		this.shotRandomForce = shotRandomForce;
		this.idleParticleForce = idleParticleForce;
		this.idleParticleRandomForce = idleParticleRandomForce;
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
