/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jtd;

import org.newdawn.slick.Image;

/**
 *
 * @author LostMekka
 */
public interface CoordinateTransformator {
	
	public void drawImage(Image i, PointF loc, float sizeInTiles, float rotation);
	public PointF transformPoint(PointF loc);
	public PointF transformPointBack(float x, float y);
	public float transformLength(float len);
	public float transformLengthBack(float len);
	
}
