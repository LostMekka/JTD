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
	
	public void drawImage(Image i, PointD loc, double sizeInTiles, double rotation);
	public PointD transformPoint(PointD loc);
	public PointD transformPointBack(double x, double y);
	public double transformLength(double len);
	public double transformLengthBack(double len);
	
}
