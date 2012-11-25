/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jtd.entities;

import java.util.LinkedList;
import jtd.PointF;
import jtd.TDGameplayState;
import jtd.effect.instant.InstantEffect;
import jtd.effect.timed.TimedEffect;
import org.newdawn.slick.Image;

/**
 *
 * @author LostMekka
 */
public class Projectile extends Entity implements KillListener{

	public ProjectileDef def;
	public Mob target;
	public PointF targetLoc;
	public Tower attacker;
	public LinkedList<InstantEffect> instantEffects;
	public LinkedList<TimedEffect> timedEffects;
	public float lifeTime;

	public Projectile(
			ProjectileDef def, Mob target, Tower attacker, 
			LinkedList<InstantEffect> instantEffects, 
			LinkedList<TimedEffect> timedEffects, 
			PointF loc) {
		super(loc, 1);
		this.def= def;
		this.target = target;
		targetLoc = target.loc;
		this.attacker = attacker;
		this.instantEffects = instantEffects;
		this.timedEffects = timedEffects;
		sprites[0] = def.sprite;
		rotation = attacker.loc.getRotationTo(target.loc);
	}
	
	@Override
	public void tick(float time) {
		// tick lifetime, kill if expired
		lifeTime += time;
		if(lifeTime >= def.lifeTime){
			kill(null);
			return;
		}
		// go to target
		loc.travelTo(targetLoc, time * def.speed, true);
		// if target is reached, deal damage
		if(loc.equals(targetLoc)){
			if(attacker.def.damageRadius > 0){
				TDGameplayState.get().dealAreaDamage(targetLoc, attacker, instantEffects, timedEffects);
			} else {
				if(target != null){
					TDGameplayState.get().dealDamage(target, attacker, instantEffects, timedEffects);
				}
			}
			kill(null);
		}
	}

	@Override
	public void EntityKilled(Entity entity, Entity killer) {
		if(entity == target){
			target = null;
		}
	}
	
}
