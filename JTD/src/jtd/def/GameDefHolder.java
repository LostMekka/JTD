/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jtd.def;

import java.util.HashMap;

/**
 *
 * @author LostMekka
 */
public class GameDefHolder {

	private HashMap<String, ParticleDef> particleDefs = new HashMap<>();
	private HashMap<String, ExplosionDef> explosionDefs = new HashMap<>();
	private HashMap<String, ProjectileDef> projectileDefs = new HashMap<>();
	private HashMap<String, MobDef> mobDefs = new HashMap<>();
	private HashMap<String, TowerDef> towerDefs = new HashMap<>();
	
	public GameDefHolder(String filename) {
		
	}

	public ParticleDef getParticleDef(String name) {
		return particleDefs.get(name);
	}
	
	public ExplosionDef getExplosionDef(String name) {
		return explosionDefs.get(name);
	}
	
	public ProjectileDef getProjectileDef(String name) {
		return projectileDefs.get(name);
	}
	
	public MobDef getMobDef(String name) {
		return mobDefs.get(name);
	}
	
	public TowerDef getTowerDef(String name) {
		return towerDefs.get(name);
	}
	
}
