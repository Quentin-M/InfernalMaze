package fr.quentinmachu.infernalmaze.game.controllers;

import static org.lwjgl.glfw.GLFW.GLFW_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_DISABLED;
import static org.lwjgl.glfw.GLFW.glfwSetInputMode;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import org.lwjgl.glfw.GLFWCursorPosCallback;

import fr.quentinmachu.infernalmaze.game.Game;

public class MouseController extends InputController {
	// Constants
	public static final float SENSITIVITY = 0.1f;
	public static final float MAX_ROTATION = 20f;
	
	// Internal variables
	private GLFWCursorPosCallback cursorPosCallback;
	private int x, y;
    private int dx, dy;

	public MouseController(Game game) {
		super(game);
	}
	
	@Override
	public void init() {
    	// Disable mouse
    	glfwSetInputMode(game.getWindow(), GLFW_CURSOR, GLFW_CURSOR_DISABLED);
    	
    	// Initialize values
		x = 0;
		y = 0;
		dx = 0;
		dy = 0;

		//TODO Add missing axis / buttons
		
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
	public void poll() {
		if(ry-dx*SENSITIVITY>MAX_ROTATION) ry = MAX_ROTATION;
		else if(ry-dx*SENSITIVITY<-MAX_ROTATION) ry = -MAX_ROTATION;
		else ry += - dx*SENSITIVITY;
		
		if(rx+dy*SENSITIVITY>MAX_ROTATION) rx = MAX_ROTATION;
		else if(rx+dy*SENSITIVITY<-MAX_ROTATION) rx = -MAX_ROTATION;
		else rx += dy*SENSITIVITY;
		
		dx = 0;
		dy = 0;
	}
	
	@Override
	public void dispose() {
		cursorPosCallback.release();
	}
}
