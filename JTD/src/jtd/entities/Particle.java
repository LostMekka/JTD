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
	
	private float lifeTimeLeft;

	public Particle(PointF loc, PointF vel, float rotation, float spinVel, ParticleDef particleDef) {
		super(loc, particleDef);
		this.def = particleDef;
		this.vel = vel;
		this.rotation = rotation;
		this.spinVel = spinVel;
		lifeTimeLeft = particleDef.lifetime + RANDOM.nextFloat() * particleDef.randomLifeTime;
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
	}
	
}
