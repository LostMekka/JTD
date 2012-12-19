/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jtd.entities;

import jtd.def.ParticleDef;
import jtd.PointF;

/**
 *
 * @author LostMekka
 */
public class Particle extends AnimatedEntity{

	public ParticleDef def;
	public PointF vel;
	public float spinVel;
	public float alphaDelay;
	
	private float lifeTimeLeft, initialLifeTime;

	public Particle(ParticleDef particleDef, PointF loc, PointF vel, float size, 
			float rotation, float spinVel, float lifeTime, float alphaDelay) {
		super(loc, particleDef);
		this.def = particleDef;
		this.vel = vel;
		sizeMultiplier = size;
		this.rotation = rotation;
		this.spinVel = spinVel;
		this.alphaDelay = alphaDelay;
		lifeTimeLeft = lifeTime;
		initialLifeTime = lifeTime;
		if(!def.isCyclic){
			float t = 0f;
			for(float f:def.times) t += f;
			if(t < initialLifeTime) initialLifeTime = t;
		}
	}

	@Override
	public void animatedEntityTick(float time) {
		lifeTimeLeft -= time;
		if(lifeTimeLeft <= 0f){
			kill(null);
			return;
		}
		loc.x += time * vel.x;
		loc.y += time * vel.y;
		rotate(time * spinVel);
		float a = lifeTimeLeft / initialLifeTime;
		if(a < 1f - alphaDelay) spriteAlpha = a / (1f - alphaDelay);
	}

	@Override
	public void animationEnded() {
		kill(null);
	}
	
}
