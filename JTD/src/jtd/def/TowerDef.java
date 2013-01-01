/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jtd.def;

import jtd.PointD;
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
	public double range = 1f;
	public double damageRadius = 0f;
	public double reloadTime = 1f;
	public double damage = 1f;
	public double headMaxVel = 1f;
	public double headAcceleration = 1f;
	public double cost = 1f;	
	public TowerDef[] upgradeOptions = {};
	public ProjectileDef projectileDef = null;
	public String name = "<NO NAME SET>";
	public int level = 0;
	public Tower.TargetingMode defaultTargetingMode = Tower.TargetingMode.nearest;
	public TimedEffectDef[] timedEffects = {};
	public InstantEffect[] instantEffects = {};
	public ParticleFactory[] idlePartFacts = {};
	public double[] idlePartCooldowns = {};
	public ParticleFactory[] shotParticleFactories = {};
	public int[] shotParticleCounts = {};
	public PointD[] shotOffsets = {new PointD()};

}
