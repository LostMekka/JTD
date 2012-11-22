/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jtd;

/**
 *
 * @author LostMekka
 */
public class Point {
	
	public float x, y;

	public Point(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public Point() {
		x = 0f;
		y = 0f;
	}
	
	public float distanceTo(Point p){
		return (float)Math.sqrt((x - p.x)*(x - p.x) + (y - p.y)*(y - p.y));
	}

	public float length(){
		return (float)Math.sqrt(x*x + y*y);
	}
	
	public void travelTo(Point p, float dist){
		float dx = x - p.x, dy = y - p.y;
		float f = (float)Math.sqrt(dx*dx + dy*dy) / dist;
		x += dx * f;
		y += dy * f;
	}
	
	@Override
	public Point clone(){
		return new Point(x, y);
	}
}
