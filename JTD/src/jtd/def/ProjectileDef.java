/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jtd.def;

import jtd.def.ExplosionDef;
import jtd.def.AnimatedEntityDef;
import jtd.entities.ParticleFactory;
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
	public boolean isHoming;
	
	public ProjectileDef(Image[] sprites, float[] sizes, float[] times,
			float speed, float lifeTime, boolean isHoming, ExplosionDef expDef, 
			ParticleFactory[] particleFactories, float[] particleCooldowns) {
		super(sprites, sizes, times, true);
		this.speed = speed;
		this.lifeTime = lifeTime;
		this.isHoming = isHoming;
		this.expDef = expDef;
		this.particleFactories = particleFactories;
		this.particleCooldowns = particleCooldowns;	
	}

}
