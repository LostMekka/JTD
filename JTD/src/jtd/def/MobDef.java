/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jtd.def;

import jtd.entities.Mob;
import jtd.entities.ParticleFactory;

/**
 *
 * @author LostMekka
 */
public class MobDef extends AnimatedEntityDef{

	public int size = 1;
	public float maxHP = 1f;
	public float hpRegen = 0f;
	public float maxShield = 0f;
	public float shieldRegen = 0f;
	public float armor = 0f;
	public float speed = 1f;
	public float radius = 0.5f;
	public ParticleFactory[] hitParticleFacts = {};
	public ParticleFactory[] deathParticleFacts = {};
	public float[] damagePerParticle = {};
	public int[] deathParticleCounts = {};

	public void defTick(Mob mob, float time){}
}
