/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jtd;

import java.util.Random;

/**
 *
 * @author LostMekka
 */
public class PointI {
	
	private static final Random ran = new Random();
	
	public int x, y;

	public PointI(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public PointI() {
		x = 0;
		y = 0;
	}
	
	public PointF getPountF(){
		return new PointF(
				x + (ran.nextFloat() - 0.5f) / 4f , 
				y + (ran.nextFloat() - 0.5f) / 4f);
	}
	
	public int distanceTo(PointI p){
		return Math.abs(x - p.x) + Math.abs(y - p.y);
	}

	public int length(){
		return Math.abs(x) + Math.abs(y);
	}
	
	@Override
	public PointI clone(){
		return new PointI(x, y);
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 89 * hash + this.x;
		hash = 89 * hash + this.y;
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final PointI p = (PointI) obj;
		return (x == p.x) && (y == p.y);
	}
	
}
