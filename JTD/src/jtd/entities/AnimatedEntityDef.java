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
public abstract class AnimatedEntityDef extends EntityDef{

	public float[] times;
	public boolean isCyclic;

	public AnimatedEntityDef(Image[] sprites, float[] sizes, float[] times, boolean isCyclic) {
		super(sprites, sizes);
		this.times = times;
		this.isCyclic = isCyclic;
	}

}
