/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jtd;

import java.util.ArrayList;
import jtd.entities.MobDef;
import jtd.entities.TowerDef;

/**
 *
 * @author LostMekka
 */
public class GameDef {
	
	private GameDef(){}
	
	public enum TowerType{
		nailgun
	}
	
	public enum MobType{
		normal, swarm, armored
	}
	
	private static ArrayList<TowerDef> TOWER_NAILGUN = new ArrayList<>();
	
	private static ArrayList<MobDef> MOB_NORMAL = new ArrayList<>(200);
	private static ArrayList<MobDef> BOSS_NORMAL = new ArrayList<>(200);

	private static ArrayList<MobDef> MOB_SWARM = new ArrayList<>(200);
	private static ArrayList<MobDef> BOSS_SWARM = new ArrayList<>(200);

	private static ArrayList<MobDef> MOB_ARMORED = new ArrayList<>(200);
	private static ArrayList<MobDef> BOSS_ARMORED = new ArrayList<>(200);
	
	public static void init(){
		// TODO: init
	}
	
	public static TowerDef getTowerDef(TowerType t, int level){
		switch(t){
			case nailgun: return getTowerDef(TOWER_NAILGUN, level);
			default: return null;
		}
	}
	
	private static TowerDef getTowerDef(ArrayList<TowerDef> list, int level){
		if((level < 0) || (level >= list.size())) return null;
		return list.get(level);
	}
	
	public static MobDef getMobDef(MobType t, int level, boolean boss){
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
	
	private static MobDef getMobDef(ArrayList<MobDef> list, MobType t, int level, boolean boss){
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
	
	private static MobDef generateMobDef(MobType t, int level, boolean boss){
		// TODO: gen mob def
		return null;
	}
}
