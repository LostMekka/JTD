/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jtd.entities;

import jtd.PointF;

/**
 *
 * @author LostMekka
 */
public class Explosion extends AnimatedEntity{

	public ExplosionDef explosionDef;
	
	private float[] particleTimes;

	public Explosion(PointF loc, ExplosionDef explosionDef) {
		super(loc, explosionDef);
		this.explosionDef = explosionDef;
		particleTimes = new float[explosionDef.particleCooldowns.length];
		System.arraycopy(explosionDef.particleCooldowns, 0, particleTimes, 0, particleTimes.length);
	}

	@Override
	public void animationEnded() {
		kill(null);
	}

	@Override
	public void animatedEntityTick(float time) {
		for(int i=0; i<particleTimes.length; i++){
			particleTimes[i] -= time;
			while(particleTimes[i] <= 0f){
				particleTimes[i] += explosionDef.particleCooldowns[i];
				GAME.addParticle(explosionDef.particleFactories[i].createParticle(
						loc.clone(), RANDOM.nextFloat() * 360f, 
						explosionDef.force, explosionDef.randomForce));
			}
		}
	}

	@Override
	public void entityInitialTick() {
		int factCount = explosionDef.initialFactories.length;
		for(int i=0; i<factCount; i++){
			ParticleFactory f = explosionDef.initialFactories[i];
			for(int n=0; n<explosionDef.initialParticleCounts[i]; n++){
				float rot = 360f / (float)explosionDef.initialParticleCounts[i] * (float)n;
				GAME.addParticle(f.createParticle(loc.clone(), rot, explosionDef.force, explosionDef.randomForce));
			}
		}
	}

}
