/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jtd.entities;

import java.util.Random;
import jtd.PointD;
import jtd.def.ParticleDef;

/**
 *
 * @author LostMekka
 */
public class ParticleFactory {
	
	private static final Random ran = new Random();
	
	public ParticleDef def;
	public PointD locationOffset, locationOffsetAfterSpin;
	public double sizeOffset, sizeRandom;
	public double rotationOffset, rotationRandom;
	public double spinOffset, spinRandom;
	public double forceOffset, forceRandom;
	public double lifeOffset, lifeRandom;
	public boolean isBackgroundParticle = false, ignoresDirection = false;
	public double alphaDelay = 0f;

	public ParticleFactory(ParticleDef def,
			double sizeOffset, double sizeRandom, 
			double rotationOffset, double rotationRandom, 
			double spinOffset, double spinRandom, 
			double forceOffset, double forceRandom,
			double lifeOffset, double lifeRandom) {
		this.def = def;
		this.sizeOffset = sizeOffset;
		this.sizeRandom = sizeRandom;
		this.rotationOffset = rotationOffset;
		this.rotationRandom = rotationRandom;
		this.spinOffset = spinOffset;
		this.spinRandom = spinRandom;
		this.forceOffset = forceOffset;
		this.forceRandom = forceRandom;
		this.lifeOffset = lifeOffset;
		this.lifeRandom = lifeRandom;
		locationOffset = new PointD();
		locationOffsetAfterSpin = new PointD();
	}

	public Particle createParticle(PointD loc, double direction){
		double size = sizeOffset + ran.nextFloat() * sizeRandom;
		double force = forceOffset + ran.nextFloat() * forceRandom;
		double rot = rotationOffset + (ran.nextFloat() - 0.5f) * rotationRandom;
		if(!ignoresDirection) rot += direction;
		double spin = spinOffset + ran.nextFloat() * spinRandom;
		double life = lifeOffset + ran.nextFloat() * lifeRandom;
		PointD start = locationOffset.clone();
		start.rotate(direction);
		start.add(loc);
		PointD off2 = locationOffsetAfterSpin.clone();
		off2.rotate(rot);
		start.add(off2);
		PointD vel = new PointD(
				(double)Math.cos(rot / 180f * Math.PI) * force, 
				(double)Math.sin(rot / 180f * Math.PI) * force);
		return new Particle(def, start, vel, size, rot, spin, life, alphaDelay);
	}
}
