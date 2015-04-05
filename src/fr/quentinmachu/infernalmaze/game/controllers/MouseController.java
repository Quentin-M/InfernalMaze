package fr.quentinmachu.infernalmaze.game.controllers;

import static org.lwjgl.glfw.GLFW.GLFW_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_DISABLED;
import static org.lwjgl.glfw.GLFW.glfwSetInputMode;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetScrollCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;

import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import fr.quentinmachu.infernalmaze.game.Game;

public class MouseController extends InputController {
	// Constants
	public static final float ROTATION_SENSITIVITY = 0.1f;
	public static final float MOVEMENT_SENSITIVITY = 0.005f;
	public static final float ROTATION_RESET_SPEED = 0.95f;
	public static final float MOVEMENT_RESET_SPEED = 0.95f;
	
	// Internal variables
	private double mdx, mdy;
	private boolean mb0, mb1;
	private GLFWCursorPosCallback cursorPosCallBack;
	private GLFWScrollCallback scrollCallback;
	private GLFWMouseButtonCallback mouseButtonCallback;

	public MouseController(Game game) {
		super(game);
	}
	
	@Override
	public void init() {
    	// Disable mouse
    	glfwSetInputMode(game.getWindow(), GLFW_CURSOR, GLFW_CURSOR_DISABLED);
    	
    	// Initialize values
		mdx = mdy = 0;

		// Set button0 + mb0 + mb1 callback
		glfwSetMouseButtonCallback(game.getWindow(), mouseButtonCallback = new GLFWMouseButtonCallback() {
			@Override
			public void invoke(long window, int btn0, int btn1, int btn2) {
				if(btn0==0 && btn1==1 && btn2==0) mb0 = true;
				else mb0 = false;
				
				if(btn0==2 && btn1==1 && btn2==0) setButton0(true);
				else setButton0(false);
				
				if(btn0==1 && btn1==1 && btn2==0) mb1 = true;
				else mb1 = false;
			}
		});
		
		// Set rx + ry callback
        glfwSetCursorPosCallback(game.getWindow(), cursorPosCallBack = new GLFWCursorPosCallback(){
            @Override
            public void invoke(long window, double xpos, double ypos) {
                int dx = 0, dy = 0;
                if(mdx != 0 && mdy != 0) {
                	dx += xpos - mdx;
                    dy += ypos - mdy;
                }
                
                mdx = xpos;
                mdy = ypos;
                
                if(!mb0 && !mb1) {
            		setRy(getRy() - dx*ROTATION_SENSITIVITY);
            		setRx(getRx() + dy*ROTATION_SENSITIVITY);
                } else if(mb0) {
                	setX(getX() + dx*MOVEMENT_SENSITIVITY);
                	setY(getY() + dy*MOVEMENT_SENSITIVITY);
                } else if(mb1) {
                	setRz(getRz() + dx*ROTATION_SENSITIVITY);
                }
            }
        });
        
        // Set z callback
        glfwSetScrollCallback(game.getWindow(), scrollCallback = new GLFWScrollCallback() {
			@Override
			public void invoke(long arg0, double xscroll, double yscroll) {
        		setZ((float) yscroll);
			}
        });
	}

	@Override
	public void poll() {
        if(!mb0) {
        	setX(getX()*MOVEMENT_RESET_SPEED);
        	setY(getY()*MOVEMENT_RESET_SPEED);
        }
        
        if(!mb1) {
        	setRz(getRz()*ROTATION_RESET_SPEED);
        }
	}
	
	@Override
	public void dispose() {
		cursorPosCallBack.release();
		scrollCallback.release();
		mouseButtonCallback.release();
	}
}
