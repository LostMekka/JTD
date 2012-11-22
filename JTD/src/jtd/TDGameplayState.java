/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jtd;

import java.util.LinkedList;
import jtd.effect.instant.InstantEffect;
import jtd.effect.timed.TimedEffect;
import jtd.entities.Entity;
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
		// TODO: handle kill
	}

	public Mob giveTarget(Tower tower){
		// TODO: return a mob if possible
		return null;
	}
	
	public void shoot(Tower tower, Mob mob, LinkedList<InstantEffect> instantEffects, LinkedList<TimedEffect> timedEffects){
		level.projectiles.add(new Projectile(tower.def.projectileDef, mob, tower, instantEffects, timedEffects, tower.loc));
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
