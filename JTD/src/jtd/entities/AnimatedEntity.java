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
	private AnimatedEntityDef animatedDef;
	
	public AnimatedEntity(PointF loc, AnimatedEntityDef animatedDef) {
		super(loc, animatedDef);
		this.animatedDef = animatedDef;
	}

	@Override
	public final void entityTick(float time) {
		if(animationTime >= 0){
			animationTime += time;
			while(animationTime >= animatedDef.times[currSprite]){
				animationTime -= animatedDef.times[currSprite];
				if(currSprite < entityDef.sprites.length - 1){
					currSprite++;
				} else {
					if(animatedDef.isCyclic){
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
