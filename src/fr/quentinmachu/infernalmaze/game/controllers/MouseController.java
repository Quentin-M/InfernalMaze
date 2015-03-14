package fr.quentinmachu.infernalmaze.game.controllers;

import static org.lwjgl.glfw.GLFW.*;

import org.lwjgl.glfw.GLFWCursorPosCallback;

import fr.quentinmachu.infernalmaze.game.Game;

public class MouseController extends InputController {
	public static float sensitivity = 0.1f;
	
	// Internal variables
	private GLFWCursorPosCallback cursorPosCallback;
	private int x, y;
    private int dx, dy;

	public MouseController(Game game) {
		super(game);
	}
	
	@Override
	public void init() {
		// Initialize values
		x = 0;
		y = 0;
		dx = 0;
		dy = 0;
		
		// Disable mouse
		glfwSetInputMode(game.getWindow(), GLFW_CURSOR, GLFW_CURSOR_DISABLED);
		
		// Set callback
        glfwSetCursorPosCallback(game.getWindow(), cursorPosCallback = new GLFWCursorPosCallback(){
            @Override
            public void invoke(long window, double xpos, double ypos) {
                if(x != 0 && y != 0) {
                	dx += (int) xpos - x;
                    dy += (int) ypos - y;
                }
                
                x = (int) xpos;
                y = (int) ypos;
            }
        });
	}

	@Override
	public void input() {
		rx = dy*sensitivity;
		ry = dx*sensitivity;
		dx = 0;
		dy = 0;
	}
	
	@Override
	public void dispose() {
		cursorPosCallback.release();
	}
}
