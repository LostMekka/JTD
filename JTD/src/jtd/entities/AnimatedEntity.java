/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jtd.entities;

import jtd.PointF;
import org.newdawn.slick.Image;

/**
 *
 * @author LostMekka
 */
public abstract class AnimatedEntity extends Entity{

	private float animationTime = 0;
	private AnimatedEntityDef def;
	
	public AnimatedEntity(PointF loc, AnimatedEntityDef animatedDef) {
		super(loc, animatedDef);
		this.def = animatedDef;
	}

	@Override
	public final void entityTick(float time) {
		if(animationTime >= 0){
			animationTime += time;
			while(animationTime >= def.times[currSprite]){
				animationTime -= def.times[currSprite];
				if(currSprite < def.sprites.length - 1){
					currSprite++;
				} else {
					if(def.isCyclic){
						currSprite = 0;
						animationCycled();
					} else {
						animationTime = -1f;
						animationEnded();
					}
				}
			}
		}
		animatedEntityTick(time);
	}
	
	public void animationCycled(){}
	public void animationEnded(){}
	public void animatedEntityTick(float time){}
	
}
