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
	public static final int TEXT_HEIGHT_1 = 14;
	public static final int TEXT_HEIGHT_2 = 19;
	public static final int TEXT_HEIGHT_3 = 24;
	public static final double SELL_REFUND = 0.5d;
	
	public enum InteractionState {
		menu, normal, selected, placeTower, scrolling, mapScrolling, zooming
	}
	
	public GameContainer gameContainer;
	public LevelDataHolder level;
	public GameDef gameDef = new GameDef();
	public GameplayGui gui = new GameplayGui();
	public int money = 100;
	
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
	private double maxSpm = 5d, spm = maxSpm, tst = 0.99d, t = -15d;
	private InteractionState interactionState = InteractionState.normal;
	private InteractionState lastInteractionState = InteractionState.normal;
	private Tower selectedTower = null;
	private TowerDef selectedTowerDef = null;
	private boolean displaySellValue = false;
	private PointI mousePos = new PointI();
	private PointI placeTowerLocation = null;
	private boolean towerIsPlaceable = false;

	// debug vars
	public boolean debugTowers = false;
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
		mob.damage(attacker.def.damage, attacker, direction);
		mob.lastDamageWasSplashDamage = true;
	}
	
	public void dealAreaDamage(PointD loc, Tower attacker, 
			LinkedList<InstantEffect> instantEffects, 
			LinkedList<TimedEffectDef> timedEffects){
		for(Mob mob:level.mobs){
			if(mob.loc.distanceTo(loc) - mob.def.radius <= attacker.def.damageRadius){
				dealDamage(mob, attacker, instantEffects, timedEffects, loc.getRotationTo(mob.loc));
				mob.lastDamageWasSplashDamage = true;
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
		TowerDef rep3 = gameDef.getTowerDef(GameDef.TowerType.repeater, 3);
		TowerDef rep4 = gameDef.getTowerDef(GameDef.TowerType.repeater, 4);
		TowerDef can2 = gameDef.getTowerDef(GameDef.TowerType.cannon, 2);
		TowerDef frz4 = gameDef.getTowerDef(GameDef.TowerType.freezer, 4);
		
		level.addTower(new Tower(rep4, new PointI(7, 7)));
		level.addTower(new Tower(rep4, new PointI(12, 6)));
		level.addTower(new Tower(rep4, new PointI(17, 6)));
		level.addTower(new Tower(rep3, new PointI(22, 5)));
		
		level.addTower(new Tower(rep4, new PointI(23, 15)));
		level.addTower(new Tower(rep4, new PointI(18, 16)));
		level.addTower(new Tower(rep4, new PointI(13, 16)));
		level.addTower(new Tower(rep3, new PointI(8, 17)));

		level.addTower(new Tower(can2, new PointI(0, 7)));
		level.addTower(new Tower(can2, new PointI(29, 14)));
		
		level.addTower(new Tower(frz4, new PointI(26, 3)));
		level.addTower(new Tower(frz4, new PointI(4, 19)));
		
		gui.updateGuiImage(gc);
		gui.updateMapImage(level);
		renderOffset = new PointD(0f, 0f);
		calculateRenderOffsetBounds();
		renderOffset = renderMaxOffset.clone();
		updateCommandCard();
	}
	
	private void printSelectedTower(Graphics g){
		gui.print(g, selectedTower.def.name, TEXT_HEIGHT_1);
		gui.print(g, String.format("level  : %d", selectedTower.def.level), TEXT_HEIGHT_1);
		if(displaySellValue){
			gui.print(g, String.format("credits spent : %d", selectedTower.cumulativeCost), TEXT_HEIGHT_1);
			gui.print(g, String.format("sell refund   : %d", (int)(selectedTower.cumulativeCost * SELL_REFUND)), TEXT_HEIGHT_1);
		} else {
			gui.print(g, String.format("damage : %1.2f", selectedTower.def.damage), TEXT_HEIGHT_1);
			gui.print(g, String.format("area   : %1.2f", selectedTower.def.damageRadius), TEXT_HEIGHT_1);
			gui.print(g, String.format("range  : %1.2f", selectedTower.def.range), TEXT_HEIGHT_1);
			gui.print(g, String.format("reload : %1.2f", selectedTower.def.reloadTime), TEXT_HEIGHT_1);
			gui.print(g, String.format("kills  : %d", selectedTower.kills), TEXT_HEIGHT_1);
			if(selectedTower.def.instantEffects.length + selectedTower.def.timedEffects.length > 0){
				gui.print(g, "effects:", TEXT_HEIGHT_1);
				for(InstantEffect e:selectedTower.def.instantEffects){
					gui.print(g, " " + e, TEXT_HEIGHT_1);
				}
				for(TimedEffectDef e:selectedTower.def.timedEffects){
					gui.print(g, " " + e, TEXT_HEIGHT_1);
				}
			}
		}					
	}
	
	private void printSelectedTowerDef(Graphics g){
		gui.print(g, selectedTowerDef.name, TEXT_HEIGHT_1);
		gui.print(g, String.format("build cost: %d", selectedTowerDef.cost), TEXT_HEIGHT_1);
		gui.print(g, String.format("level  : %d", selectedTowerDef.level), TEXT_HEIGHT_1);
		gui.print(g, String.format("damage : %1.2f", selectedTowerDef.damage), TEXT_HEIGHT_1);
		gui.print(g, String.format("area   : %1.2f", selectedTowerDef.damageRadius), TEXT_HEIGHT_1);
		gui.print(g, String.format("range  : %1.2f", selectedTowerDef.range), TEXT_HEIGHT_1);
		gui.print(g, String.format("reload : %1.2f", selectedTowerDef.reloadTime), TEXT_HEIGHT_1);
		if(selectedTowerDef.instantEffects.length + selectedTowerDef.timedEffects.length > 0){
			gui.print(g, "effects:", TEXT_HEIGHT_1);
			for(InstantEffect e:selectedTowerDef.instantEffects){
				gui.print(g, " " + e, TEXT_HEIGHT_1);
			}
			for(TimedEffectDef e:selectedTowerDef.timedEffects){
				gui.print(g, " " + e, TEXT_HEIGHT_1);
			}
		}					
	}
	private String getComparingString(double v1, double v2){
		v1 = (Math.round(v1 * 100d)) / 100d;
		v2 = (Math.round(v2 * 100d)) / 100d;
		if(v1 == v2) return String.format("%1.2f", v1);
		return String.format("%1.2f(%+1.2f)", v1, v1 - v2);
	}
	
	private void printUpgradeTowerDef(Graphics g){
		gui.print(g, selectedTowerDef.name, TEXT_HEIGHT_1);
		gui.print(g, "upgrade cost: " + selectedTowerDef.cost, TEXT_HEIGHT_1);
		gui.print(g, "level  : " + selectedTowerDef.level, TEXT_HEIGHT_1);
		gui.print(g, "damage : " + getComparingString(selectedTowerDef.damage, selectedTower.def.damage), TEXT_HEIGHT_1);
		gui.print(g, "area   : " + getComparingString(selectedTowerDef.damageRadius, selectedTower.def.damageRadius), TEXT_HEIGHT_1);
		gui.print(g, "range  : " + getComparingString(selectedTowerDef.range, selectedTower.def.range), TEXT_HEIGHT_1);
		gui.print(g, "reload : " + getComparingString(selectedTowerDef.reloadTime, selectedTower.def.reloadTime), TEXT_HEIGHT_1);
		if(selectedTower.def.instantEffects.length + selectedTower.def.timedEffects.length > 0){
			gui.print(g, "old effects:", TEXT_HEIGHT_1);
			for(InstantEffect e:selectedTower.def.instantEffects){
				gui.print(g, " " + e, TEXT_HEIGHT_1);
			}
			for(TimedEffectDef e:selectedTower.def.timedEffects){
				gui.print(g, " " + e, TEXT_HEIGHT_1);
			}
		}					
		if(selectedTowerDef.instantEffects.length + selectedTowerDef.timedEffects.length > 0){
			gui.print(g, "new effects:", TEXT_HEIGHT_1);
			for(InstantEffect e:selectedTowerDef.instantEffects){
				gui.print(g, " " + e, TEXT_HEIGHT_1);
			}
			for(TimedEffectDef e:selectedTowerDef.timedEffects){
				gui.print(g, " " + e, TEXT_HEIGHT_1);
			}
		}					
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
			if(debugTowers || (selectedTower == t)) dbgTower(g, t);
		}
		for(Mob m:level.mobs) m.draw(gc, sbg, g, this);
		for(Explosion e:level.explosions) e.draw(gc, sbg, g, this);
		for(Particle pa:level.particles) pa.draw(gc, sbg, g, this);
		for(Projectile pr:level.projectiles) pr.draw(gc, sbg, g, this);
		
		// debug stuff
		dbgStringReset();
		if((debugPathing > 0) && (debugPathing <= level.level.maxMobSize)) dbgPath(g);
		if(debugPathingWeights) dbgPathWeights(g);
		
		// tower placement
		if((interactionState == InteractionState.placeTower) && (!gui.isOnGui(mousePos.x, mousePos.y))){
			if(towerIsPlaceable){
				g.setColor(new Color(0f, 1f, 0f, 0.3f));
			} else {
				g.setColor(new Color(1f, 0f, 0f, 0.3f));
			}
			PointD p = placeTowerLocation.getPointD();
			p.x -= 0.5d;
			p.y -= 0.5d;
			p = transformPoint(p);
			float l = (float)transformLength(selectedTowerDef.size);
			g.fillRect((float)p.x, (float)p.y, l, l);
		}
		
		gui.draw(g, this);
		gui.print(g, "Credits: " + money, TEXT_HEIGHT_3);
		switch(interactionState){
			case normal:
				if(selectedTower != null){
					gui.print(g, "tower under mouse:", TEXT_HEIGHT_2);
					printSelectedTower(g);
				} else if(selectedTowerDef != null){
					gui.print(g, "build new tower:", TEXT_HEIGHT_2);
					printSelectedTowerDef(g);
				} else {
					gui.print(g, "nothing selected.", TEXT_HEIGHT_2);
				}
				break;
			case selected:
				if(selectedTowerDef != null){
					gui.print(g, "tower upgrade:", TEXT_HEIGHT_2);
					printUpgradeTowerDef(g);
				} else if(selectedTower != null){
					gui.print(g, "selected tower:", TEXT_HEIGHT_2);
					printSelectedTower(g);
				} else {
					gui.print(g, "ERROR! NO SELECTION!", TEXT_HEIGHT_2);
				}
				break;
			case placeTower:
				gui.print(g, "place tower:", TEXT_HEIGHT_2);
				printSelectedTowerDef(g);
				break;
			case mapScrolling:
			case scrolling:
				gui.print(g, "scrolling...", TEXT_HEIGHT_2);
				break;
		}
		g.flush();
	}
	
	private int dbgline = 1;
	private void dbgStringReset(){
		dbgline = 1;
	}
	private void dbgString(String s, Graphics grphcs){
		grphcs.drawString(s, 15, 15 * ++dbgline);
	}
	private void dbgTower(Graphics g, Tower t){
		g.setColor(new Color(1f, 1f, 1f, 0.5f));
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
				PathingGraph.Node n = graph.getNode(new PointI(x, y));
				if(n == null) continue;
				for(PathingGraph.Transition t:n.transitions){
					PointD p2 = transformPoint(t.n.loc.getPointD());
					Color c1 = new Color(1f, 0f, 0f, (float)t.p);
					Color c2 = new Color(1f, 1f, 0f, (float)t.p);
					grphcs.drawGradientLine(
							(float)p1.x, (float)p1.y, c1, 
							(float)p2.x, (float)p2.y, c2);
				}
			}
		}
	}
	private void dbgPathWeights(Graphics g){
		double max = level.getMaxPathingWeight();
		dbgString("maxPathingWeight=" + max, g);
		if(max <= 0) return;
		g.setColor(Color.red);
		for(int y=0; y<level.h; y++){
			for(int x=0; x<level.w; x++){
				PointI p = new PointI(x, y);
				double rad = level.getPathingWeightAt(p) / max / 2f;
				double diameter = transformLength(2f * rad);
				if(diameter <= 1f) continue;
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
		if(spm < maxSpm) spm /= tst;
	}
	
	private void mobGotKilled(Mob mob){
		level.killHappenedAt(mob.getPointI(), mob.def.size, !mob.lastDamageWasSplashDamage);
		money += mob.def.reward;
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

	private void centerScreenOn(PointD p){
		renderOffset.x = p.x - transformLength(gui.getWindowWidth() / 2d);
		renderOffset.y = p.y - transformLength(gui.getWindowHeight() / 2d);
		correctRenderOffset();
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
		if(interactionState !=InteractionState.scrolling){
			setInteractionState(InteractionState.scrolling);
		}
		scrollOffsetStart.x = renderOffset.x;
		scrollOffsetStart.y = renderOffset.y;
		Input in = gameContainer.getInput();
		scrollMouseStart = new PointI(in.getMouseX(), in.getMouseY());
	}

	private void stopScrollDragging(){
		revertInteractionState();
	}
	
	private void searchSelectedTower(int x, int y){
		PointI p = transformPointBack(x, y).getPointI();
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
	
	private void executeAction(PlayerAction a){
		if(a == null) return;
		switch(a.type){
			case buy:
				selectedTowerDef = a.def;
				setInteractionState(InteractionState.placeTower);
				towerIsPlaceable = false;
				placeTowerLocation = transformPointBack(mousePos.x, mousePos.y).getPointI(a.def.size);
				break;
			case sell:
				level.removeTower(selectedTower);
				money += selectedTower.cumulativeCost / 2;
				selectedTower = null;
				setInteractionState(InteractionState.normal);
				break;
			case upgrade:
				selectedTowerDef = a.def;
				if(selectedTowerDef.cost > money) break;
				money -= selectedTowerDef.cost;
				Tower t = new Tower(a.def, selectedTower);
				level.removeTower(selectedTower);
				level.addTower(t);
				selectedTower = t;
				updateCommandCard();
				mouseMoved(mousePos.x, mousePos.y, mousePos.x, mousePos.y);
				break;
		}
	}
	
	private void setInteractionState(InteractionState s){
		if(interactionState == s){
			if(s == InteractionState.selected) updateCommandCard();
			return;
		}
		lastInteractionState = interactionState;
		interactionState = s;
		updateCommandCard();
	}
	
	private void revertInteractionState(){
		interactionState = lastInteractionState;
		updateCommandCard();
	}
	
	private void updateCommandCard(){
		switch(interactionState){
			case mapScrolling:
			case menu:
			case placeTower:
			case scrolling:
			case zooming:
				displayEmptyCommandCard();
				break;
			case normal:
				displayBuyCommandCard();
				break;
			case selected:
				displayTowerCommandCard();
				break;
		}
	}
	
	private void displayTowerCommandCard(){
		TowerDef[] defs = selectedTower.def.upgradeOptions;
		PlayerAction[][] acts = new PlayerAction[][]{
			new PlayerAction[defs.length], 
			new PlayerAction[0], 
			new PlayerAction[1]};
		for(int i=0; i<defs.length; i++){
			PlayerAction a = new PlayerAction(defs[i]);
			a.type = PlayerAction.Type.upgrade;
			acts[0][i] = a;
		}
		acts[2][0] = new PlayerAction(PlayerAction.Type.sell);
		gui.actions = acts;
	}
	
	private void displayBuyCommandCard(){
		LinkedList<TowerDef> defs = gameDef.getBuildableTowers();
		PlayerAction[][] acts = new PlayerAction[1][defs.size()];
		for(int i=0; i<defs.size(); i++){
			PlayerAction a = new PlayerAction(defs.get(i));
			acts[0][i] = a;
		}
		gui.actions = acts;
	}

	private void displayEmptyCommandCard(){
		gui.actions = new PlayerAction[0][0];
	}

	@Override
	public void keyPressed(int key, char c) {
		switch(key){
			case Input.KEY_1: executeAction(gui.getAction(0, 0)); break;
			case Input.KEY_2: executeAction(gui.getAction(1, 0)); break;
			case Input.KEY_3: executeAction(gui.getAction(2, 0)); break;
			case Input.KEY_4: executeAction(gui.getAction(3, 0)); break;
				
			case Input.KEY_Q: executeAction(gui.getAction(0, 1)); break;
			case Input.KEY_W: executeAction(gui.getAction(1, 1)); break;
			case Input.KEY_E: executeAction(gui.getAction(2, 1)); break;
			case Input.KEY_R: executeAction(gui.getAction(3, 1)); break;
				
			case Input.KEY_A: executeAction(gui.getAction(0, 2)); break;
			case Input.KEY_S: executeAction(gui.getAction(1, 2)); break;
			case Input.KEY_D: executeAction(gui.getAction(2, 2)); break;
			case Input.KEY_F: executeAction(gui.getAction(3, 2)); break;
				
			case Input.KEY_Y: executeAction(gui.getAction(0, 3)); break;
			case Input.KEY_X: executeAction(gui.getAction(1, 3)); break;
			case Input.KEY_C: executeAction(gui.getAction(2, 3)); break;
			case Input.KEY_V: executeAction(gui.getAction(3, 3)); break;
			case Input.KEY_M:
				t += spm;
				break;
			case Input.KEY_ADD:
				timeScale *= 1.2f;
				break;
			case Input.KEY_SUBTRACT:
				timeScale /= 1.2f;
				break;
			case Input.KEY_B:
				level.resetPathingWeights();
				break;
		}
	}

	@Override
	public void mouseMoved(int oldx, int oldy, int newx, int newy) {
		mousePos.x = newx;
		mousePos.y = newy;
		switch(interactionState){
			case normal:
			case selected:
				if(gui.isOnGui(newx, newy)){
					PlayerAction a = gui.isOnActionList(newx, newy);
					if(a == null){
						selectedTowerDef = null;
						displaySellValue = false;
					} else {
						selectedTowerDef = a.def;
						displaySellValue = (a.type == PlayerAction.Type.sell);
					}
					if(interactionState == InteractionState.normal) selectedTower = null;
				} else {
					selectedTowerDef = null;
					if(interactionState == InteractionState.normal) searchSelectedTower(newx, newy);
				}
				break;
			case placeTower:
				PointD p = transformPointBack(newx, newy);
				PointI pi = p.getPointI(selectedTowerDef.size);
				if(!pi.equals(placeTowerLocation)){
					towerIsPlaceable = false;
					placeTowerLocation = pi;
				}
				break;
		}
	}

	@Override
	public void mouseDragged(int oldx, int oldy, int newx, int newy) {
		mousePos.x = newx;
		mousePos.y = newy;
		switch(interactionState){
			case scrolling:
				double dx = -transformLengthBack(scrollMouseStart.x - newx);
				double dy = -transformLengthBack(scrollMouseStart.y - newy);
				renderOffset.x = scrollOffsetStart.x + dx;
				renderOffset.y = scrollOffsetStart.y + dy;
				correctRenderOffset();
				break;
			case normal:
				searchSelectedTower(newx, newy);
				break;
		}
	}

	@Override
	public void mouseClicked(int button, int x, int y, int clickCount) {
		switch(button){
			case Input.MOUSE_LEFT_BUTTON:
				if(gui.isOnGui(x, y)){
					PlayerAction a = gui.isOnActionList(x, y);
					if(a != null){
						executeAction(a);
					} else if(gui.isOnMap(x, y)){
						centerScreenOn(gui.getMapLocation(x, y));
					} else switch(interactionState){
						case placeTower:
						case selected:
							setInteractionState(InteractionState.normal);
							selectedTower = null;
							selectedTowerDef = null;
					}
				} else {
					switch(interactionState){
						case normal:
						case selected:
							searchSelectedTower(x, y);
							if(selectedTower == null){
								setInteractionState(InteractionState.normal);
							} else {
								setInteractionState(InteractionState.selected);
							}
							break;
						case placeTower:
							if(towerIsPlaceable){
								money -= selectedTowerDef.cost;
								Tower t = new Tower(selectedTowerDef, placeTowerLocation);
								level.addTower(t);
								level.updatePaths();
								for(Mob m:level.mobs){
									m.updatePath(m.walksIntoTower(t));
								}
								// TODO: update
								setInteractionState(InteractionState.normal);
							}
							break;
					}
				}
				break;
			case Input.MOUSE_RIGHT_BUTTON:
				switch(interactionState){
					case placeTower:
					case selected:
						setInteractionState(InteractionState.normal);
						searchSelectedTower(x, y);
				}
				break;
		}
	}

	@Override
	public void mousePressed(int button, int x, int y) {
		switch(button){
			case Input.MOUSE_LEFT_BUTTON:
				if(gui.isOnMap(x, y)){
					setInteractionState(InteractionState.mapScrolling);
				}
				break;
			case Input.MOUSE_RIGHT_BUTTON:
				switch(interactionState){
					case normal: 
					case selected: 
					case placeTower: 
						initiateScrollDragging(); 
						break;
				}
				break;
		}
	}

	@Override
	public void mouseReleased(int button, int x, int y) {
		switch(button){
			case Input.MOUSE_LEFT_BUTTON:
				switch(interactionState){
					case mapScrolling:
						revertInteractionState();
						break;
				}
				break;
			case Input.MOUSE_RIGHT_BUTTON:
				switch(interactionState){
					case scrolling:
						stopScrollDragging();
						break;
				}
				break;
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
			for(Mob m:level.mobs) m.updatePath(false);
		}
		// minimap
		mapTime -= i;
		if(mapTime <= 0){
			mapTime = maxMapTime;
			gui.updateMapImage(level);
		}
		// tick entities
		double time = ((double)i) / 1000f * timeScale;
		level.update(time);
		// if state is placeTower, check if the tower can be placed at the current position
		if(interactionState == InteractionState.placeTower){
			towerIsPlaceable = (
					level.isBuildable(placeTowerLocation, selectedTowerDef.size) &&
					!level.isBlocking(placeTowerLocation, selectedTowerDef.size) && 
					(money >= selectedTowerDef.cost));
		}
		// testing stuff
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
