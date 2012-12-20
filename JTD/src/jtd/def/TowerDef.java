/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jtd.def;

import jtd.PointF;
import jtd.effect.instant.InstantEffect;
import jtd.effect.timed.TimedEffectDef;
import jtd.entities.ParticleFactory;
import jtd.entities.Tower;

/**
 *
 * @author LostMekka
 */
public class TowerDef extends EntityDef{

	public int size = 1;
	public float range = 1f;
	public float damageRadius = 0f;
	public float reloadTime = 1f;
	public float damage = 1f;
	public float headMaxVel = 1f;
	public float headAcceleration = 1f;
	public float cost = 1f;	
	public TowerDef[] upgradeOptions = {};
	public ProjectileDef projectileDef = null;
	public String name = "<NO NAME SET>";
	public int level = 0;
	public Tower.TargetingMode defaultTargetingMode = Tower.TargetingMode.nearest;
	public TimedEffectDef[] timedEffects = {};
	public InstantEffect[] instantEffects = {};
	public ParticleFactory[] idlePartFacts = {};
	public float[] idlePartCooldowns = {};
	public ParticleFactory[] shotParticleFactories = {};
	public int[] shotParticleCounts = {};
	public PointF[] shotOffsets = {new PointF()};

}
