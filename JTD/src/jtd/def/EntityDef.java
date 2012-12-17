/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jtd.def;

import jtd.AssetLoader;
import org.newdawn.slick.Image;

/**
 *
 * @author LostMekka
 */
public abstract class EntityDef {
	
	public static final Image errorImage = AssetLoader.getErrorImage();
	
	public Image[] sprites = {errorImage};
	public float[] sizes = {0};
	
	public void fillImage(String name, float size){
		sprites = new Image[]{AssetLoader.getImage(name)};
		sizes = new float[]{size};
	}

	public void fillImages(String nameStart, String nameEnd, int imageCount, int digitCount){
		sprites = new Image[imageCount];
		String zeros = "";
		double n = Math.pow(10, digitCount - 1);
		for(int i=imageCount-1; i>=0; i--){
			while((i < n) && (n > 1)){
				n /= 10;
				zeros += "0";
			}
			sprites[i] = AssetLoader.getImage(nameStart + zeros + i + nameEnd);
		}
	}
	
	public void fillImages(String nameStart, String nameEnd, int imageCount, int digitCount, float sizes){
		fillImages(nameStart, nameEnd, imageCount, digitCount);
		this.sizes = new float[imageCount];
		for(int i=0; i<imageCount; i++){
			this.sizes[i] = sizes;
		}
	}
	
}
