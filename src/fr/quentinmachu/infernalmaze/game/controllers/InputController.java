package fr.quentinmachu.infernalmaze.game.controllers;

import fr.quentinmachu.infernalmaze.game.Game;

public abstract class InputController {
	protected Game game;
	
	protected float x;
	protected float y;
	protected float z;
	
	protected float rx;
	protected float ry;
	protected float rz;
	
	protected boolean button0;
	protected boolean button1;
	
	public abstract void init();
	public abstract void poll();
	public abstract void dispose();
	
	public InputController(Game game) {
		this.game = game;
	}
	
	/**
	 * @return the x
	 */
	public float getX() {
		return x;
	}
	
	/**
	 * @return the y
	 */
	public float getY() {
		return y;
	}
	
	/**
	 * @return the z
	 */
	public float getZ() {
		return z;
	}
	
	/**
	 * @return the rx
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
	
	/**
	 * @return the rz
	 */
	public float getRz() {
		return rz;
	}
	
	/**
	 * @return the button1
	 */
	public boolean isButton0() {
		return button0;
	}
	
	/**
	 * @return the button2
	 */
	public boolean isButton1() {
		return button1;
	}
}
