package fr.quentinmachu.infernalmaze.game.objects;

import org.lwjgl.opengl.GL11;

import fr.quentinmachu.infernalmaze.game.Game;
import fr.quentinmachu.infernalmaze.maze.Direction;
import fr.quentinmachu.infernalmaze.ui.Renderer;
import fr.quentinmachu.infernalmaze.ui.Texture;
import fr.quentinmachu.infernalmaze.ui.math.Matrix4f;
import fr.quentinmachu.infernalmaze.ui.math.Vector2f;
import fr.quentinmachu.infernalmaze.ui.math.Vector3f;
import fr.quentinmachu.infernalmaze.ui.math.Vector4f;
import fr.quentinmachu.infernalmaze.ui.state.GameState;

public class BallObject implements GameObject {   
	public static final float SPHERE_STEP = 10f;
	public static final float SPHERE_RADIUS = 0.30f;	
	public static final float GRAVITY = 15f;
	public static final float COLLISION_VELOCITY_DIVIDER = 3f;
	
	private GameState gameState;
	
	private Renderer renderer; //TODO Dispose me
	private Texture texture; //TODO Delete me
	
	private Vector3f position;
	private Vector3f velocity;
	private Vector2f rotation;
	
	public BallObject(GameState gameState) {
		this.gameState = gameState;
		
		renderer = new Renderer(gameState.getCamera(), true);
		renderer.init();
		renderer.setPrimitive(GL11.GL_TRIANGLE_STRIP);
		texture = Texture.loadTexture("resources/earth.png", true);
		
		position = new Vector3f();
		velocity = new Vector3f();
		rotation = new Vector2f();
	}

	@Override
	public void input() {
		
	}

	@Override
	public void update() {
		float delta = (1f / Game.TARGET_UPS);
		
		// Set position & velocity
		float ax = (float) (GRAVITY * Math.sin(Math.toRadians(gameState.getCurrentMazeObject().getRy())));
		float ay = (float) (GRAVITY * Math.sin(Math.toRadians(-gameState.getCurrentMazeObject().getRx())));
		float dx = (float) ((velocity.x * delta) + (1/2 * ax * Math.pow(delta, 2)));
		float dy = (float) ((velocity.y * delta) + (1/2 * ay * Math.pow(delta, 2)));
		
		setPosition(position.add(new Vector3f(dx, dy, 0)));
		setVelocity(velocity.add(new Vector3f(ax * delta, ay * delta, 0)));
		setRotation(rotation.add(new Vector2f((float) - Math.toDegrees(dy/SPHERE_RADIUS), (float) Math.toDegrees(dx/SPHERE_RADIUS))));
	
		// Manage collision
		collide();
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
	
	Matrix4f rot = new Matrix4f();
	
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

		//FIXME The rotation is wrong
		Vector4f xAxis = new Vector4f(1, 0, 0, 0);
		Matrix4f rotMatrixX = Matrix4f.rotate(rotation.x, xAxis.x, xAxis.y, xAxis.z);
		Vector4f yAxis = new Vector4f(0, 1, 0, 0);
		yAxis = rotMatrixX.invert().multiply(yAxis);
		Matrix4f rotMatrixY = Matrix4f.rotate(rotation.y, yAxis.x, yAxis.y, yAxis.z);
		model = model.multiply(rotMatrixX).multiply(rotMatrixY);
		
		/*float angle = (velocity.length() * alpha) / SPHERE_RADIUS;
		Vector3f up = new Vector3f(0, 0, 1);
		Vector3f vel = velocity.normalize();
		Vector3f axis = vel.cross(up);
		if(velocity.length() != 0) rot = rot.multiply(Matrix4f.rotate(angle, axis.x, axis.y, axis.z));*/
		
		model = model.multiply(rot);
		
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

    			renderer.addVertex(r2*cos, h2, r2*sin, ((-r2*sin/Math.abs(r2*cos)) + 1)/2, ((-h2/Math.abs(r2*cos)) + 1)/2);
    			renderer.addVertex(r1*cos, h1, r1*sin, ((-r1*sin/Math.abs(r1*cos)) + 1)/2, ((-h1/Math.abs(r1*cos)) + 1)/2);
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

	/**
	 * @return the rotation
	 */
	public Vector2f getRotation() {
		return rotation;
	}

	/**
	 * @param rotation the rotation to set
	 */
	public void setRotation(Vector2f rotation) {
		this.rotation = rotation;
	}
}