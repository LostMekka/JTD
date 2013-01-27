/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jtd.entities;

import jtd.Animation;
import jtd.PointD;

/**
 *
 * @author LostMekka
 */
public class AnimatedEntity extends Entity{

	public double animationSpeed = 1f;
	public Animation[] currAnimationSet = null;
	public Animation currAnimation = null;
	
	private int currAnimationState = 0;
	private double animationTime = -1d;
	private boolean cycleAnimation = false;
	
	public AnimatedEntity(PointD loc) {
		super(loc);
	}

	@Override
	public final void entityTick(double time) {
		if(currAnimation != null){
			animationTime += time * animationSpeed;
			while(animationTime >= currAnimation.times[currAnimationState]){
				animationTime -= currAnimation.times[currAnimationState];
				if(currAnimationState < currAnimation.images.length - 1){
					currAnimationState++;
				} else {
					if(cycleAnimation){
						if(currAnimationSet == null){
							currAnimationState = 0;
						} else {
							startAnimation(currAnimationSet);
						}
						animationCycled();
					} else {
						currAnimation = null;
						animationEnded();
					}
				}
			}
		}
		animatedEntityTick(time);
	}
	
	public void setAnimation(Animation animation, boolean cycleAnimation){
		this.cycleAnimation = cycleAnimation;
		currAnimationSet = null;
		startAnimation(animation);
	}
	
	public void setAnimationSet(Animation[] animations, boolean cycleAnimation){
		this.cycleAnimation = cycleAnimation;
		currAnimationSet = animations;
		startAnimation(animations);
	}
	
	private void startAnimation(Animation[] a){
		startAnimation(a[RANDOM.nextInt(a.length)]);
	}
	
	private void startAnimation(Animation a){
		currAnimation = a;
		currAnimationState = 0;
		animationTime = 0d;
		if((a != null) && (a.images.length > 0)){
			currSprite = a.images[0];
		} else {
			currSprite = null;
			currAnimation = null;
		}
	}
	
	public void animationCycled(){}
	public void animationEnded(){}
	public void animatedEntityTick(double time){}
	
}
