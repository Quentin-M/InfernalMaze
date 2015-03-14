package fr.quentinmachu.infernalmaze.game.objects;

import org.lwjgl.opengl.GL11;

import fr.quentinmachu.infernalmaze.maze.MazeTower;
import fr.quentinmachu.infernalmaze.ui.Camera;
import fr.quentinmachu.infernalmaze.ui.state.GameState;

public class MazeTowerObject implements GameObject {
	private MazeTower mazeTower;
	private MazeObject[] mazeObjects;
	
	public MazeTowerObject(GameState gameState, MazeTower mazeTower) {
		this.mazeTower = mazeTower;
		
		mazeObjects = new MazeObject[mazeTower.getDepth()];
		for(int i = 0; i<mazeObjects.length; i++) {
			mazeObjects[i] = new MazeObject(gameState, mazeTower.getMazes()[i]);
		}
	}
	
	@Override
	public void update() {
		for(int i = 0; i<mazeObjects.length; i++) {
			mazeObjects[i].update();
		}
	}

	@Override
	public void render(float alpha) {
		/*for(int i = 0; i<mazeObjects.length; i++) {
			mazeObjects[i].render(alpha);
		}*/
		mazeObjects[0].render(alpha);
	}

	@Override
	public void input() {
		for(int i = 0; i<mazeObjects.length; i++) {
			mazeObjects[i].input();
		}
	}

	/**
	 * @return the mazeTower
	 */
	public MazeTower getMazeTower() {
		return mazeTower;
	}

	/**
	 * @return the mazeObjects
	 */
	public MazeObject[] getMazeObjects() {
		return mazeObjects;
	}
}
