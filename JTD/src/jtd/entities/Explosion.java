/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jtd.entities;

import jtd.PointF;

/**
 *
 * @author LostMekka
 */
public class Explosion extends Entity{

	public ExplosionDef def;
	public float stateAge = 0;

	public Explosion(PointF loc, ExplosionDef def) {
		super(loc, def.sprites.length);
		this.def = def;
		System.arraycopy(def.sprites, 0, sprites, 0, def.sprites.length);
	}
	
	@Override
	public void tick(float time) {
		stateAge += time;
		while(stateAge >= def.times[currSprite]){
			stateAge -= def.times[currSprite];
			if(currSprite + 1 >= def.sprites.length){
				kill(null);
				return;
			}
			currSprite++;
		}
	}
	
}
