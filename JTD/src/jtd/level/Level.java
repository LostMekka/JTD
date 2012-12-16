/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jtd.level;

import java.awt.Point;
import java.util.LinkedList;
import jtd.AssetLoader;
import jtd.def.GameDef;
import jtd.PointI;
import jtd.entities.Explosion;
import jtd.entities.Mob;
import jtd.entities.Particle;
import jtd.entities.Projectile;
import jtd.entities.Tower;
import jtd.def.TowerDef;
import org.newdawn.slick.Image;

/**
 *
 * @author LostMekka
 */
public class Level {
	
	public enum Field{
		wall, grass, floor, src, dest;
	}
	
	private static final Field[][] lev1 = {
		{Field.src,   Field.floor, Field.floor, Field.floor, Field.floor, Field.floor, Field.floor, Field.floor, Field.src, Field.floor, Field.floor, Field.wall},
		{Field.grass, Field.floor, Field.grass, Field.floor, Field.floor, Field.floor, Field.floor, Field.wall, Field.floor, Field.floor, Field.floor, Field.floor},
		{Field.grass, Field.floor, Field.grass, Field.floor, Field.floor, Field.floor, Field.floor, Field.floor, Field.floor, Field.floor, Field.floor, Field.floor},
		{Field.grass, Field.floor, Field.grass, Field.floor, Field.floor, Field.floor, Field.floor, Field.floor, Field.floor, Field.floor, Field.floor, Field.floor},
		{Field.grass, Field.floor, Field.grass, Field.floor, Field.floor, Field.floor, Field.floor, Field.floor, Field.floor, Field.floor, Field.floor, Field.floor},
		{Field.grass, Field.floor, Field.grass, Field.floor, Field.floor, Field.floor, Field.grass, Field.floor, Field.floor, Field.floor, Field.floor, Field.floor},
		{Field.grass, Field.floor, Field.grass, Field.floor, Field.floor, Field.floor, Field.floor, Field.floor, Field.floor, Field.floor, Field.floor, Field.floor},
		{Field.grass, Field.floor, Field.grass, Field.floor, Field.floor, Field.floor, Field.floor, Field.floor, Field.floor, Field.floor, Field.floor, Field.floor},
		{Field.dest, Field.floor, Field.grass, Field.floor, Field.grass, Field.floor, Field.floor, Field.floor, Field.floor, Field.wall, Field.floor, Field.dest},
	};
	
	private static final Field[][] lev2 = {
		{Field.src,   Field.floor, Field.floor, Field.floor, Field.floor},
		{Field.floor, Field.floor, Field.wall, Field.floor, Field.floor},
		{Field.floor, Field.wall, Field.floor, Field.floor, Field.floor},
		{Field.grass, Field.grass, Field.floor, Field.grass, Field.grass},
		{Field.floor, Field.floor, Field.floor, Field.floor, Field.floor},
		{Field.floor, Field.floor, Field.floor, Field.floor, Field.floor},
		{Field.floor, Field.floor, Field.floor, Field.floor, Field.floor},
		{Field.grass, Field.grass, Field.floor, Field.grass, Field.grass},
		{Field.floor, Field.floor, Field.floor, Field.floor, Field.dest},
	};
	
	public Field[][] fields;
	public int[][] killCounts;
	public float[][] damageCounts;
	public Tower[][] towers;
	public int w, h;
	public GameDef def;

	public LinkedList<Mob> mobs = new LinkedList<>();
	public LinkedList<Projectile> projectiles = new LinkedList<>();
	public LinkedList<Explosion> explosions = new LinkedList<>();
	public LinkedList<Particle> particles = new LinkedList<>();
	public LinkedList<Particle> bgParticles = new LinkedList<>();
	
	public LinkedList<Mob> mobsToDelete = new LinkedList<>();
	public LinkedList<Projectile> projectilesToDelete = new LinkedList<>();
	public LinkedList<Explosion> explosionsToDelete = new LinkedList<>();
	public LinkedList<Particle> particlesToDelete = new LinkedList<>();

	public LinkedList<PointI> sources, destinations;
	
	private Image iWall, iGrass, iFloor, iSrc, iDest;
		
	public Level(GameDef def){
		iWall = AssetLoader.getImage("wall.png", false);
		iGrass = AssetLoader.getImage("grass.png", false);
		iFloor = AssetLoader.getImage("floor.png", false);
		iSrc = AssetLoader.getImage("src.png", false);
		iDest = AssetLoader.getImage("dest.png", false);
		this.def = def;
		fields = lev1;
		h = fields.length;
		w = fields[0].length;
		towers = new Tower[h][w];
		// init pathing stuff
		killCounts = new int[h][w];
		damageCounts = new float[h][w];
		sources = new LinkedList<>();
		destinations = new LinkedList<>();
		for(int x=0; x<w; x++){
			for(int y=0;y<h; y++){
				if(fields[y][x] == Field.src) sources.add(new PointI(x, y));
				if(fields[y][x] == Field.dest) destinations.add(new PointI(x, y));
			}
		}
		// init test stuff
		addTower(new PointI(0, 3), def.getTowerDef(GameDef.TowerType.repeater, 4));
		addTower(new PointI(0, 4), def.getTowerDef(GameDef.TowerType.cannon, 4));
		addTower(new PointI(0, 5), def.getTowerDef(GameDef.TowerType.repeater, 4));
		addTower(new PointI(2, 1), def.getTowerDef(GameDef.TowerType.repeater, 2));

		addTower(new PointI(6, 5), def.getTowerDef(GameDef.TowerType.repeater, 4));
		addTower(new PointI(8, 3), def.getTowerDef(GameDef.TowerType.cannon, 1));
		addTower(new PointI(10, 2), def.getTowerDef(GameDef.TowerType.repeater, 2));
		addTower(new PointI(10, 5), def.getTowerDef(GameDef.TowerType.cannon, 2));
		addTower(new PointI(11, 4), def.getTowerDef(GameDef.TowerType.freezer, 2));
	}
	
	public void damageDealtAt(PointI p, float damage){
		damageCounts[p.y][p.x] += damage;
	}
	
	public void killHappenedAt(PointI p){
		killCounts[p.y][p.x]++;
	}
	
	public float getPathingWeightAt(PointI p){
		return killCounts[p.y][p.x] + damageCounts[p.y][p.x] / 100f;
	}
	
	public Image getTileImage(int x, int y){
		switch(fields[y][x]){
			case dest: return iDest;
			case floor: return iFloor;
			case grass: return iGrass;
			case src: return iSrc;
			case wall: return iWall;
			default: return null;
		}
	}
	
	public boolean isBuildable(PointI loc){
		for(Mob m:mobs){
			if(m.loc.equals(loc)) return false;
		}
		return ((fields[loc.y][loc.x] == Field.grass) || (fields[loc.y][loc.x] == Field.floor)) && (towers[loc.y][loc.x] == null);
	}
	
	public boolean isWalkable(PointI loc){
		return (
				(loc.x >= 0) &&
				(loc.y >= 0) &&
				(loc.x < w) &&
				(loc.y < h) && 
				((fields[loc.y][loc.x] == Field.floor) || 
				(fields[loc.y][loc.x] == Field.src) || 
				(fields[loc.y][loc.x] == Field.dest)) &&
				(towers[loc.y][loc.x] == null)
				);
	}
	
	public LinkedList<PointI> getWalkableTilesFrom(PointI p){
		LinkedList<PointI> ans = new LinkedList<>();
		if(!isWalkable(p)) return ans;
		PointI pr = new PointI(p.x + 1, p.y);
		PointI pl = new PointI(p.x - 1, p.y);
		PointI pu = new PointI(p.x, p.y + 1);
		PointI pd = new PointI(p.x, p.y - 1);
		PointI pru = new PointI(p.x + 1, p.y + 1);
		PointI plu = new PointI(p.x - 1, p.y + 1);
		PointI prd = new PointI(p.x + 1, p.y - 1);
		PointI pld = new PointI(p.x - 1, p.y - 1);
		boolean r = isWalkable(pr);
		boolean l = isWalkable(pl);
		boolean u = isWalkable(pu);
		boolean d = isWalkable(pd);
		if(r) ans.add(pr);
		if(l) ans.add(pl);
		if(u) ans.add(pu);
		if(d) ans.add(pd);
		if(r && u) ans.add(pru);
		if(l && u) ans.add(plu);
		if(r && d) ans.add(prd);
		if(l && d) ans.add(pld);
		return ans;
	}
	
	public boolean addTower(PointI loc, TowerDef def){
		if(towers[loc.y][loc.x] != null) return false;
		towers[loc.y][loc.x] = new Tower(def, loc.getPointF());
		return true;
	}
	
	public boolean removeTower(Tower t){
		for(int y=0; y<h; y++){
			for(int x=0; x<w; x++){
				if(towers[y][x] == t){
					towers[y][x] = null;
					return true;
				}
			}
		}
		return false;
	}
	
	public void markMobForDeletion(Mob m){
		mobsToDelete.add(m);
	}
	
	public void markProjectileForDeletion(Projectile p){
		projectilesToDelete.add(p);
	}
	
	public void markExplosionForDeletion(Explosion e){
		explosionsToDelete.add(e);
	}
	
	public void markParticleForDeletion(Particle p){
		particlesToDelete.add(p);
	}
	
	public void deleteMarkedEntities(){
		for(Mob m:mobsToDelete) mobs.remove(m);
		for(Projectile p:projectilesToDelete) projectiles.remove(p);
		for(Explosion e:explosionsToDelete) explosions.remove(e);
		for(Particle p:particlesToDelete){
			particles.remove(p);
			bgParticles.remove(p);
		}
		mobsToDelete.clear();
		projectilesToDelete.clear();
		explosionsToDelete.clear();
		particlesToDelete.clear();
	}
	
}
