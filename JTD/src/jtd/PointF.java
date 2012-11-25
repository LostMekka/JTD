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
	
	public float travelTo(PointF p, float dist, boolean stopAtArrival){
		float dx = x - p.x, dy = y - p.y;
		float d = (float)Math.sqrt(dx*dx + dy*dy);
		if((d >= dist) && stopAtArrival){
			x = p.x;
			y = p.y;
			return d;
		}
		x += dx * d / dist;
		y += dy * d / dist;
		return d;
	}

	public float getRotationTo(PointF p){
		if(p.x == x){
			if(p.y < y){
				return (float)Math.PI;
			} else {
				return 0f;
			}
		}
		return (float)Math.atan((p.y - y) / (p.x - x));
	}
	
	@Override
	public PointF clone(){
		return new PointF(x, y);
	}
}
