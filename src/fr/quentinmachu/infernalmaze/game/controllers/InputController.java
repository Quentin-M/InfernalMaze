package fr.quentinmachu.infernalmaze.game.controllers;

import fr.quentinmachu.infernalmaze.game.Game;

public abstract class InputController {
	protected Game game;
	
	protected float ry;
	protected float rx;
	
	public abstract void init();
	public abstract void input();
	public abstract void dispose();
	
	public InputController(Game game) {
		this.game = game;
	}
	
	/**
	 * @return the rz
	 */
	public float getRx() {
		return rx;
	}
	
	/**
	 * @return the ry
	 */
	public float getRy() {
		return ry;
	}
}
