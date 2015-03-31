package fr.quentinmachu.infernalmaze.game.objects;

import org.lwjgl.opengl.GL11;

import fr.quentinmachu.infernalmaze.game.Game;
import fr.quentinmachu.infernalmaze.maze.Direction;
import fr.quentinmachu.infernalmaze.ui.Material;
import fr.quentinmachu.infernalmaze.ui.Renderer;
import fr.quentinmachu.infernalmaze.ui.Texture;
import fr.quentinmachu.infernalmaze.ui.math.Matrix4f;
import fr.quentinmachu.infernalmaze.ui.math.Quaternion;
import fr.quentinmachu.infernalmaze.ui.math.Vector3f;
import fr.quentinmachu.infernalmaze.ui.state.GameState;

public class BallObject implements GameObject {   
	public static final float SPHERE_STEP = 10f;
	public static final float SPHERE_RADIUS = 0.30f;	
	public static final float GRAVITY = 15f;
	public static final float COLLISION_VELOCITY_DIVIDER = 3f;
	public static final Material material = new Material(new Vector3f(0.2f, 0.2f, 0.2f), new Vector3f(1.0f, 1.0f, 1.0f), new Vector3f(2.0f, 2.0f, 2.0f), 5f);

	private GameState gameState;
	
	private Renderer renderer; //TODO Dispose me
	private Texture texture; //TODO Delete me
	
	private Vector3f position;
	private Vector3f velocity;
	private Quaternion rotation;
	
	public BallObject(GameState gameState) {
		this.gameState = gameState;
		
		texture = Texture.loadTexture("resources/earth.png", true);
		renderer = new Renderer(gameState.getCamera(), texture, gameState.getMainLight(), material, gameState.getAmbient(), true);
		renderer.setPrimitive(GL11.GL_TRIANGLE_STRIP);
		
		position = new Vector3f();
		velocity = new Vector3f();
		rotation = new Quaternion();
	}

	@Override
	public void input() {
		
	}

	@Override
	public void update() {
		float delta = (1f / Game.TARGET_UPS);
		
		// Manage collision
		collide();
		
		// Set position & velocity
		float ax = (float) (GRAVITY * Math.sin(Math.toRadians(gameState.getCurrentMazeObject().getRy())));
		float ay = (float) (GRAVITY * Math.sin(Math.toRadians(-gameState.getCurrentMazeObject().getRx())));
		float dx = (float) ((velocity.x * delta) + (0.5 * ax * Math.pow(delta, 2)));
		float dy = (float) ((velocity.y * delta) + (0.5 * ay * Math.pow(delta, 2)));
		
		setPosition(position.add(new Vector3f(dx, dy, 0)));
		setVelocity(velocity.add(new Vector3f(ax * delta, ay * delta, 0)));
		
		// Set Rotation
        Vector3f up = new Vector3f(0, 0, 1);
        Vector3f vel = velocity.normalize();
        Vector3f axis = vel.cross(up);
        if(axis.x != 0 || axis.y != 0 || axis.z != 0) {
        	float angle = (velocity.length() * delta) / SPHERE_RADIUS;
        	
        	Quaternion frameRot = new Quaternion();
        	frameRot.setFromAxisAngle(axis, angle);
            rotation = rotation.multiply(frameRot);
        }
	}

	private void collide() {
		int mazeCoordX = (int) Math.floor(position.x);
		int mazeCoordY = (int) Math.floor(position.y);
		
		// Collision (Walls)
		if(velocity.x > 0 && position.x + SPHERE_RADIUS >= mazeCoordX + 1 - MazeObject.WALL_THICKNESS/2 && !gameState.getCurrentMazeObject().getMaze().isPathOpened(mazeCoordX, mazeCoordY, Direction.EAST)) {
			// Right wall
			position.x = mazeCoordX + 1 - SPHERE_RADIUS - MazeObject.WALL_THICKNESS/2;
			velocity.x = - velocity.x / COLLISION_VELOCITY_DIVIDER;
		}
		if(velocity.x < 0 && position.x - SPHERE_RADIUS <= mazeCoordX + MazeObject.WALL_THICKNESS/2 && !gameState.getCurrentMazeObject().getMaze().isPathOpened(mazeCoordX, mazeCoordY, Direction.WEST)) {
			// Left wall
			position.x = mazeCoordX + SPHERE_RADIUS + MazeObject.WALL_THICKNESS/2;
			velocity.x = - velocity.x / COLLISION_VELOCITY_DIVIDER;
		}
		if(velocity.y > 0 && position.y + SPHERE_RADIUS >= mazeCoordY + 1 - MazeObject.WALL_THICKNESS/2 && !gameState.getCurrentMazeObject().getMaze().isPathOpened(mazeCoordX, mazeCoordY, Direction.SOUTH)) {
			// South wall
			position.y = mazeCoordY + 1 - SPHERE_RADIUS - MazeObject.WALL_THICKNESS/2;
			velocity.y = - velocity.y / COLLISION_VELOCITY_DIVIDER;
		}
		if(velocity.y < 0 && position.y - SPHERE_RADIUS <= mazeCoordY + MazeObject.WALL_THICKNESS/2 && !gameState.getCurrentMazeObject().getMaze().isPathOpened(mazeCoordX, mazeCoordY, Direction.NORTH)) {
			// Top wall
			position.y = mazeCoordY + SPHERE_RADIUS + MazeObject.WALL_THICKNESS/2;
			velocity.y = - velocity.y / COLLISION_VELOCITY_DIVIDER;
		}
		// Collision (Wall sides)
		if(position.y - SPHERE_RADIUS <= mazeCoordY && position.x - SPHERE_RADIUS <= mazeCoordX) {
			if(velocity.x < 0 && mazeCoordX-1 >= 0 && !gameState.getCurrentMazeObject().getMaze().isPathOpened(mazeCoordX-1, mazeCoordY, Direction.NORTH)) {
				// Top-Left corner. Horizontal
				//position.x = mazeCoordX + SPHERE_RADIUS;
				velocity.x = - velocity.x / COLLISION_VELOCITY_DIVIDER;
				//System.out.println("SupGauche - Horizontal");
			}
			if(velocity.y < 0 && mazeCoordY-1 >= 0 && !gameState.getCurrentMazeObject().getMaze().isPathOpened(mazeCoordX, mazeCoordY-1, Direction.WEST)) {
				// Top-Left corner. Vertical
				//position.y = mazeCoordY + SPHERE_RADIUS;
				velocity.y = - velocity.y / COLLISION_VELOCITY_DIVIDER;
			}
		}
		if(position.y - SPHERE_RADIUS <= mazeCoordY && position.x + SPHERE_RADIUS >= mazeCoordX+1) {
			if(velocity.x > 0 && mazeCoordX+1 <= gameState.getCurrentMazeObject().getMaze().getWidth() && !gameState.getCurrentMazeObject().getMaze().isPathOpened(mazeCoordX+1, mazeCoordY, Direction.NORTH)) {
				// Top-Right corner. Horizontal
				//position.x = mazeCoordX + 1 - SPHERE_RADIUS;
				velocity.x = - velocity.x / COLLISION_VELOCITY_DIVIDER;
			}
			if(velocity.y < 0 && mazeCoordY-1 >= 0 && !gameState.getCurrentMazeObject().getMaze().isPathOpened(mazeCoordX, mazeCoordY-1, Direction.EAST)) {
				// Top-Right corner. Vertical
				//position.y = mazeCoordY + SPHERE_RADIUS;
				velocity.y = - velocity.y / COLLISION_VELOCITY_DIVIDER;
			}
		}
		if(position.y + SPHERE_RADIUS >= mazeCoordY+1 && position.x - SPHERE_RADIUS <= mazeCoordX) {
			if(velocity.x < 0 && mazeCoordX-1 >= 0 && !gameState.getCurrentMazeObject().getMaze().isPathOpened(mazeCoordX-1, mazeCoordY, Direction.SOUTH)) {
				// South-West corner. Horizontal
				//position.x = mazeCoordX + SPHERE_RADIUS;
				velocity.x = - velocity.x / COLLISION_VELOCITY_DIVIDER;
			}
			if(velocity.y > 0 && mazeCoordY+1 < gameState.getCurrentMazeObject().getMaze().getHeight() && !gameState.getCurrentMazeObject().getMaze().isPathOpened(mazeCoordX, mazeCoordY+1, Direction.WEST)) {
				// South-West corner. Vertical
				//position.y = mazeCoordY + 1 - SPHERE_RADIUS;
				velocity.y = - velocity.y / COLLISION_VELOCITY_DIVIDER;
			}
		}
		if(position.y + SPHERE_RADIUS >= mazeCoordY+1 && position.x + SPHERE_RADIUS >= mazeCoordX+1) {
			if(velocity.x > 0 && mazeCoordX+1 <= gameState.getCurrentMazeObject().getMaze().getWidth() && !gameState.getCurrentMazeObject().getMaze().isPathOpened(mazeCoordX+1, mazeCoordY, Direction.SOUTH)) {
				// South-East corner. Horizontal
				//position.x = mazeCoordX + 1 - SPHERE_RADIUS;
				velocity.x = - velocity.x / COLLISION_VELOCITY_DIVIDER;
			}
			if(velocity.y > 0 && mazeCoordY+1 < gameState.getCurrentMazeObject().getMaze().getHeight() && !gameState.getCurrentMazeObject().getMaze().isPathOpened(mazeCoordX, mazeCoordY+1, Direction.EAST)) {
				// South-East corner. Vertical
				//position.y = mazeCoordY + 1 - SPHERE_RADIUS;
				velocity.y = - velocity.y / COLLISION_VELOCITY_DIVIDER;
			}
		}
	}
	
	@Override
	public void render(float alpha) {
        /*Vector2f interpolatedPosition = previousPosition.lerp(position, alpha);
        float x = interpolatedPosition.x;
        float y = interpolatedPosition.y;*/
		
		Matrix4f model = new Matrix4f();
		
		// Floor transformation
		model = model.multiply(gameState.getCurrentMazeObject().getFloorTransformation());

		// Ball position / rotation
		model = model.multiply(Matrix4f.translate(position.x, position.y, position.z));
        model = model.multiply(rotation.toRotationMatrix());
		
		// Ball scale
		model = model.multiply(Matrix4f.scale(SPHERE_RADIUS, SPHERE_RADIUS, SPHERE_RADIUS));
		
		renderer.begin();
        texture.bind();
        renderer.setModel(model);
        
        renderer.reserveVertices((int) ((Math.ceil(180/SPHERE_STEP) * Math.ceil(361/SPHERE_STEP)) * 2));
        
    	float angleA, angleB;
    	float cos, sin;
    	float r1, r2;
    	float h1, h2;
        
       	for (angleA = -90.0f; angleA < 90.0f; angleA += SPHERE_STEP) {
            r1 = (float) Math.cos(angleA * Math.PI / 180.0);
    		r2 = (float) Math.cos((angleA + SPHERE_STEP) * Math.PI / 180.0);
    		h1 = (float) Math.sin(angleA * Math.PI / 180.0);
    		h2 = (float) Math.sin((angleA + SPHERE_STEP) * Math.PI / 180.0);

    		for (angleB = 0.0f; angleB <= 360.0f; angleB += SPHERE_STEP) {
    			cos = (float) Math.cos(angleB * Math.PI / 180.0);
    			sin = -(float) Math.sin(angleB * Math.PI / 180.0);

    			Vector3f p1 = new Vector3f(r2*cos, h2, r2*sin);
    			Vector3f p2 = new Vector3f(r1*cos, h1, r1*sin);
    			Vector3f n1 = p1.normalize();
    			Vector3f n2 = p2.normalize();
    			
    			renderer.addVertex(p1.x, p1.y, p1.z, ((-r2*sin/Math.abs(r2*cos)) + 1)/2, ((-h2/Math.abs(r2*cos)) + 1)/2, n1.x, n1.y, n1.z);
    			renderer.addVertex(p2.x, p2.y, p2.z, ((-r1*sin/Math.abs(r1*cos)) + 1)/2, ((-h1/Math.abs(r1*cos)) + 1)/2, n2.x, n2.y, n2.z);
    		}
    	}
       	
        renderer.end();
	}

	/**
	 * @return the position
	 */
	public Vector3f getPosition() {
		return position;
	}

	/**
	 * @param position the position to set
	 */
	public void setPosition(Vector3f position) {
		this.position.x = position.x;
		this.position.y = position.y;
		this.position.z = position.z;
	}

	/**
	 * @return the velocity
	 */
	public Vector3f getVelocity() {
		return velocity;
	}

	/**
	 * @param velocity the velocity to set
	 */
	public void setVelocity(Vector3f velocity) {
		this.velocity = velocity;
	}
}
