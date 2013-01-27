/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jtd.level;

import java.util.ArrayList;
import java.util.LinkedList;
import jtd.AssetLoader;
import jtd.CoordinateTransformator;
import jtd.PointD;
import jtd.PointI;
import jtd.TDGameplayState;
import jtd.def.GameDef;
import jtd.def.TowerDef;
import jtd.entities.Explosion;
import jtd.entities.Mob;
import jtd.entities.Particle;
import jtd.entities.Projectile;
import jtd.entities.Tower;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

/**
 *
 * @author LostMekka
 */
public class LevelDataHolder {
	
	public static final double DAMAGE_COST_MULTIPLIER = 0.05f;
	
	public Level.Field[][][] fieldsData;
	public double[][] normalKillCounts;
	public double[][] splashKillCounts;
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
	private PointD backgroundLoc;

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
		// init pathing graphs
		normalKillCounts = new double[h][w];
		splashKillCounts = new double[h][w];
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
			double ts = TDGameplayState.TILE_SIZE;
			backgroundImage = new Image((int)(ts * w), (int)(ts * h));
			Graphics g = backgroundImage.getGraphics();
			Graphics.setCurrent(g);
			for(int y=0; y<h; y++){
				for(int x=0; x<w; x++){
					g.drawImage(getTileImage(x, y), (float)(x*ts), (float)(y*ts));
				}
			}
			g.flush();
			//backgroundImage.setFilter(1);
		} catch (SlickException ex) {
			backgroundImage = null;
			System.err.println("err!!!");
		}
		backgroundLoc = new PointD(-0.5f, -0.5f);
	}
	
	public void draw(Graphics g, CoordinateTransformator t){
		if(backgroundImage == null) constructBackground();
		PointD p1 = t.transformPoint(backgroundLoc);
		backgroundImage.draw((float)p1.x, (float)p1.y, (float)t.transformLength(1d/TDGameplayState.TILE_SIZE));
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
							if((p.x >= pt.x) && (p.x < pt.x + t.getSize()) && (p.y >= pt.y) && (p.y < pt.y + t.getSize())){
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
	
	public void killHappenedAt(PointI p, int mobSize, boolean wasSplashDamage){
		if((p.x < 0) || (p.y < 0) || (p.x >= w) || (p.y >= h)) return;
		double amount = 1f / mobSize / mobSize;
		for(int y=0; y<mobSize; y++){
			for(int x=0; x<mobSize; x++){
				if(wasSplashDamage){
					splashKillCounts[p.y + y][p.x + x] += amount;
				} else {
					normalKillCounts[p.y + y][p.x + x] += amount;
				}
			}
		}
	}
	
	public double getSplashDamageRate(PointI p, int mobSize){
		double sd = 0d, nd = 0d;
		for(int x=p.x; x<p.x+mobSize; x++){
			for(int y=p.y; y<p.y+mobSize; y++){
				sd += splashKillCounts[y][x];
				nd += normalKillCounts[y][x];
			}
		}
		if(sd == 0d) return 1d;
		if(nd == 0d) return 0d;
		return sd / (sd + nd);
	}
	
	public double getPathingWeightAt(PointI p){
		return getPathingWeightAt(p.x, p.y);
	}
	
	public double getPathingWeightAt(int x, int y){
		return splashKillCounts[y][x] + normalKillCounts[y][x];
	}
	
	public double getMaxPathingWeight(){
		double ans = 0f;
		for(int y=0; y<h; y++){
			for(int x=0; x<w; x++){
				double f = getPathingWeightAt(x, y);
				if(f > ans) ans = f;
			}
		}
		return ans;
	}
	
	public void resetPathingWeights(){
		for(int y=0; y<h; y++){
			for(int x=0; x<w; x++){
				normalKillCounts[y][x] = 0d;
				normalKillCounts[y][x] = 0d;
			}
		}
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
	
	public Color getTileColor(int x, int y, int pixelSize){
		float r = 0, g = 0, b = 0, n = 0;
		for(int x2=x; x2<x+pixelSize; x2++){
			for(int y2=y; y2<y+pixelSize; y2++){
				if((x2 >= w) || (y2 > h)) break;
				n++;
				switch(level.fields[y][x]){
					case dest: b += 1f; break;
					case floor: r += 0.7f; g += 0.7f; b += 0.7f; break;
					case grass: g += 1f; break;
					case src: r += 1f; g += 0.3f; b += 0.3f; break;
					case wall: r += 0.4f; g += 0.4f; b += 0.4f; break;
				}
			}
		}
		return new Color(r/n, g/n, b/n);
	}
	
	public boolean isBuildable(PointI loc, int towerSize){
		// check coordinates
		if((loc.x < 0) || (loc.x + towerSize > w) 
				|| (loc.y < 0) || (loc.y + towerSize > h)) return false;
		// check fields
		for(int x=loc.x; x<loc.x+towerSize; x++){
			for(int y=loc.y; y<loc.y+towerSize; y++){
				if((level.fields[y][x] != Level.Field.grass) && 
						(level.fields[y][x] != Level.Field.floor)){
					return false;
				}
			}
		}
		// check towers
		for(Tower t:towers){
			int s = t.def.size;
			PointI p = t.getPointI();
			if(		(loc.x + towerSize > p.x) && (p.x + s > loc.x) && 
					(loc.y + towerSize > p.y) && (p.y + s > loc.y)){
				return false;
			}
		}
		// check mobs
		for(Mob m:mobs){
			int s = m.def.size;
			PointI p = m.getPointI();
			if(		(loc.x + towerSize > p.x) && (p.x + s > loc.x) && 
					(loc.y + towerSize > p.y) && (p.y + s > loc.y)){
				return false;
			}
		}
		return true;
	}
	
	public boolean isBlocking(PointI loc, int towerSize){
		// add temporary tower
		TowerDef td = new TowerDef();
		td.size = towerSize;
		Tower t = new Tower(td, loc);
		addTowerInternal(t);
		// generate new walkables
		Level.Field[][][] walkables = fieldsData;
		updateWalkables();
		// generate new pathing graph for biggest mob
		PathingGraph g = new PathingGraph(level.maxMobSize, this);
		// cleanup
		fieldsData = walkables;
		removeTower(t);
		return g.startingPoints.isEmpty();
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
	
	public boolean addTower(Tower t){
		if(!isBuildable(t.getPointI(), t.def.size)) return false;
		boolean ans = addTowerInternal(t);
		if(ans){
			updateWalkables();
			updatePaths();
		}
		return true;
	}
	
	public boolean removeTower(Tower t){
		if(!removeTowerInternal(t)) return false;
		updateWalkables();
		updatePaths();
		for(Mob m:mobs){
			m.updatePath(false);
		}
		return true;
	}
	
	private boolean addTowerInternal(Tower t){
		return towers.add(t);
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
	
	private static final double lambda = -Math.log(2d) / 100;
	
	public void update(double time){
		for(Particle p:particles) p.tick(time);
		for(Particle p:bgParticles) p.tick(time);
		for(Explosion e:explosions) e.tick(time);
		for(Projectile p:projectiles) p.tick(time);
		for(Mob m:mobs) m.tick(time);
		deleteMarkedEntities();
		for(Tower t:towers) t.tick(time);
		double exp = Math.exp(lambda * time);
		for(int x=0; x<w; x++){
			for(int y=0; y<h; y++){
				normalKillCounts[y][x] *= exp;
				splashKillCounts[y][x] *= exp;
			}
		}
	}
	
}
