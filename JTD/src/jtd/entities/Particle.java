/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jtd.entities;

import jtd.def.ParticleDef;
import jtd.PointD;

/**
 *
 * @author LostMekka
 */
public class Particle extends AnimatedEntity{

	public ParticleDef def;
	public PointD vel;
	public double spinVel;
	public double alphaDelay;
	
	private double lifeTimeLeft, initialLifeTime;

	public Particle(ParticleDef particleDef, PointD loc, PointD vel, double size, 
			double rotation, double spinVel, double lifeTime, double alphaDelay) {
		super(loc);
		this.def = particleDef;
		this.vel = vel;
		sizeMultiplier = size;
		this.rotation = rotation;
		this.spinVel = spinVel;
		this.alphaDelay = alphaDelay;
		lifeTimeLeft = lifeTime;
		initialLifeTime = lifeTime;
		setAnimationSet(def.animations, true);
		if(!def.animationsAreCyclic){
			double t = 0f;
			for(double f:currAnimation.times) t += f;
			if(t < initialLifeTime) initialLifeTime = t;
		}
	}

	@Override
	public void animatedEntityTick(double time) {
		lifeTimeLeft -= time;
		if(lifeTimeLeft <= 0f){
			kill(null);
			return;
		}
		loc.x += time * vel.x;
		loc.y += time * vel.y;
		rotate(time * spinVel);
		double a = lifeTimeLeft / initialLifeTime;
		if(a < 1f - alphaDelay) spriteAlpha = a / (1f - alphaDelay);
	}

	@Override
	public void animationEnded() {
		kill(null);
	}
	
}
