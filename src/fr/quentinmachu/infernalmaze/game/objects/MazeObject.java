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
	public static final float WALL_THICKNESS = 0.2f;
	public static final float WALL_HEIGHT = 1f;
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
        	drawWall(0, y, 0, Direction.WEST);
        	
        	for(int x = 0; x < maze.getWidth(); x++) {
        		drawWall(x, 0, 0, Direction.NORTH);
        		
        		if(
					((maze.getCell(x, y) != 0 || y+1 >= maze.getHeight() || maze.getCell(x, y+1) != 0) && ((maze.getCell(x, y) & Direction.SOUTH.bit) == 0))
					|| ((maze.getCell(x, y) == 0 && x+1 < maze.getWidth() && maze.getCell(x+1, y) == 0) && (y+1 >= maze.getHeight() && (maze.getCell(x, y+1) != 0 && maze.getCell(x+1, y+1) != 0)))
					|| ((maze.getCell(x, y) & Direction.EAST.bit) != 0) && (((maze.getCell(x, y) | maze.getCell(x+1, y)) & Direction.SOUTH.bit) == 0)
				) {
        			drawWall(x, y, 0, Direction.SOUTH);
				}
				
				if((maze.getCell(x, y) != 0 || x+1 >= maze.getWidth() || maze.getCell(x+1, y) != 0) && ((maze.getCell(x, y) & Direction.EAST.bit) == 0)) {
					drawWall(x, y, 0, Direction.EAST);
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
	
	private void drawWall(int x, int y, int z, Direction d) {
		int x2, y2;
		float dx, dy;
		
		switch(d) {
			case NORTH:
				x2 = x + 1;
				y2 = y;
				dx = 0;
				dy = (float) (WALL_THICKNESS / 2);
				break;
			case SOUTH:
				y = y + 1;
				x2 = x + 1;
				y2 = y;
				dx = 0;
				dy = (float) (WALL_THICKNESS / 2);
				break;
			case WEST:
				x2 = x;
				y2 = y + 1;
				dx = (float) (WALL_THICKNESS / 2);
				dy = 0;
				break;
			case EAST:
				x = x + 1;
				x2 = x;
				y2 = y + 1;
				dx = (float) (WALL_THICKNESS / 2);
				dy = 0;
				break;
			default:
				throw new IllegalArgumentException("Unsupported direction");
		}
		
		wallRenderer.drawSurface(x+dx, y+dy, 0f, x2+dx, y2+dy, 0f, x+dx, y+dy, WALL_HEIGHT, x2+dx, y2+dy, WALL_HEIGHT);
		wallRenderer.drawSurface(x-dx, y-dy, 0f, x2-dx, y2-dy, 0f, x-dx, y-dy, WALL_HEIGHT, x2-dx, y2-dy, WALL_HEIGHT);
		wallRenderer.drawSurface(x+dx, y+dy, WALL_HEIGHT, x2+dx, y2+dy, WALL_HEIGHT, x-dx, y-dy, WALL_HEIGHT, x2-dx, y2-dy, WALL_HEIGHT); // Roof
		wallRenderer.drawSurface(x+dx, y+dy, 0f, x-dx, y-dy, 0f, x+dx, y+dy, WALL_HEIGHT, x-dx, y-dy, WALL_HEIGHT); // Side 1
		wallRenderer.drawSurface(x2+dx, y2+dy, 0f, x2-dx, y2-dy, 0f, x2+dx, y2+dy, WALL_HEIGHT, x2-dx, y2-dy, WALL_HEIGHT); // Side 2
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
