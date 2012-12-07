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
public class MobDef extends AnimatedEntityDef{

	public float maxHP, hpRegen, maxShield, shieldRegen, armor, speed;

	public MobDef(Image[] sprites, float[] times, float[] sizes, 
			float maxHP, float hpRegen, 
			float maxShield, float shieldRegen, 
			float armor, float speed) {
		super(sprites, times, sizes, true);
		this.maxHP = maxHP;
		this.hpRegen = hpRegen;
		this.maxShield = maxShield;
		this.shieldRegen = shieldRegen;
		this.armor = armor;
		this.speed = speed;
	}

	public void tick(Mob mob, float time){}

}
