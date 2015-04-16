package fr.quentinmachu.infernalmaze.game;

import java.awt.Point;

import fr.quentinmachu.infernalmaze.game.objects.BallObject;
import fr.quentinmachu.infernalmaze.game.objects.MazeTowerObject;
import fr.quentinmachu.infernalmaze.maze.MazeTower;
import fr.quentinmachu.infernalmaze.maze.MazeTowerCuts;
import fr.quentinmachu.infernalmaze.ui.Camera;
import fr.quentinmachu.infernalmaze.ui.Light;
import fr.quentinmachu.infernalmaze.ui.math.Vector3f;
import fr.quentinmachu.infernalmaze.ui.math.Vector4f;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;

public class MazeGame extends Game {   
    // Constants
	public static final float CAMERA_ANGLE = 30f;
    public static final float CAMERA_FOV = 45;
    public static final float CAMERA_NEAR = 1;
    public static final float CAMERA_FAR = 100;
    public static final float CAMERA_TRAVELING_SPEED = 0.1f;
    
    public static final float Z_CONTROLLER_TIME_THRESHOLD = 0.8f;
    public static final float Z_CONTROLLER_MIN_AMPLITUDE = 0.5f;
    
    // Rendering
    private Camera camera;
    private Light mainLight;
    private Vector3f ambientLight;
    private BallObject ball;
    private MazeTowerObject mazeTower;
    private float mazeGap;
    
    // Game variables
    private MazeTowerCuts maze;
    private int cameraLevel;
    private int currentLevel;
	
    private float z_controller_time_threshold_timer;
    private Point lastGate;
    
    public MazeGame(int windowWidth, int windowHeight, String inputControllerName, MazeTowerCuts maze) {
		super("Infernal Maze", windowWidth, windowHeight, inputControllerName);
		this.maze = maze;
	}
    
    @Override
    public void init() {
    	// Initialize game variables
    	currentLevel = 0;

    	mazeGap = (maze.getHeight()+maze.getWidth())/2*1.35f;
    	z_controller_time_threshold_timer = 0;
    	
    	// Initialize lighting
    	mainLight = new Light(
			new Vector4f(0.20f, 0.20f, 1, 0.0f),
			new Vector4f(1.0f, 1.0f, 1.0f, 0.0f),
			new Vector4f(1.0f, 1.0f, 1.0f, 0.0f),
			0.0f, 0.0f, 0.0f,
			0.0f, 0.0f,
			new Vector3f(0.0f, 0.0f, 0.0f)
    	);
    	ambientLight = new Vector3f(0.1f, 0.1f, 0.1f);
    	
    	// Initialize camera
    	cameraLevel = currentLevel;
    	camera = new Camera(getCameraTargetEye(), getCameraTargetCenter(), new Vector3f(0, 0, 1), CAMERA_FOV, CAMERA_NEAR, CAMERA_FAR);
    	
        // Initialize the maze tower
    	mazeTower = new MazeTowerObject(this, maze);
    	
    	// Initialize the ball
    	ball = new BallObject(this);
    	ball.setPosition(new Vector3f((float) (mazeTower.getMazeObjects()[currentLevel].getMaze().getOrigin().getX() + 0.5f), (float) (mazeTower.getMazeObjects()[currentLevel].getMaze().getOrigin().getY() + 0.5f), BallObject.SPHERE_RADIUS));
    }

    @Override
    public void update() {
    	float delta = 1 / (float) TARGET_UPS;
    	    	
    	// Update camera
    	// If the button0 is pressed, align the camera with the current level
    	if(getInputController().isButton1()) {
    		cameraLevel = currentLevel;
    	}
    	// If the Z-axis of the controller is used, use a timer & a threshold to determine if we should go up/down
    	if(getInputController().getZ() > Z_CONTROLLER_MIN_AMPLITUDE || getInputController().getZ() < - Z_CONTROLLER_MIN_AMPLITUDE) {
    		z_controller_time_threshold_timer += delta;

    		if(z_controller_time_threshold_timer > Z_CONTROLLER_TIME_THRESHOLD) {    	
    			z_controller_time_threshold_timer = 0;
    			
    			if(getInputController().getZ()<0 && cameraLevel>0) cameraLevel--;
    			else if(getInputController().getZ()>0 && cameraLevel<maze.getDepth()-1) cameraLevel++;
    			
    			getInputController().setZ(0);
    		}
    	} else {
    		z_controller_time_threshold_timer = 0;
    	}
    	
    	// Update level if the ball is currently on a teleporter
		int ballCoordX = (int) Math.floor(ball.getPosition().x);
		int ballCoordY = (int) Math.floor(ball.getPosition().y);
		if(currentLevel<maze.getDepth()-1 && !ball.isTeleporting()) {
			for(Point p: mazeTower.getMazeObjects()[currentLevel].getMaze().getDownGates()) {
				if(getInputController().isButton0() && p.x == ballCoordX && p.y == ballCoordY) {
					ball.teleport();
					currentLevel++;
				}
			}
		}
		if(currentLevel>0 && !ball.isTeleporting()) {
			for(Point p: mazeTower.getMazeObjects()[currentLevel].getMaze().getUpGates()) {
				if(getInputController().isButton0() && p.x == ballCoordX && p.y == ballCoordY) {
					ball.teleport();
					currentLevel--;
				}
			}
		}
		
        ball.update();
        mazeTower.update();
    }

    @Override
    public void render(float alpha) {   	
    	// Clear drawing area 
    	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        
    	camera.setCenter(camera.getCenter().lerp(getCameraTargetCenter(), alpha*CAMERA_TRAVELING_SPEED));
    	camera.setEye(camera.getEye().lerp(getCameraTargetEye(), alpha*CAMERA_TRAVELING_SPEED));
    	
        // Draw game objects 
        ball.render(alpha);
        mazeTower.render(alpha);
    }

	@Override
    public void dispose() {
		
	}
    
	/////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////
	
	private Vector3f getCameraTargetCenter() {
		return new Vector3f(maze.getWidth()/2, maze.getHeight()/2, - cameraLevel*mazeGap);
	}
	
	private Vector3f getCameraTargetEye() {
		return new Vector3f(maze.getWidth()/2, maze.getHeight()*1.20f, - (cameraLevel-1)*mazeGap-0.1f);
	}
	
	/////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////
	
	/**
	 * @return the camera
	 */
    public Camera getCamera() {
    	return camera;
    }
	
	/**
	 * @return the Z gap between each maze
	 */
	public float getMazeGap() {
		return mazeGap;
	}

	/**
	 * @return the main scene light
	 */
	public Light getMainLight() {
		return mainLight;
	}
	
	/**
	 * @return the scene ambient light
	 */
	public Vector3f getAmbientLight() {
		return ambientLight;
	}
	
	/**
	 * @return the maze tower
	 */
	public MazeTowerObject getMazeTower() {
		return mazeTower;
	}

	/**
	 * @return the current level
	 */
	public int getCurrentLevel() {
		return currentLevel;
	}
	
	/**
	 * @return the camera level
	 */
	public float getCameraLevel() {
		return cameraLevel;
	}

	/**
	 * Sets the camera level
	 * 
	 * @param cameraLevel the new camera level
	 */
	public void setCameraLevel(int cameraLevel) {
		this.cameraLevel = cameraLevel;
	}
}
