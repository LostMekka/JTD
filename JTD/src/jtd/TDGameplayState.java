/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jtd;

import java.util.LinkedList;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;
import jtd.def.GameDef;
import jtd.def.MobDef;
import jtd.def.TowerDef;
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
	
	public static final double TILE_SIZE = 32d;
	public static final int TEXT_HEIGHT = 14;
	
	public enum InteractionState {
		menu, normal, placeTower, scrolling, zooming
	}
	
	public GameContainer gameContainer;
	public LevelDataHolder level;
	public GameDef gameDef = new GameDef();
	public GameplayGui gui = new GameplayGui();
	public int money = 500;
	public InteractionState interactionState = InteractionState.normal;
	public Tower selectedTower = null, placingTower = null;
	public TowerDef selectedTowerDef = null;
	
	public double renderScale = 1d;
	public double renderMaxScale = 2d;
	public double renderMinScale = 0.75d;
	public double renderScaleSpeed = 1.0005d;
	
	public double timeScale = 1d;
	public double timeMaxScale = 2d;
	public double timeMinScale = 0.5d;
	
	public PointD renderOffset = new PointD();
	public PointD renderMinOffset = null;
	public PointD renderMaxOffset = null;
		
	private PointI scrollMouseStart = null;
	private PointD scrollOffsetStart = new PointD();
	
	private Random random = new Random();
	private int maxPathTime = 500, pathTime = 0;
	private int maxMapTime = 99, mapTime = 0;
	private double spm = 1d, tst = 0.99d, t = spm;

	// debug vars
	public boolean debugTowers = true;
	public boolean debugPathingWeights = false;
	public int debugPathing = 0;

	@Override
	public void drawImage(Image i, PointD loc, double sizeInTiles, double rotation){
		float finalSizeX = (float)(TILE_SIZE * renderScale * sizeInTiles);
		float finalSizeY = (float)(TILE_SIZE * renderScale * sizeInTiles);
		i.setCenterOfRotation(finalSizeX / 2f, finalSizeY / 2f);
		i.setRotation((float)rotation);
		PointD p = transformPoint(loc);
		i.draw((float)p.x - finalSizeX / 2f, (float)p.y - finalSizeY / 2f, finalSizeX, finalSizeY);
	}

	@Override
	public PointD transformPoint(PointD loc) {
		return new PointD(
				(loc.x + renderOffset.x + 0.5f) * TILE_SIZE * renderScale, 
				(loc.y + renderOffset.y + 0.5f) * TILE_SIZE * renderScale);
	}

	@Override
	public PointD transformPointBack(double x, double y) {
		return new PointD(
				x / TILE_SIZE / renderScale - renderOffset.x - 0.5f, 
				y / TILE_SIZE / renderScale - renderOffset.y - 0.5f);
	}

	@Override
	public double transformLength(double len) {
		return len * TILE_SIZE * renderScale;
	}

	@Override
	public double transformLengthBack(double len) {
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
				((Tower)killer).kills++;
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
			PointD loc, Tower tower, Mob mob, 
			LinkedList<InstantEffect> instantEffects, 
			LinkedList<TimedEffectDef> timedEffects){
		Projectile p = new Projectile(
				tower.def.projectileDef, mob, tower, 
				instantEffects, timedEffects, loc);
		level.projectiles.add(p);
	}
	
	public void dealDamage(Mob mob, Tower attacker, 
			LinkedList<InstantEffect> instantEffects, 
			LinkedList<TimedEffectDef> timedEffects, Double direction){
		for(TimedEffectDef def:timedEffects) mob.applyTimedEffect(new TimedEffect(attacker, def));
		for(InstantEffect e:instantEffects) mob.applyInstantEffect(e);
		double dmg = mob.damage(attacker.def.damage, attacker, direction);
		level.damageDealtAt(mob.getPointI(), dmg, mob.def.size);
	}
	
	public void dealAreaDamage(PointD loc, Tower attacker, 
			LinkedList<InstantEffect> instantEffects, 
			LinkedList<TimedEffectDef> timedEffects){
		for(Mob mob:level.mobs){
			if(mob.loc.distanceTo(loc) - mob.def.radius <= attacker.def.damageRadius){
				dealDamage(mob, attacker, instantEffects, timedEffects, loc.getRotationTo(mob.loc));
			}
		}
	}
	
	public boolean addParticle(ParticleFactory f, PointD point, double dir){
		Particle p = f.createParticle(point, dir);
		if(f.isBackgroundParticle){
			return level.bgParticles.add(p);
		} else {
			return level.particles.add(p);
		}
	}
	
	public boolean movePointIntoLevl(PointD p){
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
		gameContainer = gc;
		level = new LevelDataHolder(gameDef, new Level());
		gui.updateGuiImage(gc);
		gui.updateMapImage(level);
		renderOffset = new PointD(0f, 0f);
		calculateRenderOffsetBounds();
		centerRenderOffset();
	}
	
	@Override
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
		// TODO: draw only visible stuff (visible is between gui.tlCorner and gui.brCorner)
		// draw terrain
		level.draw(g, this);
		for(Particle pa:level.bgParticles) pa.draw(gc, sbg, g, this);
		// draw towers
		for(Tower t:level.towers){
			t.draw(gc, sbg, g, this);
			if(debugTowers) dbgTower(g, t);
		}
		for(Mob m:level.mobs) m.draw(gc, sbg, g, this);
		for(Explosion e:level.explosions) e.draw(gc, sbg, g, this);
		for(Particle pa:level.particles) pa.draw(gc, sbg, g, this);
		for(Projectile pr:level.projectiles) pr.draw(gc, sbg, g, this);
		
		// debug stuff
		dbgStringReset();
		if((debugPathing > 0) && (debugPathing <= level.level.maxMobSize)) dbgPath(g);
		if(debugPathingWeights) dbgPathWeights(g);
		
		gui.draw(g, this);
		gui.drawInfoText(g, "Credits: " + money, 24);
		switch(interactionState){
			case normal:
				if(selectedTower != null){
					gui.drawInfoText(g, selectedTower.def.name, TEXT_HEIGHT);
					gui.drawInfoText(g, "level  : " + selectedTower.def.level, TEXT_HEIGHT);
					gui.drawInfoText(g, "damage : " + selectedTower.def.damage, TEXT_HEIGHT);
					gui.drawInfoText(g, "area   : " + selectedTower.def.damageRadius, TEXT_HEIGHT);
					gui.drawInfoText(g, "range  : " + selectedTower.def.range, TEXT_HEIGHT);
					gui.drawInfoText(g, "reload : " + selectedTower.def.reloadTime, TEXT_HEIGHT);
					gui.drawInfoText(g, "kills  : " + selectedTower.kills, TEXT_HEIGHT);
					if(selectedTower.def.instantEffects.length + selectedTower.def.timedEffects.length > 0){
						gui.drawInfoText(g, "effects:", TEXT_HEIGHT);
						for(InstantEffect e:selectedTower.def.instantEffects){
							gui.drawInfoText(g, " " + e, TEXT_HEIGHT);
						}
						for(TimedEffectDef e:selectedTower.def.timedEffects){
							gui.drawInfoText(g, " " + e, TEXT_HEIGHT);
						}
					}					
				} else if(selectedTowerDef != null){
					gui.drawInfoText(g, selectedTowerDef.name, TEXT_HEIGHT);
					gui.drawInfoText(g, "level  : " + selectedTowerDef.level, TEXT_HEIGHT);
					gui.drawInfoText(g, "damage : " + selectedTowerDef.damage, TEXT_HEIGHT);
					gui.drawInfoText(g, "area   : " + selectedTowerDef.damageRadius, TEXT_HEIGHT);
					gui.drawInfoText(g, "range  : " + selectedTowerDef.range, TEXT_HEIGHT);
					gui.drawInfoText(g, "reload : " + selectedTowerDef.reloadTime, TEXT_HEIGHT);
					if(selectedTowerDef.instantEffects.length + selectedTowerDef.timedEffects.length > 0){
						gui.drawInfoText(g, "effects:", TEXT_HEIGHT);
						for(InstantEffect e:selectedTowerDef.instantEffects){
							gui.drawInfoText(g, " " + e, TEXT_HEIGHT);
						}
						for(TimedEffectDef e:selectedTowerDef.timedEffects){
							gui.drawInfoText(g, " " + e, TEXT_HEIGHT);
						}
					}					
				} else {
					gui.drawInfoText(g, "nothing selected.", TEXT_HEIGHT);
				}
				break;
			case scrolling:
				gui.drawInfoText(g, "scrolling...", TEXT_HEIGHT);
				break;
		}
		g.flush();
	}
	
	private int dbgline = 1;
	private void dbgStringReset(){
		dbgline = 1;
	}
	private void dbgString(String s, Graphics grphcs){
		grphcs.drawString(s, 10, 12 * ++dbgline);
	}
	private void dbgTower(Graphics g, Tower t){
		g.setColor(Color.white);
		PointD p1 = transformPoint(new PointD(t.loc.x - t.def.range, t.loc.y - t.def.range));
		double diameter = transformLength(t.def.range * 2f);
		g.drawOval((float)p1.x, (float)p1.y, (float)diameter, (float)diameter);
		PointD p2 = transformPoint(t.loc);
		PointD p3 = t.loc.clone();
		p3.travelInDirection(t.getHeadDir(), t.def.range);
		p3 = transformPoint(p3);
		g.drawLine((float)p2.x, (float)p2.y, (float)p3.x, (float)p3.y);
	}
	private void dbgPath(Graphics grphcs){
		dbgString("update in: " + pathTime, grphcs);
		dbgString("last update time: " + level.getLastPathUpdateDuration(), grphcs);
		PathingGraph graph = level.getPathingGraph(debugPathing);
		double rad = 0.1f;
		double transformedDiameter = transformLength(2f * rad);
		for(PointI p:graph.startingPoints){
			PointD p1 = transformPoint(new PointD((double)p.x - rad, (double)p.y - rad));
			grphcs.drawOval((float)p1.x, (float)p1.y, (float)transformedDiameter, (float)transformedDiameter);
		}
		for(int x=0; x<level.w; x++){
			for(int y=0; y<level.h; y++){
				PointD p1 = transformPoint(new PointD(x, y));
				for(PointI p:graph.transitions.get(x).get(y)){
					PointD p2 = transformPoint(p.getPointF());
					grphcs.drawGradientLine(
							(float)p1.x, (float)p1.y, Color.red, 
							(float)p2.x, (float)p2.y, Color.yellow);
				}
			}
		}
	}
	private void dbgPathWeights(Graphics g){
		double max = level.getMaxPathingWeight();
		if(max <= 0) return;
		g.setColor(Color.red);
		for(int y=0; y<level.h; y++){
			for(int x=0; x<level.w; x++){
				PointI p = new PointI(x, y);
				double rad = level.getPathingWeightAt(p) / max / 2f;
				double diameter = transformLength(2f * rad);
				if(diameter <= 4f) continue;
				PointD p1 = transformPoint(new PointD(x - rad, y - rad));
				g.fillOval((float)p1.x, (float)p1.y, (float)diameter, (float)diameter);
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
	
	private void calculateRenderOffsetBounds(){
		PointD tmp = transformPoint(new PointD(level.w, level.h));
		tmp.x -= gui.brCorner.x;
		tmp.y -= gui.brCorner.y;
		renderMinOffset = transformPointBack(tmp.x, tmp.y);
		renderMinOffset.multiply(-1f);
		
		tmp = transformPoint(new PointD(0f, 0f));
		tmp.x -= gui.tlCorner.x - 1;
		tmp.y -= gui.tlCorner.y - 1;
		renderMaxOffset = transformPointBack(tmp.x, tmp.y);
		renderMaxOffset.multiply(-1f);
	}

	private void centerRenderOffset(){
		renderOffset.x = (renderMinOffset.x + renderMaxOffset.x) / 2f;
		renderOffset.y = (renderMinOffset.y + renderMaxOffset.y) / 2f;
	}

	private void correctRenderOffset(){
		boolean xl = (renderOffset.x < renderMinOffset.x);
		boolean xh = (renderOffset.x > renderMaxOffset.x);
		if(xl && xh){
			renderOffset.x = (renderMinOffset.x + renderMaxOffset.x) / 2f;
		} else {
			if(xl) renderOffset.x = renderMinOffset.x;
			if(xh) renderOffset.x = renderMaxOffset.x;
		}
		boolean yl = (renderOffset.y < renderMinOffset.y);
		boolean yh = (renderOffset.y > renderMaxOffset.y);
		if(yl && yh){
			renderOffset.y = (renderMinOffset.y + renderMaxOffset.y) / 2f;
		} else {
			if(yl) renderOffset.y = renderMinOffset.y;
			if(yh) renderOffset.y = renderMaxOffset.y;
		}
		if((interactionState == InteractionState.scrolling) && ((xl && xh) || (yl && yh))) initiateScrollDragging();
	}
	
	private void initiateScrollDragging(){
		interactionState = InteractionState.scrolling;
		scrollOffsetStart.x = renderOffset.x;
		scrollOffsetStart.y = renderOffset.y;
		Input in = gameContainer.getInput();
		scrollMouseStart = new PointI(in.getMouseX(), in.getMouseY());
	}

	private void stopScrollDragging(){
		interactionState = InteractionState.normal;
	}
	
	private void searchSelectedTower(PointD loc){
		PointI p = loc.getPointI();
		for(Tower t:level.towers){
			int n = t.def.size;
			PointI pt = t.getPointI();
			if((p.x >= pt.x) && (p.x < pt.x + n) && (p.y >= pt.y) && (p.y < pt.y + n)){
				selectedTower = t;
				return;
			}
		}
		selectedTower = null;
	}

	@Override
	public void keyPressed(int key, char c) {
	}

	@Override
	public void mouseMoved(int oldx, int oldy, int newx, int newy) {
		switch(interactionState){
			case normal:
				searchSelectedTower(transformPointBack(newx, newy));
				break;
		}
	}

	@Override
	public void mouseDragged(int oldx, int oldy, int newx, int newy) {
		switch(interactionState){
			case scrolling:
				double dx = -transformLengthBack(scrollMouseStart.x - newx);
				double dy = -transformLengthBack(scrollMouseStart.y - newy);
				renderOffset.x = scrollOffsetStart.x + dx;
				renderOffset.y = scrollOffsetStart.y + dy;
				correctRenderOffset();
				break;
			case normal:
				searchSelectedTower(transformPointBack(newx, newy));
				break;
		}
	}

	@Override
	public void mouseClicked(int button, int x, int y, int clickCount) {
	}

	@Override
	public void mousePressed(int button, int x, int y) {
		if(button == Input.MOUSE_RIGHT_BUTTON){
			switch(interactionState){
				case normal: 
					initiateScrollDragging(); 
					break;
				case placeTower: 
					interactionState = InteractionState.normal; 
					break;
			}
		}
	}

	@Override
	public void mouseReleased(int button, int x, int y) {
		if(button == Input.MOUSE_RIGHT_BUTTON){
			if(interactionState == InteractionState.scrolling){
				stopScrollDragging(); 
				return;
			}
		}
	}

	@Override
	public void mouseWheelMoved(int newValue) {
		PointD center1 = transformPointBack(gui.getWindowCenterX(), gui.getWindowCenterY());
		renderScale *= (double)Math.pow(renderScaleSpeed, newValue);
		if(renderScale < renderMinScale) renderScale = renderMinScale;
		if(renderScale > renderMaxScale) renderScale = renderMaxScale;
		PointD center2 = transformPointBack(gui.getWindowCenterX(), gui.getWindowCenterY());
		renderOffset.x -= center1.x - center2.x;
		renderOffset.y -= center1.y - center2.y;			
		if((interactionState == InteractionState.scrolling)) initiateScrollDragging();
		calculateRenderOffsetBounds();
		correctRenderOffset();
	}
	
	@Override
	public void update(GameContainer gc, StateBasedGame sbg, int i) throws SlickException {
		// pathing
		pathTime -= i;
		if(pathTime <= 0){
			pathTime = maxPathTime;
			level.updatePaths();
			for(Mob m:level.mobs) if(random.nextFloat() < 0.1f) m.updatePath();
		}
		// minimap
		mapTime -= i;
		if(mapTime <= 0){
			mapTime = maxMapTime;
			gui.updateMapImage(level);
		}
		// tick entities
		double time = ((double)i) / 1000f * timeScale;
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
		if(in.isKeyPressed(Input.KEY_ADD)) timeScale *= 1.5f;
		if(in.isKeyPressed(Input.KEY_SUBTRACT)) timeScale /= 1.5f;
		if(in.isKeyPressed(Input.KEY_R)) level.resetPathingWeights();
		if(in.isMousePressed(0)){
			PointD p = transformPointBack(in.getMouseX(), in.getMouseY());
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
