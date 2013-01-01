/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jtd.def;

import jtd.entities.ParticleFactory;

/**
 *
 * @author LostMekka
 */
public class ExplosionDef extends AnimatedEntityDef{

	public ParticleFactory[] particleFactories = {};
	public ParticleFactory[] initialParticleFactories = {};
	public double[] particleCooldowns = {};
	public int[] initialParticleCounts = {};

	public ExplosionDef() {
		isCyclic = false;
	}
	
}
