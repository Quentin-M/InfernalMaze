package fr.quentinmachu.infernalmaze.game;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import fr.quentinmachu.infernalmaze.game.controllers.InputController;
import fr.quentinmachu.infernalmaze.game.controllers.MouseController;
import fr.quentinmachu.infernalmaze.ui.Renderer;
import fr.quentinmachu.infernalmaze.ui.Timer;
import fr.quentinmachu.infernalmaze.ui.state.GameState;
import fr.quentinmachu.infernalmaze.ui.state.State;
import fr.quentinmachu.infernalmaze.ui.state.StateMachine;

import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Game {
	// Constants
	private static String WINDOW_NAME = "Infernal Maze";
	
	public final static float CAMERA_ZOOM_RATIO = 1.5f;
	public final static float CAMERA_ANGLE = 30f;
	public final static float MAZE_MAX_INCLINATION = 20f;
	
	public static int WIDTH = 800;
	public static int HEIGHT = 600;
	public static final int TARGET_FPS = 60;
    public static final int TARGET_UPS = 30;
    
    protected Timer timer;
    protected InputController input;
    protected StateMachine state;
    
	private GLFWErrorCallback errorCallback;
    private long window;
	
    public Game(String inputController) {
    	timer = new Timer();
    	state = new StateMachine();
    	
		if(inputController.equalsIgnoreCase("Mouse")) input = new MouseController(this);
		else throw new IllegalArgumentException();
    }
    
    public void run() {
        init();
        loop();
        dispose();
    }
 
    private void init() {
        glfwSetErrorCallback(errorCallback = errorCallbackPrint(System.err));
        if(glfwInit() != GL11.GL_TRUE) throw new IllegalStateException("Unable to initialize GLFW");
 
        // Configure our window
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_RESIZABLE, GL_FALSE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
 
        // Create the window
        window = glfwCreateWindow(WIDTH, HEIGHT, WINDOW_NAME, NULL, NULL);
        if(window == NULL) throw new RuntimeException("Failed to create the GLFW window");
        
        // Center our window
        ByteBuffer vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        glfwSetWindowPos(window, (GLFWvidmode.width(vidmode) - WIDTH) / 2, (GLFWvidmode.height(vidmode) - HEIGHT) / 2);

        glfwMakeContextCurrent(window); // Make the OpenGL context current
        GLContext.createFromCurrent(); // Create context  
        
        glfwSwapInterval(1); // Enable v-sync
        
        timer.init(); // Initialize timer
        input.init(); // Initialize input
        initStates(); // Initialize the states
    }
 
    /**
     * Initializes the states.
     */
    private void initStates() {
    	state.add("game", new GameState(this));
        state.change("game");
	}

	/**
     * Releases resources that where used by the game.
     */
    private void dispose() {
        // Set empty state to trigger the exit method in the current state
        state.change(null);
        
        // Dispose input
        input.dispose();
        
        // Release window
        glfwDestroyWindow(window);

        // Terminate GLFW and release the error callback
        glfwTerminate();
        errorCallback.release();
    }
    
    /**
     * Synchronizes the game at specified frames per second.
     *
     * @param fps Frames per second
     */
    private void sync(int fps) {
        double lastLoopTime = timer.getLastLoopTime();
        double now = timer.getTime();
        float targetTime = 1f / fps;

        while (now - lastLoopTime < targetTime) {
            Thread.yield();

            /* This is optional if we want our game to stop consuming too much
             CPU but you will loose some accuracy because Thread.sleep(1) could
             sleep longer than 1 millisecond */
            try {
                Thread.sleep(1);
            } catch (InterruptedException ex) {
                Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
            }

            now = timer.getTime();
        }
    }
    
    private void loop() {
        float delta;
        float accumulator = 0f;
        float interval = 1f / TARGET_UPS;
        float alpha;
        
        while(glfwWindowShouldClose(window) == GL_FALSE) {
        	delta = timer.getDelta();
            accumulator += delta;
            
            input();
            
            while(accumulator >= interval) {
                update();
                timer.updateUPS();
                accumulator -= interval;
            }
            
            // Calculate alpha value for interpolation
            alpha = accumulator / interval;
            
            // Render game
            render(alpha);
        	glfwSwapBuffers(window); // swap the color buffers
            glfwPollEvents(); // Poll for window events.

            // Update timer FPS
            timer.updateFPS();
        	
            // Draw FPS, UPS and Context version
            //int height = renderer.getDebugTextHeight("Context");
            //renderer.drawDebugText("FPS: " + timer.getFPS() + " | UPS: " + timer.getUPS(), 5, 5 + height);

            // Update timer
            timer.update();

            // Sync @ FPS
            sync(TARGET_FPS);
        }
    }

    /**
     * Handles input.
     */
    private void input() {
    	input.input();
        state.input();
    }
    
    /**
     * Updates the game (fixed timestep).
     */
    private void update() {
        state.update();
    }
    
    /**
     * Renders the game (with interpolation).
     *
     * @param alpha Alpha value, needed for interpolation
     */
    private void render(float alpha) {
        state.render(alpha);
    }
    
	/**
	 * @return the window
	 */
	public long getWindow() {
		return window;
	}

	/**
	 * @return the input
	 */
	public InputController getInput() {
		return input;
	}
}
