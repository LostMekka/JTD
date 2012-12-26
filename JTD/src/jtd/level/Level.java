/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jtd.level;

import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.logging.Logger;
import jtd.AssetLoader;
import jtd.CoordinateTransformator;
import jtd.PointF;
import jtd.def.GameDef;
import jtd.PointI;
import jtd.TDGameplayState;
import jtd.entities.Explosion;
import jtd.entities.Mob;
import jtd.entities.Particle;
import jtd.entities.Projectile;
import jtd.entities.Tower;
import jtd.def.TowerDef;
import org.newdawn.slick.BigImage;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.ImageData;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureImpl;

/**
 *
 * @author LostMekka
 */
public final class Level {
	
	public enum Field{
		wall, grass, floor, src, dest, srcAndDest;
	}
	
	public Field[][] fields;
	public Field[][][] fieldsData;
	public int[][] killCounts;
	public float[][] damageCounts;
	public int w, h;
	public GameDef def;
	
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
	
	private Image iWall, iGrass, iFloor, iSrc, iDest;
	private Image backgroundImage;
	private PointF fieldCenter;
		
	public Level(GameDef def){
		maxMobSize = 3;
		iWall = AssetLoader.getImage("wall.png", false);
		iGrass = AssetLoader.getImage("grass.png", false);
		iFloor = AssetLoader.getImage("floor.png", false);
		iSrc = AssetLoader.getImage("src.png", false);
		iDest = AssetLoader.getImage("dest.png", false);
		this.def = def;
		fields = new Field[24][32];
		h = fields.length;
		w = fields[0].length;
		for(int y=0; y<h; y++) for(int x=0; x<w; x++) fields[y][x] = Field.floor;
		fields[0][0] = Field.src;
		fields[h-1][w-1] = Field.dest;
		// init test stuff
		addTowerInternal(new PointI(15, 11), def.getTowerDef(GameDef.TowerType.freezer, 3));
		
		addTowerInternal(new PointI(10, 6), def.getTowerDef(GameDef.TowerType.repeater, 3));
		addTowerInternal(new PointI(20, 6), def.getTowerDef(GameDef.TowerType.repeater, 3));
		addTowerInternal(new PointI(10, 16), def.getTowerDef(GameDef.TowerType.repeater, 3));
		addTowerInternal(new PointI(20, 16), def.getTowerDef(GameDef.TowerType.repeater, 3));
		
		addTowerInternal(new PointI(0, 11), def.getTowerDef(GameDef.TowerType.cannon, 3));
		addTowerInternal(new PointI(29, 11), def.getTowerDef(GameDef.TowerType.cannon, 3));
		addTowerInternal(new PointI(15, 0), def.getTowerDef(GameDef.TowerType.cannon, 3));
		addTowerInternal(new PointI(15, 21), def.getTowerDef(GameDef.TowerType.cannon, 3));
		
		// init pathing graphs
		killCounts = new int[h][w];
		damageCounts = new float[h][w];
		sources = new ArrayList<>(maxMobSize);
		destinations = new ArrayList<>(maxMobSize);
		for(int i=0; i<maxMobSize; i++){
			sources.add(new LinkedList<PointI>());
			destinations.add(new LinkedList<PointI>());
		}
		fieldsData = new Field[maxMobSize][h][w];
		fieldsData[0] = fields;
		updateWalkables();
		pathingGraphs = new ArrayList<>(maxMobSize);
		for(int s=1; s<=maxMobSize; s++) pathingGraphs.add(new PathingGraph(s, this));
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
			g.drawLine(0, 0, 100, 30);
		} catch (SlickException ex) {
			backgroundImage = null;
			System.err.println("err!!!");
		}
		fieldCenter = new PointF(-0.5f, -0.5f);
	}
	
	private void updateWalkables(){
		fieldsData = new Field[maxMobSize][h][w];
		for(int s=0; s<maxMobSize; s++){
			LinkedList<PointI> src = sources.get(s);
			src.clear();
			LinkedList<PointI> dst = destinations.get(s);
			dst.clear();
			for(int y=0; y<h; y++){
				for(int x=0; x<w; x++){
					PointI p = new PointI(x, y);
					if(fieldsData[s][y][x] == Field.src) src.add(p);
					if(fieldsData[s][y][x] == Field.dest) dst.add(p);
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
							fieldsData[s][y][x] = Field.wall;
							continue;
						}
						switch(fields[y][x]){
							case dest:
								fieldsData[s][y][x] = Field.dest;
								dst.add(p);
								break;
							case src:
								fieldsData[s][y][x] = Field.src;
								src.add(p);
								break;
							case floor:
								fieldsData[s][y][x] = Field.floor;
								break;
							default:
								fieldsData[s][y][x] = Field.wall;
								break;
						}
					} else {
						// size is greater than 1. generate from previous size
						if((x < w-1) && (y < h-1)){
							// field is not on the border. lookup previous fields
							boolean bw = fieldsDataNeighboursContain(s-1, x, y, Field.wall);
							if(bw){
								fieldsData[s][y][x] = Field.wall;
								continue;
							}
							boolean bs = fieldsDataNeighboursContain(s-1, x, y, Field.src);
							boolean bd = fieldsDataNeighboursContain(s-1, x, y, Field.dest);
							boolean bsd = fieldsDataNeighboursContain(s-1, x, y, Field.srcAndDest);
							if(bsd || (bs && bd)){
								fieldsData[s][y][x] = Field.srcAndDest;
								src.add(p);
								dst.add(p);
								continue;
							}
							if(bs){
								fieldsData[s][y][x] = Field.src;
								src.add(p);
								continue;
							}
							if(bd){
								fieldsData[s][y][x] = Field.dest;
								dst.add(p);
								continue;
							}
							fieldsData[s][y][x] = Field.floor;
							continue;
						} else {
							// border fields are walls by default
							fieldsData[s][y][x] = Field.wall;
							continue;
						}
					}
				}
			}
		}
	}
	
	private boolean fieldsDataNeighboursContain(int s, int x, int y, Field f){
		return (fieldsData[s][y][x] == f) || 
				(fieldsData[s][y+1][x] == f) || 
				(fieldsData[s][y][x+1] == f) || 
				(fieldsData[s][y+1][x+1] == f);
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
	
	public void draw(Graphics g, CoordinateTransformator t){
		if(backgroundImage == null) constructBackground();
		PointF l = t.transformPoint(fieldCenter);
		g.drawImage(backgroundImage, l.x, l.y);
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
	
	public boolean isBuildable(PointI loc, int towerSize){
		if((fields[loc.y][loc.x] != Field.grass) && (fields[loc.y][loc.x] != Field.floor)){
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
				(fieldsData[mobSize-1][loc.y][loc.x] != Field.wall));
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
	
	private ArrayList<PathingGraph> pathingGraphs = null;
	public final int maxMobSize;
	
	public PathingGraph getPathingGraph(int mobSize){
		if((mobSize < 1) || (mobSize > pathingGraphs.size())){
			throw new RuntimeException("mobSize " + mobSize + " not supported!");
		}
		return pathingGraphs.get(mobSize - 1);
	}
	
	public void updatePaths(){
		for(int s=1; s<=maxMobSize; s++){
			pathingGraphs.get(s-1).generate(s, this);
		}
	}
	
	public long getLastPathUpdateDuration(){
		long ans = 0L;
		for(int s=1; s<=maxMobSize; s++){
			ans += pathingGraphs.get(s-1).lastTime;
		}
		return ans;
	}
	
}
