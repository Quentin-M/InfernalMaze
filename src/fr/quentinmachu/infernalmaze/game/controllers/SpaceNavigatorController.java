package fr.quentinmachu.infernalmaze.game.controllers;

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
	ControllerEnvironment controllerEnvironment = ControllerEnvironment.getDefaultEnvironment();
	Controller[] controllers = controllerEnvironment.getControllers();
	for (Controller controller : controllers) {
	    if ("SpaceNavigator".equalsIgnoreCase(controller.getName())) {
		spaceNavigator = controller;
		components = spaceNavigator.getComponents();
	    }
	}

	if (spaceNavigator == null) {
	    throw new RuntimeException("Could not find any Space Navigator controller.");
	}
    }

    @Override
    public void poll() {
	if (spaceNavigator.poll()) {
	    for (Component component : components) {
		switch (component.getName()) {
		case "Axe X":
		case "x":
		    setX((float) (component.getPollData() * MAX_MOVEMENT / 0.26));
		    break;

		case "Axe Y":
		case "y":
		    setY((float) (component.getPollData() * MAX_MOVEMENT / 0.26));
		    break;

		case "Axe Z":
		case "z":
		    setZ((float) (component.getPollData() * MAX_MOVEMENT / 0.26));
		    break;

		case "Rotation X":
		case "rx":
		    setRx((float) (component.getPollData() * MAX_ROTATION_XY / 0.26));
		    break;

		case "Rotation Y":
		case "ry":
		    setRy((float) (component.getPollData() * MAX_ROTATION_XY / 0.26));
		    break;

		case "Rotation Z":
		case "rz":
		    setRz((float) (component.getPollData() * MAX_ROTATION_Z / 0.26));
		    break;

		case "Bouton 0":
		case "0":
		    if (component.getPollData() == 1.0)
			setButton0(true);
		    else
			setButton0(false);
		    break;

		case "Bouton 1":
		case "1":
		    if (component.getPollData() == 1.0)
			setButton1(true);
		    else
			setButton1(false);
		    break;

		default:
		    System.out.println("Axe non supporté (pilote?): " + component.getName());
		}
	    }
	}
    }

    @Override
    public void dispose() {

    }
}
