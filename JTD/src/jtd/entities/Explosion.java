/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jtd.entities;

import jtd.PointF;
import jtd.def.ExplosionDef;

/**
 *
 * @author LostMekka
 */
public class Explosion extends AnimatedEntity{

	public ExplosionDef def;
	
	private Float initialDirection;
	private float[] particleTimes;

	public Explosion(PointF loc, ExplosionDef explosionDef, Float initialDirection) {
		super(loc, explosionDef);
		if(initialDirection == null){
			this.initialDirection = 0f;
		} else {
			this.initialDirection = initialDirection;
		}
		this.def = explosionDef;
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
				particleTimes[i] += def.particleCooldowns[i];
				GAME.addParticle(def.particleFactories[i], loc.clone(), RANDOM.nextFloat() * 360f);
			}
		}
	}

	@Override
	public void entityInitialTick() {
		for(int i=0; i<def.initialParticleFactories.length; i++){
			ParticleFactory f = def.initialParticleFactories[i];
			for(int n=0; n<def.initialParticleCounts[i]; n++){
				float rot = 360f / (float)def.initialParticleCounts[i] * (float)n;
				GAME.addParticle(f, loc.clone(), rot + initialDirection);
				// TODO: use initialDirection properly
			}
		}
	}

}
