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
public class MobDef {

	public float maxHP, hpRegen, maxShield, shieldRegen, armor, speed;
	public Image sprite;

	public MobDef(
			float maxHP, float hpRegen, 
			float maxShield, float shieldRegen, 
			float armor, float speed, 
			Image sprite) {
		this.maxHP = maxHP;
		this.hpRegen = hpRegen;
		this.maxShield = maxShield;
		this.shieldRegen = shieldRegen;
		this.armor = armor;
		this.speed = speed;
		this.sprite = sprite;
	}

	public void tick(float time){}

}
