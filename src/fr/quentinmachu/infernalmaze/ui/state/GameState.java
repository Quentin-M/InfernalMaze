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

import java.awt.Point;
import fr.quentinmachu.infernalmaze.game.Game;
import fr.quentinmachu.infernalmaze.game.controllers.InputController;
import fr.quentinmachu.infernalmaze.game.objects.BallObject;
import fr.quentinmachu.infernalmaze.game.objects.MazeObject;
import fr.quentinmachu.infernalmaze.game.objects.MazeTowerObject;
import fr.quentinmachu.infernalmaze.maze.MazeTower;
import fr.quentinmachu.infernalmaze.ui.Camera;
import fr.quentinmachu.infernalmaze.ui.Light;
import fr.quentinmachu.infernalmaze.ui.math.Vector3f;
import fr.quentinmachu.infernalmaze.ui.math.Vector4f;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;

public class GameState implements State {
    private Game game;
    
    // Game constants
	public static final int MAZE_SIZE = 10;
    
    // Camera
    public static final float FOV = 35;
    public static final float NEAR = 1;
    public static final float FAR = 100;
    private Camera camera;
    
    // Lighting
    private Light mainLight;
    private Vector3f ambient;

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
    	// Initialize light
    	mainLight = new Light(
			new Vector4f(0.0f, MAZE_SIZE, 10.0f, 1.0f),
			new Vector4f(1.0f, 1.0f, 1.0f, 0.0f),
			new Vector4f(1.0f, 1.0f, 1.0f, 0.0f),
			0.8f, 0.0f, 0.0f,
			180.0f, 0.0f,
			new Vector3f(5.0f, 5.0f, 0.0f)
    	);
    	ambient = new Vector3f(0.1f, 0.1f, 0.1f);
    	
    	// Initialize camera
    	camera = new Camera(new Vector3f(MAZE_SIZE/2, MAZE_SIZE*1.5f, 20), new Vector3f(MAZE_SIZE/2, MAZE_SIZE/2, 0), new Vector3f(0, 0, 1), FOV, NEAR, FAR);
    	
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

	public BallObject getBall() {
		return ball;
	}
	
	public Light getMainLight() {
		return mainLight;
	}
	
	public Vector3f getAmbient() {
		return ambient;
	}
	
	public MazeObject getCurrentMazeObject() {
		return mazeTower.getMazeObjects()[currentLevel];
	}
}
