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
	public double maxHP = 1f;
	public double hpRegen = 0f;
	public double maxShield = 0f;
	public double shieldRegen = 0f;
	public double armor = 0f;
	public double speed = 1f;
	public double radius = 0.5f;
	public ParticleFactory[] hitParticleFacts = {};
	public ParticleFactory[] deathParticleFacts = {};
	public double[] damagePerParticle = {};
	public int[] deathParticleCounts = {};

	public void defTick(Mob mob, double time){}
}
