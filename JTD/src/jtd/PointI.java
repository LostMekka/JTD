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
	
	public PointD getPointD(int entitySize, double randomComponent){
		double offset = ((double)entitySize - 1f) / 2f;
		return new PointD(
				x + offset + (ran.nextFloat() - 0.5d) * randomComponent, 
				y + offset + (ran.nextFloat() - 0.5d) * randomComponent);
	}
	
	public PointD getPointD(int entitySize){
		return getPointD(entitySize, 0f);
	}
	
	public PointD getPointD(double randomComponent){
		return getPointD(1, randomComponent);
	}
	
	public PointD getPointD(){
		return getPointD(1, 0f);
	}
	
	public int hammingDistanceTo(PointI p){
		return Math.abs(x - p.x) + Math.abs(y - p.y);
	}

	public int hammingLength(){
		return Math.abs(x) + Math.abs(y);
	}
	
	public double distanceTo(PointI p){
		return (double)Math.sqrt((x-p.x)*(x-p.x) + (y-p.y)*(y-p.y));
	}

	public double length(){
		return (double)Math.sqrt(x*x + y*y);
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
