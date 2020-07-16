package com.bingis_khan.shitty_roguelike.engine.display;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

/**
 * ConsoleDisplay is an implementation of a simple console in java's 
 * awt library.
 * 
 * @author bingis_khan
 *
 */
public class ConsoleDisplay implements Display {
	private final int width, height,
					  hTiles, vTiles,	
					  tileWidth, tileHeight;
	
	/** Tiles representing each character. */
	private final BufferedImage[] tiles;
	
	/** Characters already on the screen. */
	private final List<BufferedImage> characters;
	
	/** Colors of each 'tile' on the screen. */
	private final List<Color> backgrounds;
	
	/** Default ASCII tileset character grouping. */
	private static final int TILESET_H_TILE_NUM = 16, 
							 TILESET_V_TILE_NUM = 16;
	
	// Console background color is black.
	private static final Color BACKGROUND = Color.BLACK;
	
	/* Window / Frame */
	private final JFrame frame;
	private final Canvas canvas;
	
	// BufferStrategy for rendering.
	BufferStrategy bs;
	
	/**
	 * 
	 * 
	 * @param tileset Tileset which will be used by the console display.
	 * @param hTiles Number of tiles on a horizontal line in this console.
	 * @param vTiles Number of tiles on a vertical line in this console.
	 * @param windowName Name of the consoles window.
	 */
	public ConsoleDisplay(BufferedImage tileset, final int hTiles, 
			final int vTiles, String windowName) {
		assert hTiles > 0 : "Number of horizontal tiles must be greater than 0.";
		assert vTiles > 0 : "Number of vertical tiles must be greater than 0.";
		
		this.tileWidth = tileset.getWidth() / TILESET_H_TILE_NUM; 
		this.tileHeight = tileset.getHeight() / TILESET_V_TILE_NUM;
		
		this.hTiles = hTiles; this.vTiles = vTiles;
		
		this.width = hTiles * tileWidth; 
		this.height = vTiles * tileHeight;
		
		// Initialize window.
		canvas = makeCanvas(width, height);
		frame = makeFrame(canvas, width, height, windowName);
		
		// Split this tileset into seperate tiles.
		tiles = splitIntoTiles(tileset, TILESET_H_TILE_NUM, TILESET_V_TILE_NUM);
		
		characters = initializeCharacters(tiles[' '], hTiles, vTiles);
		backgrounds = initializeBackgrounds(BACKGROUND, hTiles, vTiles);
	}
	
	/** Draws backgrounds for each character. */
	private void drawBackground(Graphics g) {
		for (int yy = 0; yy < vTiles; yy++) {
			for (int xx = 0; xx < hTiles; xx++) {
				g.setColor(backgrounds.get(toIndex(xx, yy)));
				g.fillRect(xx * tileWidth, yy * tileHeight, tileWidth, tileHeight);
			}
		}
	}
	
	/** Draws all the console's characters on screen.
	 *  Does not yet support different colors. */
	private void drawCharacters(Graphics g) {
		for (int yy = 0; yy < vTiles; yy++) {
			for (int xx = 0; xx < hTiles; xx++) {
				g.drawImage(characters.get(toIndex(xx, yy)), xx * tileWidth, yy * tileHeight, tileWidth, tileHeight, null);
			}
		}
	}
	
	/** Method that handles drawing on the screen. */
	private void drawScreen(Graphics g) {
		drawBackground(g); // Must be invoked before drawCharacters.
		drawCharacters(g);
	}
	
	private boolean inBounds(int x, int y) {
		return x >= 0 && x < hTiles && y >= 0 && y < vTiles;
	}
	/**
	 * Method for rendering the contents of the display.
	 */
	public void render() {
		bs = canvas.getBufferStrategy();
		if (bs == null) {
			canvas.createBufferStrategy(3);
			return;
		}
		
		Graphics g = bs.getDrawGraphics();
		
		// Fill console's background with BACKGROUND color.
		g.setColor(BACKGROUND);
		g.fillRect(0, 0, width, height);
		
		drawScreen(g);
		
		bs.show();
		g.dispose();
	}
	
	/** All two dimensional spaces are squished into only one dimension.
	 *  Given x, y, toIndex determines element's index in a one dimensional array.  */
	private int toIndex(int x, int y) {
		return y * hTiles + x;
	}
	
	/* 'DISPLAY' INTERFACE */

	@Override
	public void putChar(int x, int y, int c) {
		// Must be changed if we have more or less than 256 possible tiles.
		assert c >= 0 : "Integer value of 'c' must be greater or equal to 0.";
		assert c < 256 : "Integer value of 'c' must be smaller than 256.";
		
		// It's not illegal to put a character 'outside' of the screen.
		// It just has no effect.
		if (!inBounds(x, y)) 
			return;
		
		// Coloring an image is very time consuming, 
		// because it must create a new image every time it recolors.
		// (if we'll be using this putChar method, where you 
		//  literally pass in a character instead of having an entity own its image.)
		
		characters.set(toIndex(x, y), tiles[c]);
	}

	@Override
	public void setBackground(int x, int y, Color color) {
		
		// Setting the color of the background outside of the console
		// is not illegal, but has no effect.
		if (!inBounds(x, y)) 
			return;
		
		backgrounds.set(toIndex(x, y), color);
	}
	
	/* Initializations */
	
	/** Initialize all char backgrounds with a single color. */
	private static List<Color> initializeBackgrounds(Color defaultColor, int hTiles, int vTiles) {
		List<Color> backgrounds = new ArrayList<Color>();
		for (int i = 0; i < hTiles * vTiles; i++) {
			backgrounds.add(defaultColor);
		}
		
		return backgrounds;
	}
	
	/** Initialize characters with default tile. */
	private static List<BufferedImage> initializeCharacters(BufferedImage defaultTile, int hTiles, int vTiles) {
		List<BufferedImage> characters = new ArrayList<BufferedImage>();
		for (int i = 0; i < hTiles * vTiles; i++) {
			characters.add(defaultTile);
		}
		
		return characters;
	}
	
	private static Canvas makeCanvas(int width, int height) {
		Canvas canvas = new Canvas();
		Dimension dim = new Dimension(width, height);
		canvas.setPreferredSize(dim);
		canvas.setMaximumSize(dim);
		canvas.setMinimumSize(dim);
		canvas.setFocusable(false);
		return canvas;
	}
	
	private static JFrame makeFrame(Canvas c, int width, int height, String name) {
		JFrame frame = new JFrame(name);
		frame.setSize(width, height);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		frame.add(c);
		frame.pack();
		return frame;
	}
	
	/**
	 * Splits this tileset into seperate tiles (BufferedImages) and returns its array.
	 * 
	 * The order of the tiles when split into an array:
	 * 
	 * AB
	 * CD -> ABCD
	 * 
	 * @param tileset Tileset.
	 * @param hTiles Number of tiles on horizontal line.
	 * @param vTiles Number of tiles on vertical line.
	 * @return Array of those tiles. Its size is equal to hTiles * vTiles.
	 */
	private static BufferedImage[] splitIntoTiles(BufferedImage tileset, 
			final int hTiles, final int vTiles) {
		
		final int numTiles = hTiles * vTiles;
		BufferedImage[] tiles = new BufferedImage[numTiles];
		
		final int tileWidth = tileset.getWidth() / TILESET_H_TILE_NUM; 
		final int tileHeight = tileset.getHeight() / TILESET_V_TILE_NUM;
		
		for (int yy = 0; yy < vTiles; yy++) {
			for (int xx = 0; xx < hTiles; xx++) {
				tiles[yy * hTiles + xx] = tileset.getSubimage(
						xx * tileWidth, yy * tileHeight, tileWidth, tileHeight);
			}
		}
		
		return tiles;
	}
}