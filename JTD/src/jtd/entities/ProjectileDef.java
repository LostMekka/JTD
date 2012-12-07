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
public class ProjectileDef extends AnimatedEntityDef{
	
	public float speed, lifeTime;
	public ExplosionDef expDef;
	public ParticleFactory[] particleFactories;
	public float[] particleCooldowns;
	public float force, randomForce;
	
	public ProjectileDef(Image[] sprites, float[] sizes, float[] times,
			float speed, float lifeTime, ExplosionDef expDef, 
			ParticleFactory[] particleFactories,
			float[] particleCooldowns, float force, float randomForce) {
		super(sprites, sizes, times, false);
		this.speed = speed;
		this.lifeTime = lifeTime;
		this.expDef = expDef;
		this.particleFactories = particleFactories;
		this.particleCooldowns = particleCooldowns;	
		this.force = force;
		this.randomForce = randomForce;
	}

}
