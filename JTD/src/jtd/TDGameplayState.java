/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jtd;

import java.util.LinkedList;
import java.util.SortedSet;
import java.util.TreeSet;
import jtd.effect.instant.InstantEffect;
import jtd.effect.timed.TimedEffect;
import jtd.effect.timed.TimedEffectDef;
import jtd.entities.Entity;
import jtd.entities.Explosion;
import jtd.entities.Mob;
import jtd.entities.Particle;
import jtd.entities.ParticleFactory;
import jtd.entities.Projectile;
import jtd.entities.Tower;
import jtd.level.Level;
import jtd.level.Path;
import jtd.level.PathListener;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.tiled.TileSet;

/**
 *
 * @author LostMekka
 */
public class TDGameplayState extends BasicGameState implements KillListener, CoordinateTransformator, PathListener{
	
	private static TDGameplayState in = null;
	public static TDGameplayState get(){
		if(in == null) in = new TDGameplayState();
		return in;
	}
	private TDGameplayState(){}
	
	public static final float TILE_SIZE = 32f;
	
	public Level level;
	public GameDef gameDef = new GameDef();
	float renderScale = 2.5f, timeScale = 1f;
	PointF renderOffset = new PointF(0f, 0f);

	@Override
	public void drawImage(Image i, PointF loc, float sizeInTiles, float rotation){
		float finalSizeX = TILE_SIZE * renderScale * sizeInTiles;
		float finalSizeY = TILE_SIZE * renderScale * sizeInTiles;
		i.setCenterOfRotation(finalSizeX / 2f, finalSizeY / 2f);
		i.setRotation(rotation);
		PointF p = transformPoint(loc);
		i.draw(p.x - finalSizeX / 2f, p.y - finalSizeY / 2f, finalSizeX, finalSizeY);
	}

	@Override
	public PointF transformPoint(PointF loc) {
		return new PointF(
				(loc.x + renderOffset.x + 0.5f) * TILE_SIZE * renderScale, 
				(loc.y + renderOffset.y + 0.5f) * TILE_SIZE * renderScale);
	}

	
	@Override
	public void fieldWalkedBy(PointI p, Mob m) {
	}

	@Override
	public void pathEndReachedBy(PointI p, Mob m) {
		mobGotThrough(m);
		m.kill(null);
	}

	@Override
	public void EntityKilled(Entity entity, Entity killer) {
		if(entity instanceof Mob){
			Mob mob = (Mob)entity;
			if((killer != null) && (killer instanceof Tower)){
				mobGotKilled(mob);
				PointI p = mob.loc.getPointI();
				if(p.x < 0) p.x = 0;
				if(p.x >= level.w) p.x = level.w - 1;
				if(p.y < 0) p.y = 0;
				if(p.y >= level.h) p.y = level.h - 1;
				level.killHappenedOn(p);
			}
			level.markMobForDeletion(mob);
			return;
		}
		if(entity instanceof Tower){
			level.removeTower((Tower)entity);
		}
		if(entity instanceof Projectile){
			Projectile p = (Projectile)entity;
			level.markProjectileForDeletion(p);
			if(p.def.expDef != null){
				level.explosions.add(new Explosion(p.loc, p.def.expDef));
			}
		}
		if(entity instanceof Particle){
			level.markParticleForDeletion((Particle)entity);
		}
		if(entity instanceof Explosion){
			level.markExplosionForDeletion((Explosion)entity);
		}
	}

	public Mob giveTarget(Tower tower){
		SortedSet<Mob> mobs = new TreeSet<>(tower.getComparator());
		for(Mob m:level.mobs){
			// hamming distance is faster. normally many mobs dont meet the distance criterion
			// therefore checking the hamming distance first should make the game faster over all
			if((m.loc.hammingDistanceTo(tower.loc) < tower.def.range) && !level.mobsToDelete.contains(m)){
				if(m.loc.distanceTo(tower.loc) < tower.def.range) mobs.add(m);
			}
		}
		if(mobs.isEmpty()) return null;
		return mobs.first();
	}
	
	public void shoot(
			PointF loc, Tower tower, Mob mob, 
			LinkedList<InstantEffect> instantEffects, 
			LinkedList<TimedEffectDef> timedEffects){
		level.projectiles.add(new Projectile(
					tower.def.projectileDef, mob, tower, 
					instantEffects, timedEffects, loc));
	}
	
	public void dealDamage(Mob mob, Tower attacker, 
			LinkedList<InstantEffect> instantEffects, 
			LinkedList<TimedEffectDef> timedEffects, Float direction){
		for(TimedEffectDef def:timedEffects) mob.applyTimedEffect(new TimedEffect(attacker, def));
		for(InstantEffect e:instantEffects) mob.applyInstantEffect(e);
		mob.damage(attacker.def.damage, attacker, direction);
	}
	
	public void dealAreaDamage(PointF loc, Tower attacker, 
			LinkedList<InstantEffect> instantEffects, 
			LinkedList<TimedEffectDef> timedEffects){
		for(Mob mob:level.mobs){
			if(mob.loc.distanceTo(loc) <= attacker.def.damageRadius){
				dealDamage(mob, attacker, instantEffects, timedEffects, loc.getRotationTo(mob.loc));
			}
		}
	}
	
	public boolean addParticle(ParticleFactory f, PointF point, float dir){
		Particle p = f.createParticle(point, dir);
		if(f.isBackgroundParticle){
			return level.bgParticles.add(p);
		} else {
			return level.particles.add(p);
		}
	}
	
	public boolean movePointIntoLevl(PointF p){
		boolean ans = false;
		if(p.x < -0.5f){
			p.x = -0.5f;
			ans = true;
		}
		if(p.x > level.w - 0.5f){
			p.x = level.w - 0.5f;
			ans = true;
		}
		if(p.y < -0.5f){
			p.y = -0.5f;
			ans = true;
		}
		if(p.y > level.h - 0.5f){
			p.y = level.h - 0.5f;
			ans = true;
		}
		return ans;
	}
	
	@Override
	public int getID() {
		return 1;
	}

	@Override
	public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
		level = new Level(gameDef);
		p = Path.generate(level);
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
		for(Particle pa:level.bgParticles) pa.draw(gc, sbg, grphcs, this);
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
	

	private void mobGotThrough(Mob mob){
		spm /= tst;
	}
	
	private void mobGotKilled(Mob mob){
		spm *= tst;
	}
	
	Path p;
	float n = 0f, spm = 1f, tst = 0.99f, t = 0f;
	
	@Override
	public void update(GameContainer gc, StateBasedGame sbg, int i) throws SlickException {
		float time = ((float)i) / 1000f * timeScale;
		for(Particle p:level.particles) p.tick(time);
		for(Particle p:level.bgParticles) p.tick(time);
		level.deleteMarkedEntities();
		for(Explosion e:level.explosions) e.tick(time);
		level.deleteMarkedEntities();
		for(Projectile p:level.projectiles) p.tick(time);
		level.deleteMarkedEntities();
		for(Mob m:level.mobs) m.tick(time);
		level.deleteMarkedEntities();
		for(Tower[] t1:level.towers) for(Tower t2:t1) if(t2 != null) t2.tick(time);
		level.deleteMarkedEntities();
		
		Input in = gc.getInput();
		if(in.isKeyPressed(Input.KEY_M)) n++;
		t += time;
		while(t > spm){
			t -= spm;
			n++;
		}
		while(n>0){
			if(level.mobs.size() >= 1000) break;
			n--;
			p = Path.generate(level);
			level.mobs.add(new Mob(gameDef.getMobDef(GameDef.MobType.swarm, 1, false), p));
		}
	}

}
