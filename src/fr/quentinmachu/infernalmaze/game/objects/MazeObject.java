package fr.quentinmachu.infernalmaze.game.objects;

import fr.quentinmachu.infernalmaze.maze.Direction;
import fr.quentinmachu.infernalmaze.maze.Maze;
import fr.quentinmachu.infernalmaze.ui.Material;
import fr.quentinmachu.infernalmaze.ui.Renderer;
import fr.quentinmachu.infernalmaze.ui.Texture;
import fr.quentinmachu.infernalmaze.ui.math.Matrix4f;
import fr.quentinmachu.infernalmaze.ui.math.Vector3f;
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
	public static final Material floorMaterial = new Material(new Vector3f(1.0f, 1.0f, 1.0f), new Vector3f(1.0f, 1.0f, 1.0f), new Vector3f(0.33f, 0.33f, 0.33f), 0.75f);
	private Renderer wallRenderer; //TODO Dispose me
	private Texture wallTexture; //TODO Delete me
	public static final Material wallMaterial = new Material(new Vector3f(1.0f, 1.0f, 1.0f), new Vector3f(1.0f, 1.0f, 1.0f), new Vector3f(0.33f, 0.33f, 0.33f), 0.75f);
	
	public MazeObject(GameState gameState, Maze maze) {
		this.gameState = gameState;
		this.maze = maze;
		setRx(0);
		setRy(0);
		
		floorTexture = Texture.loadTexture("resources/floor.png");
		floorRenderer = new Renderer(gameState.getCamera(), floorTexture, gameState.getMainLight(), floorMaterial, gameState.getAmbient());
		
		wallTexture = Texture.loadTexture("resources/wall.jpg");
		wallRenderer = new Renderer(gameState.getCamera(), wallTexture, gameState.getMainLight(), wallMaterial, gameState.getAmbient());
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
        floorRenderer.setModel(model);
        floorRenderer.drawSurface(0, 0, 0, maze.getWidth(), 0, 0, maze.getWidth(), maze.getHeight(), 0, 0, maze.getHeight(), 0);   
        floorRenderer.end();
        
        // Walls
        wallRenderer.begin();
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
		float thickness = (float) (WALL_THICKNESS / 2);
		
		switch(d) {
			case NORTH:
				wallRenderer.drawSurface(x, y+thickness, 0f, x, y+thickness, WALL_HEIGHT, x+1, y+thickness, WALL_HEIGHT, x+1, y+thickness, 0f);
				wallRenderer.drawSurface(x, y-thickness, 0f, x, y-thickness, WALL_HEIGHT, x+1, y-thickness, WALL_HEIGHT, x+1, y-thickness, 0f);
				wallRenderer.drawSurface(x, y+thickness, WALL_HEIGHT, x, y-thickness, WALL_HEIGHT, x+1, y-thickness, WALL_HEIGHT, x+1, y+thickness, WALL_HEIGHT); // Roof
				wallRenderer.drawSurface(x, y+thickness, 0f, x, y+thickness, WALL_HEIGHT, x, y-thickness, WALL_HEIGHT, x, y-thickness, 0f); // Side 1
				wallRenderer.drawSurface(x+1, y+thickness, 0f, x+1, y+thickness, WALL_HEIGHT, x+1, y-thickness, WALL_HEIGHT, x+1, y-thickness, 0f); // Side 2*/
				break;
			case SOUTH:
				wallRenderer.drawSurface(x, y+1+thickness, 0f, x, y+1+thickness, WALL_HEIGHT, x+1, y+1+thickness, WALL_HEIGHT, x+1, y+1+thickness, 0f);
				wallRenderer.drawSurface(x, y+1-thickness, 0f, x, y+1-thickness, WALL_HEIGHT, x+1, y+1-thickness, WALL_HEIGHT, x+1, y+1-thickness, 0f);
				wallRenderer.drawSurface(x, y+1+thickness, WALL_HEIGHT, x, y+1-thickness, WALL_HEIGHT, x+1, y+1-thickness, WALL_HEIGHT, x+1, y+1+thickness, WALL_HEIGHT); // Roof
				wallRenderer.drawSurface(x, y+1-thickness, 0f, x, y+1-thickness, WALL_HEIGHT, x, y+1+thickness, WALL_HEIGHT, x, y+1+thickness, 0f); // Side 1
				wallRenderer.drawSurface(x+1, y+1+thickness, 0f, x+1, y+1+thickness, WALL_HEIGHT, x+1, y+1-thickness, WALL_HEIGHT, x+1, y+1-thickness, 0f); // Side 2*/
				break;
			case WEST:
				wallRenderer.drawSurface(x+thickness, y+1, WALL_HEIGHT, x+thickness, y, WALL_HEIGHT, x+thickness, y, 0f, x+thickness, y+1, 0f);
				wallRenderer.drawSurface(x-thickness, y, 0f, x-thickness, y, WALL_HEIGHT, x-thickness, y+1, WALL_HEIGHT, x-thickness, y+1, 0f);
				wallRenderer.drawSurface(x-thickness, y, WALL_HEIGHT, x+thickness, y, WALL_HEIGHT, x+thickness, y+1, WALL_HEIGHT, x-thickness, y+1, WALL_HEIGHT); // Roof
				wallRenderer.drawSurface(x+thickness, y, 0f, x+thickness, y, WALL_HEIGHT, x-thickness, y, WALL_HEIGHT, x-thickness, y, 0f); // Side 1
				wallRenderer.drawSurface(x-thickness, y+1, 0f, x-thickness, y+1, WALL_HEIGHT, x+thickness, y+1, WALL_HEIGHT, x+thickness, y+1, 0f); // Side 2*/
				break;
			case EAST:
				wallRenderer.drawSurface(x+1+thickness, y+1, 0f, x+1+thickness, y+1, WALL_HEIGHT, x+1+thickness, y, WALL_HEIGHT, x+1+thickness, y, 0f);
				wallRenderer.drawSurface(x+1-thickness, y, 0f, x+1-thickness, y, WALL_HEIGHT, x+1-thickness, y+1, WALL_HEIGHT, x+1-thickness, y+1, 0f);
				wallRenderer.drawSurface(x+1-thickness, y, WALL_HEIGHT, x+1+thickness, y, WALL_HEIGHT, x+1+thickness, y+1, WALL_HEIGHT, x+1-thickness, y+1, WALL_HEIGHT); // Roof
				wallRenderer.drawSurface(x+1+thickness, y, 0f, x+1+thickness, y, WALL_HEIGHT, x+1-thickness, y, WALL_HEIGHT, x+1-thickness, y, 0f); // Side 1
				wallRenderer.drawSurface(x+1-thickness, y+1, 0f, x+1-thickness, y+1, WALL_HEIGHT, x+1+thickness, y+1, WALL_HEIGHT, x+1+thickness, y+1, 0f); // Side 2*/
				break;
			default:
				throw new IllegalArgumentException("Unsupported direction");
		}
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
