/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jtd.entities;

import java.util.Random;
import jtd.PointF;
import org.newdawn.slick.Image;

/**
 *
 * @author LostMekka
 */
public class ParticleFactory {
	
	private static final Random ran = new Random();
	
	public ParticleDef def;
	public PointF locationOffset;
	public float rotationOffset, rotationRandom;
	public float spinOffset, spinRandom;
	public float forceOffset, forceRandom;
	public float lifeOffset, lifeRandom;
	public boolean isBackgroundParticle;

	public ParticleFactory(ParticleDef def,
			float rotationOffset, float rotationRandom, 
			float spinOffset, float spinRandom, 
			float forceOffset, float forceRandom,
			float lifeOffset, float lifeRandom) {
		this.def = def;
		this.rotationOffset = rotationOffset;
		this.rotationRandom = rotationRandom;
		this.spinOffset = spinOffset;
		this.spinRandom = spinRandom;
		this.forceOffset = forceOffset;
		this.forceRandom = forceRandom;
		this.lifeOffset = lifeOffset;
		this.lifeRandom = lifeRandom;
		locationOffset = new PointF();
		isBackgroundParticle = false;
	}

	public Particle createParticle(PointF loc, float direction){
		float force = forceOffset + ran.nextFloat() * forceRandom;
		float rot = direction + rotationOffset + (ran.nextFloat() - 0.5f) * rotationRandom;
		float spin = spinOffset + ran.nextFloat() * spinRandom;
		float life = lifeOffset + ran.nextFloat() * lifeRandom;
		PointF start = locationOffset.clone();
		start.rotate(direction);
		start.x += loc.x;
		start.y += loc.y;
		PointF vel = new PointF(
				(float)Math.cos(rot / 180f * Math.PI) * force, 
				(float)Math.sin(rot / 180f * Math.PI) * force);
		return new Particle(def, start, vel, rot, spin, life);
	}
}
