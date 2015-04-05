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
                    	setX((float) (component.getPollData()*MAX_MOVEMENT/0.26));
                        break;
                    case "y":
                    	setY((float) (component.getPollData()*MAX_MOVEMENT/0.26));
                        break;
                    case "z":
                    	setZ((float) (component.getPollData()*MAX_MOVEMENT/0.26));
                        break;
                    case "rx":
                    	setRx((float) (component.getPollData()*MAX_ROTATION_XY/0.26));
                        break;
                    case "ry":
                    	setRy((float) (component.getPollData()*MAX_ROTATION_XY/0.26));
                        break;
                    case "rz":
                        setRz((float) (component.getPollData()*MAX_ROTATION_Z/0.26));
                        break;
                    case "0":
                    	if(component.getPollData()==1.0) setButton0(true);
                    	else setButton0(false);
                    	break;
                    case "1":
                    	break;
                }
            }
        }
	}
	
	@Override
	public void dispose() {
		
	}
}
