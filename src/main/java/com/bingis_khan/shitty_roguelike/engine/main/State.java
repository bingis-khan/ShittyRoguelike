package com.bingis_khan.shitty_roguelike.engine.main;

import com.bingis_khan.shitty_roguelike.engine.display.Display;

public abstract class State {
	public abstract void onAdd(Engine e);
	public abstract void onRemove(Engine e);
	public abstract void render(Display d);
	public abstract void update(Engine e);
}
