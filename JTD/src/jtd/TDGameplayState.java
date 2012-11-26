/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jtd;

import java.util.LinkedList;
import jtd.effect.instant.InstantEffect;
import jtd.effect.timed.TimedEffect;
import jtd.entities.Entity;
import jtd.entities.Explosion;
import jtd.entities.KillListener;
import jtd.entities.Mob;
import jtd.entities.Projectile;
import jtd.entities.Tower;
import jtd.level.Level;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

/**
 *
 * @author LostMekka
 */
public class TDGameplayState extends BasicGameState implements KillListener{
	
	private static TDGameplayState in = null;
	public static TDGameplayState get(){
		if(in == null) in = new TDGameplayState();
		return in;
	}
	private TDGameplayState(){}
	
	public Level level;

	@Override
	public void EntityKilled(Entity entity, Entity killer) {
		if(entity instanceof Mob){
			if(killer != null){
				if(killer == entity){
					// mob got through. punish player!
				}
				if(killer instanceof Tower){
					// got killed by a tower. reward player!
				}
			}
			level.mobs.remove(entity);
			return;
		}
		if(entity instanceof Tower){
			level.removeTower((Tower)entity);
		}
		if(entity instanceof Projectile){
			Projectile p = (Projectile)entity;
			level.projectiles.remove(p);
			level.explosions.add(new Explosion(p.loc, p.def.expDef));
		}
	}

	public Mob giveTarget(Tower tower){
		// TODO: return a mob if possible
		return null;
	}
	
	public void shoot(
			Tower tower, Mob mob, 
			LinkedList<InstantEffect> instantEffects, 
			LinkedList<TimedEffect> timedEffects){
		level.projectiles.add(
				new Projectile(
					tower.def.projectileDef, mob, tower, 
					instantEffects, timedEffects, tower.loc));
	}
	
	public void dealDamage(Mob mob, Tower attacker, 
			LinkedList<InstantEffect> instantEffects, 
			LinkedList<TimedEffect> timedEffects){
		for(TimedEffect e:timedEffects) mob.applyTimedEffect(e);
		for(InstantEffect e:instantEffects) mob.applyInstantEffect(e);
		mob.damage(attacker.def.damage, attacker);
	}
	
	public void dealAreaDamage(PointF loc, Tower attacker, 
			LinkedList<InstantEffect> instantEffects, 
			LinkedList<TimedEffect> timedEffects){
		for(Mob mob:level.mobs){
			if(mob.loc.distanceTo(loc) <= attacker.def.damageRadius){
				dealDamage(mob, attacker, instantEffects, timedEffects);
			}
		}
	}
	
	@Override
	public int getID() {
		return 1;
	}

	@Override
	public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
		level = new Level();
	}

	@Override
	public void render(GameContainer gc, StateBasedGame sbg, Graphics grphcs) throws SlickException {
		// TODO: render
	}

	@Override
	public void update(GameContainer gc, StateBasedGame sbg, int i) throws SlickException {
		float time = ((float)i) / 1000f;
		for(Mob m:level.mobs) m.tick(time);
		for(Tower[] t1:level.towers) for(Tower t2:t1) if(t2 != null) t2.tick(time);
		for(Projectile p:level.projectiles) p.tick(time);
	}
	
}
