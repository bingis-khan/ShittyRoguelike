package com.bingis_khan.shitty_roguelike.engine.display;

import java.awt.Color;

/**
 * Basic interface for simple console drawing on a display.
 * 
 * @author bingis_khan
 *
 */
public interface Display {
	
	/**
	 * Draws this character on screen 
	 * on these coordinates.
	 * 
	 * @param x Horizontal position.
	 * @param y Vertical position.
	 * @param c Character.
	 */
	public void putChar(int x, int y, int c);
	
	/**
	 * Sets background color on these coordinates to the given color.
	 * 
	 * @param x Horizontal position.
	 * @param y Vertical position.
	 * @param color Background color.
	 */
	public void setBackground(int x, int y, Color color);
	
	/**
	 * Writes a given string to screen from x to x + text.length().
	 * Must be overridden if ASCII is not used.
	 * 
	 * @param x X.
	 * @param y Y.
	 * @param text Text to be written to display.
	 */
	public default void writeText(int x, int y, String text) {
		int currentX = x;
		for (char c : text.toCharArray()) {
			putChar(currentX, y, c);
			currentX++;
		}
	}
}
