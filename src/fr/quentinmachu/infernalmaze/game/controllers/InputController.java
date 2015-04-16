package fr.quentinmachu.infernalmaze.game.controllers;

import fr.quentinmachu.infernalmaze.game.Game;

public abstract class InputController {
	protected Game game;
	
	public static final float MAX_ROTATION_XY = 20f;
	public static final float MAX_ROTATION_Z = 45f;
	public static final float MAX_MOVEMENT = 0.8f;

	private float x;
	private float y;
	private float z;
	
	private float rx;
	private float ry;
	private float rz;
	
	private boolean button0;
	private boolean button1;
	
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
	 * @return the button0
	 */
	public boolean isButton0() {
		return button0;
	}
	
	/**
	 * @return the button
	 */
	public boolean isButton1() {
		return button1;
	}
	
	/**
	 * @param game the game to set
	 */
	public void setGame(Game game) {
		this.game = game;
	}
	
	/**
	 * @param x the x to set
	 */
	public void setX(float x) {
		if(Math.abs(x)>MAX_MOVEMENT) this.x = Math.signum(x)*MAX_MOVEMENT;
		else this.x = x;
	}
	
	/**
	 * @param y the y to set
	 */
	public void setY(float y) {
		if(Math.abs(y)>MAX_MOVEMENT) this.y = Math.signum(y)*MAX_MOVEMENT;
		else this.y = y;
	}
	
	/**
	 * @param z the z to set
	 */
	public void setZ(float z) {
		if(Math.abs(z)>MAX_MOVEMENT) this.z = Math.signum(z)*MAX_MOVEMENT;
		else this.z = z;
	}
	
	/**
	 * @param rx the rx to set
	 */
	public void setRx(float rx) {
		if(Math.abs(rx)>MAX_ROTATION_XY) this.rx = Math.signum(rx)*MAX_ROTATION_XY;
		else this.rx = rx;
	}
	
	/**
	 * @param ry the ry to set
	 */
	public void setRy(float ry) {
		if(Math.abs(ry)>MAX_ROTATION_XY) this.ry = Math.signum(ry)*MAX_ROTATION_XY;
		else this.ry = ry;
	}
	
	/**
	 * @param rz the rz to set
	 */
	public void setRz(float rz) {
		if(Math.abs(rz)>MAX_ROTATION_Z) this.rz = Math.signum(rz)*MAX_ROTATION_Z;
		else this.rz = rz;
	}
	
	/**
	 * @param button0 the button0 to set
	 */
	public void setButton0(boolean button0) {
		this.button0 = button0;
	}
	
	/**
	 * @param button0 the button0 to set
	 */
	public void setButton1(boolean button1) {
		this.button1 = button1;
	}
}
