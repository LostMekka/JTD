/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jtd;

/**
 *
 * @author LostMekka
 */
public class PointF {
	
	public float x, y;

	public PointF(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public PointF() {
		x = 0f;
		y = 0f;
	}
	
	public PointI getPountI(){
		return new PointI(Math.round(x), Math.round(y));
	}
	
	public float distanceTo(PointF p){
		return (float)Math.sqrt((x - p.x)*(x - p.x) + (y - p.y)*(y - p.y));
	}

	public float length(){
		return (float)Math.sqrt(x*x + y*y);
	}
	
	public void travelTo(PointF p, float dist){
		float dx = x - p.x, dy = y - p.y;
		float f = (float)Math.sqrt(dx*dx + dy*dy) / dist;
		x += dx * f;
		y += dy * f;
	}
	
	@Override
	public PointF clone(){
		return new PointF(x, y);
	}
}
