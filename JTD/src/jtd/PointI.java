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
	
	public PointF getPointF(float randomComponent){
		return new PointF(
				x + (ran.nextFloat() - 0.5f) * randomComponent , 
				y + (ran.nextFloat() - 0.5f) * randomComponent);
	}
	
	public PointF getPointF(){
		return new PointF(x, y);
	}
	
	public int hammingDistanceTo(PointI p){
		return Math.abs(x - p.x) + Math.abs(y - p.y);
	}

	public int hammingLength(){
		return Math.abs(x) + Math.abs(y);
	}
	
	public float distanceTo(PointI p){
		return (float)Math.sqrt((x-p.x)*(x-p.x) + (y-p.y)*(y-p.y));
	}

	public float length(){
		return (float)Math.sqrt(x*x + y*y);
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
