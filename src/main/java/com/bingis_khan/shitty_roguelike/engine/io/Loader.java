package com.bingis_khan.shitty_roguelike.engine.io;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Loader {
	private static final String ROOT = "src/main/resources/";
	
	public static BufferedImage loadImage(String path) {
		try {
			return ImageIO.read(new File(ROOT + path));
		} catch(IOException e) {
			e.printStackTrace();
		}
		return new BufferedImage(800, 800, BufferedImage.TYPE_INT_ARGB);
	}
}
