/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jtd;

import java.util.LinkedList;
import jtd.entities.Mob;
import jtd.entities.Tower;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

/**
 *
 * @author LostMekka
 */
public class TDGameplayState extends BasicGameState {
	
	private static TDGameplayState in = null;
	public static TDGameplayState get(){
		if(in == null) in = new TDGameplayState();
		return in;
	}
	private TDGameplayState(){}
	
	private LinkedList<Mob> mobs;
	private LinkedList<Tower> towers;

	public Mob giveTarget(Tower tower){
		// TODO: return a mob if possible
		return null;
	}
	
	@Override
	public int getID() {
		return 1;
	}

	@Override
	public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
		mobs = new LinkedList<>();
		towers = new LinkedList<>();
	}

	@Override
	public void render(GameContainer gc, StateBasedGame sbg, Graphics grphcs) throws SlickException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void update(GameContainer gc, StateBasedGame sbg, int i) throws SlickException {
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
}
