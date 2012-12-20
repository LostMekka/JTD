/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jtd.def;

import java.util.ArrayList;
import jtd.AssetLoader;
import jtd.PointF;
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
		part_blood_000.fillImage("blood_000_000.png", 1f, 1f);

		part_bloodsplat_000 = new ParticleDef();
		part_bloodsplat_000.fillImages("bloodsplat_000_", ".png", 6, 3, 1f, 0.12f);
		part_bloodsplat_000.isCyclic = false;

		part_iceshard_000 = new ParticleDef();
		part_iceshard_000.fillImage("iceshard_000_000.png", 1f, 1f);

		part_dust_000 = new ParticleDef();
		part_dust_000.fillImages("dust_000_", ".png", 5, 3, 1f, 1f);

		part_dust_001 = new ParticleDef();
		part_dust_001.fillImages("dust_001_", ".png", 2, 3, 1f, 1f);

		part_muzzle_000 = new ParticleDef();
		part_muzzle_000.fillImage("muzzleflash_000_000.png", 1f, 1f);
		
		part_casing_000 = new ParticleDef();
		part_casing_000.fillImage("casing_000_000.png", 1f, 1f);
	}
	
	private void initCannonTower(){
		for(int lev=0; lev<5; lev++){
			ParticleDef cannonballPart = new ParticleDef();
			cannonballPart.fillImage("shot.png", 1f, 1f);
			
			ExplosionDef explosionDef = new ExplosionDef();
			explosionDef.fillImage("explosion.png", 0.5f, 0.25f);
			explosionDef.initialParticleFactories = new ParticleFactory[]{
				new ParticleFactory(part_dust_001, 0.15f, 0f, 0f, 360f, 0f, 0f, 1f, 0.2f, 0.5f, 0.5f),
				new ParticleFactory(cannonballPart, 0.15f, 0f, 0f, 0f, 0f, 0f, 0.8f, 0.2f, 0.5f, 0.3f)};
			explosionDef.initialParticleFactories[1].alphaDelay = 0.75f;
			explosionDef.initialParticleCounts = new int[]{8, 1};

			ProjectileDef cannonBall = new ProjectileDef();
			cannonBall.fillImage("shot.png", 0.15f, 0.2f);
			cannonBall.isHoming = false;
			cannonBall.speed = 12f;
			cannonBall.lifeTime = 5f;
			cannonBall.expDef = explosionDef;

			TowerDef t = new TowerDef();
			t.sprites = new Image[]{
				AssetLoader.getImage("tower_body.png"), 
				AssetLoader.getImage("tower_head.png")};
			t.sizes = new float[]{1f, 1f};
			t.damage = 1f + lev;
			t.damageRadius = 0.25f;
			t.reloadTime = 1.5f;
			t.range = 2f + 0.5f * lev;
			t.headAcceleration = 360f;
			t.headMaxVel = 140f;
			t.projectileDef = cannonBall;
			t.shotOffsets = new PointF[]{new PointF(0.5f, 0f)};
			t.shotParticleFactories = new ParticleFactory[]{
				new ParticleFactory(part_muzzle_000, 0.3f, 0.1f, 0f, 0f, 0f, 0f, 0f, 0f, 0.1f, 0.1f),
				new ParticleFactory(part_dust_001, 0.15f, 0f, 0f, 50f, 0f, 0f, 1f, 2.5f, 0.2f, 0.3f)};
			t.shotParticleFactories[0].locationOffset = new PointF(0.62f, 0f);
			t.shotParticleFactories[1].locationOffset = new PointF(0.5f, 0f);
			t.shotParticleCounts = new int[]{1, 12};
			t.name = "Cannon Tower";
			t.level = lev + 1;
			if(lev > 0) t.upgradeOptions = new TowerDef[]{TOWER_CANNON.get(lev-1)};
			TOWER_CANNON.add(t);
		}
	}
	
	private void initFreezerTower(){
		for(int lev=0; lev<5; lev++){
			ExplosionDef explosionDef = new ExplosionDef();
			explosionDef.fillImage("explosion.png", 0.5f, 0.25f);
			explosionDef.particleFactories = new ParticleFactory[]{
					new ParticleFactory(part_dust_001, 0.15f, 0f, 0f, 360f, 0f, 0f, 1f, 1.5f, 0.5f, 0.5f)};
			explosionDef.particleCooldowns = new float[]{0.1f};
			explosionDef.initialParticleFactories = new ParticleFactory[]{
					new ParticleFactory(part_iceshard_000, 0.25f, 0.1f, 0f, 300f, 0f, 0f, 0.5f, 2f, 0.5f, 0.8f)};
			explosionDef.initialParticleFactories[0].alphaDelay = 0.8f;
			explosionDef.initialParticleFactories[0].locationOffset = new PointF(0.2f, 0f);
			explosionDef.initialParticleCounts = new int[]{24};

			ProjectileDef rocketDef = new ProjectileDef();
			rocketDef.expDef = explosionDef;
			rocketDef.speed = 4f;
			rocketDef.lifeTime = 5f;
			rocketDef.fillImages("icerocket_000_", ".png", 2, 3, 0.5f, 0.2f);
			rocketDef.particleFactories = new ParticleFactory[]{
				new ParticleFactory(part_dust_001, 0.15f, 0f, 0f, 50f, 0f, 0f, 1f, 0.1f, 1f, 1f)};
			rocketDef.particleFactories[0].locationOffset = new PointF(-0.15f, 0f);
			rocketDef.particleCooldowns = new float[]{0.04f};

			TowerDef t = new TowerDef();
			t.sprites = new Image[]{
				AssetLoader.getImage("freezertower_000_000.png"),
				AssetLoader.getImage("freezertower_000_001.png")};
			t.sizes = new float[]{1f, 1f};
			t.damage = 0.1f;
			t.damageRadius = 0.5f + 0.5f + lev;
			t.reloadTime = 3f;
			t.range = 3.5f + lev;
			t.headAcceleration = 140f;
			t.headMaxVel = 40f;
			t.projectileDef = rocketDef;
			t.timedEffects = new TimedEffectDef[]{new SlowEffectDef(0.2f, 2f, 0f)};
			t.shotParticleFactories = new ParticleFactory[]{
				new ParticleFactory(part_dust_001, 0.15f, 0f, 180f, 50f, 0f, 0f, 1f, 1.5f, 1f, 1f)};
			t.shotParticleCounts = new int[]{15};
			t.shotOffsets = new PointF[]{new PointF(0.15f, 0f)};
			t.name = "Freezer Tower";
			t.level = lev + 1;
			if(lev > 0) t.upgradeOptions = new TowerDef[]{TOWER_FREEZER.get(lev-1)};
			TOWER_FREEZER.add(t);
		}
	}
	
	private void initRepeaterTower(){
		for(int lev=0; lev<5; lev++){
			TowerDef t = new TowerDef();
			t.damage = 0.1f + 0.05f * lev;
			t.damageRadius = 0f;
			t.reloadTime = 0.1f;
			t.range = 1f + 0.333f * lev;
			t.headAcceleration = 360f;
			t.headMaxVel = 150f;
			t.sprites = new Image[]{
				AssetLoader.getImage("repeatertower_000_000.png"),
				AssetLoader.getImage("repeatertower_000_001.png")};
			t.sizes = new float[]{1f, 1f};
			t.shotParticleFactories = new ParticleFactory[]{
				new ParticleFactory(part_casing_000, 0.1f, 0f, 80f, 60f, 400f, 1000f, 0.2f, 0.4f, 1f, 0.5f),
				new ParticleFactory(part_muzzle_000, 0.2f, 0.01f, 0f, 0f, 0f, 0f, 0f, 0f, 0.05f, 0.01f)};
			t.shotParticleFactories[0].locationOffset = new PointF(0.05f, 0.15f);
			t.shotParticleFactories[0].alphaDelay = 0.8f;
			t.shotParticleFactories[1].locationOffset = new PointF(0.5f, 0f);
			t.shotParticleFactories[1].alphaDelay = 0.5f;
			t.shotParticleCounts = new int[]{1, 1};
			t.shotOffsets = new PointF[]{new PointF(0.15f, 0f)};
			t.name = "Repeater Tower";
			t.level = lev + 1;
			if(lev > 0) t.upgradeOptions = new TowerDef[]{TOWER_REPEATER.get(lev-1)};
			t.defaultTargetingMode = Tower.TargetingMode.random;
			TOWER_REPEATER.add(t);
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
	
	private MobDef generateNormalMobDef(int level, boolean boss){
		if(boss) level += 10;
		MobDef m = new MobDef();
		m.maxHP = 2f * (1f + level);
		m.speed = 0.7f;
		Image i = AssetLoader.getImage("ball_000_000.png");
		m.sprites = new Image[]{i, i, i, i, i};
		m.times = new float[]{0.15f, 0.15f, 0.15f, 0.15f, 0.15f};
		m.sizes = new float[]{0.5f, 0.6f, 0.65f, 0.6f, 0.5f};
		m.deathParticleFacts = new ParticleFactory[]{
			new ParticleFactory(part_bloodsplat_000, 0.3f, 0.1f, 0f, 0f, 0f, 0f, 0.1f, 0.5f, 5f, 0f),
			new ParticleFactory(part_blood_000, 0.25f, 0.1f, 0f, 0f, 0f, 0f, 0f, 0f, 10f, 10f)};
		m.deathParticleFacts[0].locationOffset = new PointF(0.3f, 0f);
		m.deathParticleFacts[0].alphaDelay = 1f;
		m.deathParticleFacts[1].isBackgroundParticle = true;
		m.deathParticleFacts[1].alphaDelay = 0.9f;
		m.deathParticleCounts = new int[]{16, 1};
		m.hitParticleFacts = new ParticleFactory[]{
			new ParticleFactory(part_bloodsplat_000, 0.25f, 0.1f, 0f, 40f, 0f, 0f, 0.6f, 0.5f, 5f, 0f)};
		m.hitParticleFacts[0].locationOffset = new PointF(0.3f, 0f);
		m.hitParticleFacts[0].alphaDelay = 1f;
		m.damagePerParticle = new float[]{0.2f};
		return m;
	}
	
	private MobDef generateSwarmMobDef(int level, boolean boss){
		ParticleDef gib000 = new ParticleDef();
		gib000.fillImages("hummer_000_gib000_", ".png", 2, 3, 1f, 0.25f);
		ParticleDef gib001 = new ParticleDef();
		gib001.fillImages("hummer_000_gib001_", ".png", 2, 3, 1f, 0.25f);
		
		if(boss) level += 10;
		MobDef m = new MobDef();
		m.fillImages("hummer_000_", ".png", 4, 3, 0.5f, 0.09f);
		m.maxHP = 2f + level;
		m.speed = 1f;
		
		ParticleFactory deathSplat = new ParticleFactory(part_bloodsplat_000, 0.3f, 0.1f, 0f, 360f, 0f, 0f, 0.1f, 0.5f, 5f, 0f);
		deathSplat.locationOffset = new PointF(0.25f, 0f);
		deathSplat.alphaDelay = 1f;
		ParticleFactory deathBlood = new ParticleFactory(part_blood_000, 0.25f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 10f, 10f);
		deathBlood.isBackgroundParticle = true;
		deathBlood.alphaDelay = 0.9f;
		ParticleFactory deathGib000 = new ParticleFactory(gib000, 0.21f, 0.1f, 0f, 30f, -90f, 180f, -0.5f, -0.5f, 0.4f, 0.5f);
		deathGib000.locationOffset = new PointF(-0.1f, 0.1f);
		deathGib000.alphaDelay = 0.8f;
		ParticleFactory deathGib001 = new ParticleFactory(gib001, 0.21f, 0.1f, 20f, 40f, -90f, 180f, 0.3f, 0.4f, 0.4f, 0.5f);
		deathGib001.locationOffset = new PointF(0.15f, 0.05f);
		deathGib001.alphaDelay = 0.8f;
		m.deathParticleFacts = new ParticleFactory[]{deathSplat, deathBlood, deathGib000, deathGib001};
		m.deathParticleCounts = new int[]{16, 1, 1, 1};
		
		m.hitParticleFacts = new ParticleFactory[]{
			new ParticleFactory(part_bloodsplat_000, 0.25f, 0.1f, 0f, 40f, 0f, 0f, 0.6f, 0.5f, 5f, 0f)};
		m.hitParticleFacts[0].locationOffset = new PointF(0.25f, 0f);
		m.hitParticleFacts[0].alphaDelay = 1f;
		m.damagePerParticle = new float[]{0.15f};
		return m;
	}
	
}
