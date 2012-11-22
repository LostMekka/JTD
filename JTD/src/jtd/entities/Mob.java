/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jtd.entities;

import java.util.Iterator;
import java.util.LinkedList;
import jtd.PointF;
import jtd.level.Path;
import jtd.effect.timed.TimedEffect;

/**
 *
 * @author LostMekka
 */
public abstract class Mob extends Entity {

	public float maxHP, hpRegen, shield, maxShield, shieldRegen, armor, speed;

	private float hp;
	private Iterator<PointF> path;
	private PointF pathTarget;
	private LinkedList<TimedEffect> effects = new LinkedList<>();
	
	public Mob(
			float maxHP, float hpRegen, 
			float maxShield, float shieldRegen, 
			float armor, float speed, Path p) {
		super(p.getStart().clone());
		this.maxHP = maxHP;
		this.hpRegen = hpRegen;
		this.maxShield = maxShield;
		this.shieldRegen = shieldRegen;
		this.armor = armor;
		this.speed = speed;
		hp = maxHP;
		shield = maxShield;
		path = p.getPointIterator();
		path.next();
		pathTarget = path.next();
	}
	
	public void applyTimedEffect(TimedEffect effect){
		effects.add(effect);
		effect.apply(this);
	}
	
	public void removeTimedEffect(TimedEffect effect){
		effects.remove(effect);
		effect.remove(this);
	}
	
	public void damage(float damage, Entity attacker){
		damage -= armor;
		if(damage <= 0) return;
		if(damage <= shield){
			shield -= damage;
		} else {
			hp -= damage - shield;
			shield = 0;
		}
		if(hp == 0) kill(attacker);
	}

	@Override
	public void tick(float time) {
		// tick effects
		for(TimedEffect e:effects) e.tick(time, this);
		// regen hp
		hp += time * hpRegen;
		if(hp > maxHP) hp = maxHP;
		if(hp <= 0){
			
		}
		// regen shield
		shield += time * shieldRegen;
		if(shield > maxShield) shield = maxShield;
		// move mob
		if(pathTarget != null){
			float travel = time * speed;
			float currDist = loc.distanceTo(pathTarget);
			while(travel > currDist){
				travel -= currDist;
				pathTarget = path.next();
				if(pathTarget == null){
					travel = 0;
				}
			}
			if(travel > 0){
				loc.travelTo(pathTarget, travel);
			}
		}
		mobTick(time);
	}
	
	public abstract void mobTick(float time);
	
}
