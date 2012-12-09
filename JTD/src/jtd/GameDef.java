/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jtd;

import java.util.ArrayList;
import jtd.effect.instant.InstantEffect;
import jtd.effect.timed.SlowEffectDef;
import jtd.effect.timed.TimedEffectDef;
import jtd.entities.ExplosionDef;
import jtd.entities.MobDef;
import jtd.entities.ParticleDef;
import jtd.entities.ParticleFactory;
import jtd.entities.ProjectileDef;
import jtd.entities.Tower;
import jtd.entities.TowerDef;
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
	
	// common variables for initialisation
	private int n;
	private Image[] imageArray;
	private float[] timesArray, sizesArray, particleCooldowns, idleParticleCooldowns;
	private ParticleFactory[] particleFactories, initialParticleFactories, idleParticleFactorys, shotParticleFactorys;
	private int[] initialParticleCounts, shotParticleCounts;
	
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
		// blood 000
		n = 1;
		imageArray = new Image[n];
		timesArray = new float[n];
		sizesArray = new float[n];
		for(int i=0; i<n; i++){
			imageArray[i] = AssetLoader.getImage("blood_000_00" + i + ".png", false);
			timesArray[i] = 0.12f;
			sizesArray[i] = 0.25f;
		}
		part_blood_000 = new ParticleDef(imageArray, sizesArray, timesArray);

		// blood splat 000
		n = 6;
		imageArray = new Image[n];
		timesArray = new float[n];
		sizesArray = new float[n];
		for(int i=0; i<n; i++){
			imageArray[i] = AssetLoader.getImage("bloodsplat_000_00" + i + ".png", false);
			timesArray[i] = 0.12f;
			sizesArray[i] = 0.25f;
		}
		part_bloodsplat_000 = new ParticleDef(imageArray, sizesArray, timesArray, false);

		// ice shard 000
		imageArray = new Image[1];
		timesArray = new float[1];
		sizesArray = new float[1];
		imageArray[0] = AssetLoader.getImage("iceshard_000_000.png", false);
		timesArray[0] = 1f;
		sizesArray[0] = 0.25f;
		part_iceshard_000 = new ParticleDef(imageArray, sizesArray, timesArray);

		// dust 000
		n = 5;
		imageArray = new Image[n];
		timesArray = new float[n];
		sizesArray = new float[n];
		for(int i=0; i<n; i++){
			imageArray[i] = AssetLoader.getImage("dust_000_00" + i + ".png", false);
			timesArray[i] = 0.15f;
			sizesArray[i] = 0.25f;
		}
		part_dust_000 = new ParticleDef(imageArray, sizesArray, timesArray);

		// dust 001
		n = 2;
		imageArray = new Image[n];
		timesArray = new float[n];
		sizesArray = new float[n];
		for(int i=0; i<n; i++){
			imageArray[i] = AssetLoader.getImage("dust_001_00" + i + ".png", false);
			timesArray[i] = 0.25f;
			sizesArray[i] = 0.15f;
		}
		part_dust_001 = new ParticleDef(imageArray, sizesArray, timesArray);

		// muzzle flash 000
		n = 1;
		imageArray = new Image[n];
		timesArray = new float[n];
		sizesArray = new float[n];
		for(int i=0; i<n; i++){
			imageArray[i] = AssetLoader.getImage("muzzleflash_000_00" + i + ".png", false);
			timesArray[i] = 1f;
			sizesArray[i] = 0.2f;
		}
		part_muzzle_000 = new ParticleDef(imageArray, sizesArray, timesArray);
		
		// bullet casing 000
		n = 1;
		imageArray = new Image[n];
		timesArray = new float[n];
		sizesArray = new float[n];
		for(int i=0; i<n; i++){
			imageArray[i] = AssetLoader.getImage("casing_000_00" + i + ".png", false);
			timesArray[i] = 1f;
			sizesArray[i] = 0.1f;
		}
		part_casing_000 = new ParticleDef(imageArray, sizesArray, timesArray);
	}
	
	private void initCannonTower(){
		for(int lev=0; lev<5; lev++){
			// -------- explosions -------------------------------------------------
			// main explosion
			imageArray = new Image[1];
			timesArray = new float[1];
			sizesArray = new float[1];
			imageArray[0] = AssetLoader.getImage("explosion.png", false);
			timesArray[0] = 0.25f;
			sizesArray[0] = 0.5f;
			particleFactories = new ParticleFactory[0];
			particleCooldowns = new float[0];
			initialParticleFactories = new ParticleFactory[1];
			initialParticleCounts = new int[1];
			initialParticleFactories[0] = new ParticleFactory(part_dust_001, 0f, 360f, 0f, 0f, 1f, 0.2f, 0.5f, 0.5f);
			initialParticleCounts[0] = 8;
			ExplosionDef explosionDef = new ExplosionDef(imageArray, sizesArray, timesArray, 
					particleFactories, particleCooldowns, 
					initialParticleFactories, initialParticleCounts);

			// -------- projectiles ------------------------------------------------
			// main projectile (cannon ball)
			imageArray = new Image[1];
			timesArray = new float[1];
			sizesArray = new float[1];
			imageArray[0] = AssetLoader.getImage("shot.png", false);
			timesArray[0] = 0.2f;
			sizesArray[0] = 0.15f;
			particleFactories = new ParticleFactory[0];
			particleCooldowns = new float[0];
			ProjectileDef rocketDef = new ProjectileDef(
					imageArray, sizesArray, timesArray, 5f, 5f,
					explosionDef, particleFactories, particleCooldowns);

			// -------- effects ----------------------------------------------------
			// timed effects
			TimedEffectDef[] towerTimedEffects = new TimedEffectDef[0];
			// instant effects
			InstantEffect[] towerInstantEffects = new InstantEffect[0];

			// -------- towers -----------------------------------------------------
			// images
			imageArray = new Image[2];
			sizesArray = new float[2];
			imageArray[0] = AssetLoader.getImage("tower_body.png", false);
			sizesArray[0] = 1f;
			imageArray[1] = AssetLoader.getImage("tower_head.png", false);
			sizesArray[1] = 1f;
			// idle particles
			idleParticleFactorys = new ParticleFactory[0];
			idleParticleCooldowns = new float[0];
			// shot particles
			shotParticleFactorys = new ParticleFactory[1];
			shotParticleCounts = new int[1];
			shotParticleFactorys[0] = new ParticleFactory(part_muzzle_000, 
					0f, 0f, 0f, 0f, 0f, 0f, 0.05f, 0.01f);
			shotParticleFactorys[0].locationOffset = new PointF(0.5f, 0f);
			shotParticleCounts[0] = 1;
			// towers
			TowerDef t = new TowerDef(imageArray, sizesArray, Tower.TargetingMode.nearest,
					1.5f + lev, 0f, 1.5f, 2f + lev, 
					0.5f, 360f, 140f, -80f,
					towerTimedEffects, towerInstantEffects, rocketDef, 
					TowerType.cannon, "Cannon Tower", lev, 
					idleParticleFactorys, idleParticleCooldowns, 
					shotParticleFactorys, shotParticleCounts);
			TOWER_CANNON.add(t);
		}
	}
	
	private void initFreezerTower(){
		for(int lev=0; lev<5; lev++){
			// -------- explosions -------------------------------------------------
			// main explosion
			imageArray = new Image[1];
			timesArray = new float[1];
			sizesArray = new float[1];
			imageArray[0] = AssetLoader.getImage("explosion.png", false);
			timesArray[0] = 0.5f;
			sizesArray[0] = 0.5f;
			particleFactories = new ParticleFactory[1];
			particleCooldowns = new float[1];
			particleFactories[0] = new ParticleFactory(part_dust_001, 0f, 360f, 0f, 0f, 1f, 1.5f, 0.5f, 0.5f);
			particleCooldowns[0] = 0.1f;
			initialParticleFactories = new ParticleFactory[1];
			initialParticleCounts = new int[1];
			initialParticleFactories[0] = new ParticleFactory(part_iceshard_000, 0f, 0f, 0f, 0f, 1f, 1.5f, 1f, 0.8f);
			initialParticleCounts[0] = 24;
			ExplosionDef explosionDef = new ExplosionDef(imageArray, sizesArray, timesArray, 
					particleFactories, particleCooldowns, 
					initialParticleFactories, initialParticleCounts);

			// -------- projectiles ------------------------------------------------
			// main projectile (blue rocket)
			n = 2;
			imageArray = new Image[n];
			timesArray = new float[n];
			sizesArray = new float[n];
			for(int i=0; i<n; i++){
				imageArray[i] = AssetLoader.getImage("icerocket_000_00" + i + ".png", false);
				timesArray[i] = 0.2f;
				sizesArray[i] = 0.5f;
			}
			particleFactories = new ParticleFactory[1];
			particleCooldowns = new float[1];
			particleFactories[0] = new ParticleFactory(part_dust_001, 
					0f, 50f, 0f, 0f, 1f, 0.1f, 1f, 1f);
			particleFactories[0].locationOffset = new PointF(-0.15f, 0f);
			particleCooldowns[0] = 0.04f;
			ProjectileDef rocketDef = new ProjectileDef(
					imageArray, sizesArray, timesArray, 4f, 5f, 
					explosionDef, particleFactories, particleCooldowns);

			// -------- effects ----------------------------------------------------
			// timed effects
			TimedEffectDef[] towerTimedEffects = new TimedEffectDef[1];
			towerTimedEffects[0] = new SlowEffectDef(0.2f, 2f, 0f);
			// instant effects
			InstantEffect[] towerInstantEffects = new InstantEffect[0];

			// -------- towers -----------------------------------------------------
			// images
			imageArray = new Image[2];
			sizesArray = new float[2];
			imageArray[0] = AssetLoader.getImage("freezertower_000_000.png", false);
			sizesArray[0] = 1f;
			imageArray[1] = AssetLoader.getImage("freezertower_000_001.png", false);
			sizesArray[1] = 1f;
			// idle particles
			idleParticleFactorys = new ParticleFactory[0];
			idleParticleCooldowns = new float[0];
			// shot particles
			shotParticleFactorys = new ParticleFactory[1];
			shotParticleCounts = new int[1];
			shotParticleFactorys[0] = new ParticleFactory(part_dust_001, 
					180f, 50f, 0f, 0f, 1f, 1.5f, 1f, 1f);
			shotParticleFactorys[0].locationOffset = new PointF(-0.15f, 0f);
			shotParticleCounts[0] = 15;
			// towers
			TowerDef t = new TowerDef(imageArray, sizesArray, Tower.TargetingMode.nearest,
					3.5f + lev, 0.5f + 0.5f * lev, 3f, 0.5f, 
					0.15f, 140f, 40f, 30f,
					towerTimedEffects, towerInstantEffects, rocketDef, 
					TowerType.freezer, "Freezer Tower", lev, 
					idleParticleFactorys, idleParticleCooldowns, 
					shotParticleFactorys, shotParticleCounts);
			TOWER_FREEZER.add(t);
		}
	}
	
	private void initRepeaterTower(){
		for(int lev=0; lev<5; lev++){
			// -------- effects ----------------------------------------------------
			// timed effects
			TimedEffectDef[] towerTimedEffects = new TimedEffectDef[0];
			// instant effects
			InstantEffect[] towerInstantEffects = new InstantEffect[0];

			// -------- towers -----------------------------------------------------
			// images
			imageArray = new Image[2];
			sizesArray = new float[2];
			imageArray[0] = AssetLoader.getImage("repeatertower_000_000.png", false);
			sizesArray[0] = 1f;
			imageArray[1] = AssetLoader.getImage("repeatertower_000_001.png", false);
			sizesArray[1] = 1f;
			// idle particles
			idleParticleFactorys = new ParticleFactory[0];
			idleParticleCooldowns = new float[0];
			// shot particles
			shotParticleFactorys = new ParticleFactory[2];
			shotParticleCounts = new int[2];
			shotParticleFactorys[0] = new ParticleFactory(part_casing_000, 
					80f, 60f, 400f, 1000f, 0.2f, 0.4f, 1f, 0.5f);
			shotParticleFactorys[0].locationOffset = new PointF(0.05f, 0.15f);
			shotParticleCounts[0] = 1;
			shotParticleFactorys[1] = new ParticleFactory(part_muzzle_000, 
					0f, 0f, 0f, 0f, 0f, 0f, 0.05f, 0.01f);
			shotParticleFactorys[1].locationOffset = new PointF(0.45f, 0f);
			shotParticleCounts[1] = 1;
			// towers
			TowerDef t = new TowerDef(imageArray, sizesArray, Tower.TargetingMode.random,
					1f + lev, 0f, 0.1f, 0.125f + 0.125f * lev, 
					0.3f, 600f, 400f, 20f,
					towerTimedEffects, towerInstantEffects, null, 
					TowerType.repeater, "Repeater Tower", lev, 
					idleParticleFactorys, idleParticleCooldowns, 
					shotParticleFactorys, shotParticleCounts);
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
		return list.get(level);
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
		imageArray = new Image[1];
		timesArray = new float[1];
		sizesArray = new float[1];
		imageArray[0] = AssetLoader.getImage("mob.png", false);
		timesArray[0] = 0.5f;
		sizesArray[0] = 0.5f;
		
		ParticleFactory[] deathFacts = new ParticleFactory[2];
		int[] deathCounts = new int[2];
		deathFacts[0] = new ParticleFactory(part_bloodsplat_000, 0f, 0f, 0f, 0f, 0.1f, 0.5f, 5f, 0f);
		deathCounts[0] = 8;
		deathFacts[1] = new ParticleFactory(part_blood_000, 0f, 0f, 0f, 0f, 0f, 0f, 10f, 10f);
		deathFacts[1].isBackgroundParticle = true;
		deathCounts[1] = 1;
		
		ParticleFactory[] hitFacts = new ParticleFactory[1];
		float[] hitRatios = new float[1];
		hitFacts[0] = new ParticleFactory(part_bloodsplat_000, 0f, 40f, 0f, 0f, 0.4f, 0.5f, 5f, 0f);
		hitRatios[0] = 0.5f;
		return new MobDef(imageArray, timesArray, sizesArray, 
				2 + 2 * level, 0, 0, 0, 0, 1, 
				deathFacts, deathCounts, hitFacts, hitRatios);
	}
	
	private String getStr(int i){
		if(i <= 9) return "00" + i;
		if(i <= 99) return "0" + i;
		if(i <= 999) return "" + i;
		throw new RuntimeException("WTF!!! YOU USE TOO MANY SPRITES PER PARTICLE!!!1!!eleven!");
	}
	
}
