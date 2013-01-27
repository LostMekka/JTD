/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jtd.def;

import jtd.Animation;
import jtd.entities.ParticleFactory;

/**
 *
 * @author LostMekka
 */
public class ProjectileDef{
	
	public Animation[] animations = null;
	public double speed = 1f;
	public double lifeTime = 1f;
	public ExplosionDef expDef = null;
	public ParticleFactory[] particleFactories = {};
	public double[] particleCooldowns = {};
	public boolean isHoming = true;
	
}
