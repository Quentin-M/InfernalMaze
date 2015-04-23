package fr.quentinmachu.infernalmaze.game.objects;

import java.awt.Point;
import java.util.ArrayList;

import fr.quentinmachu.infernalmaze.game.Game;
import fr.quentinmachu.infernalmaze.game.MazeGame;
import fr.quentinmachu.infernalmaze.maze.Direction;
import fr.quentinmachu.infernalmaze.maze.Maze;
import fr.quentinmachu.infernalmaze.ui.Light;
import fr.quentinmachu.infernalmaze.ui.Material;
import fr.quentinmachu.infernalmaze.ui.Renderer;
import fr.quentinmachu.infernalmaze.ui.Texture;
import fr.quentinmachu.infernalmaze.ui.math.Matrix4f;
import fr.quentinmachu.infernalmaze.ui.math.Vector3f;
import fr.quentinmachu.infernalmaze.ui.math.Vector4f;

public class MazeObject implements GameObject {

    private MazeGame gameState;
    private Maze maze;
    private int level;

    private final Vector3f initial_position;
    private final Vector3f initial_rotation;
    private Vector3f position;
    private Vector3f rotation;

    // Constants
    public static final float WALL_THICKNESS = 0.2f;
    public static final float WALL_HEIGHT = 1f;
    public static final float MAZE_TO_INIT_ANIMATION_SPEED = 1f;
    public static final float MAZE_ANIMATION_SPEED = 10f;
    public static final Vector4f ENTRANCE_GATE_DIFFUSE_LIGHT_COLOR = new Vector4f(0.4f, 1.0f, 0.4f, 0.0f);
    public static final Vector4f ENTRANCE_GATE_SPECULAR_LIGHT_COLOR = new Vector4f(0.4f, 1.0f, 0.4f, 0.0f);
    public static final Vector4f EXIT_GATE_DIFFUSE_LIGHT_COLOR = new Vector4f(0.4f, 1.0f, 0.4f, 0.0f);
    public static final Vector4f EXIT_GATE_SPECULAR_LIGHT_COLOR = new Vector4f(0.4f, 1.0f, 0.4f, 0.0f);
    public static final Vector4f UP_GATE_DIFFUSE_LIGHT_COLOR = new Vector4f(1.0f, 0.4f, 0.4f, 0.0f);
    public static final Vector4f UP_GATE_SPECULAR_LIGHT_COLOR = new Vector4f(1.0f, 0.4f, 0.4f, 0.0f);
    public static final Vector4f DOWN_GATE_DIFFUSE_LIGHT_COLOR = new Vector4f(0.4f, 0.4f, 1.0f, 0.0f);
    public static final Vector4f DOWN_GATE_SPECULAR_LIGHT_COLOR = new Vector4f(0.4f, 0.4f, 1.0f, 0.0f);
    public static final Vector3f GATE_LIGHT_ATTENUATION = new Vector3f(0.0f, 0.5f, 1.0f);

    // Rendering
    private final Renderer floorRenderer;
    private final Texture floorTexture;
    private final Material floorMaterial;

    private final Renderer wallRenderer;
    private final Texture wallTexture;
    private final Material wallMaterial;

    public MazeObject(MazeGame gameState, Maze maze, int level) {
	this.gameState = gameState;
	this.maze = maze;
	this.level = level;

	// Initialize position/rotation
	initial_position = new Vector3f(0, 0, -gameState.getMazeGap() * level);
	initial_rotation = new Vector3f(0, 0, 0);
	position = new Vector3f(initial_position.x, initial_position.y, initial_position.z);
	rotation = new Vector3f(initial_rotation.x, initial_rotation.y, initial_rotation.z);

	// Initialize floor rendering
	floorTexture = Texture.loadTexture("resources/floor.png");
	floorMaterial = new Material(new Vector3f(1.0f, 1.0f, 1.0f), new Vector3f(1.0f, 1.0f, 1.0f), new Vector3f(0.33f, 0.33f, 0.33f), 0.75f);
	floorRenderer = new Renderer(gameState.getCamera(), floorTexture, floorMaterial, gameState.getAmbientLight());

	// Initialize wall rendering
	wallTexture = Texture.loadTexture("resources/wall.jpg");
	wallMaterial = new Material(new Vector3f(1.0f, 1.0f, 1.0f), new Vector3f(1.0f, 1.0f, 1.0f), new Vector3f(0.33f, 0.33f, 0.33f), 0.75f);
	wallRenderer = new Renderer(gameState.getCamera(), wallTexture, wallMaterial, gameState.getAmbientLight());
    }

    @Override
    public void update() {
	float delta = 1 / (float) Game.TARGET_UPS;

	if (level - gameState.getCameraLevel() < 0.01) {
	    // Update position to controller XY + Current Level Z smoothly
	    position = position.lerp(new Vector3f(gameState.getInputController().getX(), gameState.getInputController().getY(), position.z), delta * MAZE_ANIMATION_SPEED);

	    // Update rotation
	    rotation = rotation.lerp(new Vector3f(-gameState.getInputController().getRx(), -gameState.getInputController().getRy(), -gameState.getInputController().getRz()), delta * MAZE_ANIMATION_SPEED);
	} else {
	    // Go back to initial state smoothly
	    position = position.lerp(initial_position, delta * MAZE_TO_INIT_ANIMATION_SPEED);
	    rotation = rotation.lerp(initial_rotation, delta * MAZE_TO_INIT_ANIMATION_SPEED);
	}

    }

    @Override
    public void render(float alpha) {
	Matrix4f model = new Matrix4f();

	// Floor transformation
	model = model.multiply(getFloorTransformation());

	// Floor
	floorRenderer.begin();
	floorRenderer.setModel(model);
	floorRenderer.setLights(getLights());
	floorRenderer.drawSurface(0, 0, 0, maze.getWidth(), 0, 0, maze.getWidth(), maze.getHeight(), 0, 0, maze.getHeight(), 0);
	floorRenderer.end();

	// Walls
	wallRenderer.begin();
	wallRenderer.setModel(model);
	wallRenderer.setLights(getLights());

	for (int y = 0; y < maze.getHeight(); y++) {
	    drawWall(0, y, 0, Direction.WEST);

	    for (int x = 0; x < maze.getWidth(); x++) {
		drawWall(x, 0, 0, Direction.NORTH);

		if (((maze.getCell(x, y) != 0 || y + 1 >= maze.getHeight() || maze.getCell(x, y + 1) != 0) && ((maze.getCell(x, y) & Direction.SOUTH.bit) == 0)) || ((maze.getCell(x, y) == 0 && x + 1 < maze.getWidth() && maze.getCell(x + 1, y) == 0) && (y + 1 >= maze.getHeight() && (maze.getCell(x, y + 1) != 0 && maze.getCell(x + 1, y + 1) != 0))) || ((maze.getCell(x, y) & Direction.EAST.bit) != 0) && (((maze.getCell(x, y) | maze.getCell(x + 1, y)) & Direction.SOUTH.bit) == 0)) {
		    drawWall(x, y, 0, Direction.SOUTH);
		}

		if ((maze.getCell(x, y) != 0 || x + 1 >= maze.getWidth() || maze.getCell(x + 1, y) != 0) && ((maze.getCell(x, y) & Direction.EAST.bit) == 0)) {
		    drawWall(x, y, 0, Direction.EAST);
		}
	    }
	}
	wallRenderer.end();
    }

    public Matrix4f getFloorTransformation() {
	Matrix4f transform = new Matrix4f();

	// Apply position
	transform = transform.multiply(Matrix4f.translate(position.x, position.y, position.z));

	// Apply rotation
	transform = transform.multiply(Matrix4f.translate(maze.getWidth() / 2, maze.getHeight() / 2, 0));
	transform = transform.multiply(Matrix4f.rotate(rotation.x, 1, 0, 0));
	transform = transform.multiply(Matrix4f.rotate(rotation.y, 0, 1, 0));
	transform = transform.multiply(Matrix4f.rotate(rotation.z, 0, 0, 1));
	transform = transform.multiply(Matrix4f.translate(-maze.getWidth() / 2, -maze.getHeight() / 2, 0));

	return transform;
    }

    public ArrayList<Light> getLights() {
	ArrayList<Light> lights = new ArrayList<Light>();
	Vector3f spotDirection = new Vector3f(0.0f, 0.0f, 0.0f);
	lights.add(gameState.getMainLight());

	if (level == 0) // Entrance gate
	    lights.add(new Light(getFloorTransformation().multiply(new Vector4f((float) (gameState.getMazeTower().getMazeTower().getOrigin().getX() + 0.5f), (float) (gameState.getMazeTower().getMazeTower().getOrigin().getY() + 0.5f), WALL_HEIGHT / 2, 1.0f)), ENTRANCE_GATE_DIFFUSE_LIGHT_COLOR, ENTRANCE_GATE_SPECULAR_LIGHT_COLOR, GATE_LIGHT_ATTENUATION.x, GATE_LIGHT_ATTENUATION.y, GATE_LIGHT_ATTENUATION.z, 360f, 0.0f, spotDirection));

	if (level == gameState.getMazeTower().getMazeTower().getEndLevel())
	    lights.add(new Light(getFloorTransformation().multiply(new Vector4f((float) (gameState.getMazeTower().getMazeTower().getEnd().getX() + 0.5f), (float) (gameState.getMazeTower().getMazeTower().getEnd().getY() + 0.5f), WALL_HEIGHT / 2, 1.0f)), EXIT_GATE_DIFFUSE_LIGHT_COLOR, EXIT_GATE_SPECULAR_LIGHT_COLOR, GATE_LIGHT_ATTENUATION.x, GATE_LIGHT_ATTENUATION.y, GATE_LIGHT_ATTENUATION.z, 360f, 0.0f, spotDirection));

	// Down gates
	for (Point p : maze.getDownGates()) {
	    Light l = new Light(getFloorTransformation().multiply(new Vector4f(p.x + 0.5f, p.y + 0.5f, WALL_HEIGHT / 2, 1.0f)), DOWN_GATE_DIFFUSE_LIGHT_COLOR, DOWN_GATE_SPECULAR_LIGHT_COLOR, GATE_LIGHT_ATTENUATION.x, GATE_LIGHT_ATTENUATION.y, GATE_LIGHT_ATTENUATION.z, 360f, 0.0f, spotDirection);

	    lights.add(l);
	}

	// Up gates
	for (Point p : maze.getUpGates()) {
	    Light l = new Light(getFloorTransformation().multiply(new Vector4f(p.x + 0.5f, p.y + 0.5f, WALL_HEIGHT / 2, 1.0f)), UP_GATE_DIFFUSE_LIGHT_COLOR, UP_GATE_SPECULAR_LIGHT_COLOR, GATE_LIGHT_ATTENUATION.x, GATE_LIGHT_ATTENUATION.y, GATE_LIGHT_ATTENUATION.z, 360f, 0.0f, spotDirection);

	    lights.add(l);
	}

	return lights;
    }

    private void drawWall(int x, int y, int z, Direction d) {
	float thickness = (float) (WALL_THICKNESS / 2);

	switch (d) {
	case NORTH:
	    wallRenderer.drawSurface(x, y + thickness, 0f, x, y + thickness, WALL_HEIGHT + 0.01f, x + 1, y + thickness, WALL_HEIGHT + 0.01f, x + 1, y + thickness, 0f);
	    wallRenderer.drawSurface(x, y - thickness, 0f, x, y - thickness, WALL_HEIGHT + 0.01f, x + 1, y - thickness, WALL_HEIGHT + 0.01f, x + 1, y - thickness, 0f);
	    wallRenderer.drawSurface(x, y + thickness, WALL_HEIGHT + 0.01f, x, y - thickness, WALL_HEIGHT + 0.01f, x + 1, y - thickness, WALL_HEIGHT + 0.01f, x + 1, y + thickness, WALL_HEIGHT + 0.01f); // Roof
	    wallRenderer.drawSurface(x, y + thickness, 0f, x, y + thickness, WALL_HEIGHT + 0.01f, x, y - thickness, WALL_HEIGHT + 0.01f, x, y - thickness, 0f); // Side 1
	    wallRenderer.drawSurface(x + 1, y + thickness, 0f, x + 1, y + thickness, WALL_HEIGHT + 0.01f, x + 1, y - thickness, WALL_HEIGHT + 0.01f, x + 1, y - thickness, 0f); // Side 2
	    break;
	case SOUTH:
	    wallRenderer.drawSurface(x, y + 1 + thickness, 0f, x, y + 1 + thickness, WALL_HEIGHT + 0.01f, x + 1, y + 1 + thickness, WALL_HEIGHT + 0.01f, x + 1, y + 1 + thickness, 0f);
	    wallRenderer.drawSurface(x + 1, y + 1 - thickness, 0f, x + 1, y + 1 - thickness, WALL_HEIGHT + 0.01f, x, y + 1 - thickness, WALL_HEIGHT + 0.01f, x, y + 1 - thickness, 0f);
	    wallRenderer.drawSurface(x, y + 1 + thickness, WALL_HEIGHT + 0.01f, x, y + 1 - thickness, WALL_HEIGHT + 0.01f, x + 1, y + 1 - thickness, WALL_HEIGHT + 0.01f, x + 1, y + 1 + thickness, WALL_HEIGHT + 0.01f); // Roof
	    wallRenderer.drawSurface(x, y + 1 - thickness, 0f, x, y + 1 - thickness, WALL_HEIGHT + 0.01f, x, y + 1 + thickness, WALL_HEIGHT + 0.01f, x, y + 1 + thickness, 0f); // Side 1
	    wallRenderer.drawSurface(x + 1, y + 1 + thickness, 0f, x + 1, y + 1 + thickness, WALL_HEIGHT + 0.01f, x + 1, y + 1 - thickness, WALL_HEIGHT + 0.01f, x + 1, y + 1 - thickness, 0f); // Side 2
	    break;
	case WEST:
	    wallRenderer.drawSurface(x + thickness, y + 1, 0f, x + thickness, y + 1, WALL_HEIGHT, x + thickness, y, WALL_HEIGHT, x + thickness, y, 0f);
	    wallRenderer.drawSurface(x - thickness, y, 0f, x - thickness, y, WALL_HEIGHT, x - thickness, y + 1, WALL_HEIGHT, x - thickness, y + 1, 0f);
	    wallRenderer.drawSurface(x - thickness, y, WALL_HEIGHT, x + thickness, y, WALL_HEIGHT, x + thickness, y + 1, WALL_HEIGHT, x - thickness, y + 1, WALL_HEIGHT); // Roof
	    wallRenderer.drawSurface(x + thickness, y, 0f, x + thickness, y, WALL_HEIGHT, x - thickness, y, WALL_HEIGHT, x - thickness, y, 0f); // Side 1
	    wallRenderer.drawSurface(x - thickness, y + 1, 0f, x - thickness, y + 1, WALL_HEIGHT, x + thickness, y + 1, WALL_HEIGHT, x + thickness, y + 1, 0f); // Side 2
	    break;
	case EAST:
	    wallRenderer.drawSurface(x + 1 + thickness, y + 1, 0f, x + 1 + thickness, y + 1, WALL_HEIGHT, x + 1 + thickness, y, WALL_HEIGHT, x + 1 + thickness, y, 0f);
	    wallRenderer.drawSurface(x + 1 - thickness, y, 0f, x + 1 - thickness, y, WALL_HEIGHT, x + 1 - thickness, y + 1, WALL_HEIGHT, x + 1 - thickness, y + 1, 0f);
	    wallRenderer.drawSurface(x + 1 - thickness, y, WALL_HEIGHT, x + 1 + thickness, y, WALL_HEIGHT, x + 1 + thickness, y + 1, WALL_HEIGHT, x + 1 - thickness, y + 1, WALL_HEIGHT); // Roof
	    wallRenderer.drawSurface(x + 1 + thickness, y, 0f, x + 1 + thickness, y, WALL_HEIGHT, x + 1 - thickness, y, WALL_HEIGHT, x + 1 - thickness, y, 0f); // Side 1
	    wallRenderer.drawSurface(x + 1 - thickness, y + 1, 0f, x + 1 - thickness, y + 1, WALL_HEIGHT, x + 1 + thickness, y + 1, WALL_HEIGHT, x + 1 + thickness, y + 1, 0f); // Side 2
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
     * @return the level
     */
    public int getLevel() {
	return level;
    }

    /**
     * @return the rotation
     */
    public Vector3f getRotation() {
	return rotation;
    }
}
