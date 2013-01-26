/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jtd;

import java.util.LinkedList;
import jtd.effect.instant.InstantEffect;
import jtd.effect.timed.TimedEffectDef;
import jtd.entities.Entity;
import jtd.entities.Mob;
import jtd.entities.ParticleFactory;
import jtd.entities.Projectile;
import jtd.entities.Tower;
import jtd.level.PathListener;
import jtd.level.PathingGraph;

/**
 *
 * @author LostMekka
 */
public interface GameControllerInterface extends CoordinateTransformator, KillListener, PathListener {

	public Mob giveTarget(Tower tower);
	public void dealDamage(Mob mob, Tower attacker, 
			LinkedList<InstantEffect> instantEffects, 
			LinkedList<TimedEffectDef> timedEffects, Double direction);
	public void dealAreaDamage(PointD loc, Tower attacker, 
			LinkedList<InstantEffect> instantEffects, 
			LinkedList<TimedEffectDef> timedEffects);
	public boolean addParticle(ParticleFactory f, PointD point, double dir);
	public void addProjectile(Projectile p);
	public boolean movePointIntoLevel(PointD p);
	public boolean isVisible(Entity e);
	public PathingGraph getCurrentPathingGraph(int mobSize);
	
}
