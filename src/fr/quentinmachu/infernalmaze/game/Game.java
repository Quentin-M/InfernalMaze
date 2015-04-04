package fr.quentinmachu.infernalmaze.game;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import fr.quentinmachu.infernalmaze.game.controllers.InputController;
import fr.quentinmachu.infernalmaze.ui.Timer;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

public abstract class Game {
	// Constants
	public static int TARGET_FPS = 60;
    public static int TARGET_UPS = 30;
    
    // Variables
    private String windowName;
	private int windowWidth;
	private int windowHeight;
	private String inputControllerName;
	private InputController inputController;
    
	// Internal variables
    private Timer timer;
	private GLFWErrorCallback errorCallback;
    private long window;
	
    public Game(String windowName, int windowWidth, int windowHeight, String inputControllerName) {
    	this.windowName = windowName;
    	this.windowWidth = windowWidth;
    	this.windowHeight = windowHeight;
    	this.inputControllerName = inputControllerName;
    }
    
    public void run() {
        _init();
        _loop();
        _dispose();
    }
 
    private void _init() {    	
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
        window = glfwCreateWindow(windowWidth, windowHeight, windowName, NULL, NULL);
        if(window == NULL) throw new RuntimeException("Failed to create the GLFW window");
        
        // Center our window
        ByteBuffer vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        glfwSetWindowPos(window, (GLFWvidmode.width(vidmode) - windowWidth) / 2, (GLFWvidmode.height(vidmode) - windowHeight) / 2);

        glfwMakeContextCurrent(window); // Make the OpenGL context current
        GLContext.createFromCurrent(); // Create context  
        
        glfwSwapInterval(1); // Enable v-sync
        
    	// Initialize timer
    	timer = new Timer();
    	timer.init(); 
    	
    	// Initialize input
    	try {
        	inputController = (InputController) Class.forName("fr.quentinmachu.infernalmaze.game.controllers."+inputControllerName).getConstructor(Game.class).newInstance(this);
    	} catch(Exception e) {
    		throw new IllegalArgumentException(inputControllerName + " is not a supported controller.");
    	}
    	inputController.init();
    	
    	// Initialize the game
    	init();
    }

	/**
     * Releases resources that where used by the game.
     */
    private void _dispose() {
        // Dispose input
        inputController.dispose();
        
        // Release window
        glfwDestroyWindow(window);

        // Terminate GLFW and release the error callback
        glfwTerminate();
        errorCallback.release();
        
        // Dispose the game
        dispose();
    }
    
    /**
     * Loop the game
     */
    private void _loop() {
        float delta;
        float accumulator = 0f;
        float interval = 1f / TARGET_UPS;
        float alpha;
        
        while(glfwWindowShouldClose(window) == GL_FALSE) {
        	delta = timer.getDelta();
            accumulator += delta;
            
            inputController.poll();
            
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

            // Update timer
            timer.updateFPS();
            timer.update();

            // Sync @ FPS
            _sync(TARGET_FPS);
        }
    }
    
    /**
     * Synchronizes the game at specified frames per second.
     *
     * @param fps Frames per second
     */
    private void _sync(int fps) {
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
    
	/////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////
    
    /**
     * Initializes the game
     */
    public abstract void init();
    
    /**
     * Updates the game (fixed timestep).
     */
    public abstract void update();
    
    /**
     * Renders the game (with interpolation).
     *
     * @param alpha Alpha value, needed for interpolation
     */
    public abstract void render(float alpha);
    
    /**
     * Disposes the game
     */
    public abstract void dispose();
    
	/////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////
    
	/**
	 * @return the window
	 */
	public long getWindow() {
		return window;
	}

	/**
	 * @return the input
	 */
	public InputController getInputController() {
		return inputController;
	}
}
