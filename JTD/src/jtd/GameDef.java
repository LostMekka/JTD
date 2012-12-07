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
import jtd.entities.TowerDef;
import org.newdawn.slick.Image;

/**
 *
 * @author LostMekka
 */
public final class GameDef {
	
	public enum TowerType{
		cannon, freezer
	}
	
	public enum MobType{
		normal, swarm, armored
	}

	public GameDef() {
		initCommonDefs();
		initCannonTower();
		initFreezerTower();
	}
	
	// common variables for initialisation
	private int n;
	private Image[] imageArray;
	private float[] timesArray, sizesArray, particleCooldowns, idleParticleCooldowns;
	private ParticleFactory[] particleFactories, initialParticleFactories, idleParticleFactorys, shotParticleFactorys;
	private int[] initialParticleCounts, shotParticleCounts;
	
	// common defs
	private ParticleDef part_dust000, part_dust001, part_muzzle000, part_iceshard000;
	
	private void initCommonDefs(){
		//-------- particles ---------------------------------------------------
		// ice shard 000
		imageArray = new Image[1];
		timesArray = new float[1];
		sizesArray = new float[1];
		imageArray[0] = AssetLoader.getImage("iceshard_000_000.png", false);
		timesArray[0] = 1f;
		sizesArray[0] = 0.25f;
		part_iceshard000 = new ParticleDef(imageArray, sizesArray, timesArray, 0.5f, 0.75f);

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
		part_dust000 = new ParticleDef(imageArray, sizesArray, timesArray, 1f, 0.5f);

		// dust 001
		n = 2;
		imageArray = new Image[n];
		timesArray = new float[n];
		sizesArray = new float[n];
		for(int i=0; i<n; i++){
			imageArray[i] = AssetLoader.getImage("dust_001_00" + i + ".png", false);
			timesArray[i] = 0.25f;
			sizesArray[i] = 0.2f;
		}
		part_dust001 = new ParticleDef(imageArray, sizesArray, timesArray, 1f, 0.5f);

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
		part_muzzle000 = new ParticleDef(imageArray, sizesArray, timesArray, 0.1f, 0f);
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
			particleFactories = new ParticleFactory[1];
			particleCooldowns = new float[1];
			particleFactories[0] = new ParticleFactory(part_dust000, 0f, 360f, 0f, 0f);
			particleCooldowns[0] = 0.1f;
			initialParticleFactories = new ParticleFactory[0];
			initialParticleCounts = new int[0];
			ExplosionDef explosionDef = new ExplosionDef(imageArray, sizesArray, timesArray, 
					particleFactories, particleCooldowns, 2f, 0f, 
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
					explosionDef, particleFactories, particleCooldowns, 1f, 0f);

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
			shotParticleFactorys[0] = new ParticleFactory(part_muzzle000, 0f, 0f, 0f, 0f);
			shotParticleCounts[0] = 1;
			// towers
			TowerDef t = new TowerDef(imageArray, sizesArray, 
					1.5f + lev, 0f, 1.5f, 2f + lev, 0.6f, 
					towerTimedEffects, towerInstantEffects, rocketDef, 
					TowerType.cannon, "Cannon Tower", lev, 
					idleParticleFactorys, idleParticleCooldowns, 0f, 0f, 
					shotParticleFactorys, shotParticleCounts, 0f, 0f);
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
			particleFactories[0] = new ParticleFactory(part_dust000, 0f, 360f, 0f, 0f);
			particleCooldowns[0] = 0.1f;
			initialParticleFactories = new ParticleFactory[1];
			initialParticleCounts = new int[1];
			initialParticleFactories[0] = new ParticleFactory(part_iceshard000, 0f, 0f, 0f, 0f);
			initialParticleCounts[0] = 16;
			ExplosionDef explosionDef = new ExplosionDef(imageArray, sizesArray, timesArray, 
					particleFactories, particleCooldowns, 1f, 1.5f, 
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
			particleFactories[0] = new ParticleFactory(part_dust001, 0f, 50f, 0f, 0f);
			particleCooldowns[0] = 0.1f;
			ProjectileDef rocketDef = new ProjectileDef(
					imageArray, sizesArray, timesArray, 4f, 5f, 
					explosionDef, particleFactories, particleCooldowns, 1f, 0.1f);

			// -------- effects ----------------------------------------------------
			// timed effects
			TimedEffectDef[] towerTimedEffects = new TimedEffectDef[1];
			towerTimedEffects[0] = new SlowEffectDef(0.2f, 4f, 5f);
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
			shotParticleFactorys[0] = new ParticleFactory(part_dust001, 180f, 50f, 0f, 0f);
			shotParticleCounts[0] = 7;
			// towers
			TowerDef t = new TowerDef(imageArray, sizesArray, 
					3.5f + lev, 0.5f + 0.5f * lev, 2.5f, 0.5f, 0.1f, 
					towerTimedEffects, towerInstantEffects, rocketDef, 
					TowerType.freezer, "Freezer Tower", lev, 
					idleParticleFactorys, idleParticleCooldowns, 0.2f, 0.5f, 
					shotParticleFactorys, shotParticleCounts, 1f, 1.5f);
			TOWER_FREEZER.add(t);
		}
	}
	
	private ArrayList<TowerDef> TOWER_CANNON = new ArrayList<>();
	private ArrayList<TowerDef> TOWER_FREEZER = new ArrayList<>();
	
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
		Image[] imageArray = new Image[1];
		float[] timesArray = new float[1];
		float[] sizesArray = new float[1];
		imageArray[0] = AssetLoader.getImage("mob.png", false);
		timesArray[0] = 0.5f;
		sizesArray[0] = 0.5f;
		return new MobDef(imageArray, timesArray, sizesArray, 
				2 * level, 0, 0, 0, 0, 1);
	}
}
