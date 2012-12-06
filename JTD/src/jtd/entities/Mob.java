/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jtd.entities;

import java.util.Iterator;
import java.util.LinkedList;
import jtd.PointF;
import jtd.effect.instant.InstantEffect;
import jtd.effect.timed.TimedEffect;
import jtd.level.Path;

/**
 *
 * @author LostMekka
 */
public class Mob extends Entity {

	public MobDef def;
	public float speedMultiplier = 1f, armorOffset = 0f, bonusDamage = 0f;
	
	private float hp, shield;
	private Iterator<PointF> path;
	private PointF pathTarget;
	private LinkedList<TimedEffect> effects = new LinkedList<>();
	
	public Mob(MobDef def, Path p){
		super(p.getStart().clone(), 1);
		this.def = def;
		hp = def.maxHP;
		shield = def.maxShield;
		path = p.getPointIterator();
		PointF tmp = path.next();
		pathTarget = path.next();
		sprites[0] = def.sprite;
		rotation = tmp.getRotationTo(pathTarget);
		sizeInTiles = 0.6f;
	}
	
	public void applyInstantEffect(InstantEffect effect){
		effect.apply(this);
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
		damage += bonusDamage;
		damage -= def.armor + armorOffset;
		if(damage <= 0) return;
		if(damage <= shield){
			shield -= damage;
		} else {
			hp -= damage - shield;
			shield = 0;
		}
		if(hp <= 0) kill(attacker);
	}

	@Override
	public void tick(float time) {
		// tick effects
		for(TimedEffect e:effects) e.tick(time, this);
		// regen hp
		hp += time * def.hpRegen;
		if(hp > def.maxHP) hp = def.maxHP;
		if(hp <= 0) kill(null);
		// regen shield
		shield += time * def.shieldRegen;
		if(shield > def.maxShield) shield = def.maxShield;
		if(shield < 0) shield = 0;
		// move mob
		if(pathTarget != null){
			float travel = time * def.speed * speedMultiplier;
			while(travel > 0){
				travel -= loc.travelTo(pathTarget, travel, true);
				if(travel > 0){
					pathTarget = path.next();
					if(pathTarget == null){
						pathTarget = loc.clone();
						float d = random.nextFloat() * 1.5f + 0.5f;
						float a = random.nextFloat() * 360f;
						pathTarget.x += d * (float)Math.cos(a);
						pathTarget.y += d * (float)Math.sin(a);
					}
				}
			}
		}
		if(pathTarget == null){
			rotation = random.nextFloat() * 2f * (float)Math.PI;
		} else {
			rotation = loc.getRotationTo(pathTarget) + 90f;
		}
		def.tick(time);
	}

}
