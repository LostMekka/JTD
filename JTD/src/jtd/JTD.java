/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jtd;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

/**
 *
 * @author LostMekka
 */
public class JTD {

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		StateBasedGame game = new StateBasedGame("JTD") {
			@Override
			public void initStatesList(GameContainer gc) throws SlickException {
				addState(TDGameplayState.get());
			}
		};
		try {
			AppGameContainer gc = new AppGameContainer(game, 1024, 768, false);
			gc.setUpdateOnlyWhenVisible(false);
			//gc.setTargetFrameRate(60);
			gc.start();
		} catch (SlickException ex) {
			Logger.getLogger(JTD.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
