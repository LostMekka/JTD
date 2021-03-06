/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jtd;

import java.util.HashMap;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;

/**
 *
 * @author LostMekka
 */
public class AssetLoader {
	
	private AssetLoader(){}

	private static Image errorImage = null;
	private static HashMap<String, Sound> sounds = new HashMap<>();
	private static HashMap<String, Image> images = new HashMap<>();
	
	public static Image getErrorImage(){
		if(errorImage == null){
			try {
				errorImage = new Image("error.png");
				errorImage.setFilter(Image.FILTER_NEAREST);
			} catch (Exception ex) {
				System.err.println("---------- ERROR: can't load error image");
				return null;
			}
		}
		return errorImage;
	}
	
	public static Image getImage(String name){
		return getImage(name, false, false);
	}
	
	public static Image getImage(String name, boolean smoothFiltered){
		return getImage(name, smoothFiltered, false);
	}
	
	public static Image getImage(String name, boolean smoothFiltered, boolean flipped){
		if(flipped){
			String name2 = "__FLIPPED__" + name;
			if(images.containsKey(name2)){
				return images.get(name2);
			} else {
				Image i = getImage(name, false);
				i = i.getFlippedCopy(true, false);
				images.put(name2, i);
				return i;
			}
		} else {
			if(images.containsKey(name)){
				return images.get(name);
			} else {
				Image i;
				try {
					i = new Image(name);
					if(!smoothFiltered) i.setFilter(Image.FILTER_NEAREST);
				} catch (Exception ex) {
					System.err.println("---------- ERROR: can't load image \"" + name + "\"");
					return getErrorImage();
				}
				if(i != null){
					images.put(name, i);
				}
				return i;
			}
		}
	}
	
	public static Sound getSound(String name){
		if(sounds.containsKey(name)){
			return sounds.get(name);
		} else {
			Sound s = null;
			try {
				s = new Sound(name);
			} catch (SlickException ex) {}
			if(s != null){
				sounds.put(name, s);
			}
			return s;
		}
	}
	
}
