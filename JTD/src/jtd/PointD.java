/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jtd;

/**
 *
 * @author LostMekka
 */
public class PointD {
	
	public double x, y;

	public PointD(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public PointD() {
		x = 0f;
		y = 0f;
	}
	
	public PointI getPointI(int entitySize){
		double offset = (double)(entitySize - 1) / 2f - 0.001f;
		return new PointI((int)Math.round(x - offset), (int)Math.round(y - offset));
	}
	
	public PointI getPointI(){
		return getPointI(1);
	}
	
	public double distanceTo(PointD p){
		return (double)Math.sqrt((x - p.x)*(x - p.x) + (y - p.y)*(y - p.y));
	}

	public double quadraticDistanceTo(PointD p){
		return (x - p.x)*(x - p.x) + (y - p.y)*(y - p.y);
	}

	public double hammingDistanceTo(PointD p){
		return Math.abs(x - p.x) + Math.abs(y - p.y);
	}

	public double length(){
		return (double)Math.sqrt(x*x + y*y);
	}
	
	public double travelTo(PointD p, double travelDistance, boolean stopAtArrival){
		double dx = p.x - x, dy = p.y - y;
		double d = (double)Math.sqrt(dx*dx + dy*dy);
		if((travelDistance >= d) && stopAtArrival){
			x = p.x;
			y = p.y;
			return d;
		}
		x += dx * travelDistance / d;
		y += dy * travelDistance / d;
		return travelDistance;
	}

	public void travelInDirection(double travelDirection, double travelDistance){
		double dirInPi = travelDirection / 180d * Math.PI;
		x += travelDistance * (double)Math.cos(dirInPi);
		y += travelDistance * (double)Math.sin(dirInPi);
	}

	public double getRotationTo(PointD p){
		if(p.x == x){
			if(p.y < y){
				return 180f;
			} else {
				return 0f;
			}
		}
		return (double)(Math.atan2(p.y - y, p.x - x) / Math.PI) * 180f;
	}
	
	public void rotate(double amount){
		amount = amount / 180f * (double)Math.PI;
		double tmp = x * (double)Math.cos(amount) - y * (double)Math.sin(amount);
		y = x * (double)Math.sin(amount) + y * (double)Math.cos(amount);
		x = tmp;
	}
	
	public void add(PointD p){
		x += p.x;
		y += p.y;
	}
	
	public void multiply(double f){
		x *= f;
		y *= f;
	}
	
	public boolean isInSameTileWith(PointD p){
		return ((Math.round(x) == Math.round(p.x)) && (Math.round(y) == Math.round(p.y)));
	}
	
	@Override
	public PointD clone(){
		return new PointD(x, y);
	}

	@Override
	public String toString() {
		return "PointF{" + "x=" + x + ", y=" + y + '}';
	}
	
}
