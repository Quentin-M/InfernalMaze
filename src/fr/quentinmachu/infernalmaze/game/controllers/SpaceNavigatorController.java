package fr.quentinmachu.infernalmaze.game.controllers;

import static org.lwjgl.glfw.GLFW.GLFW_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_DISABLED;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_HIDDEN;
import static org.lwjgl.glfw.GLFW.glfwSetInputMode;
import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import fr.quentinmachu.infernalmaze.game.Game;

public class SpaceNavigatorController extends InputController {
	// Constats
	public static final float ROTATION_SENSITIVITY = 100.0f;
	public static final float POSITION_SENSITIVITY = 5.0f;
	
	// Internal variables
	private Controller spaceNavigator;
	private Component[] components;
	
	public SpaceNavigatorController(Game game) {
		super(game);
	}
	
	@Override
	public void init() {
    	// Hide mouse
    	glfwSetInputMode(game.getWindow(), GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
    	
		ControllerEnvironment controllerEnvironment = ControllerEnvironment.getDefaultEnvironment();
        Controller[] controllers = controllerEnvironment.getControllers();
        for(Controller controller: controllers){
            if ("SpaceNavigator".equalsIgnoreCase(controller.getName())){
                spaceNavigator = controller;
                components = spaceNavigator.getComponents();
            }
        }
		
        if(spaceNavigator == null) {
        	throw new RuntimeException("Could not find any Space Navigator controller.");
        }
        
		// Disable mouse
		glfwSetInputMode(game.getWindow(), GLFW_CURSOR, GLFW_CURSOR_DISABLED);
	}

	@Override
	public void poll() {
		if (spaceNavigator.poll()) {
            for(Component component: components) {
                switch(component.getName()) {
                    case "x":
                    	x = POSITION_SENSITIVITY * component.getPollData();
                        break;
                    case "y":
                    	y = POSITION_SENSITIVITY * component.getPollData();
                        break;
                    case "z":
                    	z = POSITION_SENSITIVITY * component.getPollData();
                        break;
                    case "rx":
                    	rx = ROTATION_SENSITIVITY * component.getPollData();
                        break;
                    case "ry":
                    	ry = ROTATION_SENSITIVITY * component.getPollData();
                        break;
                    case "rz":
                        rz = ROTATION_SENSITIVITY * component.getPollData();
                        break;
                    case "0":
                    	if(component.getPollData()==1.0) button0 = true;
                    	else button0 = false;
                    	break;
                    case "1":
                    	if(component.getPollData()==1.0) button1 = true;
                    	else button1= false;
                    	break;
                }
            }
        }
	}
	
	@Override
	public void dispose() {
		//cursorPosCallback.release();
	}
}
