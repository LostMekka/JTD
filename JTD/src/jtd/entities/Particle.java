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

	public Particle(ParticleDef particleDef, PointF loc, PointF vel, float rotation, float spinVel, float lifeTime) {
		super(loc, particleDef);
		this.def = particleDef;
		this.vel = vel;
		this.rotation = rotation;
		this.spinVel = spinVel;
		lifeTimeLeft = lifeTime;
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

	@Override
	public void animationEnded() {
		kill(null);
	}
	
}
