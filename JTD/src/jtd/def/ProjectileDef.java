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
public class ProjectileDef extends AnimatedEntityDef{
	
	public float speed = 1f;
	public float lifeTime = 1f;
	public ExplosionDef expDef = null;
	public ParticleFactory[] particleFactories = {};
	public float[] particleCooldowns = {};
	public boolean isHoming = true;
	
}
