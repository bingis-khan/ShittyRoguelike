package com.bingis_khan.shitty_roguelike.engine.main;

import java.awt.image.BufferedImage;
import java.util.LinkedList;

import com.bingis_khan.shitty_roguelike.engine.display.ConsoleDisplay;

/**
 * Base engine class.
 * 
 * Hosts a game loop, a basic display,
 * a state 'manager' and nothing else, really.
 * 
 * Game Loop:
 * - number of updates per second determined by ticksPerSecond.
 * - renders as much as it can in a second.
 * 
 * States:
 * - renders all states, from bottom to top of the stack.
 * - updates only the state on the top.
 * 
 *   Useful, when making menus or targeting systems.
 * 
 * @author bingis_khan
 *
 */
public class Engine {
	
	private final ConsoleDisplay display;
	
	/** Number of updates per second, basically FPS. */
	private final int ticksPerSecond;
	
	/** State stack. LinkedList, because it already has all the methods needed. */
	private final LinkedList<State> states;
	
	private boolean running = false, 
					removed = false;
	
	/**
	 * Creates a new Engine instance. 
	 * To start the engine, run() must also be invoked.
	 * 
	 * @param ticksPerSecond FPS, ideally 60.
	 * @param hTiles Number of characters on a single horizontal line.
	 * @param vTiles Number of characters on a single vertical line.
	 * @param tileset Tileset image, for characters ranging from 0 to 256.
	 * @param windowName Name of the created window.
	 */
	public Engine(int ticksPerSecond, int hTiles, int vTiles, 
				BufferedImage tileset, String windowName) {
		display = new ConsoleDisplay(tileset, hTiles, vTiles, windowName);
		
		this.ticksPerSecond = ticksPerSecond;
		states = new LinkedList<>();
	}
	
	/**
	 * Renders each state on stack, starting from
	 * the bottom of the stack.
	 */
	private void render() {
		for (State state : states) {
			state.render(display); // Render each state.
		}
		
		display.render();
	}
	
	/**
	 * Begins the process of rendering and updating the state.
	 * 
	 * @param initialState The state with which the engine starts with.
	 */
	public void run(State initialState) {
		assert !running : "Method 'run' should not be invoked while engine is running.";
		
		running = true;
	
		int timePerTick = 1_000_000_000 / ticksPerSecond;
		double delta = 0;
		long now;
		long lastTime = System.nanoTime();
		long timer = 0;
		int ticks = 0; // Enable FPS counter in the future?
		
		// Set initial state. 
		overlayState(initialState);
		while (!states.isEmpty()) {
			render();
			
			now = System.nanoTime();
			delta+= (now - lastTime) / timePerTick;
			timer += now - lastTime;
			lastTime = now;
			if(delta >= 1) {
				update();
				ticks++;
				delta--;
				
				removed = false;
			}
			if(timer >= 1_000_000_000) {
				ticks = 0;
				timer = 0;
			}
			
		}
	}
	
	/**
	 * Updates the state on top of the stack.
	 */
	private void update() {
		activeState().update(this);
	}
	
	/* HANDLING STATES */
	private State activeState() {
		return states.getLast();
	}
	
	/**
	 * Adds newState on top of the stack, making it the new active state.
	 * Calls newState's onAdd method.
	 * 
	 * @param newState New state to be added.
	 */
	public void overlayState(State newState) {
		states.add(newState);
		newState.onAdd(this);
	}
	
	/**
	 * Removes an active state, making the one under it active.
	 * Calls removed state's onRemove method.
	 */
	public void removeActiveState() {
		assert !states.isEmpty() : "Attempted to remove a state while there are none!";
		
		if (removed) {
			throw new RuntimeException("Active state cannot remove any other state than itself.");
		}
		
		removed = true;
		states.removeLast().onRemove(this);
	}
	
	/**
	 * Replaces current active state with newState.
	 * 
	 * @param newState New active state.
	 */
	public void switchState(State newState) {
		removeActiveState();
		overlayState(newState);
	}
}
