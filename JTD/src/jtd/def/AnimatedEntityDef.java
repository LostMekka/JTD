/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jtd.def;

/**
 *
 * @author LostMekka
 */
public abstract class AnimatedEntityDef extends EntityDef{

	public float[] times = {1f};
	public boolean isCyclic = true;

	public void fillImage(String name, float size, float time){
		fillImage(name, size);
		times = new float[]{time};
	}
	
	public void fillImages(String nameStart, String nameEnd, int imageCount, int digitCount, float sizes, float times){
		fillImages(nameStart, nameEnd, imageCount, digitCount, sizes);
		this.times = new float[imageCount];
		for(int i=0; i<imageCount; i++){
			this.times[i] = times;
		}
	}
	
}
