/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jtd.def;

import java.util.ArrayList;
import java.util.LinkedList;
import jtd.AssetLoader;
import jtd.PointD;
import jtd.effect.timed.SlowEffectDef;
import jtd.effect.timed.TimedEffectDef;
import jtd.entities.ParticleFactory;
import jtd.entities.Tower;
import org.newdawn.slick.Image;

/**
 *
 * @author LostMekka
 */
public final class GameDef {
	
	public enum TowerType{
		cannon, freezer, repeater
	}
	
	public enum MobType{
		normal, swarm, armored
	}

	public GameDef() {
		initCommonDefs();
		initCannonTower();
		initFreezerTower();
		initRepeaterTower();
	}
	
	// common defs
	private ParticleDef part_blood_000;
	private ParticleDef part_bloodsplat_000;
	private ParticleDef part_dust_000;
	private ParticleDef part_dust_001;
	private ParticleDef part_casing_000;
	private ParticleDef part_muzzle_000;
	private ParticleDef part_iceshard_000;
	
	private void initCommonDefs(){
		//-------- particles ---------------------------------------------------
		part_blood_000 = new ParticleDef();
		part_blood_000.fillImage("blood_000_000.png", 1d, 1d);

		part_bloodsplat_000 = new ParticleDef();
		part_bloodsplat_000.fillImages("bloodsplat_000_", ".png", 6, 3, 1d, 0.12d);
		part_bloodsplat_000.isCyclic = false;

		part_iceshard_000 = new ParticleDef();
		part_iceshard_000.fillImage("iceshard_000_000.png", 1d, 1d);

		part_dust_000 = new ParticleDef();
		part_dust_000.fillImages("dust_000_", ".png", 5, 3, 1d, 1d);

		part_dust_001 = new ParticleDef();
		part_dust_001.fillImages("dust_001_", ".png", 2, 3, 1d, 1d);

		part_muzzle_000 = new ParticleDef();
		part_muzzle_000.fillImage("muzzleflash_000_000.png", 1d, 1d);
		
		part_casing_000 = new ParticleDef();
		part_casing_000.fillImage("casing_000_000.png", 1d, 1d);
	}
	
	private void initCannonTower(){
		for(int lev=0; lev<5; lev++){
			ParticleDef cannonballPart = new ParticleDef();
			cannonballPart.fillImage("shot.png", 1d, 1d);
			
			ExplosionDef explosionDef = new ExplosionDef();
			explosionDef.fillImage("explosion.png", 0.8d, 0.25d);
			explosionDef.initialParticleFactories = new ParticleFactory[]{
				new ParticleFactory(part_dust_001, 0.4d, 0d, 0d, 360d, 0d, 0d, 1d, 0.2d, 0.5d, 0.5d),
				new ParticleFactory(cannonballPart, 0.4d, 0d, 0d, 0d, 0d, 0d, 1.6d, 0.4d, 0.5d, 0.3d)};
			explosionDef.initialParticleFactories[1].alphaDelay = 0.75d;
			explosionDef.initialParticleCounts = new int[]{8, 1};

			ProjectileDef cannonBall = new ProjectileDef();
			cannonBall.fillImage("shot.png", 0.4d, 0.2d);
			cannonBall.isHoming = false;
			cannonBall.speed = 22d;
			cannonBall.lifeTime = 5d;
			cannonBall.expDef = explosionDef;

			TowerDef t = new TowerDef();
			t.cost = 150 * (int)Math.pow(2, lev);
			t.size = 3;
			t.sprites = new Image[]{
				AssetLoader.getImage("tower_body.png"), 
				AssetLoader.getImage("tower_head.png")};
			t.sizes = new double[]{1d, 1d};
			t.damage = 2d + lev;
			t.damageRadius = 0.2d;
			t.reloadTime = 1.2d;
			t.range = 4d + lev;
			t.headAcceleration = 180d;
			t.headMaxVel = 100d;
			t.projectileDef = cannonBall;
			t.shotOffsets = new PointD[]{new PointD(1.5d, 0.35d), new PointD(1.5d, -0.35d)};
			t.shotParticleFactories = new ParticleFactory[]{
				new ParticleFactory(part_muzzle_000, 1d, 0.1d, 0d, 0d, 0d, 0d, 0d, 0d, 0.2d, 0.1d),
				new ParticleFactory(part_dust_001, 0.4d, 0d, 0d, 50d, 0d, 0d, 1d, 2.5d, 0.2d, 0.3d)};
			t.shotParticleFactories[0].locationOffset = new PointD(0.45d, 0d);
			t.shotParticleCounts = new int[]{1, 12};
			t.name = "Cannon Tower";
			t.level = lev + 1;
			TOWER_CANNON.add(t);
			if(lev > 0) TOWER_CANNON.get(lev-1).upgradeOptions = new TowerDef[]{t};
		}
	}
	
	private void initFreezerTower(){
		for(double lev=0; lev<5; lev++){
			ExplosionDef explosionDef = new ExplosionDef();
			explosionDef.fillImage("explosion.png", 0.5d, 0.25d);
			explosionDef.particleFactories = new ParticleFactory[]{
					new ParticleFactory(part_dust_001, 0.4d, 0d, 0d, 360d, 0d, 0d, 1d, 1.5d, 0.5d, 0.5d)};
			explosionDef.particleCooldowns = new double[]{0.1d};
			explosionDef.initialParticleFactories = new ParticleFactory[]{
					new ParticleFactory(part_iceshard_000, 0.55d, 0.1d, 0d, 300d, 0d, 0d, 1d, 3.5d, 0.5d, 0.8d)};
			explosionDef.initialParticleFactories[0].locationOffsetAfterSpin = new PointD(0.3d, 0d);
			explosionDef.initialParticleFactories[0].alphaDelay = 0.8d;
			explosionDef.initialParticleCounts = new int[]{24};

			ProjectileDef rocketDef = new ProjectileDef();
			rocketDef.expDef = explosionDef;
			rocketDef.speed = 4d;
			rocketDef.lifeTime = 5d;
			rocketDef.fillImages("icerocket_000_", ".png", 2, 3, 1d, 0.2d);
			rocketDef.particleFactories = new ParticleFactory[]{
				new ParticleFactory(part_dust_001, 0.15d, 0d, 0d, 50d, 0d, 0d, 1d, 0.1d, 1d, 1d)};
			rocketDef.particleFactories[0].locationOffset = new PointD(-0.15d, 0d);
			rocketDef.particleCooldowns = new double[]{0.04d};

			TowerDef t = new TowerDef();
			t.cost = 80 * (int)Math.pow(2, lev);
			t.size = 2;
			t.sprites = new Image[]{
				AssetLoader.getImage("freezertower_000_000.png"),
				AssetLoader.getImage("freezertower_000_001.png")};
			t.sizes = new double[]{1d, 1d};
			t.damage = 0d;
			t.damageRadius = 0.33d + 0.33d * lev;
			t.reloadTime = 3d;
			t.range = 3.5d + lev;
			t.headAcceleration = 140d;
			t.headMaxVel = 40d;
			t.projectileDef = rocketDef;
			t.timedEffects = new TimedEffectDef[]{new SlowEffectDef(0.9 - 0.1 * lev, 2d, 0d)};
			t.shotParticleFactories = new ParticleFactory[]{
				new ParticleFactory(part_dust_001, 0.15d, 0d, 180d, 50d, 0d, 0d, 1d, 1.5d, 1d, 1d)};
			t.shotParticleFactories[0].locationOffset = new PointD(-1d, 0d);
			t.shotParticleCounts = new int[]{15};
			t.shotOffsets = new PointD[]{
				new PointD(0.6d, -0.6d), 
				new PointD(0.6d, -0.2d), 
				new PointD(0.6d, 0.2d), 
				new PointD(0.6d, 0.6d)};
			t.name = "Freezer Tower";
			t.level = (int)lev + 1;
			TOWER_FREEZER.add(t);
			if(lev > 0) TOWER_FREEZER.get((int)lev-1).upgradeOptions = new TowerDef[]{t};
		}
	}
	
	private void initRepeaterTower(){
		for(int lev=0; lev<5; lev++){
			TowerDef t = new TowerDef();
			t.cost = 30 * (int)Math.pow(2, lev);
			t.size = 2;
			t.damage = 0.2d + 0.1d * lev;
			t.damageRadius = 0d;
			t.reloadTime = 0.1d;
			t.range = 3d + lev * 0.2d;
			t.headAcceleration = 600d;
			t.headMaxVel = 450d;
			t.sprites = new Image[]{
				AssetLoader.getImage("repeatertower_000_000.png"),
				AssetLoader.getImage("repeatertower_000_001.png")};
			t.sizes = new double[]{1d, 1d};
			t.shotParticleFactories = new ParticleFactory[]{
				new ParticleFactory(part_casing_000, 0.2d, 0d, 80d, 60d, 400d, 1000d, 0.4d, 0.9d, 1d, 0.5d),
				new ParticleFactory(part_muzzle_000, 0.4d, 0.02d, 0d, 0d, 0d, 0d, 0d, 0d, 0.05d, 0.01d)};
			t.shotParticleFactories[0].locationOffset = new PointD(0.1d, 0.3d);
			t.shotParticleFactories[0].alphaDelay = 0.8d;
			t.shotParticleFactories[1].locationOffset = new PointD(1d, 0d);
			t.shotParticleFactories[1].alphaDelay = 0.5d;
			t.shotParticleCounts = new int[]{1, 1};
			t.name = "Repeater Tower";
			t.level = lev + 1;
			t.defaultTargetingMode = Tower.TargetingMode.random;
			TOWER_REPEATER.add(t);
			if(lev > 0) TOWER_REPEATER.get(lev-1).upgradeOptions = new TowerDef[]{t};
		}
	}
	
	private ArrayList<TowerDef> TOWER_CANNON = new ArrayList<>();
	private ArrayList<TowerDef> TOWER_FREEZER = new ArrayList<>();
	private ArrayList<TowerDef> TOWER_REPEATER = new ArrayList<>();
	
	private ArrayList<MobDef> MOB_NORMAL = new ArrayList<>(200);
	private ArrayList<MobDef> BOSS_NORMAL = new ArrayList<>(200);

	private ArrayList<MobDef> MOB_SWARM = new ArrayList<>(200);
	private ArrayList<MobDef> BOSS_SWARM = new ArrayList<>(200);

	private ArrayList<MobDef> MOB_ARMORED = new ArrayList<>(200);
	private ArrayList<MobDef> BOSS_ARMORED = new ArrayList<>(200);
	
	private ArrayList<TowerDef> getTowerList(TowerType t){
		switch(t){
			case cannon: return TOWER_CANNON;
			case freezer: return TOWER_FREEZER;
			case repeater: return TOWER_REPEATER;
			default: return null;
		}
	}
	
	public int getTowerCount(TowerType t){
		return getTowerList(t).size();
	}
	
	public TowerDef getTowerDef(TowerType t, int level){
		return getTowerDef(getTowerList(t), level);
	}
	
	private TowerDef getTowerDef(ArrayList<TowerDef> list, int level){
		if((level < 0) || (list == null) || (level >= list.size())) return null;
		return list.get(level - 1);
	}
	
	public MobDef getMobDef(MobType t, int level, boolean boss){
		if(boss){
			switch(t){
				case normal: return getMobDef(BOSS_NORMAL, t, level, boss);
				case swarm: return getMobDef(BOSS_SWARM, t, level, boss);
				case armored: return getMobDef(BOSS_ARMORED, t, level, boss);
				default: return null;
			}
		} else {
			switch(t){
				case normal: return getMobDef(MOB_NORMAL, t, level, boss);
				case swarm: return getMobDef(MOB_SWARM, t, level, boss);
				case armored: return getMobDef(MOB_ARMORED, t, level, boss);
				default: return null;
			}
		}
	}
	
	private MobDef getMobDef(ArrayList<MobDef> list, MobType t, int level, boolean boss){
		if(level < 0) return null;
		if(level >= list.size()){
			list.ensureCapacity(level);
			for(int i=list.size(); i<level; i++) list.add(null);
			MobDef d = generateMobDef(t, level, boss);
			list.add(d);
			return d;
		}
		MobDef d = list.get(level);
		if(d == null){
			generateMobDef(t, level, boss);
			list.set(level, d);
		}
		return d;
	}
	
	private MobDef generateMobDef(MobType t, int level, boolean boss){
		switch(t){
			case normal: return generateNormalMobDef(level, boss);
			case swarm: return generateSwarmMobDef(level, boss);
			default: return null;
		}
	}
	
	private MobDef generateSwarmMobDef(int level, boolean boss){
		if(boss) level += 10;
		MobDef m = new MobDef();
		m.reward = 2 * (level + 1);
		m.maxHP = 1d + 0.5d * level;
		m.speed = 2.1d;
		Image i = AssetLoader.getImage("ball_000_000.png");
		m.sprites = new Image[]{i, i, i, i, i};
		m.times = new double[]{0.15d, 0.15d, 0.15d, 0.15d, 0.15d};
		m.sizes = new double[]{0.5d, 0.6d, 0.65d, 0.6d, 0.5d};
		m.deathParticleFacts = new ParticleFactory[]{
			new ParticleFactory(part_bloodsplat_000, 0.4d, 0.15d, 0d, 360d, 0d, 0d, 0.1d, 0.8d, 5d, 0d),
			new ParticleFactory(part_blood_000, 0.5d, 0.1d, 0d, 0d, 0d, 0d, 0d, 0d, 10d, 10d)};
		m.deathParticleFacts[0].locationOffsetAfterSpin = new PointD(0.2d, 0d);
		m.deathParticleFacts[0].alphaDelay = 1d;
		m.deathParticleFacts[1].isBackgroundParticle = true;
		m.deathParticleFacts[1].ignoresDirection = true;
		m.deathParticleFacts[1].alphaDelay = 0.9d;
		m.deathParticleCounts = new int[]{16, 1};
		m.hitParticleFacts = new ParticleFactory[]{
			new ParticleFactory(part_bloodsplat_000, 0.4d, 0.15d, 0d, 0d, 0d, 0d, 0.1d, 0.8d, 5d, 0d)};
		m.hitParticleFacts[0].locationOffsetAfterSpin = new PointD(0.35d, 0d);
		m.hitParticleFacts[0].alphaDelay = 1d;
		m.damagePerParticle = new double[]{0.2d};
		return m;
	}
	
	private MobDef generateNormalMobDef(int level, boolean boss){
		ParticleDef gib000 = new ParticleDef();
		gib000.fillImages("hummer_000_gib000_", ".png", 2, 3, 1d, 0.25d);
		ParticleDef gib001 = new ParticleDef();
		gib001.fillImages("hummer_000_gib001_", ".png", 2, 3, 1d, 0.25d);
		
		if(boss) level += 10;
		MobDef m = new MobDef();
		m.reward = 5 * (level + 1);
		m.size = 2;
		m.fillImages("hummer_000_", ".png", 4, 3, 0.8d, 0.09d);
		m.maxHP = 3d + 2d * level;
		m.speed = 1.5d;
		
		ParticleFactory deathSplat = new ParticleFactory(part_bloodsplat_000, 0.5d, 0.15d, 0d, 360d, 0d, 0d, 0.8d, 1.3d, 5d, 0d);
		deathSplat.alphaDelay = 1d;
		ParticleFactory deathBlood = new ParticleFactory(part_blood_000, 0.8d, 0.2d, 0d, 0d, 0d, 0d, 0d, 0d, 10d, 10d);
		deathBlood.isBackgroundParticle = true;
		deathBlood.ignoresDirection = true;
		deathBlood.alphaDelay = 0.9d;
		ParticleFactory deathGib000 = new ParticleFactory(gib000, 0.8d, 0d, 0d, 30d, -90d, 180d, -0.5d, -0.5d, 0.4d, 0.5d);
		deathGib000.locationOffset = new PointD(-0.2d, 0.2d);
		deathGib000.alphaDelay = 0.8d;
		ParticleFactory deathGib001 = new ParticleFactory(gib001, 0.8d, 0d, 20d, 40d, -90d, 180d, 0.3d, 0.4d, 0.4d, 0.5d);
		deathGib001.locationOffset = new PointD(0.3d, 0.1d);
		deathGib001.alphaDelay = 0.8d;
		m.deathParticleFacts = new ParticleFactory[]{deathSplat, deathBlood, deathGib000, deathGib001};
		m.deathParticleCounts = new int[]{30, 1, 1, 1};
		
		m.hitParticleFacts = new ParticleFactory[]{
			new ParticleFactory(part_bloodsplat_000, 0.4d, 0.15d, 0d, 360d, 0d, 0d, 0.5d, 0.7d, 5d, 0d)};
		m.hitParticleFacts[0].locationOffset = new PointD(0.25d, 0d);
		m.hitParticleFacts[0].alphaDelay = 1d;
		m.damagePerParticle = new double[]{0.15d};
		return m;
	}
	
	
	public LinkedList<TowerDef> getBuildableTowers(){
		LinkedList<TowerDef> ans = new LinkedList<>();
		ans.add(TOWER_REPEATER.get(0));
		ans.add(TOWER_CANNON.get(0));
		ans.add(TOWER_FREEZER.get(0));
		return ans;
	}
	
}
