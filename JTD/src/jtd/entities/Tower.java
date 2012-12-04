/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jtd.entities;

import java.util.LinkedList;
import jtd.PointF;
import jtd.TDGameplayState;
import jtd.effect.instant.InstantEffect;
import jtd.effect.timed.TimedEffect;
import jtd.entities.Entity;
import jtd.entities.Mob;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.StateBasedGame;

/**
 *
 * @author LostMekka
 */
public class Tower extends Entity{

	public static final TDGameplayState GAME = TDGameplayState.get();
	
	public TowerDef def;
	private float cooldown;
	private float[] instantEffectCooldowns, timedEffectCooldowns;
	private Mob target = null;
	private PointF lastTargetLocation = null;

	public Tower(TowerDef def, PointF loc) {
		super(loc, 2);
		lastTargetLocation = loc.clone();
		lastTargetLocation.y -= 1;
		updateTowerDef(def);
	}
	
	public final void updateTowerDef(TowerDef def){
		if(this.def == def) return;
		this.def = def;
		sprites[0] = def.body;
		sprites[1] = def.head;
		cooldown = def.reloadTime;
		instantEffectCooldowns = new float[def.instantEffects.length];
		for(int i=0; i<instantEffectCooldowns.length; i++){
			instantEffectCooldowns[i] = def.instantEffects[i].cooldown;
		}
		timedEffectCooldowns = new float[def.timedEffects.length];
		for(int i=0; i<timedEffectCooldowns.length; i++){
			timedEffectCooldowns[i] = def.timedEffects[i].cooldown;
		}
	}
	
	@Override
	public void tick(float time) {
		// targeting
		if(target == null){
			target = GAME.giveTarget(this);
		} else {
			if(loc.distanceTo(target.loc) > def.range){
				lastTargetLocation = target.loc.clone();
				target = GAME.giveTarget(this);
			}
		}
		// cooldowns
		cooldown -= time;
		for(int i=0; i<instantEffectCooldowns.length; i++){
			instantEffectCooldowns[i] -= time;
		}
		for(int i=0; i<timedEffectCooldowns.length; i++){
			timedEffectCooldowns[i] -= time;
		}
		// shooting
		while(cooldown <= 0){
			LinkedList<InstantEffect> instantEffects = new LinkedList<>();
			LinkedList<TimedEffect> timedEffects = new LinkedList<>();
			for(int i=0; i<instantEffectCooldowns.length; i++){
				if(instantEffectCooldowns[i] <= 0){
					instantEffectCooldowns[i] -= def.instantEffects[i].cooldown;
					instantEffects.add(def.instantEffects[i]);
				}
			}
			for(int i=0; i<timedEffectCooldowns.length; i++){
				if(timedEffectCooldowns[i] <= 0){
					timedEffectCooldowns[i] -= def.timedEffects[i].cooldown;
					timedEffects.add(new TimedEffect(this, def.timedEffects[i]));
				}
			}
			GAME.shoot(this, target, instantEffects, timedEffects);
			cooldown += def.reloadTime;
		}		
	}

	@Override
	public void entityDraw(GameContainer gc, StateBasedGame sbg, Graphics grphcs) {
		// draw head
		if(sprites[1] != null){
			if(target == null){
				sprites[1].setRotation(loc.getRotationTo(lastTargetLocation));
			} else {
				sprites[1].setRotation(loc.getRotationTo(target.loc));
			}
		}
	}
	
}
