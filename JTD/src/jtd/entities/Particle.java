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
public class Particle extends AnimatedEntity{

	public ParticleDef def;
	public PointF vel;
	public float spinVel;
	public boolean fadeAlpha;
	
	private float lifeTimeLeft, initialLifeTime;

	public Particle(ParticleDef particleDef, PointF loc, PointF vel, float size, 
			float rotation, float spinVel, float lifeTime, boolean fadeAlpha) {
		super(loc, particleDef);
		this.def = particleDef;
		this.vel = vel;
		sizeMultiplier = size;
		this.rotation = rotation;
		this.spinVel = spinVel;
		this.fadeAlpha = fadeAlpha;
		lifeTimeLeft = lifeTime;
		initialLifeTime = lifeTime;
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
		if(fadeAlpha) spriteAlpha = initialLifeTime / lifeTimeLeft;
	}

	@Override
	public void animationEnded() {
		kill(null);
	}
	
}
