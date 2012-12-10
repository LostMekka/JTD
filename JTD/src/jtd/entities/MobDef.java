/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jtd.entities;

import org.newdawn.slick.Image;

/**
 *
 * @author LostMekka
 */
public class MobDef extends AnimatedEntityDef{

	public float maxHP, hpRegen, maxShield, shieldRegen, armor, speed;
	public ParticleFactory[] hitParticleFacts, deathParticleFacts;
	public float[] damagePerParticle;
	public int[] deathParticleCounts;

	public MobDef(Image[] sprites, float[] sizes, float[] times, 
			float maxHP, float hpRegen, 
			float maxShield, float shieldRegen, 
			float armor, float speed, 
			ParticleFactory[] deathParticleFacts, int[] deathParticleCounts,
			ParticleFactory[] hitParticleFacts, float[] damagePerParticle) {
		super(sprites, sizes, times, true);
		this.maxHP = maxHP;
		this.hpRegen = hpRegen;
		this.maxShield = maxShield;
		this.shieldRegen = shieldRegen;
		this.armor = armor;
		this.speed = speed;
		this.deathParticleFacts = deathParticleFacts;
		this.deathParticleCounts = deathParticleCounts;
		this.hitParticleFacts = hitParticleFacts;
		this.damagePerParticle = damagePerParticle;
	}

	public void tick(Mob mob, float time){}

}
