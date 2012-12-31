/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jtd.level;

/**
 *
 * @author LostMekka
 */
public final class Level {
	
	public enum Field{
		wall, grass, floor, src, dest, srcAndDest;
	}
	
	public final int maxMobSize;
	public Field[][] fields;
	
	public Level(){
		maxMobSize = 3;
		fields = new Field[24][32];
		int h = fields.length;
		int w = fields[0].length;
		for(int y=0; y<h; y++) for(int x=0; x<w; x++) fields[y][x] = Field.floor;
		//fields[3][28] = Field.grass;
		fields[0][0] = Field.src;
		fields[h-1][w-1] = Field.dest;
	}
	
}
