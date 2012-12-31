/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jtd.level;

import java.util.ArrayList;
import java.util.LinkedList;
import jtd.AssetLoader;
import jtd.CoordinateTransformator;
import jtd.PointF;
import jtd.PointI;
import jtd.TDGameplayState;
import jtd.def.GameDef;
import jtd.def.TowerDef;
import jtd.entities.Explosion;
import jtd.entities.Mob;
import jtd.entities.Particle;
import jtd.entities.Projectile;
import jtd.entities.Tower;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

/**
 *
 * @author LostMekka
 */
public class LevelDataHolder {
	
	public static final float DAMAGE_COST_MULTIPLIER = 0.05f;
	
	public Level.Field[][][] fieldsData;
	public float[][] pathingWeights;
	public int w, h;
	
	public GameDef def;
	public Level level;
	
	public LinkedList<Mob> mobs = new LinkedList<>();
	public LinkedList<Tower> towers = new LinkedList<>();
	public LinkedList<Projectile> projectiles = new LinkedList<>();
	public LinkedList<Explosion> explosions = new LinkedList<>();
	public LinkedList<Particle> particles = new LinkedList<>();
	public LinkedList<Particle> bgParticles = new LinkedList<>();
	
	public LinkedList<Mob> mobsToDelete = new LinkedList<>();
	public LinkedList<Projectile> projectilesToDelete = new LinkedList<>();
	public LinkedList<Explosion> explosionsToDelete = new LinkedList<>();
	public LinkedList<Particle> particlesToDelete = new LinkedList<>();

	public ArrayList<LinkedList<PointI>> sources, destinations;
	
	private ArrayList<PathingGraph> pathingGraphs = null;
	
	private Image iWall, iGrass, iFloor, iSrc, iDest;
	private Image backgroundImage;
	private PointF backgroundLoc;

	public LevelDataHolder(GameDef def, Level level) {
		this.def = def;
		this.level = level;
		h = level.fields.length;
		w = level.fields[0].length;
		iWall = AssetLoader.getImage("wall.png", false);
		iGrass = AssetLoader.getImage("grass.png", false);
		iFloor = AssetLoader.getImage("floor.png", false);
		iSrc = AssetLoader.getImage("src.png", false);
		iDest = AssetLoader.getImage("dest.png", false);
		// init test stuff
//		addTowerInternal(new PointI(15, 11), def.getTowerDef(GameDef.TowerType.freezer, 3));
//		
//		addTowerInternal(new PointI(10, 6), def.getTowerDef(GameDef.TowerType.repeater, 3));
//		addTowerInternal(new PointI(20, 6), def.getTowerDef(GameDef.TowerType.repeater, 3));
//		addTowerInternal(new PointI(10, 16), def.getTowerDef(GameDef.TowerType.repeater, 3));
//		addTowerInternal(new PointI(20, 16), def.getTowerDef(GameDef.TowerType.repeater, 3));
//		
//		addTowerInternal(new PointI(0, 11), def.getTowerDef(GameDef.TowerType.cannon, 3));
//		addTowerInternal(new PointI(29, 11), def.getTowerDef(GameDef.TowerType.cannon, 3));
//		addTowerInternal(new PointI(15, 0), def.getTowerDef(GameDef.TowerType.cannon, 3));
	
		addTowerInternal(new PointI(7, 7), def.getTowerDef(GameDef.TowerType.repeater, 4));
		addTowerInternal(new PointI(12, 6), def.getTowerDef(GameDef.TowerType.repeater, 4));
		addTowerInternal(new PointI(17, 6), def.getTowerDef(GameDef.TowerType.repeater, 4));
		addTowerInternal(new PointI(22, 5), def.getTowerDef(GameDef.TowerType.repeater, 3));

		addTowerInternal(new PointI(23, 15), def.getTowerDef(GameDef.TowerType.repeater, 4));
		addTowerInternal(new PointI(18, 16), def.getTowerDef(GameDef.TowerType.repeater, 4));
		addTowerInternal(new PointI(13, 16), def.getTowerDef(GameDef.TowerType.repeater, 4));
		addTowerInternal(new PointI(8, 17), def.getTowerDef(GameDef.TowerType.repeater, 3));
		
		addTowerInternal(new PointI(0, 7), def.getTowerDef(GameDef.TowerType.cannon, 2));
		addTowerInternal(new PointI(29, 14), def.getTowerDef(GameDef.TowerType.cannon, 2));
		
		addTowerInternal(new PointI(26, 3), def.getTowerDef(GameDef.TowerType.freezer, 4));
		addTowerInternal(new PointI(4, 19), def.getTowerDef(GameDef.TowerType.freezer, 4));
		
		// init pathing graphs
		pathingWeights = new float[h][w];
		sources = new ArrayList<>(level.maxMobSize);
		destinations = new ArrayList<>(level.maxMobSize);
		for(int i=0; i<level.maxMobSize; i++){
			sources.add(new LinkedList<PointI>());
			destinations.add(new LinkedList<PointI>());
		}
		fieldsData = new Level.Field[level.maxMobSize][h][w];
		fieldsData[0] = level.fields;
		updateWalkables();
		pathingGraphs = new ArrayList<>(level.maxMobSize);
		for(int s=1; s<=level.maxMobSize; s++) pathingGraphs.add(new PathingGraph(s, this));
	}
		

	public void constructBackground(){
		try {
			float ts = TDGameplayState.TILE_SIZE;
			backgroundImage = new Image((int)(ts * w), (int)(ts * h));
			Graphics g = backgroundImage.getGraphics();
			for(int y=0; y<h; y++){
				for(int x=0; x<w; x++){
					g.drawImage(getTileImage(x, y), x*ts, y*ts);
				}
			}
			g.flush();
			//backgroundImage.setFilter(1);
		} catch (SlickException ex) {
			backgroundImage = null;
			System.err.println("err!!!");
		}
		backgroundLoc = new PointF(-0.5f, -0.5f);
	}
	
	public void draw(Graphics g, CoordinateTransformator t){
		if(backgroundImage == null) constructBackground();
		PointF p1 = t.transformPoint(backgroundLoc);
		backgroundImage.draw(p1.x, p1.y, t.transformLength(1f/TDGameplayState.TILE_SIZE));
	}
	
	private void updateWalkables(){
		fieldsData = new Level.Field[level.maxMobSize][h][w];
		for(int s=0; s<level.maxMobSize; s++){
			LinkedList<PointI> src = sources.get(s);
			src.clear();
			LinkedList<PointI> dst = destinations.get(s);
			dst.clear();
			for(int y=0; y<h; y++){
				for(int x=0; x<w; x++){
					PointI p = new PointI(x, y);
					if(fieldsData[s][y][x] == Level.Field.src) src.add(p);
					if(fieldsData[s][y][x] == Level.Field.dest) dst.add(p);
					if(s == 0){
						// size is 1. simply copy level
						boolean blocked = false;
						for(Tower t:towers){
							PointI pt = t.getPointI();
							if((p.x >= pt.x) && (p.x < pt.x + t.entitySize) && (p.y >= pt.y) && (p.y < pt.y + t.entitySize)){
								blocked = true;
								break;
							}
						}
						if(blocked){
							fieldsData[s][y][x] = Level.Field.wall;
							continue;
						}
						switch(level.fields[y][x]){
							case dest:
								fieldsData[s][y][x] = Level.Field.dest;
								dst.add(p);
								break;
							case src:
								fieldsData[s][y][x] = Level.Field.src;
								src.add(p);
								break;
							case floor:
								fieldsData[s][y][x] = Level.Field.floor;
								break;
							default:
								fieldsData[s][y][x] = Level.Field.wall;
								break;
						}
					} else {
						// size is greater than 1. generate from previous size
						if((x < w-1) && (y < h-1)){
							// field is not on the border. lookup previous fields
							boolean bw = fieldsDataNeighboursContain(s-1, x, y, Level.Field.wall);
							if(bw){
								fieldsData[s][y][x] = Level.Field.wall;
								continue;
							}
							boolean bs = fieldsDataNeighboursContain(s-1, x, y, Level.Field.src);
							boolean bd = fieldsDataNeighboursContain(s-1, x, y, Level.Field.dest);
							boolean bsd = fieldsDataNeighboursContain(s-1, x, y, Level.Field.srcAndDest);
							if(bsd || (bs && bd)){
								fieldsData[s][y][x] = Level.Field.srcAndDest;
								src.add(p);
								dst.add(p);
								continue;
							}
							if(bs){
								fieldsData[s][y][x] = Level.Field.src;
								src.add(p);
								continue;
							}
							if(bd){
								fieldsData[s][y][x] = Level.Field.dest;
								dst.add(p);
								continue;
							}
							fieldsData[s][y][x] = Level.Field.floor;
							continue;
						} else {
							// border fields are walls by default
							fieldsData[s][y][x] = Level.Field.wall;
							continue;
						}
					}
				}
			}
		}
	}
	
	private boolean fieldsDataNeighboursContain(int s, int x, int y, Level.Field f){
		return (fieldsData[s][y][x] == f) || 
				(fieldsData[s][y+1][x] == f) || 
				(fieldsData[s][y][x+1] == f) || 
				(fieldsData[s][y+1][x+1] == f);
	}
	
	public void damageDealtAt(PointI p, float damage, int mobSize){
		if((p.x < 0) || (p.y < 0) || (p.x >= w) || (p.y >= h)) return;
		float amount = damage / mobSize * mobSize * DAMAGE_COST_MULTIPLIER;
		for(int y=0; (y<mobSize)&&(p.y+y<h); y++){
			for(int x=0; (x<mobSize)&&(p.x+x<w); x++){
				pathingWeights[p.y + y][p.x + x] += amount;
			}
		}
	}
	
	public void killHappenedAt(PointI p, int mobSize){
		if((p.x < 0) || (p.y < 0) || (p.x >= w) || (p.y >= h)) return;
		float amount = 1f / mobSize / mobSize;
		for(int y=0; y<mobSize; y++){
			for(int x=0; x<mobSize; x++){
				pathingWeights[p.y+ y][p.x + x] += amount;
			}
		}
	}
	
	public float getPathingWeightAt(PointI p){
		return pathingWeights[p.y][p.x];
	}
	
	public float getMaxPathingWeight(){
		float ans = 0f;
		for(int y=0; y<h; y++){
			for(int x=0; x<w; x++){
				float f = pathingWeights[y][x];
				if(f > ans) ans = f;
			}
		}
		return ans;
	}
	
	public Image getTileImage(int x, int y){
		switch(level.fields[y][x]){
			case dest: return iDest;
			case floor: return iFloor;
			case grass: return iGrass;
			case src: return iSrc;
			case wall: return iWall;
			default: return null;
		}
	}
	
	public boolean isBuildable(PointI loc, int towerSize){
		if((level.fields[loc.y][loc.x] != Level.Field.grass) && 
				(level.fields[loc.y][loc.x] != Level.Field.floor)){
			return false;
		}
		for(Mob m:mobs){
			int s = m.def.size;
			PointI p = m.getPointI();
			if(		(loc.x + towerSize > p.x) && (p.x + s > loc.x) && 
					(loc.y + towerSize > p.y) && (p.y + s > loc.y)){
				return false;
			}
		}
		for(Tower t:towers){
			int s = t.def.size;
			PointI p = t.getPointI();
			if(		(loc.x + towerSize > p.x) && (p.x + s > loc.x) && 
					(loc.y + towerSize > p.y) && (p.y + s > loc.y)){
				return false;
			}
		}
		// TODO: check for path blockage
		return true;
	}
	
	public boolean isWalkable(PointI loc, int mobSize){
		return ((loc.x >= 0) && (loc.y >= 0) && (loc.x < w) && (loc.y < h) && 
				(fieldsData[mobSize-1][loc.y][loc.x] != Level.Field.wall));
	}
	
	public LinkedList<PointI> getWalkableTilesFrom(PointI p, int mobSize){
		LinkedList<PointI> ans = new LinkedList<>();
		if(!isWalkable(p, mobSize)) return ans;
		PointI pr = new PointI(p.x + 1, p.y);
		PointI pl = new PointI(p.x - 1, p.y);
		PointI pu = new PointI(p.x, p.y + 1);
		PointI pd = new PointI(p.x, p.y - 1);
		PointI pru = new PointI(p.x + 1, p.y + 1);
		PointI plu = new PointI(p.x - 1, p.y + 1);
		PointI prd = new PointI(p.x + 1, p.y - 1);
		PointI pld = new PointI(p.x - 1, p.y - 1);
		boolean r = isWalkable(pr, mobSize);
		boolean l = isWalkable(pl, mobSize);
		boolean u = isWalkable(pu, mobSize);
		boolean d = isWalkable(pd, mobSize);
		if(r) ans.add(pr);
		if(l) ans.add(pl);
		if(u) ans.add(pu);
		if(d) ans.add(pd);
		if(r && u && isWalkable(pru, mobSize)) ans.add(pru);
		if(l && u && isWalkable(plu, mobSize)) ans.add(plu);
		if(r && d && isWalkable(prd, mobSize)) ans.add(prd);
		if(l && d && isWalkable(pld, mobSize)) ans.add(pld);
		return ans;
	}
	
	public boolean addTower(PointI loc, TowerDef def){
		boolean ans = addTowerInternal(loc, def);
		if(ans){
			updateWalkables();
			updatePaths();
		}
		return true;
	}
	
	public boolean removeTower(Tower t){
		boolean ans = removeTowerInternal(t);
		if(ans){
			updateWalkables();
			updatePaths();
		}
		return true;
	}
	
	private boolean addTowerInternal(PointI loc, TowerDef def){
		if(!isBuildable(loc, def.size)) return false;
		return towers.add(new Tower(def, loc));
	}
	
	private boolean removeTowerInternal(Tower t){
		return towers.remove(t);
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
	
	public PathingGraph getPathingGraph(int mobSize){
		if((mobSize < 1) || (mobSize > pathingGraphs.size())){
			throw new RuntimeException("mobSize " + mobSize + " not supported!");
		}
		return pathingGraphs.get(mobSize - 1);
	}
	
	public void updatePaths(){
		for(int s=1; s<=level.maxMobSize; s++){
			pathingGraphs.get(s-1).generate(s, this);
		}
	}
	
	public long getLastPathUpdateDuration(){
		long ans = 0L;
		for(int s=1; s<=level.maxMobSize; s++){
			ans += pathingGraphs.get(s-1).lastTime;
		}
		return ans;
	}
	
}
