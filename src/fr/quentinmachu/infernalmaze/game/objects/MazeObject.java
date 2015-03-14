package fr.quentinmachu.infernalmaze.game.objects;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glVertex3d;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glTexCoord2f; 

import java.awt.Color;
import java.awt.Point;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import fr.quentinmachu.infernalmaze.game.Game;
import fr.quentinmachu.infernalmaze.maze.Direction;
import fr.quentinmachu.infernalmaze.maze.Maze;
import fr.quentinmachu.infernalmaze.ui.Camera;
import fr.quentinmachu.infernalmaze.ui.Renderer;
import fr.quentinmachu.infernalmaze.ui.Texture;
import fr.quentinmachu.infernalmaze.ui.math.Matrix4f;
import fr.quentinmachu.infernalmaze.ui.math.Vector3f;
import fr.quentinmachu.infernalmaze.ui.math.Vector4f;
import fr.quentinmachu.infernalmaze.ui.state.GameState;

public class MazeObject implements GameObject {
	public static final double WALL_THICKNESS = 0.2;
	public static final double WALL_HEIGHT = 1;
	public static final float MAZE_MAX_INCLINATION = 20f;
	
	private GameState gameState;
	private Maze maze;
	
	private float ry;
	private float rx;
	
	private Renderer floorRenderer; //TODO Dispose me
	private Texture floorTexture; //TODO Delete me
	private Renderer wallRenderer; //TODO Dispose me
	private Texture wallTexture; //TODO Delete me
	
	public MazeObject(GameState gameState, Maze maze) {
		this.gameState = gameState;
		this.maze = maze;
		setRx(0);
		setRy(0);
		
		floorRenderer = new Renderer(gameState.getCamera());
		floorRenderer.init();
		floorTexture = Texture.loadTexture("resources/floor.png");
		
		wallRenderer = new Renderer(gameState.getCamera());
		wallRenderer.init();
		wallTexture = Texture.loadTexture("resources/wall.jpg");
	}


	@Override
	public void input() {
		// Update maze rotation
		setRy(ry+gameState.getInput().getRy());
		setRx(rx+gameState.getInput().getRx());
	}

	@Override
	public void update() {
		
	}

	@Override
	public void render(float alpha) {
		Matrix4f model = new Matrix4f();
		
		// Floor transformation
		model = model.multiply(getFloorTransformation());
    	
        /* SLERP Model
        float lerpAngle = (1f - alpha) * previousAngle + alpha * angle;
	    Matrix4f model = Matrix4f.rotate(lerpAngle, 0f, 0f, 1f);
	    glUniformMatrix4(uniModel, false, model.getBuffer());
	    */
        
        /*LERP Position
        Vector2f interpolatedPosition = previousPosition.lerp(position, alpha);
        float x = interpolatedPosition.x;
        float y = interpolatedPosition.y;*/
     	
        // Floor
		
        floorRenderer.begin();
        floorTexture.bind();
        floorRenderer.setModel(model);
        
        floorRenderer.reserveVertices(6);

        floorRenderer.addVertex(0, 0, 0, 0, 0);
        floorRenderer.addVertex(maze.getWidth(), maze.getHeight(), 0, 1, 1);
        floorRenderer.addVertex(0, maze.getHeight(), 0, 0, 1);
        
        floorRenderer.addVertex(0, 0, 0, 0, 0);
        floorRenderer.addVertex(maze.getWidth(), maze.getHeight(), 0, 1, 1);
        floorRenderer.addVertex(maze.getWidth(), 0, 0, 1, 0);
                
        floorRenderer.end();
        
        // Walls
        wallRenderer.begin();
        wallTexture.bind();
        wallRenderer.setModel(model);
                
        for(int y = 0; y < maze.getHeight(); y++) {
        	// Left wall
        	drawXYAlignedWall(0, y, 0, 0, y+1, 0);
        	for(int x = 0; x < maze.getWidth(); x++) {
				// Top wall
        		drawXYAlignedWall(x, 0, 0, x+1, 0, 0);
        		if(
					((maze.getCell(x, y) != 0 || y+1 >= maze.getHeight() || maze.getCell(x, y+1) != 0) && ((maze.getCell(x, y) & Direction.SOUTH.bit) == 0))
					|| ((maze.getCell(x, y) == 0 && x+1 < maze.getWidth() && maze.getCell(x+1, y) == 0) && (y+1 >= maze.getHeight() && (maze.getCell(x, y+1) != 0 && maze.getCell(x+1, y+1) != 0)))
					|| ((maze.getCell(x, y) & Direction.EAST.bit) != 0) && (((maze.getCell(x, y) | maze.getCell(x+1, y)) & Direction.SOUTH.bit) == 0)
				) {
					// Bottom wall
					drawXYAlignedWall(x, y+1, 0, x+1, y+1, 0);
				}
				
				if((maze.getCell(x, y) != 0 || x+1 >= maze.getWidth() || maze.getCell(x+1, y) != 0) && ((maze.getCell(x, y) & Direction.EAST.bit) == 0)) {
					// Right wall
					drawXYAlignedWall(x+1, y, 0, x+1, y+1, 0);
				}
			}
		}
        wallRenderer.end();
	}
	
	public Matrix4f getFloorTransformation() {
		Matrix4f transform = new Matrix4f();
		transform = transform.multiply(Matrix4f.translate(maze.getWidth()/2, maze.getHeight()/2, 0));
		transform = transform.multiply(Matrix4f.rotate(rx, 1, 0, 0));
		transform = transform.multiply(Matrix4f.rotate(ry, 0, 1, 0));
		transform = transform.multiply(Matrix4f.translate(-maze.getWidth()/2, -maze.getHeight()/2, 0));
		return transform;
	}


	private void drawXYAlignedWall(int x1, int y1, int z1, int x2, int y2, int z2) {
		wallRenderer.reserveVertices(6);
		
		wallRenderer.addVertex(x1, y1, z1, 0, 0);
		wallRenderer.addVertex(x1, y1, (float) (z1+WALL_HEIGHT), 0, 1);
		wallRenderer.addVertex(x2, y2, z2, 1, 0);
		
		wallRenderer.addVertex(x2, y2, z2, 1, 0);
		wallRenderer.addVertex(x1, y1, (float) (z1+WALL_HEIGHT), 0, 1);
		wallRenderer.addVertex(x2, y2, (float) (z2+WALL_HEIGHT), 1, 1);
	}

	/**
	 * @return the maze
	 */
	public Maze getMaze() {
		return maze;
	}

	/**
	 * @return the rx
	 */
	public float getRy() {
		return ry;
	}

	/**
	 * @param rx the rx to set
	 */
	private void setRy(float ry) {
		if(ry>MAZE_MAX_INCLINATION) this.ry = MAZE_MAX_INCLINATION;
		else if(ry<-MAZE_MAX_INCLINATION) this.ry = -MAZE_MAX_INCLINATION;
		else this.ry = ry;
	}

	/**
	 * @return the rz
	 */
	public float getRx() {
		return rx;
	}

	/**
	 * @param rz the rz to set
	 */
	private void setRx(float rx) {
		if(rx>MAZE_MAX_INCLINATION) this.rx = MAZE_MAX_INCLINATION;
		else if(rx<-MAZE_MAX_INCLINATION) this.rx = -MAZE_MAX_INCLINATION;
		else this.rx = rx;
	}
}
