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

	public ParticleDef(Image[] sprites, float[] sizes, float[] times, boolean isCyclic) {
		super(sprites, sizes, times, isCyclic);
	}

	public ParticleDef(Image[] sprites, float[] sizes, float[] times) {
		super(sprites, sizes, times, true);
	}
	
}
