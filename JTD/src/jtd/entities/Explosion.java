/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jtd.entities;

import jtd.GameCtrl;
import jtd.PointD;
import jtd.def.ExplosionDef;

/**
 *
 * @author LostMekka
 */
public class Explosion extends AnimatedEntity{

	public ExplosionDef def;
	
	private Double initialDirection;
	private double[] particleTimes;

	public Explosion(PointD loc, ExplosionDef explosionDef, Double initialDirection) {
		super(loc);
		if(initialDirection == null){
			this.initialDirection = 0d;
		} else {
			this.initialDirection = initialDirection;
		}
		def = explosionDef;
		setAnimationSet(def.animations, false);
		particleTimes = new double[explosionDef.particleCooldowns.length];
		System.arraycopy(explosionDef.particleCooldowns, 0, particleTimes, 0, particleTimes.length);
	}

	@Override
	public void animationEnded() {
		kill(null);
	}

	@Override
	public void animatedEntityTick(double time) {
		for(int i=0; i<particleTimes.length; i++){
			particleTimes[i] -= time;
			while(particleTimes[i] <= 0f){
				particleTimes[i] += def.particleCooldowns[i];
				GameCtrl.get().addParticle(def.particleFactories[i], loc.clone(), RANDOM.nextFloat() * 360f);
			}
		}
	}

	@Override
	public void entityInitialTick() {
		for(int i=0; i<def.initialParticleFactories.length; i++){
			ParticleFactory f = def.initialParticleFactories[i];
			for(int n=0; n<def.initialParticleCounts[i]; n++){
				GameCtrl.get().addParticle(f, loc.clone(), initialDirection);
			}
		}
	}

}
