/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jtd.entities;

import org.newdawn.slick.Image;

/**
 *
 * @author LostMekka
 */
public class ParticleDef extends AnimatedEntityDef{

	public float lifetime, randomLifeTime;
	
	public ParticleDef(Image[] sprites, float[] sizes, float[] times, float lifetime, float randomLifeTime) {
		super(sprites, sizes, times, true);
		this.lifetime = lifetime;
		this.randomLifeTime = randomLifeTime;
	}
	
}
