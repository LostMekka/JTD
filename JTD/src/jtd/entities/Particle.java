/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jtd.entities;

import jtd.AssetLoader;
import jtd.PointF;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.state.StateBasedGame;

/**
 *
 * @author LostMekka
 */
public class Particle extends Entity{

	public PointF vel;
	public float size, spinVel, lifetime;
	private float age;
	
	public Particle(PointF loc, float size, PointF vel, float spinVel, Image sprite) {
		super(loc, 1);
		this.size = size;
		this.spinVel = spinVel;
		this.vel = vel;
		sprites[0] = sprite;
		age = 0;
	}

	@Override
	public void tick(float time) {
		age += time;
		if(age >= lifetime){
			kill(null);
			return;
		}
		loc.x += time * vel.x;
		loc.y += time * vel.y;
		rotate(time * spinVel);
	}

}
