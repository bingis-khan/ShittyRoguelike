package com.bingis_khan.shitty_roguelike.game.main;

import java.awt.image.BufferedImage;

import com.bingis_khan.shitty_roguelike.engine.display.Display;
import com.bingis_khan.shitty_roguelike.engine.io.Loader;
import com.bingis_khan.shitty_roguelike.engine.main.Engine;
import com.bingis_khan.shitty_roguelike.engine.main.State;

public class Game {
	public static void main(String[] args) {
		BufferedImage tileset = Loader.loadImage("tilesetrs.png");
		Engine engine = new Engine(60, 40, 30, tileset, 
								"bob");
		engine.run(new State() {

			@Override
			public void onAdd(Engine e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onRemove(Engine e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void render(Display d) {
				d.writeText(0, 0, "dupa");
			}

			@Override
			public void update(Engine e) {
				// TODO Auto-generated method stub
				
			}
			
		});
	}
}
