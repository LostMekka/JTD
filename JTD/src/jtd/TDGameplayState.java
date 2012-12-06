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
import jtd.entities.Particle;
import jtd.entities.Projectile;
import jtd.entities.Tower;
import jtd.level.Level;
import jtd.level.Path;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

/**
 *
 * @author LostMekka
 */
public class TDGameplayState extends BasicGameState implements KillListener, CoordinateTransformator{
	
	public static final float TILE_SIZE = 32f;
	
	private static TDGameplayState in = null;
	public static TDGameplayState get(){
		if(in == null) in = new TDGameplayState();
		return in;
	}
	private TDGameplayState(){}
	
	public Level level;
	public GameDef gameDef = new GameDef();
	float renderScale = 1f;
	PointF renderOffset = new PointF(0f, 0f);

	@Override
	public float getRenderOffsetX() {
		return renderOffset.x;
	}

	@Override
	public float getRenderOffsetY() {
		return renderOffset.y;
	}

	@Override
	public float getTileSize() {
		return TILE_SIZE;
	}

	@Override
	public float getRenderScale() {
		return renderScale;
	}
	
	@Override
	public void drawImage(Image i, PointF loc, float sizeInTiles, float rotation){
		i.setCenterOfRotation(
				(float)i.getWidth() / 2f * renderScale * sizeInTiles, 
				(float)i.getHeight() / 2f * renderScale * sizeInTiles);
		i.setRotation(rotation);
		i.draw(
				(loc.x + renderOffset.x + 0.5f - sizeInTiles / 2f) * TILE_SIZE * renderScale, 
				(loc.y + renderOffset.y + 0.5f - sizeInTiles / 2f) * TILE_SIZE * renderScale, 
				renderScale * sizeInTiles * TILE_SIZE / (float)i.getWidth());
	}

	@Override
	public PointF transformX(PointF loc) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	
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
		level = new Level(gameDef);
		p.generate();
	}

	@Override
	public void render(GameContainer gc, StateBasedGame sbg, Graphics grphcs) throws SlickException {
		// draw terrain
		for(int x=0; x<level.w; x++){
			for(int y=0; y<level.h; y++){
				Image terrain = level.getTileImage(x, y);
				drawImage(terrain, new PointF(x, y), 1f, 0f);
			}
		}
		// draw turret
		for(int x=0; x<level.w; x++){
			for(int y=0; y<level.h; y++){
				Tower t = level.towers[y][x];
				if(t != null){
					t.draw(gc, sbg, grphcs, this);
				}
			}
		}
		for(Mob m:level.mobs) m.draw(gc, sbg, grphcs, this);
		for(Explosion e:level.explosions) e.draw(gc, sbg, grphcs, this);
		for(Particle pa:level.particles) pa.draw(gc, sbg, grphcs, this);
		for(Projectile pr:level.projectiles) pr.draw(gc, sbg, grphcs, this);
	}

	Path p = new Path();
	int t = 0, n=0, diff=100;
	
	@Override
	public void update(GameContainer gc, StateBasedGame sbg, int i) throws SlickException {
		float time = ((float)i) / 1000f;
		for(Mob m:level.mobs) m.tick(time);
		for(Tower[] t1:level.towers) for(Tower t2:t1) if(t2 != null) t2.tick(time);
		for(Projectile p:level.projectiles) p.tick(time);
		if(n>50)return;
		t+=i;
		while(t>diff){
			t-=diff;
			n++;
			level.mobs.add(new Mob(gameDef.getMobDef(GameDef.MobType.swarm, 1, false), p));
		}
	}

}
