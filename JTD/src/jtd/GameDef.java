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
import jtd.entities.ProjectileDef;
import jtd.entities.TowerDef;
import org.newdawn.slick.Image;

/**
 *
 * @author LostMekka
 */
public class GameDef {
	
	public enum TowerType{
		nailgun
	}
	
	public enum MobType{
		normal, swarm, armored
	}

	public GameDef() {
		// TODO: init
		Image body = AssetLoader.getImage("tower_body.png", false);
		Image head = AssetLoader.getImage("tower_head.png", false);
		Image proj = AssetLoader.getImage("shot.png", false);
		Image expl = AssetLoader.getImage("explosion.png", false);
		
		TimedEffectDef[] te = new TimedEffectDef[1];
		te[0] = new SlowEffectDef(0.5f, 3f, 5f);
		InstantEffect[] ie = new InstantEffect[0];
		
		Image[] es = new Image[1];
		float[] et = new float[1];
		es[0] = expl;
		et[0] = 0.5f;
		ExplosionDef e = new ExplosionDef(es, et);
		
		ProjectileDef p = new ProjectileDef(4f, 10f, proj, e);
		
		for(int lev=0; lev<3; lev++){
			TowerDef t = new TowerDef(4f, 0f, 0.5f, lev, te, ie, p, body, head);
			TOWER_NAILGUN.add(t);
		}
	}
	
	private ArrayList<TowerDef> TOWER_NAILGUN = new ArrayList<>();
	
	private ArrayList<MobDef> MOB_NORMAL = new ArrayList<>(200);
	private ArrayList<MobDef> BOSS_NORMAL = new ArrayList<>(200);

	private ArrayList<MobDef> MOB_SWARM = new ArrayList<>(200);
	private ArrayList<MobDef> BOSS_SWARM = new ArrayList<>(200);

	private ArrayList<MobDef> MOB_ARMORED = new ArrayList<>(200);
	private ArrayList<MobDef> BOSS_ARMORED = new ArrayList<>(200);
	
	public int getTowerCount(TowerType t){
		switch(t){
			case nailgun: return TOWER_NAILGUN.size();
			default: return -1;
		}
	}
	
	public TowerDef getTowerDef(TowerType t, int level){
		switch(t){
			case nailgun: return getTowerDef(TOWER_NAILGUN, level);
			default: return null;
		}
	}
	
	private TowerDef getTowerDef(ArrayList<TowerDef> list, int level){
		if((level < 0) || (level >= list.size())) return null;
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
		return list.get(level);
	}
	
	private MobDef generateMobDef(MobType t, int level, boolean boss){
		return new MobDef(level * 5, 0, 0, 0, 0, 3f, AssetLoader.getImage("mob.png", false));
	}
}
