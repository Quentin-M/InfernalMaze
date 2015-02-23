package fr.quentinmachu.infernalmaze.ui;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

public abstract class GameWindow implements State {
	private static int WIDTH = 800;
	private static int HEIGHT = 600;
	private static String WINDOW_NAME = "Infernal Maze";
	private static final int TARGET_FPS = 60;
    private static final int TARGET_UPS = 30;
    
    private Timer timer;
    
	private GLFWErrorCallback errorCallback;
    private GLFWKeyCallback keyCallback;
    private long window;
 
    public GameWindow() {
    	timer = new Timer();
    }
    
    public void run() {
        try {
            init();
            loop();
 
            // Release window and window callbacks
            glfwDestroyWindow(window);
            keyCallback.release();
        } finally {
            // Terminate GLFW and release the GLFWerrorfun
            glfwTerminate();
            errorCallback.release();
        }
    }
 
    private void init() {
        glfwSetErrorCallback(errorCallback = errorCallbackPrint(System.err));
        if(glfwInit() != GL11.GL_TRUE) throw new IllegalStateException("Unable to initialize GLFW");
 
        // Configure our window
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GL_FALSE);
 
        // Create the window
        window = glfwCreateWindow(WIDTH, HEIGHT, WINDOW_NAME, NULL, NULL);
        if(window == NULL) throw new RuntimeException("Failed to create the GLFW window");
        // Center our window
        ByteBuffer vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        glfwSetWindowPos(window, (GLFWvidmode.width(vidmode) - WIDTH) / 2, (GLFWvidmode.height(vidmode) - HEIGHT) / 2);
        
        // Key callback
        glfwSetKeyCallback(window, keyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if(key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                    glfwSetWindowShouldClose(window, GL_TRUE); // We will detect this in our rendering loop
            }
        });

        timer.init(); // Initialize timer
        
        glfwMakeContextCurrent(window); // Make the OpenGL context current
        glfwSwapInterval(1); // Enable v-sync
        glfwShowWindow(window); // Make the window visible
        
        GLContext.createFromCurrent(); // Create context  
        glClearColor(0.75f, 0.75f, 0.75f, 1.0f); // Set the clear color
        
        // Enable blending
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        
        glEnable(GL_DEPTH_TEST); // Enable Z-Buffer
    }
 
    /**
     * Synchronizes the game at specified frames per second.
     *
     * @param fps Frames per second
     */
    public void sync(int fps) {
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
                Logger.getLogger(GameWindow.class.getName()).log(Level.SEVERE, null, ex);
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
            
            while(accumulator >= interval) {
                update();
                timer.updateUPS();
                accumulator -= interval;
            }
            
            // Calculate alpha value for interpolation
            alpha = accumulator / interval;
            
            // Render game
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // Clear the framebuffer
            render(alpha);
        	glfwSwapBuffers(window); // swap the color buffers
            glfwPollEvents(); // Poll for window events.

            // Update timer FPS
            timer.updateFPS();
        	// Logger.getLogger(Main.class.getName()).log(Level.INFO, "FPS: " + timer.getFPS() + " | UPS: " + timer.getUPS());

            // Update timer
            timer.update();

            // Sync @ FPS
            sync(TARGET_FPS);
        }
    }
}
