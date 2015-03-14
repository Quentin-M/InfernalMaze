/*
 * The MIT License (MIT)
 *
 * Copyright © 2015, Heiko Brumme
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package fr.quentinmachu.infernalmaze.ui.state;

import java.awt.Color;
import java.awt.Point;
import java.awt.Toolkit;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;

import fr.quentinmachu.infernalmaze.game.Game;
import fr.quentinmachu.infernalmaze.game.controllers.InputController;
import fr.quentinmachu.infernalmaze.game.objects.BallObject;
import fr.quentinmachu.infernalmaze.game.objects.MazeObject;
import fr.quentinmachu.infernalmaze.game.objects.MazeTowerObject;
import fr.quentinmachu.infernalmaze.maze.MazeTower;
import fr.quentinmachu.infernalmaze.ui.Camera;
import fr.quentinmachu.infernalmaze.ui.Renderer;
import fr.quentinmachu.infernalmaze.ui.Texture;
import fr.quentinmachu.infernalmaze.ui.math.Vector3f;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;

public class GameState implements State {
    private Game game;
    
    // Game constants
	public static final int MAZE_SIZE = 10;
    
    // Camera
    public static final float FOV = 40;
    public static final float NEAR = 1;
    public static final float FAR = 100;
    private Camera camera;
    
    // Game objects
    private BallObject ball;
    private MazeTowerObject mazeTower;
    
    // Game variables
    private int currentLevel = 0;
    
    public GameState(Game game) {
    	this.game = game;
    }

    @Override
    public void enter() {   	
    	// Initialize camera
    	camera = new Camera(new Vector3f(MAZE_SIZE/2, MAZE_SIZE*2, 20), new Vector3f(MAZE_SIZE/2, MAZE_SIZE/2, 0), new Vector3f(0, 0, 1), FOV, NEAR, FAR);
    	
        // Initialize the maze tower
    	mazeTower = new MazeTowerObject(this, new MazeTower(10, 10, 3));
    	
    	// Initialize the ball
    	Point origin = mazeTower.getMazeTower().getOrigin();
    	ball = new BallObject(this);
    	ball.setPosition(new Vector3f((float) (origin.getX() + 0.5f), (float) (origin.getY() + 0.5f), BallObject.SPHERE_RADIUS));
    }
    
    @Override
    public void input() {
        ball.input();
        mazeTower.input();
    }

    @Override
    public void update() {
        // Update position
        ball.update();

        // Check for collision
        /*player.checkCollision(gameHeight);
        ball.collidesWith(player);
        opponent.checkCollision(gameHeight);
        ball.collidesWith(opponent);*/
    }

    @Override
    public void render(float alpha) {   	
    	// Clear drawing area 
    	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // Draw game objects 
        ball.render(alpha);
        mazeTower.render(alpha);
    }

    @Override
    public void exit() {
        //texture.delete();
    }
    
    public Camera getCamera() {
    	return camera;
    }
    
    public InputController getInput() {
    	return game.getInput();
    }

	/**
	 * @return the ball
	 */
	public BallObject getBall() {
		return ball;
	}
	
	public MazeObject getCurrentMazeObject() {
		return mazeTower.getMazeObjects()[currentLevel];
	}
}
