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
public class ExplosionDef {
	
	public Image[] sprites;
	public float[] times;

	public ExplosionDef(Image[] sprites, float[] times) {
		this.sprites = sprites;
		this.times = times;
	}
	
}
