/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jtd;

import org.newdawn.slick.Image;

/**
 *
 * @author LostMekka
 */
public final class Animation {
	
	public Image[] images = {null};
	public double[] times = {1d}, sizes = {1d};

	public Animation() {
	}

	public Animation(String nameStart, String nameEnd, int imageCount, int digitCount, double time, double size) {
		initImages(nameStart, nameEnd, imageCount, digitCount);
		initTimes(imageCount, time);
		initSizes(imageCount, size);
	}
	
	public Animation(String name, double time, double size) {
		images = new Image[]{AssetLoader.getImage(name)};
		times = new double[]{time};
		sizes = new double[]{size};
	}
	
	public void initImages(String nameStart, String nameEnd, int imageCount, int digitCount){
		images = new Image[imageCount];
		String zeros = "";
		double n = Math.pow(10, digitCount - 1);
		for(int i=imageCount-1; i>=0; i--){
			while((i < n) && (n > 1)){
				n /= 10;
				zeros += "0";
			}
			images[i] = AssetLoader.getImage(nameStart + zeros + i + nameEnd);
		}
	
	}
	
	public void initTimes(int imageCount, double time){
		this.times = new double[imageCount];
		for(int i=0; i<imageCount; i++){
			this.times[i] = time;
		}
	}
	
	public void initSizes(int imageCount, double size){
		sizes = new double[imageCount];
		for(int i=0; i<imageCount; i++){
			sizes[i] = size;
		}
	}
	
}
