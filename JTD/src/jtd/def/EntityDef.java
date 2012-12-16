/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jtd.def;

import org.newdawn.slick.Image;

/**
 *
 * @author LostMekka
 */
public abstract class EntityDef {
	
	public Image[] sprites;
	public float[] sizes;

	public EntityDef(Image[] sprites, float[] sizes) {
		this.sprites = sprites;
		this.sizes = sizes;
	}
	
}
