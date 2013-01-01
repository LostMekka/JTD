/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jtd.entities;

import jtd.def.AnimatedEntityDef;
import jtd.PointD;

/**
 *
 * @author LostMekka
 */
public abstract class AnimatedEntity extends Entity{

	public double animationSpeed = 1f;
	
	private double animationTime;
	private AnimatedEntityDef def;
	
	public AnimatedEntity(PointD loc, AnimatedEntityDef animatedDef) {
		super(loc, animatedDef);
		this.def = animatedDef;
		animationTime = 0f;
		currSprite = RANDOM.nextInt(def.times.length);
		animationTime = def.times[currSprite] * RANDOM.nextDouble();
	}

	@Override
	public final void entityTick(double time) {
		if(animationTime >= 0){
			animationTime += time * animationSpeed;
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
	public void animatedEntityTick(double time){}
	
}
