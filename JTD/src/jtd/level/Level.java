/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jtd.level;

import jtd.entities.Projectile;
import java.util.LinkedList;
import jtd.PointI;
import jtd.entities.Mob;
import jtd.entities.Tower;

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
	public LinkedList<Mob> mobs;
	public LinkedList<Projectile> projectiles;
	public int w, h;
	
	public Level(){
		fields = lev1;
		h = fields.length;
		w = fields[0].length;
		towers = new Tower[h][w];
		mobs = new LinkedList<>();
		projectiles = new LinkedList<>();
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
	
}
