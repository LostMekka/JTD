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
	public float rotationOffset, rotationRandom, spinOffset, spinRandom;

	public ParticleFactory(ParticleDef def, float rotationOffset, float rotationRandom, float spinOffset, float spinRandom) {
		this.def = def;
		this.rotationOffset = rotationOffset;
		this.rotationRandom = rotationRandom;
		this.spinOffset = spinOffset;
		this.spinRandom = spinRandom;
	}

	public Particle createParticle(PointF loc, float direction, float force, float randomForce){
		force += ran.nextFloat() * randomForce;
		float rot = direction + rotationOffset + (ran.nextFloat() - 0.5f) * rotationRandom;
		float spin = spinOffset + ran.nextFloat() * spinRandom;
		PointF vel = new PointF((float)Math.cos(rot / 180f * Math.PI) * force, (float)Math.sin(rot / 180f * Math.PI) * force);
		return new Particle(loc.clone(), vel, rot, spin, def);
	}
}
