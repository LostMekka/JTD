/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jtd;

/**
 *
 * @author LostMekka
 */
public final class GameCtrl {

	private static GameControllerInterface ctrl = null;

	public static GameControllerInterface get() {
		return ctrl;
	}

	public static void setController(GameControllerInterface ctrl) {
		GameCtrl.ctrl = ctrl;
	}
	
	private GameCtrl() {}
	
}
