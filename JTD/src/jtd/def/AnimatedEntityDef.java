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

	public double[] times = {1f};
	public boolean isCyclic = true;

	public void fillImage(String name, double size, double time){
		fillImage(name, size);
		times = new double[]{time};
	}
	
	public void fillImages(String nameStart, String nameEnd, int imageCount, int digitCount, double sizes, double times){
		fillImages(nameStart, nameEnd, imageCount, digitCount, sizes);
		this.times = new double[imageCount];
		for(int i=0; i<imageCount; i++){
			this.times[i] = times;
		}
	}
	
}
