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
public class ProjectileDef {
	
	public float speed, lifeTime;
	public Image sprite;
	public ExplosionDef expDef;

	public ProjectileDef(float speed, float lifeTime, Image sprite, ExplosionDef expDef) {
		this.speed = speed;
		this.lifeTime = lifeTime;
		this.sprite = sprite;
		this.expDef = expDef;
	}

}
