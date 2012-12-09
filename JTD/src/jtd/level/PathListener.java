/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jtd.level;

import jtd.PointI;
import jtd.entities.Mob;

/**
 *
 * @author LostMekka
 */
public interface PathListener {
	
	public void fieldWalkedBy(PointI p, Mob m);
	public void pathEndReachedBy(PointI p, Mob m);
	
}
