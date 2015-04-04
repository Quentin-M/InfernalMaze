package fr.quentinmachu.infernalmaze.game.objects;

import fr.quentinmachu.infernalmaze.game.MazeGame;
import fr.quentinmachu.infernalmaze.maze.MazeTower;

public class MazeTowerObject implements GameObject {
	private MazeTower mazeTower;
	private MazeObject[] mazeObjects;
	
	public MazeTowerObject(MazeGame gameState, MazeTower mazeTower) {
		this.mazeTower = mazeTower;
		
		mazeObjects = new MazeObject[mazeTower.getDepth()];
		for(int i = 0; i<mazeObjects.length; i++) {
			mazeObjects[i] = new MazeObject(gameState, mazeTower.getMazes()[i], i);
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
		for(int i = 0; i<mazeObjects.length; i++) {
			mazeObjects[i].render(alpha);
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
