/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jtd.level;

import jtd.entities.Projectile;
import java.util.LinkedList;
import jtd.AssetLoader;
import jtd.GameDef;
import jtd.PointI;
import jtd.entities.Explosion;
import jtd.entities.Mob;
import jtd.entities.Particle;
import jtd.entities.Tower;
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
		{Field.src,   Field.floor, Field.floor, Field.floor, Field.floor},
		{Field.floor, Field.floor, Field.floor, Field.floor, Field.floor},
		{Field.floor, Field.floor, Field.floor, Field.floor, Field.floor},
		{Field.grass, Field.grass, Field.floor, Field.grass, Field.grass},
		{Field.floor, Field.floor, Field.floor, Field.floor, Field.floor},
		{Field.floor, Field.floor, Field.floor, Field.floor, Field.floor},
		{Field.floor, Field.floor, Field.floor, Field.floor, Field.floor},
		{Field.grass, Field.grass, Field.floor, Field.grass, Field.grass},
		{Field.floor, Field.floor, Field.floor, Field.floor, Field.floor},
		{Field.floor, Field.floor, Field.floor, Field.floor, Field.floor},
		{Field.floor, Field.floor, Field.floor, Field.floor, Field.floor},
		{Field.floor, Field.floor, Field.floor, Field.floor, Field.dest},
	};
	
	public Field[][] fields;
	public Tower[][] towers;
	public LinkedList<Mob> mobs = new LinkedList<>();
	public LinkedList<Projectile> projectiles = new LinkedList<>();
	public LinkedList<Explosion> explosions = new LinkedList<>();
	public LinkedList<Particle> particles = new LinkedList<>();
	public int w, h;
	public GameDef def;
	private Image iWall, iGrass, iFloor, iSrc, iDest;
	
	public Level(GameDef def){
		iWall = AssetLoader.getImage("wall.png");
		iGrass = AssetLoader.getImage("grass.png");
		iFloor = AssetLoader.getImage("floor.png");
		iSrc = AssetLoader.getImage("src.png");
		iDest = AssetLoader.getImage("dest.png");
		this.def = def;
		fields = lev1;
		h = fields.length;
		w = fields[0].length;
		towers = new Tower[h][w];
		// init test stuff
		
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
	
	public boolean iWalkable(PointI loc){
		return (fields[loc.y][loc.x] == Field.floor) && (towers[loc.y][loc.x] == null);
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
	
}
