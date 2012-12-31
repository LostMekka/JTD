/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jtd;

import jtd.def.GameDef;
import java.util.LinkedList;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;
import jtd.def.MobDef;
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
import jtd.level.LevelDataHolder;
import jtd.level.PathListener;
import jtd.level.PathingGraph;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

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
	
	public LevelDataHolder level;
	public GameDef gameDef = new GameDef();
	float renderScale = 1f, timeScale = 1f;
	PointF renderOffset = new PointF(0f, 0f);
	
	// debug vars
	public boolean debugTowers = true;
	public boolean debugPathingWeights = true;
	public int debugPathing = 0;

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
	public PointF transformPointBack(float x, float y) {
		return new PointF(
				x / TILE_SIZE / renderScale - renderOffset.x - 0.5f, 
				y / TILE_SIZE / renderScale - renderOffset.y - 0.5f);
	}

	@Override
	public float transformLength(float len) {
		return len * TILE_SIZE * renderScale;
	}

	@Override
	public float transformLengthBack(float len) {
		return len / TILE_SIZE / renderScale;
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
				level.explosions.add(new Explosion(p.loc, p.def.expDef, p.rotation));
				if(p.rotation == 0f){
					int i = 0;
				}
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
			if(m.loc.quadraticDistanceTo(tower.loc) < (tower.def.range + m.def.radius) * (tower.def.range + m.def.radius)){
				mobs.add(m);
			}
		}
		if(mobs.isEmpty()) return null;
		return mobs.first();
	}
	
	public void shoot(
			PointF loc, Tower tower, Mob mob, 
			LinkedList<InstantEffect> instantEffects, 
			LinkedList<TimedEffectDef> timedEffects){
		Projectile p = new Projectile(
				tower.def.projectileDef, mob, tower, 
				instantEffects, timedEffects, loc);
		level.projectiles.add(p);
	}
	
	public void dealDamage(Mob mob, Tower attacker, 
			LinkedList<InstantEffect> instantEffects, 
			LinkedList<TimedEffectDef> timedEffects, Float direction){
		for(TimedEffectDef def:timedEffects) mob.applyTimedEffect(new TimedEffect(attacker, def));
		for(InstantEffect e:instantEffects) mob.applyInstantEffect(e);
		float dmg = mob.damage(attacker.def.damage, attacker, direction);
		level.damageDealtAt(mob.getPointI(), dmg, mob.def.size);
	}
	
	public void dealAreaDamage(PointF loc, Tower attacker, 
			LinkedList<InstantEffect> instantEffects, 
			LinkedList<TimedEffectDef> timedEffects){
		for(Mob mob:level.mobs){
			if(mob.loc.distanceTo(loc) - mob.def.radius <= attacker.def.damageRadius){
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
		level = new LevelDataHolder(gameDef, new Level());
	}

	@Override
	public void render(GameContainer gc, StateBasedGame sbg, Graphics grphcs) throws SlickException {
		// draw terrain
		level.draw(grphcs, this);
		for(Particle pa:level.bgParticles) pa.draw(gc, sbg, grphcs, this);
		// draw towers
		for(Tower t:level.towers){
			t.draw(gc, sbg, grphcs, this);
			if(debugTowers) dbgTower(grphcs, t);
		}
		for(Mob m:level.mobs) m.draw(gc, sbg, grphcs, this);
		for(Explosion e:level.explosions) e.draw(gc, sbg, grphcs, this);
		for(Particle pa:level.particles) pa.draw(gc, sbg, grphcs, this);
		for(Projectile pr:level.projectiles) pr.draw(gc, sbg, grphcs, this);
		
		// debug stuff
		dbgStringReset();
		if((debugPathing > 0) && (debugPathing <= level.level.maxMobSize)) dbgPath(grphcs);
		if(debugPathingWeights) dbgPathWeights(grphcs);
		
		grphcs.flush();
	}
	
	private int dbgline = 1;
	private void dbgStringReset(){
		dbgline = 1;
	}
	private void dbgString(String s, Graphics grphcs){
		grphcs.drawString(s, 10, 10 * ++dbgline);
	}
	private void dbgTower(Graphics g, Tower t){
		g.setColor(Color.white);
		PointF p1 = transformPoint(new PointF(t.loc.x - t.def.range, t.loc.y - t.def.range));
		float diameter = transformLength(t.def.range * 2f);
		g.drawOval(p1.x, p1.y, diameter, diameter);
		PointF p2 = transformPoint(t.loc);
		PointF p3 = t.loc.clone();
		p3.travelInDirection(t.getHeadDir(), t.def.range);
		p3 = transformPoint(p3);
		g.drawLine(p2.x, p2.y, p3.x, p3.y);
	}
	private void dbgPath(Graphics grphcs){
		dbgString("update in: " + pathTime, grphcs);
		dbgString("last update time: " + level.getLastPathUpdateDuration(), grphcs);
		PathingGraph graph = level.getPathingGraph(debugPathing);
		float rad = 0.1f;
		float transformedDiameter = transformLength(2f * rad);
		for(PointI p:graph.startingPoints){
			PointF p1 = transformPoint(new PointF((float)p.x - rad, (float)p.y - rad));
			grphcs.drawOval(p1.x, p1.y, transformedDiameter, transformedDiameter);
		}
		for(int x=0; x<level.w; x++){
			for(int y=0; y<level.h; y++){
				PointF p1 = transformPoint(new PointF(x, y));
				for(PointI p:graph.transitions.get(x).get(y)){
					PointF p2 = transformPoint(p.getPointF());
					grphcs.drawGradientLine(p1.x, p1.y, Color.red, p2.x, p2.y, Color.yellow);
				}
			}
		}
	}
	private void dbgPathWeights(Graphics g){
		float max = level.getMaxPathingWeight();
		if(max <= 0) return;
		g.setColor(Color.red);
		for(int y=0; y<level.h; y++){
			for(int x=0; x<level.w; x++){
				PointI p = new PointI(x, y);
				float rad = level.getPathingWeightAt(p) / max / 2f;
				float diameter = transformLength(2f * rad);
				if(diameter <= 4f) continue;
				PointF p1 = transformPoint(new PointF(x - rad, y - rad));
				g.fillOval(p1.x, p1.y, diameter, diameter);
			}
		}
	}
	
	public PathingGraph getCurrentPathingGraph(int mobSize){
		return level.getPathingGraph(mobSize);
	}
	
	private void mobGotThrough(Mob mob){
		// TODO: exchange testing code with useful stuff
		if(spm < 1.5f) spm /= tst;
	}
	
	private void mobGotKilled(Mob mob){
		level.killHappenedAt(mob.getPointI(), mob.def.size);
		// TODO: exchange testing code with useful stuff
		if(spm > 0.01f) spm *= tst;
	}
	
	Random random = new Random();
	int maxPathTime = 500, pathTime = maxPathTime;
	float spm = 1f, tst = 0.99f, t = spm;
	boolean pos = true;
	
	@Override
	public void update(GameContainer gc, StateBasedGame sbg, int i) throws SlickException {
		//System.gc();
		// pathing
		pathTime -= i;
		if(pathTime <= 0){
			pathTime = maxPathTime;
			level.updatePaths();
			for(Mob m:level.mobs) m.updatePath();
		}
		// tick entities
		float time = ((float)i) / 1000f * timeScale;
		for(Particle p:level.particles) p.tick(time);
		for(Particle p:level.bgParticles) p.tick(time);
		for(Explosion e:level.explosions) e.tick(time);
		for(Projectile p:level.projectiles) p.tick(time);
		for(Mob m:level.mobs) m.tick(time);
		level.deleteMarkedEntities();
		for(Tower t:level.towers) t.tick(time);
		// testing stuff
		Input in = gc.getInput();
		if(in.isKeyPressed(Input.KEY_M)) t += spm;
		if(in.isMousePressed(0)){
			PointF p = transformPointBack(in.getMouseX(), in.getMouseY());
			MobDef mDef = gameDef.getMobDef(GameDef.MobType.swarm, 1, false);
			if(level.isWalkable(p.getPointI(), mDef.size)){
				level.mobs.add(new Mob(p, mDef));
			}
		}
		t += time;
		while(t > spm){
			t -= spm;
			if(level.mobs.size() >= 1000) break;
			GameDef.MobType t = null;
			switch(random.nextInt(2)){
				case 0: t = GameDef.MobType.normal; break;
				case 1: t = GameDef.MobType.swarm; break;
			}
			level.mobs.add(new Mob(gameDef.getMobDef(t, 1, false)));
		}
		
	}

}
