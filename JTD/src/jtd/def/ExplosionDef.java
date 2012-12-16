/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jtd.def;

import jtd.def.AnimatedEntityDef;
import jtd.entities.ParticleFactory;
import jtd.entities.ParticleFactory;
import org.newdawn.slick.Image;

/**
 *
 * @author LostMekka
 */
public class ExplosionDef extends AnimatedEntityDef{

	public ParticleFactory[] particleFactories, initialFactories;
	public float[] particleCooldowns;
	public int[] initialParticleCounts;
	
	public ExplosionDef(
			Image[] sprites, float[] sizes, float[] times, 
			ParticleFactory[] particleFactories, float[] particleCooldowns,
			ParticleFactory[] initialFactories, int[] initialParticleCounts) {
		super(sprites, sizes, times, false);
		this.particleFactories = particleFactories;
		this.particleCooldowns = particleCooldowns;	
		this.initialFactories = initialFactories;
		this.initialParticleCounts = initialParticleCounts;
	}

}
